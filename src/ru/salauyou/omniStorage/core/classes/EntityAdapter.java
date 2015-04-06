package ru.salauyou.omnistorage.core.classes;

import ru.salauyou.omnistorage.core.Bundle;


public interface EntityAdapter {
	
	
	/**
	 * Callback method to create new (empty) entity instance of given type and with given id.
	 * Though one adapter can serve multiple entity types, type of entity is also passed
	 */
	public Entity create(String type, Object id);
	
	
	/**
	 * Callback method to fill provided empty bundle with data from provided entity
	 */
	public void toBundle(Entity e, Bundle b);
	
	
	/**
	 * Callback method to fill provided empty entity with data from provided bundle
	 */
	public void fromBundle(Entity e, Bundle b);
	
}
