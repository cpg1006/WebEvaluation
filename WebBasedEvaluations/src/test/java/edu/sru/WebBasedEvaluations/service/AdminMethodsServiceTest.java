package edu.sru.WebBasedEvaluations.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import antlr.collections.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.controller.AddUserControllerTest;
import edu.sru.WebBasedEvaluations.controller.UserController;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.RoleRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;

//https://junit.org/junit5/docs/current/user-guide/#overview
@Suite
@SuiteDisplayName("Admin Methods")
@IncludeClassNamePatterns(".*Tests")
public class AdminMethodsServiceTest {
	private static User user = new User();
	private static User user2 = new User();
	private static User user3 = new User();
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	RoleRepository roleRepo;
	
	AdminMethodsService adminMeth = new AdminMethodsService(userRepo,roleRepo);

	@BeforeAll
	public static void newUser() {
		
		Company co  = new Company("Thangiah Inc");
		Role role = new Role("USER",co);
		// User missing some details (Job Title & Date of Hire)
		user.setFirstName("Sam");
		user.setLastName("Thangiah");
		user.setCompanyName("Thangiah Inc");
		user.setDivisionBranch("Retroville");
		user.setSupervisor(null);
		user.setEmail("sam.thangiah@sru.edu");
		user.setEncryptedPassword("test");
		
		// User with all valid information
		user2.setFirstName("Dalton");
		user2.setLastName("Stenzel");
		user2.setEmail("daltonrstenzel@gmail.com");
		user2.setEncryptedPassword("test");
		
		user2.setCompanyName("Thangiah Inc");
		user2.setDivisionBranch("Retroville");
		user2.setSupervisor("Brandon");
		user2.setDateOfHire("10/15/2022");
		user2.setJobTitle("Assistant");
		
		// User with all information, but has errors (email has space)
		user3.setFirstName("Dalton");
		user3.setLastName("Stenzel");
		user3.setEmail("daltonrstenzel @gmail.com");
		user3.setEncryptedPassword("test");
		
		user3.setCompanyName("Thangiah Inc");
		user3.setDivisionBranch("Retroville");
		user3.setSupervisor(null);
		user3.setDateOfHire("10/15/2022");
		user3.setJobTitle("Assistant");


	}
	
    @Test
    public void firstNameTest() {
        String name = "Sam";
        user.setLastName("Neutron");
        assertEquals(user.getFirstName(), name);

    }
    
    @Test
    public void adminMethoCapTest() {
    	String capVal = adminMeth.capitalize("test");
        String finalVal = "Test";
        assertEquals(capVal, finalVal);

    }
    
    @Test
    public void adminMethoSpaceTest() {
        assertTrue(adminMeth.hasSpace(user3.getEmail()));
        assertFalse(adminMeth.hasSpace(user2.getEmail()));

    }
    
    /* This method needs a session
    @Test
    public void adminMethoCheckTest() {
        assertFalse(adminMeth.checkAndUpdate(user));
        assertTrue(adminMeth.checkAndUpdate(user2));
        assertFalse(adminMeth.checkAndUpdate(user3));

    }

     */

}

