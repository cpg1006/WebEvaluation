package edu.sru.WebBasedEvaluations.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.apache.tomcat.util.http.parser.MediaType;
//https://reflectoring.io/spring-boot-test/
//https://www.baeldung.com/spring-boot-testing
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.sru.WebBasedEvaluations.WebBasedEvaluationsApplication;
import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;

@ExtendWith(SpringExtension.class)
@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = WebBasedEvaluationsApplication.class)
@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class RepositoryIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository repo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private static Company co = new Company("testco");
    
    @Test
    public void givenUsers_whenGetUsers_thenUserExists() throws Exception {
    	//given
    	/*
    	String name = "test";
    	String firstName = "test";
    	String lastName = "test";
    	String email = "test";
    	String password = "test";
    	String role = "test";
    	String deptName = "test";
		int employeeId = 1;
		String dateOfHire = "test";
		String jobTitle = "test";
//		String supervisor = null;
		String divisionBranch = "test";
		Company co = new Company("test");
		Role role1 = new Role("Test",co);
		String roleName = "Test";
        User user = new User(name, firstName, lastName, email, password, employeeId, dateOfHire, jobTitle, null, divisionBranch, deptName, co, role1, false,false);
        
        user.setName("intTest");

        //when
        
        mvc.perform(post("/add_user").content(objectMapper.writeValueAsString(user))).andExpect(status().isFound());
        
        //then
        User actual = repo.findByName("test");
        assertThat(actual.getEmail()).isEqualTo("test");
        /*
        assertThat(repo.findByCompanyName("test")).isEqualTo(user);
        assertThat(repo.findByEmail(email)).isEqualTo(user);
        assertThat(repo.findByFirstName(firstName)).isEqualTo(user);
        assertThat(repo.findByid(employeeId)).isEqualTo(user);
        assertThat(repo.findByLastName(lastName)).isEqualTo(user);
        assertThat(repo.findByName(name)).isEqualTo(user);
        */
    }
}