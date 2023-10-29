package model;

public class EdgeUser {
	private Location location = new Location();
	private int clusterID = 0;
	
	public EdgeUser() {
		
	}
	public EdgeUser(EdgeUser u) {
		this.location = u.location;
	}
	
	public EdgeUser(Location location) {
		this.location = location;
	}
	
	public EdgeUser(double lat, double lng) {
		this.location.setLat(lat);
		this.location.setLng(lng);
	}
	
	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the cluster ID
	 */
	public int getClusterID() {
		return clusterID;
	}

	/**
	 * @param clusterID
	 */
	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}
	

}
