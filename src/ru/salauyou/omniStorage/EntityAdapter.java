package ru.salauyou.omniStorage;

public interface EntityAdapter {
	
	public Entity create(String type, String id);
	
	public void toBundle(Entity e, Bundle b);
	
	public void fromBundle(Entity e, Bundle b);
	
}
