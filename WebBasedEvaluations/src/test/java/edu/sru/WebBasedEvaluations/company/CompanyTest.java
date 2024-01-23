package edu.sru.WebBasedEvaluations.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.domain.User;

@Suite
@SpringBootTest
public class CompanyTest {
	
	static String name = "CD project red";
	static Company co;
	
	@BeforeAll
	static public void initialize() {
		
		co = new Company(name);
		
	}
	
	@Test
	public void activationTest() {
		assertTrue(co.getActivation());
	}
	
	@Test
	public void addLocationTest() {
		
		Location loc = new Location();
		
		assertTrue(co.addLocation(loc));
		
	}
	
	@Test
	public void addLocationsTest() {
		
		List<Location> locations = new ArrayList<Location>();
		Location otherChild = new Location();
		locations.add(otherChild);
		
		assertTrue(co.addLocations(locations));
		
	}
	
	@Test
	public void removeLocationTest() {
		
		Location loc = new Location();
		co.addLocation(loc);
		
		assertTrue(co.removeLocation(loc));
		
	}
	
	@Test
	public void addUserTest() {
		
		User user = new User();
		
		assertTrue(co.addUser(user));
		
	}
	
	@Test
	public void addUsersTest() {
		
		List<User> users = new ArrayList<User>();
		User otherChild = new User();
		users.add(otherChild);
		
		assertTrue(co.addUsers(users));
		
	}
	
	@Test
	public void removeUserTest() {
		
		User user = new User();
		co.addUser(user);
		
		assertTrue(co.removeUser(user));
		
	}
	
	@Test
	public void getIdTest() {
		
		long id = 1;
		co.setId(id);
		assertThat(co.getId() == id); 
		
	}
	
	@Test
	public void setIdTest() {
		
		long id = 1;
		co.setId(id);
		assertThat(co.getId() == id);
		
	}
	
	@Test
	public void getCompanyNameTest() {
		
		assertThat(co.getCompanyName().contains(name));
		
	}
	
	@Test
	public void setCompanyNameTest() {
		
		String otherName = "name";
		co.setCompanyName(otherName);
		assertThat(co.getCompanyName().contains(otherName));
	}
	
	@Test
	public void getNumEmployeesTest() {
		
		assertTrue(co.getNumEmployees() == 0);
		
	}
	
	@Test
	public void setNumEmployeesTest() {
		
		
		co.setNumEmployees(3);
		assertTrue(co.getNumEmployees() == 3);
		
	}
	
	@Test
	public void getNumLocationsTest() {
		
		assertTrue(co.getNumLocations() == 1);
		
	}
	
	@Test
	public void setNumLocationsTest() {
		
		co.setNumLocations(3);
		assertTrue(co.getNumLocations() == 3);
		
	}
	

}
