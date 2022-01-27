package ap_Lab12;

import static spark.Spark.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Task3 {
	
public static void main(String[] args) {
		
		createDBfromJSON();
		
		try {
			File file = new File("./hygieneRating.html");
			Desktop desktop = Desktop.getDesktop();
			if (file.exists()) {
				desktop.open(file);
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		System.setProperty("org.eclipse.jetty.util.log.announce", "false");
		System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
		
		port(8080);
		
		get("/hygieneArray", new Route() {	
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				String postcode = arg0.queryParams("postcode");
				if (postcode.length() < 3) {
					return "<html><head></head><body><p>Array plain-text web service requires at least three characters as a search term</p></body></html>";
				}
				return "<html><head></head><body><p>" + getResultsArray(postcode).toString() + "</p></body></html>";
			}
		});
		
		get("/hygieneJSON", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				arg1.type("application/json");
				String postcode = arg0.queryParams("postcode");
				if (postcode.length() < 3) {
					return "<html><head></head><body><p>JSON web service requires at least three characters as a search term</p></body></html>";
				}
				return resultsArrayToJSON(postcode);
			}
		});
		
		get("/hygieneXML", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				arg1.type("application/xml");
				String postcode = arg0.queryParams("postcode");
				if (postcode.length() < 3) {
					return "<html><head></head><body><p>XML web service requires at least three characters as a search term</p></body></html>";
				}
				return resultsArrayToXML(postcode);
			}
		});
		
		GenerateHygieneRatingUI();	
		
	}
	
	public static void createDBfromJSON() {
		
		ArrayList<Task3_Establishment> listData = new ArrayList<>();
		// IF DATABASE HAS ALREADY BEEN CREATED, THEREFORE THE ARRAYLIST WILL ALREADY BE POPULATED. SKIP THIS STEP AND GO STRAIGHT TO QUERYING, UI GENERATOR AND WEB SERVICE ONLINE
		if (listData.isEmpty()) {
			try {
				new Thread(new Runnable() {
					public void run() {
						try {
							// CAN'T SAVE WEBPAGE TO FILE, CERTIFICATION ISSUES. MANUALLY SAVED TO PROJECT FOLDER
							BufferedReader br = Files.newBufferedReader(Paths.get("./FHRS415en-GB.json"));
							String data = br.readLine();
							// CHANGED TO START READING AT CHARACTER 127, ISSUES WITH NOT FINDING THE JSONARRAY FROM THE JSONOBJECT ROOT. THIS SKIPS STRAIGHT TO JSONARRAY
							data = data.substring(127);
							JSONArray arr = new JSONArray(data);
							for (int i=0; i<arr.length(); i++) {
								Task3_Establishment restaurant = new Task3_Establishment();
								JSONObject businessObject = arr.getJSONObject(i);
								int id = businessObject.getInt("LocalAuthorityBusinessID");
								String BusinessName = businessObject.getString("BusinessName");
								String AddressLine1 = "";
								if (businessObject.has("AddressLine1")) {
									AddressLine1 = businessObject.getString("AddressLine1");
								}
								String AddressLine2 = "";
								if (businessObject.has("AddressLine2")) {
									AddressLine2 = businessObject.getString("AddressLine2");
								}
								String AddressLine3 = "";
								if (businessObject.has("AddressLine3")) {
									AddressLine3 = businessObject.getString("AddressLine3");
								}
								String AddressLine4 = "";
								if (businessObject.has("AddressLine4")) {
									AddressLine4 = businessObject.getString("AddressLine4");
								}
								String Postcode = "";
								if (businessObject.has("PostCode")) {
									Postcode = businessObject.getString("PostCode");
								}
								String RatingValue = businessObject.getString("RatingValue");
								String RatingDate = "";
								if (businessObject.has("RatingDate") && !businessObject.isNull("RatingDate")) {
									RatingDate = businessObject.getString("RatingDate");
								}
								String Longitude = "";
								String Latitude = "";
								if (businessObject.has("Geocode") && !businessObject.isNull("Geocode")) {
									JSONObject Geocode = businessObject.getJSONObject("Geocode");
									if (Geocode.has("Longitude")) {
										Longitude = Geocode.getString("Longitude");
									}
									if (Geocode.has("Latitude")) {
										Latitude = Geocode.getString("Latitude");
									}
								}
								
								restaurant.setId(id);
								restaurant.setBusinessName(BusinessName);
								restaurant.setAddressLine1(AddressLine1);
								restaurant.setAddressLine2(AddressLine2);
								restaurant.setAddressLine3(AddressLine3);
								restaurant.setAddressLine4(AddressLine4);
								restaurant.setPostcode(Postcode);
								restaurant.setRatingValue(RatingValue);
								restaurant.setRatingDate(RatingDate);
								restaurant.setLongitude(Longitude);
								restaurant.setLatitude(Latitude);
								listData.add(restaurant);
							}
						}
						catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
				}).start();
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			
			// MANUALLY CREATED DATABASE WITH RESTAURANT TABLE AND COLUMNS. COULD EXTEND THIS TO CREATE THE DB AND TABLE/COLUMNS BEFORE INSERTING THE DATA
			try (Connection c = DriverManager.getConnection("jdbc:sqlite:DBtest.db")) {
				Statement s = c.createStatement();
				ResultSet check = s.executeQuery("SELECT COUNT(*) AS total FROM Restaurant;");
				while(check.next()) {
					if (check.getInt("total") == 0) {
						for (int i=0; i<listData.size(); i++) {
							String insertSQL = "INSERT INTO Restaurant(id, BusinessName, AddressLine1, AddressLine2, AddressLine3, AddressLine4, Postcode, RatingValue, RatingDate, Latitude, Longitude) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
							PreparedStatement ps = c.prepareStatement(insertSQL);
							ps.setInt(1, listData.get(i).getId());
							ps.setString(2, listData.get(i).getBusinessName());
							ps.setString(3, listData.get(i).getAddressLine1());
							ps.setString(4, listData.get(i).getAddressLine2());
							ps.setString(5, listData.get(i).getAddressLine3());
							ps.setString(6, listData.get(i).getAddressLine4());
							ps.setString(7, listData.get(i).getPostcode());
							ps.setString(8, listData.get(i).getRatingValue());
							ps.setString(9, listData.get(i).getRatingDate());
							ps.setString(10, listData.get(i).getLatitude());
							ps.setString(11, listData.get(i).getLongitude());
							ps.executeUpdate();
						}
					}
				}
			}
			catch (SQLException se) {
				se.printStackTrace();
			}
		} else {
			
		}
	}
	
	public static ArrayList<Task3_Establishment> getResultsArray (String postcode) {
		ArrayList<Task3_Establishment> resultsArray = new ArrayList<>();
		try (Connection c = DriverManager.getConnection("jdbc:sqlite:DBtest.db")) {
			String selectSQL = "SELECT * FROM Restaurant WHERE PostCode LIKE ?;";
			// CURRENTLY RETURNING ALL RESULTS FROM QUERY. MAYBE CHANGE SQL TO ONLY SHOW FIRST 10 RESULTS?
			PreparedStatement ps2 = c.prepareStatement(selectSQL);
			ps2.setString(1, postcode + "%");
			ResultSet results = ps2.executeQuery();
			while(results.next()) {
				resultsArray.add(new Task3_Establishment(results.getInt("id"), results.getString("BusinessName"), results.getString("AddressLine1"), results.getString("AddressLine2"), results.getString("AddressLine3"), results.getString("AddressLine4"), results.getString("Postcode"), results.getString("RatingValue"), results.getString("RatingDate"), results.getString("Latitude"), results.getString("Longitude")));
			}
			
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		
		return resultsArray;
		
	}
	
	public static String resultsArrayToJSON(String postcode) {
		ArrayList<Task3_Establishment> resultsArray = new ArrayList<>();
		try (Connection c = DriverManager.getConnection("jdbc:sqlite:DBtest.db")) {
			String selectSQL = "SELECT * FROM Restaurant WHERE PostCode LIKE ?;";
			// CURRENTLY RETURNING ALL RESULTS FROM QUERY. MAYBE CHANGE SQL TO ONLY SHOW FIRST 10 RESULTS?
			PreparedStatement ps2 = c.prepareStatement(selectSQL);
			ps2.setString(1, postcode + "%");
			ResultSet results = ps2.executeQuery();
			while(results.next()) {
				resultsArray.add(new Task3_Establishment(results.getInt("id"), results.getString("BusinessName"), results.getString("AddressLine1"), results.getString("AddressLine2"), results.getString("AddressLine3"), results.getString("AddressLine4"), results.getString("Postcode"), results.getString("RatingValue"), results.getString("RatingDate"), results.getString("Latitude"), results.getString("Longitude")));
			}
			
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		
		JSONArray root = new JSONArray();
		for (int i=0; i<resultsArray.size(); i++) {
			JSONObject test = new JSONObject();
			root.put(test);
			test.put("id", resultsArray.get(i).getId());
			test.put("BusinessName", resultsArray.get(i).getBusinessName());
			test.put("AddressLine1", resultsArray.get(i).getAddressLine1());
			test.put("AddressLine2", resultsArray.get(i).getAddressLine2());
			test.put("AddressLine3", resultsArray.get(i).getAddressLine3());
			test.put("AddressLine4", resultsArray.get(i).getAddressLine4());
			test.put("PostCode", resultsArray.get(i).getPostcode());
			test.put("RatingValue", resultsArray.get(i).getRatingValue());
			test.put("RatingDate", resultsArray.get(i).getRatingDate());
			JSONObject location = new JSONObject();
			test.put("Location", location);
			location.put("Latitude", resultsArray.get(i).getLatitude());
			location.put("Longitude", resultsArray.get(i).getLongitude());
			
		}
		
		return root.toString(4);
		
	}
	
	public static Writer resultsArrayToXML(String postcode) {
		ArrayList<Task3_Establishment> resultsArray = new ArrayList<>();
		try (Connection c = DriverManager.getConnection("jdbc:sqlite:DBtest.db")) {
			String selectSQL = "SELECT * FROM Restaurant WHERE PostCode LIKE ?;";
			// CURRENTLY RETURNING ALL RESULTS FROM QUERY. MAYBE CHANGE SQL TO ONLY SHOW FIRST 10 RESULTS?
			PreparedStatement ps2 = c.prepareStatement(selectSQL);
			ps2.setString(1, postcode + "%");
			ResultSet results = ps2.executeQuery();
			while(results.next()) {
				resultsArray.add(new Task3_Establishment(results.getInt("id"), results.getString("BusinessName"), results.getString("AddressLine1"), results.getString("AddressLine2"), results.getString("AddressLine3"), results.getString("AddressLine4"), results.getString("Postcode"), results.getString("RatingValue"), results.getString("RatingDate"), results.getString("Latitude"), results.getString("Longitude")));
			}
			
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		
		try {
			DocumentBuilder db2 = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc2 = db2.newDocument();
			Element root = doc2.createElement("Establishments");
			doc2.appendChild(root);
			
			for (int i=0; i<resultsArray.size(); i++) {
				Element establishment = doc2.createElement("Establishment");
				root.appendChild(establishment);
				Element id = doc2.createElement("id");
				String idStr = String.valueOf(resultsArray.get(i).getId());
				establishment.appendChild(id).appendChild(doc2.createTextNode(idStr));
				Element businessName = doc2.createElement("BusinessName");
				establishment.appendChild(businessName).appendChild(doc2.createTextNode(resultsArray.get(i).getBusinessName()));
				Element address1 = doc2.createElement("AddressLine1");
				establishment.appendChild(address1).appendChild(doc2.createTextNode(resultsArray.get(i).getAddressLine1()));
				Element address2 = doc2.createElement("AddressLine2");
				establishment.appendChild(address2).appendChild(doc2.createTextNode(resultsArray.get(i).getAddressLine2()));
				Element address3 = doc2.createElement("AddressLine3");
				establishment.appendChild(address3).appendChild(doc2.createTextNode(resultsArray.get(i).getAddressLine3()));
				Element address4 = doc2.createElement("AddressLine4");
				establishment.appendChild(address4).appendChild(doc2.createTextNode(resultsArray.get(i).getAddressLine4()));
				Element postcodeXML = doc2.createElement("Postcode");
				establishment.appendChild(postcodeXML).appendChild(doc2.createTextNode(resultsArray.get(i).getPostcode()));
				Element ratingvalue = doc2.createElement("RatingValue");
				establishment.appendChild(ratingvalue).appendChild(doc2.createTextNode(resultsArray.get(i).getRatingValue()));
				Element ratingdate = doc2.createElement("RatingDate");
				establishment.appendChild(ratingdate).appendChild(doc2.createTextNode(resultsArray.get(i).getRatingDate()));
				Element locations = doc2.createElement("Location");
				establishment.appendChild(locations);
				Element latitudeXML = doc2.createElement("Latitude");
				locations.appendChild(latitudeXML).appendChild(doc2.createTextNode(resultsArray.get(i).getLatitude()));
				Element longitudeXML = doc2.createElement("Longitude");
				locations.appendChild(longitudeXML).appendChild(doc2.createTextNode(resultsArray.get(i).getLongitude()));
			
			}
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Writer output = new StringWriter();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc2), new StreamResult(output));
			
			return output;
		}
		catch (ParserConfigurationException | TransformerException pce) {
			pce.printStackTrace();
		}

		return null;
	}
	
	
	public static void GenerateHygieneRatingUI() {
		
		JFrame window = new JFrame("Find Local Restaurant's Hygiene Rating!");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel JPnorth = new JPanel();
		JTextField postcodeField = new JTextField("", 10);
		JButton searchBtn = new JButton("Search");
		JButton clearBtn = new JButton("Clear");
		
		JPanel JPcenter = new JPanel();
		JTextArea resultsBox = new JTextArea(43, 32);
		resultsBox.append("Enter a postcode to see your local restaurant's hygiene rating!");
	
		Container c = window.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(JPnorth, BorderLayout.NORTH);	
		c.add(JPcenter, BorderLayout.CENTER);
		JPnorth.setLayout(new FlowLayout());
		JPcenter.setLayout(new FlowLayout());
		JPnorth.add(postcodeField);
		JPnorth.add(searchBtn);
		JPnorth.add(clearBtn);

		JPanel JPcenterRes = new JPanel();
		JPcenter.add(JPcenterRes);
		
		JScrollPane scroll = new JScrollPane(resultsBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPcenterRes.add(scroll);
		
		searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new Thread(new Runnable() {
						public void run() {
							resultsBox.setText("");
							String userInput = postcodeField.getText();
							if (userInput.length() < 3) {
								resultsBox.append("Searches require at least three characters as a search term");
							} else {
								ArrayList<Task3_Establishment> results = getResultsArray(userInput);
								if (results.size() == 0) {
									resultsBox.append("No results");
								} else {
									for (int j=0; j<results.size(); j++) {
										resultsBox.append(results.get(j).toString());
									}
								}
							}
							JPcenterRes.repaint();
							c.revalidate();
						}
					}).start();
				}
				catch (JSONException jse) {
					jse.printStackTrace();
				}
			}
		});

		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultsBox.setText("Enter a postcode to see your local restaurant's hygiene rating!");
				JPcenterRes.repaint();
				c.invalidate();
			}
		});
		
		window.setSize(420,800);
		window.setVisible(true);
	}

}