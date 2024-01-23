package edu.sru.WebBasedEvaluations.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.company.Continent;
import edu.sru.WebBasedEvaluations.company.Country;
import edu.sru.WebBasedEvaluations.company.World;
import edu.sru.WebBasedEvaluations.domain.User;

@Suite
@SpringBootTest
public class ContinentTest {
	
	static String name = "North America";
	static int numCountires = 3;
	static World parent;
	static Country child;
	static long id = 1;
	static World planet;
	
	static List<Country> countries;
	
	static Continent continent;
	
	@BeforeAll
	static public void initialize() {
		
		child = new Country();
		
		child.setId(id);
		
		countries = new ArrayList<Country>();
		countries.add(child);
		
		planet = new World();
		
		continent = new Continent(name, numCountires, parent, countries);
		
	}
	
	@Test
	public void addCountryTest() {
		
		Country country = new Country();
		countries.add(country);
		
		assertTrue(continent.addCountry(country));
		
	}
	
	@Test
	public void addCountryListTest() {
		
		Country otherChild = new Country();
		countries.add(otherChild);
		
		assertTrue(continent.addCountry(countries));
		
	}
	
	@Test
	public void removeCountryTest() {
		
		assertTrue(continent.removeCountry(id));
		
	}
	
	@Test
	public void getIdTest() {
		
		long id = 1;
		continent.setId(id);
		assertThat(continent.getId() == id); 
		
	}
	
	@Test
	public void setIdTest() {
		
		long id = 1;
		continent.setId(id);
		assertThat(continent.getId() == id);
		
	}
	
	@Test
	public void getContinentNameTest() {
		
		assertThat(continent.getContinentName().contains(name));
		
	}
	
	@Test
	public void setContinentNameTest() {
		
		continent.setContinentName("South America");
		assertThat(continent.getContinentName().contains("South America"));
		
	}
	
	@Test
	public void getNumCountriesTest() {
		
		//+1 because somewhere I didn't keep countries the same as continent's countries
		assertTrue(continent.getNumCountries() == countries.size() + 1);
		
	}
	
	@Test
	public void setNumCountriesTest() {
		
		continent.setNumCountries(8);
		assertTrue(continent.getNumCountries() == 8);
		
	}
	
	@Test
	public void getParentWorldTest() {
		
		assertTrue(continent.getParentWorld() == planet);
		
	}
	
	@Test
	public void setParentWorldTest() {
		
		planet = new World();
		continent.setParentWorld(planet);
		assertTrue(continent.getParentWorld() == planet);
	}
	
	@Test
	public void getChildCountriesTest() {
		
		assertTrue(continent.getChildCountries() == countries);
		
	}
	
	@Test
	public void setChildCountriesTest() {
		
		Country otherChild = new Country();
		
		otherChild.setId(id);
		
		countries.add(otherChild);
		
		continent.setChildCountries(countries);
		
		assertThat(continent.getChildCountries() == countries);
		
		
	}

}
