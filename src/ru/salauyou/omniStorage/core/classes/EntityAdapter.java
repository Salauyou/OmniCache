package ru.salauyou.omnistorage.core.classes;

import ru.salauyou.omnistorage.core.Bundle;


public interface EntityAdapter<T> {
	
	
	/**
	 * Callback method to create new (empty) entity instance of given type and with given id.
	 * Though one adapter can serve multiple entity types, type of entity is also passed
	 */
	public Entity<T> create(String type, T id);
	
	
	/**
	 * Callback method to fill provided empty bundle with data from provided entity
	 */
	public void toBundle(Entity<T> e, Bundle b);
	
	
	/**
	 * Callback method to fill provided empty entity with data from provided bundle
	 */
	public void fromBundle(Entity<T> e, Bundle b);
	
}
