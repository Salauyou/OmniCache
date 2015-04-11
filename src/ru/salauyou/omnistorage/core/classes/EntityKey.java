package ru.salauyou.omnistorage.core.classes;

import java.util.Objects;


final public class EntityKey {
	
	public final String type;
	protected final Object id;
	
	public EntityKey(String type, Object id) {
		this.type = type;
		this.id = id;
	}
	
	@Override final public int hashCode() {
		return Objects.hashCode(id);
	}
	
	@Override final public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || o instanceof EntityKey == false) return false;
		EntityKey k = (EntityKey) o;
		return (k.id != null && id != null && k.type != null && type != null 
				&& k.id.equals(id) && k.type.equals(type));
	}
}
