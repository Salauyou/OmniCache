package model;

import ru.salauyou.omniStorage.Bundle;
import ru.salauyou.omniStorage.Entity;
import ru.salauyou.omniStorage.EntityAdapter;

public class City implements Entity {

	public static final String TYPE = "City";

	private String id;
	private String name;
	private Double lat;
	private Double lon;

	
	private City(String id) {
		this.id = id;
	}
	
	
	public City(String id, String name, Double latitude, Double longitude) {
		this.id = id;
		this.name = name;
		this.lat = latitude;
		this.lon = longitude;
	}
	
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	
	@Override 
	public String getId() {
		return id;
	}
	
	
	@Override
	public String toString() {
		return "City[" + getId() + "]: " + name + ", lat=" + lat + ", lon=" + lon;
	}


	private String getName() {
		return name;
	}


	private void setName(String name) {
		this.name = name;
	} 


	private Double getLat() {
		return lat;
	}


	private void setLat(Double lat) {
		this.lat = lat;
	}


	private Double getLon() {
		return lon;
	}


	private void setLon(Double lon) {
		this.lon = lon;
	}
	
	
	
	/**
	 *  Adapter
	 */
	
	static public EntityAdapter adapter = new EntityAdapter(){

		@Override
		public Entity create(String type, String id) {
			return new City(id);
		}
		
		@Override
		public void toBundle(Entity e, Bundle b) {
			City c = (City) e;
			b.set("name", c.name);
			b.set("lat", c.lat);
			b.set("lon", c.lon);
		}

		@Override
		public void fromBundle(Entity e, Bundle b) {
			City c = (City) e;
			c.name = (String) b.get("name");
			c.lat = (Double) b.get("lat");
			c.lon = (Double) b.get("lon");
		}
	};
	

}
