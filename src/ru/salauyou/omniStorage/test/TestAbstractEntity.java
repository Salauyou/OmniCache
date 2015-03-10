package ru.salauyou.omniStorage.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.salauyou.omniStorage.AbstractEntity;

public class TestAbstractEntity {

	
	/** test classes **/
	
	static class EntityOne extends AbstractEntity {

		@Override
		public void setId(String id) {
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
	
	// same returned type but different class
	static class Entity2 extends AbstractEntity {

		@Override
		public String getType() {
			return("Two");
		}
		
		public Entity2(String id) {
			this.id = id;
		}
		
		@Override
		public void setId(String id) {
			this.id = id;
		}
		
	}
	
	/** end of test classes **/
	
	

	

	@Test
	public void testToString() {
		EntityOne e1 = new EntityOne();
		e1.setId("one");
		assertTrue(e1.toString().equals("@Entity{One}[one]"));
		
		e1 = new EntityOne();
		assertTrue(e1.toString().equals("@Entity{One}[null]"));
		
		EntityTwo e2 = new EntityTwo("two");
		assertTrue(e2.toString().equals("@Entity{Two}[two]"));
		
		e2 = new EntityTwo(null);
		assertTrue(e2.toString().equals("@Entity{Two}[null]"));
		
		Entity2 eTwo = new Entity2("twenty-two");
		assertTrue(eTwo.toString().equals("@Entity{Two}[twenty-two]"));
	}
	
	
	
	
	@Test
	public void testEqualsAndHashcode() {
		EntityOne e1 = new EntityOne();
		e1.setId("one");
		EntityTwo e2 = new EntityTwo("two");
		assertFalse(e1.equals(e2));
		assertFalse(e2.equals(e1));
		
		EntityTwo two = new EntityTwo("two");
		assertTrue(e2.equals(two));
		assertTrue(two.equals(e2));
		assertTrue(two.hashCode() == e2.hashCode());
		
		EntityOne one = new EntityOne();
		one.setId("one");
		assertTrue(e1.equals(one));
		assertTrue(one.equals(e1));
		assertTrue(e1.hashCode() == one.hashCode());
		
		two = new EntityTwo("twenty-two");
		assertFalse(e2.equals(two));
		assertFalse(two.equals(e2));
		
		e2 = new EntityTwo(null);
		two = new EntityTwo(null);
		assertFalse(e2.equals(two));
		assertFalse(two.equals(e2));
		
		e1.setId("two");
		e2 = new EntityTwo("two");
		assertFalse(e1.equals(e2));
		assertFalse(e2.equals(e1));
		
		assertFalse(e2.equals("two"));
		assertFalse("two".equals(e2));
		assertTrue(e2.equals(e2));
		assertFalse(e2.equals(null));
		
		Entity2 eTwo = new Entity2("two");
		assertTrue(e2.equals(eTwo));
		assertTrue(eTwo.equals(e2));
		
		eTwo.setId("twenty-two");
		assertFalse(e2.equals(eTwo));
		assertFalse(eTwo.equals(e2));
	}
	
	
	
	@Test
	public void testCreate() {
		EntityOne e1 = new EntityOne();
		e1.setId("one");
		EntityTwo e2 = new EntityTwo("two");
		Entity2 eTwo = new Entity2("twenty-two");
		assertEquals(e1.getType(), "One");
		assertEquals(e1.getId(), "one");
		assertEquals(e2.getType(), "Two");
		assertEquals(e2.getId(), "two");
		assertEquals(eTwo.getType(), "Two");
		assertEquals(eTwo.getId(), "twenty-two");
	}
	


}
