
package edu.sru.WebBasedEvaluations.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.apache.tomcat.util.http.parser.MediaType;
import org.junit.jupiter.api.BeforeAll;
//https://reflectoring.io/spring-boot-test/
//https://www.baeldung.com/spring-boot-testing
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.sru.WebBasedEvaluations.WebBasedEvaluationsApplication;
import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.controller.AddUserController;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;


//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//@WebMvcTest(AddUserController.class)
//@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class ServiceIntegrationTest {
	
	/*
	 * This is broken, I have no idea what is wrong with this and using MyUserDetailsService
	 * and UserDetails
	 *
	
	
	//@Autowired
    //private WebApplicationContext context;
	
	//@Autowired
	private MockMvc mvc;

	@Autowired
	private UserRepository repo;

	@Autowired
	private ObjectMapper objectMapper;
	/*
	@BeforeAll
    public void setup() {
        mvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(SecurityMockMvcConfigurers.springSecurity())
          .build();
    }
	*
	*/
/*
	@Test
	@WithMockUser(value = "test")
	public void givenUsers_whenGetUsers_thenUserExists() throws Exception {
		// given
		/*
		String name = "test";
		String firstName = "test";
		String lastName = "test";
		String email = "test";
		String password = "test";
		int employeeId = 69420;
		String dateOfHire = "test";
		String jobTitle = "test";
		String supervisor = "test";
		String divisionBranch = "test";
		String deptName = "test";
		Company co = new Company("test");
		Role role = new Role();
		role.setName("test");
		boolean companySuperUser = true;
		boolean superUser = true;
		
		User user = new User(name, firstName, lastName, email, password, employeeId, dateOfHire, jobTitle,
				supervisor, divisionBranch, deptName, co, role, companySuperUser, superUser);
		

		
		
		// when

		mvc.perform(post("/adduser/").content(objectMapper.writeValueAsString(user))).andExpect(status().isFound());

		// then
		User actual = repo.findByName("intTest");
		assertThat(actual.getEmail()).isEqualTo("test");
		repo.deleteById((long) employeeId);
		/*
		 * assertThat(repo.findByCompanyName("test")).isEqualTo(user);
		 * assertThat(repo.findByEmail(email)).isEqualTo(user);
		 * assertThat(repo.findByFirstName(firstName)).isEqualTo(user);
		 * assertThat(repo.findByid(employeeId)).isEqualTo(user);
		 * assertThat(repo.findByLastName(lastName)).isEqualTo(user);
		 * assertThat(repo.findByName(name)).isEqualTo(user);
		 *
	}*/
}
