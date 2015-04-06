package ru.salauyou.omnistorage.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.salauyou.omnistorage.core.classes.AbstractEntity;


public class TestAbstractEntity {

	
	/** test classes **/
	
	static class EntityOne extends AbstractEntity {

		@Override
		public void setId(Object id) {
			this.id = id;
		}
		
		@Override
		public String getType() {
			return("One");
		}
		
	}
	
	static class EntityTwo extends AbstractEntity {

		@Override
		public String getType() {
			return("Two");
		}
		
		public EntityTwo(String id) {
			this.id = id;
		}
		
	}
	
	// same as EntityTwo returned type but different class
	static class Entity2 extends AbstractEntity {

		@Override
		public String getType() {
			return("Two");
		}
		
		public Entity2(String id) {
			this.id = id;
		}
		
		@Override
		public void setId(Object id) {
			this.id = id;
		}
		
	}
	
	/** end of test classes **/
	
	

	

	@Test
	public void testToString() {
		EntityOne e1 = new EntityOne();
		e1.setId("one");
		assertEquals("@Entity{One}[one]", e1.toString());
		
		e1 = new EntityOne();
		assertEquals("@Entity{One}[null]", e1.toString());
		
		EntityTwo e2 = new EntityTwo("two");
		assertEquals("@Entity{Two}[two]", e2.toString());
		
		e2 = new EntityTwo(null);
		assertEquals("@Entity{Two}[null]", e2.toString());
		
		Entity2 eTwo = new Entity2("twenty-two");
		assertEquals("@Entity{Two}[twenty-two]", eTwo.toString());
	}
	
	
	
	
	@Test
	public void testEqualsAndHashcode() {
		EntityOne e1 = new EntityOne();
		e1.setId("one");
		EntityTwo e2 = new EntityTwo("two");
		assertNotEquals(e1, e2);
		assertNotEquals(e2, e1);
		
		EntityTwo two = new EntityTwo("two");
		assertEquals(e2, two);
		assertEquals(two, e2);
		assertTrue(two.hashCode() == e2.hashCode());
		
		EntityOne one = new EntityOne();
		one.setId("one");
		assertEquals(e1, one);
		assertEquals(one, e1);
		assertTrue(e1.hashCode() == one.hashCode());
		
		two = new EntityTwo("twenty-two");
		assertNotEquals(e2, two);
		assertNotEquals(two, e2);
		
		e2 = new EntityTwo(null);
		two = new EntityTwo(null);
		assertNotEquals(e2, two);
		assertNotEquals(two, e2);
		assertNull(e2.getId());
		
		e1.setId("two");
		e2 = new EntityTwo("two");
		assertNotEquals(e1, e2);
		assertNotEquals(e2, e1);
		
		assertNotEquals("two", e2);
		assertNotEquals(e2, "two");
		assertEquals(e2, e2);
		assertNotEquals(e2, null);
		
		Entity2 eTwo = new Entity2("two");
		assertEquals(e2, eTwo);
		assertEquals(eTwo, e2);
		
		eTwo.setId("twenty-two");
		assertNotEquals(e2, eTwo);
		assertNotEquals(eTwo, e2);
	}
	
	
	
	@Test
	public void testCreate() {
		EntityOne e1 = new EntityOne();
		e1.setId("one");
		EntityTwo e2 = new EntityTwo("two");
		Entity2 eTwo = new Entity2("twenty-two");
		assertEquals("One", e1.getType());
		assertEquals("one", e1.getId());
		assertEquals("Two", e2.getType());
		assertEquals("two", e2.getId());
		assertEquals("Two", eTwo.getType());
		assertEquals("twenty-two", eTwo.getId());
	}
	


}
