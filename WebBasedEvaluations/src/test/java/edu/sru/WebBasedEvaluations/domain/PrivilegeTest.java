package edu.sru.WebBasedEvaluations.domain;

import lombok.NonNull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;

@Suite
@SuiteDisplayName("Privilege Methods")
@IncludeClassNamePatterns(".*Tests")
public class PrivilegeTest {
	
	static Role role;
	static Privilege p;
	static Company testCo;
	static User user;
	static Department dep;
	static Location loc;
	static LocationGroup locG;
	@BeforeAll
	static void setup() {
		testCo = new Company("test");
		user = new User();
		user.setName("test");
		dep = new Department();
		dep.addUser(user);
		loc = new Location();
		
		locG = new LocationGroup(loc, p, "test",testCo);

		role = new Role("test", user, p, testCo);
		user.setRole(role);
		p = new Privilege("testAll", role, true, true, true, true);
		p.addCompany(testCo);
		p.addDept(dep);
		p.addLocGroup(locG);
	}

	@Test
    public void addRemoveCompanyTest() {
		assertThat(p.getCompanies().toString()).isEqualTo("[" + testCo.toString() + "]");
		Company otherCo = new Company("otherCo");
		p.addCompany(otherCo);
		if (p.getCompanies().toString() == ("[" + testCo.toString() + ", " + otherCo.toString() + "]") || 
				p.getCompanies().toString() == ("[" + otherCo.toString() + ", " + testCo.toString() + "]")){
					assertTrue(true);
				}
		assertTrue(p.removeCompany(testCo));
	}
    
	@Test
    public void addRemoveLocGroupTest() {
		assertThat(p.getLocationGroups().toString()).isEqualTo("[" + locG.toString() + "]");
		
		Location loc2 = new Location();
		LocationGroup otherLocG = new LocationGroup(loc2, p, "test",testCo);
		p.addLocGroup(otherLocG);
		
		if(p.getLocationGroups().toString() == "[" + locG.toString() + ", " + otherLocG.toString() + "]" || 
				p.getLocationGroups().toString() == "[" + otherLocG.toString() + ", " + locG.toString() + "]") {
			assertTrue(true);
		}
		assertTrue(p.removeLocGroup(otherLocG));
		}
    
	@Test
    public void addRemoveDeptTest() {
		assertThat(p.getDepts().toString()).isEqualTo("[" + dep.toString() + "]");
		
		Department dep2 = new Department();
		p.addDept(dep2);
		
		if(p.getDepts().toString() == "[" + dep.toString() + ", " + dep2.toString() + "]" || 
				p.getDepts().toString() == "[" + dep2.toString() + ", " + dep.toString() + "]") {
			assertTrue(true);
		}
		assertTrue(p.removeDept(dep2));
	}

	@Test
    public void addRemoveRoleTest() {
		assertThat(p.getRoles().toString()).isEqualTo("[" + role.toString() + "]");
		
		Role role2 = new Role();
		p.addRole(role2);
		
		if(p.getRoles().toString() == "[" + role.toString() + ", " + role2.toString() + "]" || 
				p.getDepts().toString() == "[" + role2.toString() + ", " + role.toString() + "]") {
			assertTrue(true);
		}
		assertTrue(p.removeRole(role2));
    }
    
	@Test
    public void getReadTest() {
	}
    
	@Test
	public void getSetEditEvaluatorTest() {
	}

	@Test
	public void getSetCompaniesTest() {
	}

	@Test
	public void isSetGetRTest() {
	}

	@Test
	public void isSetGetWTest() {
	}

	@Test
	public void isSetGetDTest() {
	}

	@Test
	public void getEditEvaluateTest() {
	}

	@Test
	public void setEvaluateTest() {
	}
	
	@Test
	public void getSetDeptsTest() {
	}
	
	@Test
	public void getSetLocationGroupsTest() {
	}
	
	@Test
	public void getSetIdTest() {
	}

	@Test
	public void getSetNameTest() {
	}

	@Test
	public void getSetRolesTest() {
	}
    
}
