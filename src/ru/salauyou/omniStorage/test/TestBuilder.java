package ru.salauyou.omniStorage.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.salauyou.omniStorage.Builder;
import ru.salauyou.omniStorage.Bundle;
import ru.salauyou.omniStorage.Entity;
import ru.salauyou.omniStorage.EntityAdapter;
import ru.salauyou.omniStorage.OmniStorage;
import ru.salauyou.omniStorage.Schema.Nullable;

public class TestBuilder {

	
	/** inner class for entity type **/

	static class Type1 implements Entity {

		@Override
		public String getType() {
			return "Type1";
		}

		@Override
		public String getId() {
			return "0";
		}
		
		static EntityAdapter adapter = new EntityAdapter() {

			@Override
			public Entity create(String type, String id) {
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
			b.addType("");		// empty string
			fail();
		} catch (RuntimeException e) { 
			assertTrue(e.getMessage().contains("null"));
		}
		try {
			b.addType(null);	// null string
			fail();
		} catch (RuntimeException e) { 
			assertTrue(e.getMessage().contains("empty"));
		}
		b.addType("Type1");
		try {
			b.addType("Type2");		// elements not added
			fail();
		} catch (RuntimeException e) { 
			assertTrue(e.getMessage().contains("must contain"));
		}
		
		// new builder
		b = OmniStorage.builder().addType("Type1").addScalar("scalar1", String.class);
		try {
			b.addType("Type2");		// adapter not defined
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("adapter"));
		}
		b.defineAdapter(Type1.adapter);
		try {
			b.addType("Type1");		// existing type
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("already defined"));
		}
		b.addType("Type2");
		try {
			b.addType("Type3");		// elements not added
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("must contain"));
		}
		b.addScalar("scalar1", String.class).defineAdapter(Type1.adapter);
	}
	
	
	
	@Test
	public void testAddScalar() {
		Builder b = OmniStorage.builder().addType("Type1");
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
			b.addScalar("scalar1", Type1.class);  // entity class in addScalar
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("Entity"));  
		}
		
		// new builder
		b = OmniStorage.builder().addType("Type1");
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
		
	}
	
	
	
	@Test
	public void testAddEntity() {
		
	}
	
	
	
	@Test
	public void testDefineAdapter() {
		
	}
	
	
	
	@Test
	public void testBuild() {
		Builder b = OmniStorage.builder().addType("Type1")
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
	}

}
