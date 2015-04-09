package ru.salauyou.omnistorage.core.classes;

import java.util.Objects;

public abstract class AbstractEntity<T> implements Entity<T> {
	
	protected T id = null;
	
	protected void setId(T id) {
		this.id = id;
	}
	
	
	@Override
	abstract public String getType();

	
	@Override
	final public T getId() {
		return id;
	}
	
	
	@Override
	public String toString() {
		return "@Entity{" + getType() + "}[" + Objects.toString(getId()) + "]";
	}
	

	@Override
	final public int hashCode() {
		return Objects.hashCode(getId());
	}
		
			
	@Override
	final public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || o instanceof Entity == false) return false;
			
		@SuppressWarnings("unchecked")
		Entity<T> e = (Entity<T>) o;
		if (e.getType() == null || e.getType().equals("") || getType() == null || getType().equals("") 
		   || e.getId() == null || getId() == null || id.equals(""))
			return false;
			
		return getType().equals(e.getType()) && getId().equals(e.getId());
	}

}
