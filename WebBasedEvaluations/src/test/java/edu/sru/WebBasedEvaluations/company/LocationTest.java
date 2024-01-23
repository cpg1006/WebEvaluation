package edu.sru.WebBasedEvaluations.company;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.company.City;
import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;
import edu.sru.WebBasedEvaluations.domain.User;

@Suite
@SpringBootTest
public class LocationTest {
	
	static long id = 1;
	
	static String locationName = "Name";
	static City parentCity = new City();
	static City homeCity = new City();
	static City newCity;
	static Company co = new Company();
	static LocationGroup locGroup = new LocationGroup();
	
	
	static List<User> users;
	static User user = new User();
	
	
	static List<Department> departments;
	static Department newDepartment = new Department(co);
	
	static Location loc;
	
	@BeforeAll
	static public void initialize() {
		
		departments = new ArrayList<Department>();
		departments.add(newDepartment);
		
		users = new ArrayList<User>();
		users.add(user);
		
		loc = new Location(locationName, parentCity, co, locGroup);
		
		loc.setId(id);
		loc.setUsers(users);
		loc.setDepartments(departments);
		
		loc.setNumEmployees(0);
		loc.setParentCity(homeCity);
		
		
		
	}
	@Test
	public void activationTest() {
		assertTrue(loc.getActivation());
	}
	
	@Test
	public void addDeptTest() {
		
		departments.add(newDepartment);
		assertTrue(loc.addDept(newDepartment));
		
	}
	
	@Test
	public void removeDeptTest() {
		
		departments.remove(newDepartment);
		
		loc.addDept(newDepartment);
		
		loc.getDepartments().get(0).setId(id);
		
		assertTrue(loc.removeDept(id));
		
	}
	
	@Test
	public void getLocGroupTest() {
		
		assertTrue(loc.getLocGroup() == locGroup);
		
	}
	
	@Test
	public void setLocGroupTest() {
		
		LocationGroup newLocGroup = new LocationGroup();
		
		loc.setLocGroup(newLocGroup);
		assertTrue(loc.getLocGroup() == newLocGroup);
		
	}
	
	@Test
	public void getDepartmentsTest() {
		
		assertTrue(loc.getDepartments() == departments);
		
	}
	
	@Test
	public void setDepartmentsTest() {
		
		departments.clear();
		departments.add(newDepartment);
		
		loc.setDepartments(departments);
		assertTrue(loc.getDepartments() == departments);
		
	}
	
	@Test
	public void addUserTest() {
		
		User user2 = new User();
		
		users.add(user2);
		loc.addUser(user2);
		
		assertTrue(loc.getUsers() == users);
		
	}
	
	@Test
	public void addUsersTest() {
		
		loc.addUsers(users);
		users.addAll(users);
		
		assertTrue(loc.getUsers() == users);
		
	}
	
	@Test
	public void removeUserTest() {
		
		users.remove(user);
		loc.removeUser(user);
		
		assertTrue(loc.getUsers() == users);
		
	}
	
	@Test
	public void getLocationNameTest() {
		
		assertTrue(loc.getLocationName() == locationName);
		
	}
	
	@Test
	public void setLocationNameTest() {
		
		locationName = "a different name";
		
		loc.setLocationName(locationName);
		
		assertTrue(loc.getLocationName() == locationName);
		
	}
	
	@Test
	public void getParentCityTest() {
		
		assertTrue(loc.getParentCity() == newCity);
		
	}
	
	@Test
	public void setParentCityTest() {
		
		newCity = new City();
		
		loc.setParentCity(newCity);
		assertTrue(loc.getParentCity() == newCity);
		
	}
	
	@Test
	public void getNumEmployeesTest() {
		
		assertTrue(loc.getNumEmployees() == 0);
		
	}
	
	@Test
	public void setNumEmployeesTest() {
		
		loc.setNumEmployees(3);
		
		assertTrue(loc.getNumEmployees() == 3);
		
	}
	
	@Test
	public void getIdTest() {
		
		assertTrue(loc.getId() != null);
		
	}
	
	@Test
	public void setIdTest() {
		
		long newId = 7;
		
		loc.setId(newId);
		
		assertTrue(loc.getId() == newId);
		
	}
	
	@Test
	public void getHomeCityTest() {
		
		loc.setParentCity(homeCity);
		
		assertTrue(loc.getHomeCity() == homeCity);
		
	}
	
	@Test
	public void setHomeCityTest() {
		
		City newCity = new City();
		
		loc.setParentCity(newCity);
		
		assertTrue(loc.getHomeCity() == newCity);
		
	}
	
	@Test
	public void getCompanyTest() {
		
		assertTrue(loc.getCompany() == co);
		
	}
	
	@Test
	public void setCompanyTest() {
		
		Company newCo = new Company();
		
		loc.setCompany(newCo);
		
		assertTrue(loc.getCompany() == newCo);

	}
	
	@Test
	public void getUsersTest() {
		
		assertTrue(loc.getUsers() == users);
		
	}
	
	@Test
	public void setUsersTest() {
		
		users.clear();
		
		loc.setUsers(users);
		
		assertTrue(loc.getUsers() == users);
		
		
		
	}

}
