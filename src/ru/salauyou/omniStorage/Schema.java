package ru.salauyou.omniStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class Schema {

	
	public static enum Nullable {
		YES, NO;
	}
	
	
	public static enum ElementKind {
		SCALAR, ENTITY, LIST;
	}
	
	
	// no instantiation
	private Schema(){};
	
	
	
	
	
	
	static final protected class SchemaType {
		
		protected final Map<String, SchemaElement> elementMap;
		protected final List<SchemaElement> elements;
		protected final String type;
		
		protected SchemaType(String type, List<SchemaElement> elements) {
			this.type = type;
			this.elements = Collections.unmodifiableList(new ArrayList<SchemaElement>(elements));
			Map<String, SchemaElement> m = new HashMap<String, SchemaElement>();
			for (SchemaElement e : elements) {
				m.put(e.name, e);
			}
			elementMap = Collections.unmodifiableMap(m);
		}
		
		
		public List<SchemaElement> getElements() {
			return this.elements;
		}
		

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\nEntity{").append(type).append("}:");
			for (SchemaElement e : elements) {
				sb.append("\n     ").append(e.toString());
			}
			return sb.toString();
		}
	}
	
	
}
