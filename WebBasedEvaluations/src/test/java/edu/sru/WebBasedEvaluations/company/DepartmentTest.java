package edu.sru.WebBasedEvaluations.company;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.User;

@Suite
@SpringBootTest
public class DepartmentTest {
	
	static long id = 1;
	static String newName;

	static User user = new User();
	static List<User> users;
	static Location loc = new Location();
	static String name = "Test name";
	static Privilege priv = new Privilege();
	static List<Location> locations;
	static Location newloc = new Location();
	static List<Privilege> Privilegies;
	static Company co;
	static Department dep;
	
	
	@BeforeAll
	static public void initialize() {
		
		
		locations = new ArrayList<Location>();
		locations.add(loc);
		
		Privilegies = new ArrayList<Privilege>();
		Privilegies.add(priv);
		
		users = new ArrayList<User>();
		users.add(user);
		
		dep = new Department(users, locations, name, Privilegies,co);
		dep.setId(id);
		
	}
	
	@Test
	public void activationTest() {
		assertTrue(dep.getActivation());
	}
	
	@Test
	public void addPrivilegeTest() {
		
		Privilege newPriv = new Privilege();
		
		dep.addLocation(loc);
		
		Privilegies.add(newPriv);
		Privilegies.add(priv);
		
		assertTrue(dep.getPrivileges() == Privilegies);
		
	}
	
	@Test
	public void removePrivilegeTest() {
		
		assertTrue(dep.removePrivilege(priv));
		
	}
	
	@Test
	public void addUserTest() {
		
		User newUser = new User();
		
		assertTrue(dep.addUser(newUser));
		
	}
	
	@Test
	public void addUsersTest() {
		
		User newUser = new User();
		users.add(newUser);
		
		assertTrue(dep.addUsers(users));
		
	}
	
	@Test
	public void removeUserTest() {
		
		assertTrue(dep.removeUser(user));
		
	}
	
	@Test
	public void addLocationTest() {
		
		
		dep.addLocation(newloc);
		locations.add(newloc);
		locations.add(loc);
		assertTrue(dep.getLocations() == locations);
		
	}
	
	@Test
	public void addLocationsTest() {
		
		
		locations.add(newloc);
		locations.add(loc);
		dep.addLocations(locations);
		assertTrue(dep.getLocations() == locations);
	}
	
	@Test
	public void removeLocationTest() {
		
		locations.add(newloc);
		locations.add(loc);
		dep.removeLocation(loc);
		assertTrue(dep.getLocations() == locations);
		
	}
	
	@Test
	public void getNameTest() {
		
		assertTrue(dep.getName() == newName);
		
	}
	
	@Test
	public void getIdTest() {
		
		assertTrue(dep.getId() != null);
		
	}
	
	@Test
	public void setIdTest() {
		
		long newId = 2;
		
		dep.setId(newId);
		assertTrue(dep.getId() == newId);
		
	}
	
	@Test
	public void getPrivilegesTest() {
		
		assertTrue(dep.getPrivileges() == Privilegies);
		
	}
	
	@Test
	public void setPrivilegesTest() {
		
		Privilege newpriv = new Privilege();
		Privilegies.add(newpriv);
		dep.setPrivileges(Privilegies);
		
		assertTrue(dep.getPrivileges() == Privilegies);
		
	}
	
	@Test
	public void setNameTest() {
		
		newName = "Different name";
		
		dep.setName(newName);
		assertTrue(dep.getName() == newName);
		
	}
	
	@Test
	public void getLocationsTest() {
		
		assertTrue(dep.getLocations() == locations);
		
	}
	
	@Test
	public void setLocationsTest() {
		
		Location newLoc = new Location();
		locations.add(newLoc);
		
		dep.setLocations(locations);
		
		assertTrue(dep.getLocations() == locations);
		
	}
	
	@Test
	public void getUsersTest() {
		
		assertTrue(dep.getUsers() == users);
		
	}
	
	@Test
	public void setUsersTest() {
		
		User newUser = new User();
		
		users.add(newUser);
		
		dep.setUsers(users);
		assertTrue(dep.getUsers() == users);
		
	}

}
