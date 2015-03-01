package ru.salauyou.omniStorage;

import ru.salauyou.omniStorage.Schema.ElementKind;
import ru.salauyou.omniStorage.Schema.Nullable;

public class SchemaElement {

	protected final String name;
	protected final Class<?> clazz;
	protected final String column;
	protected final Nullable nullable;
	protected final ElementKind kind;
	protected final String type;
	protected final int index;

	
	protected SchemaElement(ElementKind kind, String name, Class<?> clazz, 
			                String type, String column, Nullable nullable, int index) {
		this.kind = kind;
		this.name = name;
		this.clazz = clazz;
		this.column = column;
		this.nullable = nullable;
		this.type = type;
		this.index = index;
	}

	
	
	@Override
	public String toString() {
		String t = "";
		switch (this.kind) {
		case SCALAR:
			t = clazz.getSimpleName();
			break;
		case ENTITY:
			t = String.format("Entity{%s}", type);
			break;
		case LIST:
			t = String.format("List{%s}", clazz.getSimpleName());
			break;
		}
		return String.format("%s@%s%s", name, t, nullable == Nullable.YES ? "" : " not-null");
	}

}
