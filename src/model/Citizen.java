package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ru.salauyou.omnistorage.core.Bundle;
import ru.salauyou.omnistorage.core.classes.AbstractEntity;
import ru.salauyou.omnistorage.core.classes.Entity;
import ru.salauyou.omnistorage.core.classes.EntityAdapter;




public class Citizen extends AbstractEntity<Integer>  {
	
	static public final String TYPE = "Citizen";
	
	private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	//private final Integer id;
	private String name;
	private String surname;
	private LocalDate birthDate;
	private Citizen mother;
	private Citizen father;
	private City birthPlace;
	
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	/*
	@Override
	public Integer getId() {
		return id;
	}
	*/
	
	public Citizen(Integer id, String name, String surname, LocalDate birthDate, City birthPlace) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.birthPlace = birthPlace;
	}
	
	
	private Citizen(Integer id) { 
		this.id = id;
	}


	public Citizen setMom(Citizen mother) {
		this.mother = mother;
		return this;
	}
	
	
	public Citizen setDad(Citizen father) {
		this.father = father;
		return this;
	}

	
	
	
	@Override
	public String toString() {
		
		return "Citizen[" + getId() + "]: " + name + " " + surname 
				+ ", date of birth=" + (birthDate == null ? "null" : df.format(birthDate)) 
				+ ", mother=" + (mother == null ? "null" : (mother.name + " " + mother.surname)) 
				+ ", father=" + (father == null ? "null" : (father.name + " " + father.surname))
				+ ", birth place=" + (birthPlace == null ? "null" : birthPlace);
	}
	
	
	//-------------- apapter -----------------
	
	static public EntityAdapter<Integer> adapter = new EntityAdapter<Integer>(){

		@Override
		public Entity<Integer> create(String type, Integer id) {
			return new Citizen(id);
		}
		
		@Override
		public void toBundle(Entity<Integer> e, Bundle b) {
			Citizen c = (Citizen) e;
			b.set("name", c.name);
			b.set("surname", c.surname);
			b.set("birthDate", c.birthDate);
			b.set("mother", c.mother);
			b.set("father", c.father);
			b.set("birthPlace", c.birthPlace);
		}
		
		@Override
		public void fromBundle(Entity<Integer> e, Bundle b) {
			Citizen c = (Citizen) e;
			c.name = (String) b.get("name");
			c.surname = (String) b.get("surname");
			c.birthDate = (LocalDate) b.get("birthDate");
			c.mother = (Citizen) b.get("mother");
			c.father = (Citizen) b.get("father");
			c.birthPlace = (City) b.get("birthPlace");
		}
	};


	private String getName() {
		return name;
	}


	private void setName(String name) {
		this.name = name;
	}


	private String getSurname() {
		return surname;
	}


	private void setSurname(String surname) {
		this.surname = surname;
	}


	private LocalDate getBirthDate() {
		return birthDate;
	}


	private void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}


	private Citizen getMother() {
		return mother;
	}


	private void setMother(Citizen mother) {
		this.mother = mother;
	}


	private City getBirthPlace() {
		return birthPlace;
	}


	private void setBirthPlace(City birthPlace) {
		this.birthPlace = birthPlace;
	}


	private void setFather(Citizen father) {
		this.father = father;
	}


	private Citizen getFather() {
		return this.father;
	}


	
	

}
