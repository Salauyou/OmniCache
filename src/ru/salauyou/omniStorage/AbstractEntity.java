package ru.salauyou.omniStorage;

import java.util.Objects;

public abstract class AbstractEntity implements Entity {
	
	protected String id = null;
	
	protected void setId(String id) {
		this.id = id;
	}
	
	
	@Override
	abstract public String getType();

	
	@Override
	final public String getId() {
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
			
		Entity e = (Entity) o;
		if (e.getType() == null || e.getType().equals("") || getType() == null || getType().equals("") 
		   || e.getId() == null || e.getId().equals("")   || getId() == null   || id.equals(""))
			return false;
			
		return getType().equals(e.getType()) && getId().equals(e.getId());
	}

}