package edu.sru.WebBasedEvaluations.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.company.City;
import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Country;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;
import edu.sru.WebBasedEvaluations.company.Province;

@Suite
@SpringBootTest
public class CityTest {
	
	long id = 1;
	static String name = "Newaygo";
	static int locations = 1;
	static Province parent;
	static Country grandparent;
	static Location child;
	static Company co;
	static LocationGroup locGroup;
	
	static City city;
	
	@BeforeAll
	static public void initialize() {
		parent = new Province(name, locations, grandparent, city);
		child = new Location(name, city, co, locGroup);
		city = new City(name, locations, parent, child);
		
	}
	
	@Test
	public void addLocationTest() {
		//Make a location
		Location otherChild = new Location("Hell", city, co, locGroup);
		
		assertTrue(city.addLocation(otherChild));
	}
	
	@Test
	public void addLocationsTest() {
		
		List<Location> locations = new ArrayList<Location>();
		Location otherChild = new Location("Hell", city, co, locGroup);
		locations.add(otherChild);
		
		assertTrue(city.addLocations(locations));
		
	}
	
	@Test
	public void removeLocationTest() {
		
		assertTrue(city.removeLocation(child));
		
	}
	
	@Test
	public void getIdTest() {
		
		city.setId(id);
		assertTrue(city.getId() == id);
		
	}
	
	@Test
	public void setIdTest() {
		
		city.setId(id);
		assertThat(!city.getId().toString().isEmpty());
		
	}
	
	@Test
	public void getCityNameTest() {
		
		assertThat(city.getCityName().toString().contains(name));
		
	}
	
	@Test
	public void setCityNameTest() {
		city.setCityName("Grant");
		
		assertThat(city.getCityName().toString().contains("Grant"));
	}
	
	@Test
	public void getNumLocationsTest() {
		
		assertThat(city.getNumLocations() == 1);
		
	}
	
	@Test
	public void setNumLocationsTest() {
		
		city.setNumLocations(5);
		
		
		assertThat(city.getNumLocations() == 5);
	}
	
	@Test
	public void getParentProvinceTest() {
		
		assertFalse(city.getParentProvince().toString().isEmpty());
		
	}
	
	@Test
	public void setParentProvinceTest() {
		
		Province newDad = new Province(name, locations, grandparent, city);
		city.setParentProvince(newDad);
		
		assertFalse(city.getParentProvince().toString().isEmpty());
	}
	
	@Test
	public void getChildLocationsTest() {
		
		assertFalse(city.getChildLocations().isEmpty());
		
	}
	
	@Test
	public void setChildLocationsTest() {
		
		List<Location> locations = new ArrayList<Location>();
		Location otherChild = new Location("Hell", city, co, locGroup);
		
		locations.add(otherChild);
		
		city.setChildLocations(locations);
		
		assertFalse(city.getChildLocations().isEmpty());
		
	}

}
