package ru.salauyou.omnistorage.core;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.salauyou.omnistorage.core.classes.Entity;
import ru.salauyou.omnistorage.core.classes.EntityAdapter;
import ru.salauyou.omnistorage.core.classes.Schema.SchemaElement;



public class ReflectionEntityAdapter<T> implements EntityAdapter<T> {

	private final Map<String, Method> getters = new HashMap<>();
	private final Map<String, Method> setters = new HashMap<>();
	
	private Constructor<? extends Entity<T>> emptyConstructor = null;
	private Constructor<? extends Entity<T>> idConstructor = null;
	
	private final static Pattern setterPattern = Pattern.compile("^set([A-Z][_A-Za-z0-9]*)$");
	private final static Pattern getterPattern = Pattern.compile("^(?:get|is)([A-Z][_A-Za-z0-9]*)$");
	
	
	
	/**
	 * Reflection adapter that uses no-arg constructor and setId() method to create 
	 * an entity instance with given id
	 * 
	 * @param clazz		entity class
	 */
	public ReflectionEntityAdapter(Class<? extends Entity<T>> clazz) {
		this(clazz, null);
	}

	
	
	/**
	 * Reflection adapter that uses 1-argument constructor to create an entity instance
	 * with given id
	 * 
	 * @param clazz		entity class
	 * @param idClass	class of entity id
	 */
	public ReflectionEntityAdapter(Class<? extends Entity<T>> clazz, Class<T> idClass) {
		
		Matcher matcher;
		for (Method m : clazz.getDeclaredMethods()) {
			matcher = setterPattern.matcher(m.getName());
			if (matcher.matches() && m.getParameterCount() == 1) {
				setters.put(lowerFirstChar(matcher.group(1)), m);
			}
			matcher = getterPattern.matcher(m.getName());
			if (matcher.matches() && m.getParameterCount() == 0) {
				getters.put(lowerFirstChar(matcher.group(1)), m);
			}
		}
		
		// try to resolve constructor with id parameter
		if (idClass != null) {
			try {
				idConstructor = clazz.getDeclaredConstructor(idClass);
				idConstructor.setAccessible(true);
			} catch (NoSuchMethodException e) { 
				idConstructor = null;
			}
		}
				
		// try to resolve default empty constructor
		if (idConstructor == null) {
			try {
				emptyConstructor = clazz.getDeclaredConstructor();
				try {
					emptyConstructor.setAccessible(true);
					emptyConstructor.newInstance();
				} catch (ReflectiveOperationException e1) {
					throw e1;
				}
			} catch (ReflectiveOperationException ex) {
				emptyConstructor = null;
			}
		}
		
		if (idConstructor == null && emptyConstructor == null)
			throw new IllegalStateException(String.format(
					"Cannot find suitable constructor for class %s", clazz.getSimpleName()
					));
	}
	
	
	
	private String lowerFirstChar(String input) {
		return input.substring(0, 1).toLowerCase(Locale.ROOT) + input.substring(1);
	}
	

	
	@Override
	public Entity<T> create(String type, T id) {
		Entity<T> e = null;
		
		if (idConstructor != null) {
			try {
				e = idConstructor.newInstance(id);
			} catch (ReflectiveOperationException ex) { 
				e = null; 
			}
		} else {
			try {
				e = emptyConstructor.newInstance();
				setters.get("id").setAccessible(true);
				setters.get("id").invoke(id);
				setters.get("id").setAccessible(false);
			} catch (ReflectiveOperationException ex) {
				e = null;
			}
		}
		if (e == null)
			throw new IllegalStateException(String.format(
					"Failed to create Entity{%s}[%s]", type, id
					));
		return e;
	}

	
	
	@Override
	public void toBundle(Entity<T> e, Bundle b) {
		for (SchemaElement se : b.schemaType.elements) {
			if (getters.containsKey(se.name)) {
				Method getter = getters.get(se.name);
				try {
					getter.setAccessible(true);
					b.set(se.name, getter.invoke(e));
					getter.setAccessible(false);
				} catch (ReflectiveOperationException e1) {
					throw new IllegalStateException(String.format(
							"Failed to get {%s}.%s element by reflection", b.schemaType.type, se.name
							));
				}
			}
		}
	}

	
	
	@Override
	public void fromBundle(Entity<T> e, Bundle b) {
		for (SchemaElement se : b.schemaType.elements) {
			if (setters.containsKey(se.name)) {
				Method setter = setters.get(se.name);
				try {
					setter.setAccessible(true);
					setter.invoke(e, b.get(se.name));
					setter.setAccessible(false);
				} catch (ReflectiveOperationException e1) {
					throw new IllegalStateException(String.format(
							"Failed to set {%s}.%s element by reflection", b.schemaType.type, se.name
							));
				}
			}
		}
	}

}
