package ru.salauyou.omniStorage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.salauyou.omniStorage.SchemaElement;

public class ReflectionEntityAdapter implements EntityAdapter {

	private final Map<String, Method> getters = new HashMap<>();
	private final Map<String, Method> setters = new HashMap<>();
	
	private Constructor<? extends Entity> emptyConstructor = null;
	private Constructor<? extends Entity> stringConstructor = null;
	
	private final static Pattern setterPattern 
							= Pattern.compile("^set([A-Z][_A-Za-z0-9]+)$");
	private final static Pattern getterPattern 
							= Pattern.compile("^get([A-Z][_A-Za-z0-9]+)$");
	
	
	
	public ReflectionEntityAdapter(Class<? extends Entity> clazz) {
		
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
		
		// try to resolve constructor with String parameter
		try {
			stringConstructor = clazz.getDeclaredConstructor(String.class);
			try {
				stringConstructor.setAccessible(true);
				stringConstructor.newInstance("test");
				return;
			} catch (Exception e1) {
				throw e1;
			}
		} catch (Exception ex) { 
			stringConstructor = null;
		}
				
		// try to resolve default empty constructor
		if (stringConstructor == null) {
			try {
				emptyConstructor = clazz.getDeclaredConstructor();
				try {
					emptyConstructor.setAccessible(true);
					emptyConstructor.newInstance();
				} catch (Exception e1) {
					throw e1;
				}
			} catch (Exception ex) {
				emptyConstructor = null;
			}
		}
		
		if (stringConstructor == null && emptyConstructor == null)
			throw new IllegalStateException(String.format(
					"Cannot find suitable constructor for class %s", clazz.getSimpleName()
					));
		
	}
	
	
	
	private String lowerFirstChar(String input) {
		StringBuilder sb = new StringBuilder(input);
		char c = String.valueOf(sb.charAt(0)).toLowerCase(Locale.ROOT).toCharArray()[0];
		sb.setCharAt(0, c);
		return sb.toString();
	}
	
	
	
	@Override
	public Entity create(String type, String id) {
		Entity e = null;
		
		if (stringConstructor != null) {
			try {
				e = stringConstructor.newInstance(id);
			} catch (Exception ex) { 
				e = null; 
			}
		} else {
			try {
				e = emptyConstructor.newInstance();
				setters.get("id").setAccessible(true);
				setters.get("id").invoke(id);
				setters.get("id").setAccessible(false);
			} catch (Exception ex) {
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
	public void toBundle(Entity e, Bundle b) {
		for (SchemaElement se : b.schemaType.elements) {
			if (getters.containsKey(se.name)) {
				Method getter = getters.get(se.name);
				try {
					getter.setAccessible(true);
					b.set(se.name, getter.invoke(e));
					getter.setAccessible(false);
				} catch (Exception e1) {
					throw new IllegalStateException(String.format(
							"Failed to get {%s}.%s element by reflection", b.schemaType.type, se.name
							));
				}
			}
		}
	}

	
	
	@Override
	public void fromBundle(Entity e, Bundle b) {
		for (SchemaElement se : b.schemaType.elements) {
			if (setters.containsKey(se.name)) {
				Method setter = setters.get(se.name);
				try {
					setter.setAccessible(true);
					setter.invoke(e, b.get(se.name));
					setter.setAccessible(false);
				} catch (Exception e1) {
					throw new IllegalStateException(String.format(
							"Failed to set {%s}.%s element by reflection", b.schemaType.type, se.name
							));
				}
			}
		}
	}

}
