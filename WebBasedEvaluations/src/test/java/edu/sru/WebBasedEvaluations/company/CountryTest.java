package edu.sru.WebBasedEvaluations.company;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.company.Continent;
import edu.sru.WebBasedEvaluations.company.Country;
import edu.sru.WebBasedEvaluations.company.Province;

@Suite
@SpringBootTest
public class CountryTest {
	
	static long id = 1;
	static String name = "USA";
	static int numProv = 1;
	static Continent parent;
	static Province child;
	static List<Province> provincies;
	
	static Country country;

	@BeforeAll
	static public void initialize() {
		
		provincies = new ArrayList<Province>();
		
		child = new Province();
		
		provincies.add(child);
		
		country = new Country(name, numProv, parent, provincies);
		country.setId(id);
		
		id = country.getId();
		
		
	}
	
	@Test
	public void addProvinceTest() {
		
		provincies.add(child);
		
		assertTrue(country.addProvince(child));
		
	}
	
	@Test
	public void addProvincesTest() {
		
		Province otherChild = new Province();
		provincies.add(otherChild);
		
		assertTrue(country.addProvinces(provincies));
		
	}
	
	@Test
	public void removeProvinceTest() {
		
		provincies.remove(child);
		
		assertTrue(country.removeProvince(child));
		
	}
	
	@Test
	public void getIdTest() {
		
		
		assertTrue(country.getId().toString() != null);
	}
	
	@Test
	public void setIdTest() {
		
		long newId = 3;
		country.setId(newId);
		assertTrue(country.getId() == 3);
		
	}
	
	@Test
	public void getCountryNameTest() {
		
		assertTrue(country.getCountryName() == name);
		
	}
	
	@Test
	public void setCountryNameTest() {
		
		String newName = "Canada";
		country.setCountryName(newName);
		
		assertTrue(country.getCountryName() == newName);
		
	}
	
	@Test
	public void getNumProvincesTest() {
		
		assertTrue(country.getNumProvinces() == provincies.size());
		
	}
	
	@Test
	public void setNumProvincesTest() {
		
		country.setNumProvinces(4);
		assertTrue(country.getNumProvinces() == 4);
		
	}
	
	@Test
	public void getParentContinentTest() {
		
		assertTrue(country.getParentContinent() == parent);
		
	}
	
	@Test
	public void setParentContinentTest() {
		
		Continent newContinent = new Continent();
		country.setParentContinent(newContinent);
		
		assertTrue(country.getParentContinent() == newContinent);
	}
	
	@Test
	public void getChildProvincesTest() {
		
		assertTrue(country.getChildProvinces() == provincies);
		
	}
	
	@Test
	public void setChildProvincesTest() {
		
		
		country.setChildProvinces(provincies);
		
		assertTrue(country.getChildProvinces() == provincies);
		
	}
	
}
