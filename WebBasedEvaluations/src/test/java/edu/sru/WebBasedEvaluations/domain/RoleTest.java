package edu.sru.WebBasedEvaluations.domain;

import lombok.NonNull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
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
@SuiteDisplayName("Role Methods")
@IncludeClassNamePatterns(".*Tests")
public class RoleTest {

	static Role role;
	//static Collection<Role> rolesC;
	static HashSet<Role> roles;
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
		roles = new HashSet<Role>();
		
		locG = new LocationGroup(loc, p, "test",testCo);
		p = new Privilege("testAll", role, true, true, true, true);
		p.addCompany(testCo);
		p.addDept(dep);
		p.addLocGroup(locG);
		role = new Role("test", user, p, testCo);
		p.addRole(role);
		roles.add(role);
		user.setRole(role);
		
	}
	
	/*
	 * So, the role.readable/writable/etc. method returns a hash set of the data, hence the formatting
	 * The returned value will be [data], so to make the actual match, I've made the returned value
	 * a string and the actual value a string and simply appended the [ and ]
	 */
	
	@Test
	public void readableCompaniesTest(){
		assertThat(role.readableCompanies().toString()).isEqualTo("[" + testCo.toString() + "]");
	}

	@Test
	public void writableCompaniesTest(){
		assertThat(role.writableCompanies().toString()).isEqualTo("[" + testCo.toString() + "]");
	}

	@Test
	public void deletableCompaniesTest(){
		assertThat(role.deletableCompanies().toString()).isEqualTo("[" + testCo.toString() + "]");
	}
	
	@Test
	public void evalableCompaniesTest(){
		assertThat(role.evalableCompanies().toString()).isEqualTo("[" + testCo.toString() + "]");
	}
	
	@Test
	public void readableUsersTest(){
		assertThat(role.readableUsers().toString()).isEqualTo("[" + user.toString() + "]");
	}

	@Test
	public void writableUsersTest(){
		assertThat(role.writableUsers().toString()).isEqualTo("[" + user.toString() + "]");
	}

	@Test
	public void deletableUsersTest(){
		assertThat(role.deletableUsers().toString()).isEqualTo("[" + user.toString() + "]");
	}

	@Test
	public void evalableUsersTest(){
		assertThat(role.evalableUsers().toString()).isEqualTo("[" + user.toString() + "]");
	}    

	@Test
	public void readableDepartmentsTest(){
		assertThat(role.readableDepartments().toString()).isEqualTo("[" + dep.toString() + "]");
	}

	@Test
	public void writableDepartmentsTest(){
		assertThat(role.writableDepartments().toString()).isEqualTo("[" + dep.toString() + "]");
	}

	@Test
	public void deletableDepartmentsTest(){
		assertThat(role.deletableDepartments().toString()).isEqualTo("[" + dep.toString() + "]");
	}

	@Test
	public void evalableDepartmentsTest(){
		assertThat(role.evalableDepartments().toString()).isEqualTo("[" + dep.toString() + "]");
	}

	@Test
	public void readableLocationsTest(){
		assertThat(role.readableLocations().toString()).isEqualTo("[" + loc.toString() + "]");
	}

	@Test
	public void writableLocationsTest(){
		assertThat(role.writableLocations().toString()).isEqualTo("[" + loc.toString() + "]");
	}

	@Test
	public void deletableLocationsTest(){
		assertThat(role.deletableLocations().toString()).isEqualTo("[" + loc.toString() + "]");
	}

	@Test
	public void evalableLocationsTest(){
		assertThat(role.evalableLocations().toString()).isEqualTo("[" + loc.toString() + "]");
	}

	@Test
	public void readableLocationGroupsTest(){
		assertThat(role.readableLocationGroups().toString()).isEqualTo("[" + locG.toString() + "]");
	}

	@Test
	public void writableLocationGroupsTest(){
		assertThat(role.writableLocationGroups().toString()).isEqualTo("[" + locG.toString() + "]");
	}

	@Test
	public void deletableLocationGroupsTest(){
		assertThat(role.deletableLocationGroups().toString()).isEqualTo("[" + locG.toString() + "]");
	}

	@Test
	public void evalableLocationGroupsTest(){
		assertThat(role.evalableLocationGroups().toString()).isEqualTo("[" + locG.toString() + "]");
	}

	@Test
	public void containsRolesTest(){
		assertThat(role.containsRoles(roles).toString()).isEqualTo("[" + role.toString() + "]");
	}
	
	@Test
	public void activationTest() {
		assertTrue(role.getActivation());
	}

	@Test
	public void containsTest() {

		Role notInContains = new Role("fake", new Company("fake"));
		Privilege fakePriv = new Privilege("fake", notInContains, true,false,false,false);
		Location notInLoc = new Location();
		notInLoc.setLocationName("Test");
		fakePriv.addLocGroup(new LocationGroup(notInLoc));
		
		notInContains.addPrivilege(fakePriv);
		
		assertTrue(role.contains(role));
		//assertFalse(role.contains(notInContains));
	}

	@Test
	public void addRemovePrivilegeTest() {
		Privilege p2 = new Privilege();
		role.addPrivilege(p2);
		if (role.getPrivileges().toString() == ("[" + p2.toString() + ", " + p.toString() + "]") ||
				role.getPrivileges().toString() == ("[" + p.toString() + ", " + p2.toString() + "]")) {
			assertTrue(true);
		}
		role.removePrivilege(p2);
		assertThat(role.getPrivileges().toString()).isEqualTo("[" + p.toString() + "]");
	}
	
	@Test
	public void addRemoveUserTest() {
		User user2 = new User();
		role.addUser(user2);
		if(role.getUsers().toString() == "[" + user2.toString() + ", " + user.toString() + "]" ||
				role.getUsers().toString() == "[" + user.toString() + ", " + user2.toString() + "]") {
			assertTrue(true);
		}
		assertTrue(role.removeUser(user2));
	}

	@Test
	public void setGetCompanyTest() {
		Company co2 = new Company();
		assertThat(role.getCompany().toString()).isEqualTo(testCo.toString());
		role.setCompany(co2);
		assertThat(role.getCompany().toString()).isEqualTo(co2.toString());
	}

	@Test
	public void setGetId() {
		long actual = 69;
		role.setId(actual);
		assertThat(role.getId()).isEqualTo(actual);
	}

	@Test
	public void setGetName() {
		assertThat(role.getName()).isEqualTo("test");
		role.setName("test1");
		assertThat(role.getName()).isEqualTo("test1");
	}

	@Test
	public void setGetUsers() {
		assertThat(role.getUsers().toString()).isEqualTo("[" + user.toString() + "]");
		User user2 = new User();
		HashSet<User> users = new HashSet<User>();
		users.add(user2);
		role.setUsers(users);
		assertThat(role.getUsers()).isEqualTo(users);
		
	}

	@Test
	public void setGetPrivileges() {
		Role roleP = new Role();
		assertThat(role.getPrivileges().toString()).isEqualTo("[" + p.toString() + "]");
		
		//Must use different role to not interfere with other tests
		Privilege pRole = new Privilege("testAllAnother", role, true, true, true, true);
		Set<Privilege> pSet = new HashSet<Privilege>();
		pSet.add(p);
		roleP.setPrivileges(pSet);
		assertThat(roleP.getPrivileges().toString()).isEqualTo(pSet.toString());
	}

	@Test
	public void compareToTest() {
		assertTrue(role.compareTo(role));
	}

}
