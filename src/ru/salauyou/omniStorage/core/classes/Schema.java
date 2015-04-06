package ru.salauyou.omnistorage.core.classes;

import static java.util.stream.Collectors.toSet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;


public final class Schema {

	
	public static enum Nullable {
		YES, NO;
	}
	
	
	public static enum ElementKind {
		SCALAR, ENTITY, LIST;
	}
	

	/**
	 * Immutable classes that are allowed to represent scalar elements of entities
	 */
	public static final Set<Class<?>> ALLOWED_SCALAR_CLASSES = Collections.unmodifiableSet(Stream.of(
			Integer.class, Long.class, Float.class, Double.class, Byte.class, Character.class, Short.class,
			BigInteger.class, BigDecimal.class,
			Boolean.class, 
			String.class, 
			UUID.class,
			Instant.class, LocalDate.class, LocalTime.class, LocalDateTime.class, 
			OffsetDateTime.class, OffsetTime.class, ZonedDateTime.class 
			).collect(toSet()));
	

	
	public static final class SchemaType {
		
		public final Map<String, SchemaElement> elementMap;
		public final List<SchemaElement> elements;
		public final String type;
		public final Class<?> idClass;
		
		public SchemaType(String type, Class<?> idClass, List<SchemaElement> elements) {
			this.type = type;
			this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
			this.idClass = idClass;
			Map<String, SchemaElement> m = new HashMap<>();
			for (SchemaElement e : elements) {
				m.put(e.name, e);
			}
			elementMap = Collections.unmodifiableMap(m);
		}
		
		public List<SchemaElement> getElements() {
			return elements;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\nEntity{").append(type).append("}");
			sb.append("\n     id@").append(idClass.getSimpleName());
			for (SchemaElement e : elements) {
				sb.append("\n     ").append(e.toString());
			}
			return sb.toString();
		}
	}
	
  
	
	public static final class SchemaElement {

		public final String name;
		public final Class<?> clazz;
		public final String column;
		public final Nullable nullable;
		public final ElementKind kind;
		public final String type;
		public final int index;

		public SchemaElement(ElementKind kind, String name, Class<?> clazz, 
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
	
	
	
	// no instantiation
	private Schema(){};
	
}
