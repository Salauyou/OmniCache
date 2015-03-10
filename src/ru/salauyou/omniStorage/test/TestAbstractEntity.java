package ru.salauyou.omniStorage.test;

import static org.junit.Assert.assertEquals;

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
		assertEquals(e1.toString().equals("@Entity{One}[one]"), true);
		
		e1 = new EntityOne();
		assertEquals(e1.toString().equals("@Entity{One}[null]"), true);
		
		EntityTwo e2 = new EntityTwo("two");
		assertEquals(e2.toString().equals("@Entity{Two}[two]"), true);
		
		e2 = new EntityTwo(null);
		assertEquals(e2.toString().equals("@Entity{Two}[null]"), true);
		
		Entity2 eTwo = new Entity2("twenty-two");
		assertEquals(eTwo.toString().equals("@Entity{Two}[twenty-two]"), true);
	}
	
	
	
	
	@Test
	public void testEqualsAndHashcode() {
		EntityOne e1 = new EntityOne();
		e1.setId("one");
		EntityTwo e2 = new EntityTwo("two");
		assertEquals(e1.equals(e2), false);
		assertEquals(e2.equals(e1), false);
		
		EntityTwo two = new EntityTwo("two");
		assertEquals(e2.equals(two), true);
		assertEquals(two.equals(e2), true);
		assertEquals(two.hashCode() == e2.hashCode(), true);
		
		EntityOne one = new EntityOne();
		one.setId("one");
		assertEquals(e1.equals(one), true);
		assertEquals(one.equals(e1), true);
		assertEquals(e1.hashCode() == one.hashCode(), true);
		
		two = new EntityTwo("twenty-two");
		assertEquals(e2.equals(two), false);
		assertEquals(two.equals(e2), false);
		
		e2 = new EntityTwo(null);
		two = new EntityTwo(null);
		assertEquals(e2.equals(two), false);
		assertEquals(two.equals(e2), false);
		
		e1.setId("two");
		e2 = new EntityTwo("two");
		assertEquals(e1.equals(e2), false);
		assertEquals(e2.equals(e1), false);
		
		assertEquals(e2.equals("two"), false);
		assertEquals("two".equals(e2), false);
		assertEquals(e2.equals(e2), true);
		assertEquals(e2.equals(null), false);
		
		Entity2 eTwo = new Entity2("two");
		assertEquals(e2.equals(eTwo), true);
		assertEquals(eTwo.equals(e2), true);
		
		eTwo.setId("twenty-two");
		assertEquals(e2.equals(eTwo), false);
		assertEquals(eTwo.equals(e2), false);
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
