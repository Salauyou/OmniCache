package ru.salauyou.omniStorage;

import ru.salauyou.omniStorage.Schema.Nullable;
import ru.salauyou.omniStorage.Schema.SchemaType;

final class Tuple {
	
	final protected Object[] elements;

	
	// instantiation inside the class only
	private Tuple(SchemaType st) {
		elements = new Object[st.elements.size()];
	}
	
	
	
	static protected Tuple fromBundle(Bundle b) {
		
		SchemaType st = b.schemaType;
		Tuple t = new Tuple(st);
		Object[] m = b.elements;
		
		for (SchemaElement se : st.elements) {

			Object v = m[se.index];
			if (se.nullable == Nullable.NO && v == null)
				throw new IllegalArgumentException(String.format("Element {%s}.%s cannot be null", st.type, se.name));
			
			if (v != null) {
				switch (se.kind) {
				case SCALAR:
					if (se.clazz.isAssignableFrom(v.getClass()) == false)
						throw new IllegalArgumentException(String.format(
								"Element {%s}.%s must be of class %s", st.type, se.name, se.clazz.getSimpleName()
								));
					t.elements[se.index] = v;
					break;
	
				case ENTITY:
					if (v instanceof Entity == false)
						throw new IllegalArgumentException(String.format(
								"Element {%s}.%s must be Entity{%s}, not %s", st.type, se.name, se.type, v.getClass().getSimpleName()
								));
					Entity e = (Entity) v;
					if (se.type.equals(e.getType()) == false)
						throw new IllegalArgumentException(String.format(
								"Element {%s}.%s must be of type {%s}, not {%s}", st.type, se.name, se.type, e.getType()
								));
					OmniStorage.validateId(e.getId());
					t.elements[se.index] = e.getId();
					break;
						
				case LIST:
					throw new UnsupportedOperationException("List in entities aren't supported yet");
				}
			}
		}
		
		return t;
	}
	
	
	
	
}
