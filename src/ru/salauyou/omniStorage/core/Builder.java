package ru.salauyou.omnistorage.core;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;










import ru.salauyou.omnistorage.core.classes.EntityAdapter;
import ru.salauyou.omnistorage.core.classes.Schema;
import ru.salauyou.omnistorage.core.classes.Schema.ElementKind;
import ru.salauyou.omnistorage.core.classes.Schema.Nullable;
import ru.salauyou.omnistorage.core.classes.Schema.SchemaElement;
import ru.salauyou.omnistorage.core.classes.Schema.SchemaType;


public class Builder {
	
	private List<SchemaType> schema = new ArrayList<>();
	private Set<String> schemaTypes = new HashSet<>();
	private List<SchemaElement> currentTypeList = new ArrayList<>();
	private Set<String> currentNames = new HashSet<>();
	private String currentType = null;
	@SuppressWarnings("rawtypes")
	private EntityAdapter currentAdapter = null;
	@SuppressWarnings("rawtypes")
	private Map<String, EntityAdapter> adapters = new HashMap<>();
	private int currentIndex = 0;
	private Class<?> currentIdClass = null;
	private boolean isBuilt = false;
	
	// no instatiation outside the package
	protected Builder(){};
	
	
	public Builder addType(String type, Class<?> idClass) throws IllegalStateException, IllegalArgumentException {
		if (currentType != null) {
			if (currentTypeList.size() == 0)
				throw new IllegalStateException("Type must contain at least one element");
			if (currentAdapter == null)
				throw new IllegalStateException("Must define Entity adapter to add type to schema");
			
			schema.add(new SchemaType(currentType, currentIdClass, currentTypeList));
			adapters.put(currentType, currentAdapter);
			schemaTypes.add(currentType);
			currentType = null;
			currentAdapter = null;
			currentIndex = 0;
			currentIdClass = null;
		}
		if (type == null || type.isEmpty())
			throw new IllegalArgumentException("Type cannot be null neither empty");
		if (idClass == null)
			throw new IllegalArgumentException("Id class cannot be null");
		if (!Schema.ALLOWED_SCALAR_CLASSES.contains(idClass)) 
			throw new IllegalArgumentException(String.format(
					"Class %s provided for Entity{%s}.id is not allowed scalar type", idClass.getSimpleName(), type
					));
		if (schemaTypes.contains(type))
			throw new IllegalArgumentException(String.format("Entity{%s} is already defined in schema", type));

		currentIdClass = idClass;
		currentType = type;
		currentTypeList.clear();
		currentNames.clear();
		currentIndex = 0;
		return this;
	}
	
	
	
	public Builder addScalar(String name, Class<?> clazz) 
							 throws IllegalStateException, IllegalArgumentException {
		addElement(ElementKind.SCALAR, name, clazz, null, Nullable.YES);
		return this;
	}
	
	
	public Builder addScalar(String name, Class<?> clazz, Nullable nullable) 
						 throws IllegalStateException, IllegalArgumentException{
		addElement(ElementKind.SCALAR, name, clazz, null, nullable);
		return this;
	}
	
	
	public Builder addEntity(String name, String type) 
							 throws IllegalStateException, IllegalArgumentException{
		addElement(ElementKind.ENTITY, name, null, type, Nullable.YES);
		return this;
	}
	
	
	public Builder addEntity(String name, String type, Nullable nullable) 
							 throws IllegalStateException, IllegalArgumentException{
		addElement(ElementKind.ENTITY, name, null, type, nullable);
		return this;
	}
	
	
	
	private void addElement(ElementKind kind, String name, Class<?> clazz, String type, Nullable nullable) 
							throws IllegalStateException, IllegalArgumentException {
		
		if (currentType == null)
			throw new IllegalStateException("Entity type must be defined first");
		if (name == null || name.isEmpty()) 
			throw new IllegalArgumentException("Element name cannot be null neither empty");
		if (currentNames.contains(name))
			throw new IllegalArgumentException(String.format("Element {%s}.%s is already defined", currentType, name));
		
		switch (kind) {
		case SCALAR:
			if (clazz == null)
				throw new IllegalArgumentException("Element class cannot be null");
			if (!Schema.ALLOWED_SCALAR_CLASSES.contains(clazz)) 
				throw new IllegalArgumentException(String.format(
						"Class %s provided for {%s}.%s is not allowed as a scalar element type", clazz.getSimpleName(), currentType, name
						));
			break;
		
		case ENTITY:
			if (type == null || type.isEmpty())
				throw new IllegalArgumentException("Element type cannot be null neither empty");
			break;
		
		case LIST:
			throw new UnsupportedOperationException("List types are not currently supported");
		}
		
		currentTypeList.add(new SchemaElement(kind, name, clazz, type, "", nullable, currentIndex++));
		currentNames.add(name);
	}
	
	
	
	public <T> Builder defineAdapter(EntityAdapter<T> adapter) throws IllegalStateException {
		if (adapter == null)
			throw new IllegalStateException("Adapter cannot be null");
		currentAdapter = adapter;
		return this;	
		// TODO: implement check of adapter correctness
	}
	
	
	
	public OmniStorage build() throws IllegalStateException, IllegalArgumentException {
		if (isBuilt) 
			throw new IllegalStateException("Already built");
		if (currentType != null) {
			if (currentTypeList.size() == 0)
				throw new IllegalStateException("Type must contain at least one element");
			if (currentAdapter == null)
				throw new IllegalStateException("Must define Entity Adapter to add type to schema");
			schema.add(new SchemaType(currentType, currentIdClass, currentTypeList));
			adapters.put(currentType, currentAdapter);
		}
		try {
			validateEntityTypes();
		} catch (RuntimeException e) {
			schema.remove(schema.size() - 1);
			adapters.remove(currentType);
			throw e;
		}
		isBuilt = true;
		return new OmniStorage(schema, adapters);
	}
	
	
	
	/** 
	 * Checks if all entity types are defined in schema 
	 **/
	private void validateEntityTypes() throws RuntimeException {
		Set<String> types = schema.stream().map((st) -> st.type).collect(toSet());
		schema.stream().forEach((st) -> {
			for (SchemaElement e : st.elements) {
				if (e.kind == ElementKind.ENTITY && !types.contains(e.type)) {
					throw new IllegalStateException(
							String.format("Type {%s} is not defined in schema", e.type)
							);
				}
			}
		});
	}
	
	
}
