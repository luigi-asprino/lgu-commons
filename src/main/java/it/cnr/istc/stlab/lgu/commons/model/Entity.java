package it.cnr.istc.stlab.lgu.commons.model;

public class Entity {

	private String URI;
	private String latitude, longitude;

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public boolean isGeoLocated() {
		return latitude != null & longitude != null;
	}

	@Override
	public String toString() {
		return "Entity [URI=" + URI + ", latitude=" + latitude + ", longitude=" + longitude + "]";
	}

}
