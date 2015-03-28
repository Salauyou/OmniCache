package ru.salauyou.omniCache;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import model.Citizen;
import model.City;

import org.apache.log4j.Logger;

import ru.salauyou.omniStorage.OmniStorage;
import ru.salauyou.omniStorage.Schema.Nullable;
import test.TestHelper;

public class App {

	private static Logger log = Logger.getLogger(App.class);
	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	
	public void run() throws ParseException, InterruptedException {
		
		long timeStart = System.nanoTime();

		OmniStorage storage = OmniStorage.builder()
			.addType(Citizen.TYPE)
				.addScalar("name", String.class, Nullable.NO)
				.addScalar("surname", String.class, Nullable.NO)
				.addScalar("birthDate", LocalDate.class, Nullable.NO)
				.addEntity("mother", Citizen.TYPE)
				.addEntity("father", Citizen.TYPE)
				.addEntity("birthPlace", City.TYPE, Nullable.NO)
				.defineAdapter(Citizen.adapter)
				
			.addType(City.TYPE)
				.addScalar("name", String.class, Nullable.NO)
				.addScalar("lat", Double.class)
				.addScalar("lon", Double.class)
				.defineAdapter(City.adapter)
				
			.build();
		
 		log.info("Executed in " + (System.nanoTime() - timeStart)/1000 + " μs.");
		log.info(storage.printSchema());
		
		City grodno = new City("by-grodno", "Гродно", 25d, 37d);
		City moscow = new City("ru-moscow", "Москва", 55d, 37.5d);
		City yaroslavl = new City("ru-yaroslavl", "Ярославль", 56d, 38d);
		
		Citizen mom = new Citizen("mom", "Людмила", "Соловьева", LocalDate.parse("1953-10-19", df), grodno);
		Citizen dad = new Citizen("dad", "Николай", "Соловьев", LocalDate.parse("1949-03-14", df), yaroslavl);
		
		mom.setMom(mom).setDad(dad);
		
		storage
			.save(grodno)
			.save(mom)
			.save(yaroslavl)
			.save(dad)
			.save(moscow);
		
		log.info("Original: " + grodno);
		log.info("Original: " + moscow);
		log.info("Original: " + yaroslavl);
		log.info("Original: " + mom);
		
		log.info("Storage:  " + storage.getById(City.TYPE, "by-grodno"));
		log.info("Storage:  " + storage.getById(City.TYPE, "ru-moscow"));
		log.info("Storage:  " + storage.getById(City.TYPE, "ru-yaroslavl"));
		log.info("Storage:  " + storage.getById(Citizen.TYPE, "mom"));
		
		log.info("Started");
		int count = 1000_000;
		List<String> ids = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		List<String> surnames = new ArrayList<String>();
		List<LocalDate> birthDates = new ArrayList<LocalDate>();
		
		log.info("Generating samples...");
		Random rnd = new Random();
		for (int i = 0; i < count; i++) {
			ids.add(Long.toHexString(UUID.randomUUID().getLeastSignificantBits()));
			names.add(TestHelper.generateName(rnd, 1));
			surnames.add(TestHelper.generateName(rnd, 1));
			birthDates.add(LocalDate.now());
		}
		
		Thread.sleep(5_000);
		for (int iteration = 0; iteration < 10; iteration ++) {
			timeStart = System.currentTimeMillis();
			
			storage.clean();
			storage
				.save(grodno)
				.save(mom)
				.save(yaroslavl)
				.save(dad)
				.save(moscow);
			
			for (int i = 0; i < count; i++) {
				Citizen c = new Citizen(ids.get(i), names.get(i), surnames.get(i), birthDates.get(i), moscow);
				c.setDad(dad).setMom(mom);
				
				try {
					storage.save(c);
				} catch (Exception e) {
					log.warn(e.getMessage());
				}
				//if (i % 200_000 == 0) {
				//	log.info(c);
				//}
			}
			log.info(count + " entities generated and saved in " + (System.currentTimeMillis() - timeStart) / 1000d + " s.");
			
			Thread.sleep(1000);
			
			timeStart = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				Citizen c = (Citizen) storage.getById(Citizen.TYPE, ids.get(i));
				if (c == null) {
					log.warn("Null entity returned");
				}
				//if (i % 200_000 == 0) {
				//	log.info(c);
				//}
			}
			log.info(count + " entities loaded from storage in " + (System.currentTimeMillis() - timeStart) / 1000d + " s.\n\n");
			
			Thread.sleep(2000);
		}
		
		Thread.sleep(1000 * 60 * 5); // 5 minutes
		storage.clean();
	}

}
