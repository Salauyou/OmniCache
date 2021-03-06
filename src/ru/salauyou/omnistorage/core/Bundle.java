package ru.salauyou.omnistorage.core;

import ru.salauyou.omnistorage.core.classes.Entity;
import ru.salauyou.omnistorage.core.classes.EntityKey;
import ru.salauyou.omnistorage.core.classes.Schema.Nullable;
import ru.salauyou.omnistorage.core.classes.Schema.SchemaElement;
import ru.salauyou.omnistorage.core.classes.Schema.SchemaType;




public final class Bundle {

	final Object[] elements;
	final EntityKey entityKey;
	public final SchemaType schemaType;
	

		
	public Bundle(String type, Object id, SchemaType schemaType) {
		this.entityKey = new EntityKey(type, id);
		this.schemaType = schemaType;
		this.elements = new Object[schemaType.elements.size()];
	}
	
	
	
	public Bundle set(String name, Object v) {
		
		String type = entityKey.type;
		if (!schemaType.elementMap.containsKey(name))
			throw new IllegalArgumentException(String.format("Element {%s}.%s are not defined in schema", entityKey.type, name));
		
		SchemaElement se = schemaType.elementMap.get(name);
		if (elements[se.index] != null)
			throw new IllegalStateException(String.format("Value of element {%s}.%s is already set", entityKey.type, name));
	
		if (se.nullable == Nullable.NO && v == null)
			throw new IllegalArgumentException(String.format("Element {%s}.%s cannot be null", type, se.name));
		
		if (v != null) {
			switch (se.kind) {
			case SCALAR:
				if (se.clazz.isAssignableFrom(v.getClass()) == false)
					throw new IllegalArgumentException(String.format(
							"Element {%s}.%s must be of class %s", type, se.name, se.clazz.getSimpleName())
							);
				break;

			case ENTITY:
				if (v instanceof Entity == false)
					throw new IllegalArgumentException(String.format(
							"Element {%s}.%s must be Entity{%s}, not %s", type, se.name, se.type, v.getClass().getSimpleName())
							);
				@SuppressWarnings("unchecked")
				Entity<Object> e = (Entity<Object>) v;
				if (se.type.equals(e.getType()) == false)
					throw new IllegalArgumentException(String.format(
							"Element {%s}.%s must be of type {%s}, not {%s}", type, se.name, se.type, e.getType())
							);
				if (e.getId() == null || e.getId().equals(""))
					throw new IllegalArgumentException("Entity id cannot be null neither empty");
				break;
					
			case LIST:
				throw new UnsupportedOperationException("List in entities aren't supported yet");
			}
			
			elements[se.index] = v;
		}
		
		return this;
	}
	
	
	
	public Object get(String name) {
		if (!schemaType.elementMap.containsKey(name))
			throw new IllegalArgumentException(String.format("Element {%s}.%s are not defined in schema", entityKey.type, name));
		return elements[schemaType.elementMap.get(name).index];
	}
	
	

	@Override
	public int hashCode() {
		return entityKey.hashCode();
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Bundle)) return false;
		return ((Bundle) o).entityKey.equals(this.entityKey);
	}
}
