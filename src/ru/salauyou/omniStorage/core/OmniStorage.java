package ru.salauyou.omnistorage.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.salauyou.omnistorage.core.classes.Entity;
import ru.salauyou.omnistorage.core.classes.EntityAdapter;
import ru.salauyou.omnistorage.core.classes.EntityKey;
import ru.salauyou.omnistorage.core.classes.Schema.SchemaElement;
import ru.salauyou.omnistorage.core.classes.Schema.SchemaType;


public final class OmniStorage {
	
	private final Map<String, SchemaType> schema;
	private final List<SchemaType> schemaTypes;
	private final Map<String, EntityAdapter> adapters;
	private final Map<String, Map<Object, Tuple>> storage = new HashMap<>();
	


	// no instantiation outside the package
	protected OmniStorage(List<SchemaType> schema, Map<String, EntityAdapter> adapters) {
		Map<String, SchemaType> m = new HashMap<>();
		for (SchemaType t : schema) {
			storage.put(t.type, new HashMap<>());
			m.put(t.type, t);
		}
		this.schemaTypes = Collections.unmodifiableList(new ArrayList<>(schema));
		this.schema = Collections.unmodifiableMap(m);
		this.adapters = Collections.unmodifiableMap(new HashMap<>(adapters));
	}
		
	
	
	/**
	 * Returns a description of storage schema
	 */
	public String printSchema() {
		StringBuilder sb = new StringBuilder();
		for (SchemaType e : schemaTypes) {
			sb.append(e.toString());
		}
		return sb.toString();
	}
	
	
	
	/**
	 * Saves a given entity in the storage
	 */
	public OmniStorage save(Entity e) throws IllegalArgumentException {
		
		String type = e.getType();
		validateType(type);
		SchemaType t = schema.get(type);
		
		Object id = e.getId();
		validateId(t, id);
		
		if (storage.get(type).containsKey(id))
			throw new IllegalStateException(String.format("Storage already contains Entity{%s}[%s]", type, id));
		
		Bundle b = new Bundle(type, id, t);
		adapters.get(type).toBundle(e, b);
		storage.get(type).put(id, Tuple.fromBundle(b));
		return this;
	}
	
	
	
	/**
	 * Returns an entity of given type with given id,
	 * or null if such entity cannot be found in the storage 
	 */
	public Entity getById(String type, Object id) {
		
		validateType(type);
		SchemaType t = schema.get(type);
		validateId(t, id);
	
		if (storage.get(type).containsKey(id) == false)
			return null;
		
		Entity e = adapters.get(type).create(type, id);
		validateNewEntity(e, type, id);
		Map<EntityKey, Entity> c = new HashMap<>();
		c.put(new EntityKey(type, id), e);
		resolve(type, e, c);
		return e;
	}
	
	
	
	public Tuple getTuple(String type, Object id) {
		validateType(type);
		SchemaType t = schema.get(type);
		validateId(t, id);
		return storage.get(type).get(id);
	}
	
	
	
	private void resolve(String type, Entity e, Map<EntityKey, Entity> c) {
	
		Tuple t = storage.get(type).get(e.getId());
		if (t == null) 
			return;
		
		SchemaType st = schema.get(type);
		Bundle b = new Bundle(type, e.getId(), st);
		
		for (SchemaElement se : st.elements) {
			switch (se.kind) {
			case ENTITY:
				String eType = se.type;
				Object eId = t.getByIndex(se.index);
				if (eId == null) {
					b.elements[se.index] = null;
				} else {
					EntityKey k = new EntityKey(eType, eId);
					if (c.containsKey(k)) {
						b.elements[se.index] = c.get(k);
					} else {
						Entity entity = adapters.get(eType).create(eType, eId);
						validateNewEntity(entity, eType, eId);
						b.elements[se.index] = entity;
						c.put(k, entity);
						resolve(eType, entity, c);
					} 
				}
				break;
					
			case SCALAR:
				b.elements[se.index] = t.getByIndex(se.index);
				break;
					
			case LIST:
				throw new UnsupportedOperationException("Lists in entities aren't supported yet");
			}
		}
		adapters.get(type).fromBundle(e, b);
	}
	
	

	static protected void validateId(SchemaType type, Object id) {
		if (type != null && !id.getClass().equals(type.idClass)) 
			throw new IllegalArgumentException(String.format(
					"Entity{%s}.id must be of class %s", type.type, type.idClass.getSimpleName()
					));
		if (id == null || id.equals(""))
			throw new IllegalArgumentException("Id cannot be null neither empty");
	}
	
	
	
	private void validateType(String type) {
		if (type == null || type.equals(""))
			throw new IllegalArgumentException("Type cannot be null neither empty");
		if (schema.containsKey(type) == false)
			throw new IllegalArgumentException(String.format("Entity{%s} is not defined in schema", type));
	}
	
	
	
	static protected void validateNewEntity(Entity e, String type, Object id) {
		if (e == null) 
			throw new IllegalStateException(String.format(
					"Adapter returned null for Entity{%s}", type
					));
		if (e.getType().equals(type) == false)
			throw new IllegalStateException(String.format(
					"Adapter returned wrong type {%s} for Entity{%s}", e.getType(), type
					));
		if (e.getId().equals(id) == false)
			throw new IllegalStateException(String.format(
					"Adapter returned wrong id '%s' for Entity{%s}[%s]", e.getId(), type, id
					));
	}
	
	

	public OmniStorage clean() {
		for (SchemaType st : schemaTypes) {
			storage.get(st.type).clear();
		}
		return this;
	}
	
	
	
	public static Builder builder() {
		return new Builder();
	}
	
	
	public List<SchemaType> getSchema() {
		return schemaTypes;
	}
	
}
