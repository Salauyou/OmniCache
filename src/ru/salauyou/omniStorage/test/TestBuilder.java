package ru.salauyou.omniStorage.test;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.util.List;

import org.junit.Test;

import ru.salauyou.omniStorage.Builder;
import ru.salauyou.omniStorage.Bundle;
import ru.salauyou.omniStorage.Entity;
import ru.salauyou.omniStorage.EntityAdapter;
import ru.salauyou.omniStorage.OmniStorage;
import ru.salauyou.omniStorage.Schema.ElementKind;
import ru.salauyou.omniStorage.Schema.Nullable;
import ru.salauyou.omniStorage.Schema.SchemaElement;

public class TestBuilder {

	
	/** inner class for entity type **/

	static class Type1 implements Entity {

		@Override
		public String getType() {
			return "Type1";
		}

		@Override
		public Object getId() {
			return "0";
		}
		
		static EntityAdapter adapter = new EntityAdapter() {

			@Override
			public Entity create(String type, Object id) {
				return new Type1();
			}

			@Override
			public void toBundle(Entity e, Bundle b) { }

			@Override
			public void fromBundle(Entity e, Bundle b) { }
		};
	}
	
	/** end of inner class **/
	
	
	
	
	@Test
	public void testAddType() {
		Builder b = OmniStorage.builder();
		try {
			b.addType("", String.class);		// empty string
			fail();
		} catch (RuntimeException e) { 
			assertTrue(e.getMessage().contains("null"));
		}
		try {
			b.addType(null, String.class);	 // null string
			fail();
		} catch (RuntimeException e) { 
			assertTrue(e.getMessage().contains("empty"));
		}
		try {
			b.addType("Type1", null);		// null id class
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("null"));
		}
		try {
			b.addType("Type1", Date.class); 	// wrong id class
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("not allowed"));
		}
		b.addType("Type1", String.class);
		try {
			b.addType("Type2", String.class);		// elements not added
			fail();
		} catch (RuntimeException e) { 
			assertTrue(e.getMessage().contains("must contain"));
		}
		
		// new builder
		b = OmniStorage.builder().addType("Type1", String.class).addScalar("scalar1", String.class);
		try {
			b.addType("Type2", String.class);		// adapter not defined
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("adapter"));
		}
		b.defineAdapter(Type1.adapter);
		try {
			b.addType("Type1", String.class);		// existing type
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("already defined"));
		}
		b.addType("Type2", String.class);
		try {
			b.addType("Type3", String.class);		// elements not added
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("must contain"));
		}
		b.addScalar("scalar1", String.class).defineAdapter(Type1.adapter);
	}
	
	
	
	@Test
	public void testAddScalar() {
		Builder b = OmniStorage.builder().addType("Type1", String.class);
		try {
			b.addScalar(null, String.class);  // null element name
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("null"));
		}
		try {
			b.addScalar("", String.class);  // empty element name
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("empty"));
		}
		try {
			b.addScalar("scalar1", null);  // null class
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("null"));
		}
		try {
			b.addScalar("scalar1", Date.class);
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("not allowed"));
		}
		
		// new builder
		b = OmniStorage.builder().addType("Type1", String.class);
		b.addScalar("scalar1", String.class);
		try {
			b.addScalar("scalar1", String.class);  // same element name
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("already defined"));
		}
		b.addScalar("scalar2", Integer.class, Nullable.YES)
		 .addScalar("scalar3", new Double(1.2).getClass(), Nullable.NO)
		 .defineAdapter(Type1.adapter);
		
		OmniStorage s = b.build();
		assertEquals(1, s.getSchema().size());
		assertEquals(3, s.getSchema().get(0).getElements().size());
		assertEquals("scalar1", s.getSchema().get(0).getElements().get(0).name);
		assertEquals(ElementKind.SCALAR, s.getSchema().get(0).getElements().get(0).kind);
	}
	
	
	
	@Test
	public void testAddEntity() {
		Builder b = OmniStorage.builder().addType("Type1", String.class);
		try {
			b.addEntity("", "Type1"); // empty name
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("empty"));
		} 
		try {
			b.addEntity(null, "Type1"); // null name
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("null"));
		}
		try {
			b.addEntity("type1", null); // null type
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("null"));
		}
		try {
			b.addEntity("type1", ""); // empty type
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("empty"));
		}
		
		b.defineAdapter(Type1.adapter);
		
		// try to add non-existing type and build schema
		b.addEntity("type2", "Type2");
		try {
			b.build();
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("not defined"));
		}
		
		b.addType("Type2", String.class)
		 .addScalar("string1", String.class)
		 .addScalar("string2", String.class)
		 .addEntity("type1", "Type1")
		 .defineAdapter(Type1.adapter);
		
		// after non-existing type is defined, build must go well
		OmniStorage s = b.build();
		assertEquals(2, s.getSchema().size());
		assertEquals(1, s.getSchema().get(0).getElements().size());
		assertEquals(3, s.getSchema().get(1).getElements().size());
		assertEquals("type2", s.getSchema().get(0).getElements().get(0).name);
		assertEquals("type1", s.getSchema().get(1).getElements().get(2).name);
		assertEquals(ElementKind.ENTITY, s.getSchema().get(0).getElements().get(0).kind);
		assertEquals(ElementKind.ENTITY, s.getSchema().get(1).getElements().get(2).kind);
	}
	
	
	
	@Test
	public void testDefineAdapter() {
		// TODO: add test after implementing `DefineAdapter()` more carefully
	}
	
	
	
	@Test
	public void testBuild() {
		Builder b = OmniStorage.builder().addType("Type1", String.class)
						.addScalar("string", String.class)
						.addScalar("integer", Integer.class, Nullable.YES)
						.addScalar("double", Double.class, Nullable.NO)
						.defineAdapter(Type1.adapter);
		
		OmniStorage s = b.build();
		try {
			b.build();  // calling build() second time
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("built"));
		}
		
		assertEquals(1, s.getSchema().size());
		assertEquals("Type1", s.getSchema().get(0).type);
		assertEquals(String.class, s.getSchema().get(0).idClass);
		
		List<SchemaElement> es = s.getSchema().get(0).getElements();
		List<String> names = es.stream().map((e) -> e.name).collect(toList());
		List<Class<?>> classes = es.stream().map((e) -> e.clazz).collect(toList());
		List<Nullable> nullables = es.stream().map((e) -> e.nullable).collect(toList());
		assertEquals(asList("string", "integer", "double"), names);
		assertEquals(asList(String.class, Integer.class, Double.class), classes);
		assertEquals(asList(Nullable.YES, Nullable.YES, Nullable.NO), nullables);
	}

}
