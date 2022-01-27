package ap_Lab12;

public class Task3_Establishment {
	
	int id;
	String BusinessName;
	String AddressLine1;
	String AddressLine2;
	String AddressLine3;
	String AddressLine4;
	String Postcode;
	String RatingValue;
	String RatingDate;
	String Latitude;
	String Longitude;
	
	public Task3_Establishment() {	
	}
	
	public Task3_Establishment(int id, String BusinessName, String AddressLine1, String AddressLine2, String AddressLine3, String AddressLine4, String Postcode, String RatingValue, String RatingDate, String Latitude, String Longitude) {
		this.id = id;
		this.BusinessName = BusinessName;
		this.AddressLine1 = AddressLine1;
		this.AddressLine2 = AddressLine2;
		this.AddressLine3 = AddressLine3;
		this.AddressLine4 = AddressLine4;
		this.Postcode = Postcode;
		this.RatingValue = RatingValue;
		this.RatingDate = RatingDate;
		this.Latitude = Latitude;
		this.Longitude = Longitude;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBusinessName() {
		return BusinessName;
	}
	public void setBusinessName(String businessName) {
		BusinessName = businessName;
	}
	public String getAddressLine1() {
		return AddressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		AddressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return AddressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		AddressLine2 = addressLine2;
	}
	public String getAddressLine3() {
		return AddressLine3;
	}
	public void setAddressLine3(String addressLine3) {
		AddressLine3 = addressLine3;
	}
	public String getAddressLine4() {
		return AddressLine4;
	}
	public void setAddressLine4(String addressLine4) {
		AddressLine4 = addressLine4;
	}
	public String getPostcode() {
		return Postcode;
	}
	public void setPostcode(String postcode) {
		Postcode = postcode;
	}
	public String getRatingValue() {
		return RatingValue;
	}
	public void setRatingValue(String ratingValue) {
		RatingValue = ratingValue;
	}
	public String getRatingDate() {
		return RatingDate;
	}
	public void setRatingDate(String ratingDate) {
		RatingDate = ratingDate;
	}
	public String getLongitude() {
		return Longitude;
	}
	public void setLongitude(String longitude) {
		Longitude = longitude;
	}
	public String getLatitude() {
		return Latitude;
	}
	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

	@Override
	public String toString() {
		return "ID: \"" + id + "\",\nBusinessName: \"" + BusinessName + "\",\nAddressLine1: \"" + AddressLine1
				+ "\",\nAddressLine2: \"" + AddressLine2 + "\",\nAddressLine3: \"" + AddressLine3 + "\",\nAddressLine4: \"" + AddressLine4 + "\",\nPostcode: \"" + Postcode
				+ "\",\nRatingValue: \"" + RatingValue + "\",\nRatingDate: \"" + RatingDate + "\",\nLongitude: \"" + Longitude
				+ "\",\nLatitude: \"" + Latitude + "\"\n\n";
	}

}
