package model;

/**
 * @author gcui
 *
 */
public class BaseStation {
	private int ID = 0;
	private Location location = new Location();
	private double radius = 0;
	private int clusterID = 0;
	
	public BaseStation(){
		
	}
	
	public BaseStation(BaseStation s) {		
		this.ID = s.getID();
		this.location = s.getLocation();
		this.radius = s.getRadius();
		this.clusterID = s.getClusterID();
	}
	
	public BaseStation(Location location, double radius, int clusterID) {
		this.location = location;
		this.radius = radius;
		this.clusterID = clusterID;
	}
	
	
	/**
	 * @return the BaseStation ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param ID
	 */
	public void setID(int ID) {
		this.ID = ID;
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
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
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
