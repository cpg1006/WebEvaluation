package edu.sru.WebBasedEvaluations.company;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;
import edu.sru.WebBasedEvaluations.domain.Privilege;

@Suite
@SpringBootTest
public class LocationGroupTest {
	
	static Location loc;
	static Privilege priv;
	static long id = 1;
	static Company co = new Company("test");
	
	static List<Privilege> privs;
	static List<Location> locs;
	
	static LocationGroup group;
	
	@BeforeAll
	static public void initialize() {
		
		loc = new Location();
		priv = new Privilege();
		
		privs = new ArrayList<Privilege>();
		privs.add(priv);
		
		locs = new ArrayList<Location>();
		locs.add(loc);
		
		group = new LocationGroup(locs, privs,"name",co);
		
		group.setId(id);
		group.setNumLocations(5);
		
	}
	
	@Test
	public void addPrivilegeTest() {
		
		Privilege newPriv = new Privilege();
		
		privs.add(newPriv);
		group.addPrivilege(newPriv);
		
		assertTrue(group.getPrivileges() == privs);
		
	}
	
	@Test
	public void removePrivilegeTest() {
		
		assertTrue(group.removePrivilege(priv));
		
	}
	
	@Test
	public void addLocationTest() {
		
		Location loc2 = new Location();
		
		locs.add(loc2);
		group.addLocation(loc2);
		
		assertTrue(group.getLocations() == locs);
		
	}
	
	@Test
	public void removeLocationTest() {
		
		assertTrue(group.removeLocation(loc));
		
	}
	
	
	public void addLocationsTest() {
		
		group.addLocations(locs);
		
		locs.addAll(locs);
		assertTrue(group.getLocations() == locs);
		
	}
	
	@Test
	public void getIdTest() {
		
		assertTrue(group.getId() != null);
		
	}
	
	@Test
	public void setIdTest() {
		
		long newId = 5;
		
		group.setId(newId);
		
		assertTrue(group.getId() == newId);
		
	}
	
	@Test
	public void getPrivilegesTest() {
		
		assertTrue(group.getPrivileges() == privs);
		
	}
	
	
	public void setPrivilegesTest() {
		
		privs.clear();
		
		group.setPrivileges(privs);
		
		assertTrue(group.getPrivileges() == privs);
		
	}
	
	@Test
	public void getLocationsTest() {
		
		assertTrue(group.getLocations() == locs);
		
	}
	
	@Test
	public void setLocationsTest() {
		
		locs.clear();
		
		group.setLocations(locs);
		
		assertTrue(group.getLocations() == locs);
		
	}
	
	@Test
	public void getNumLocationsTest() {
		
		assertTrue(group.getNumLocations() == 5);
		
	}
	
	@Test
	public void setNumLocationsTest() {
		
		int numThings = 12;
		
		group.setNumLocations(numThings);
		
		assertTrue(group.getNumLocations() == numThings);
		
	}

}
