package edu.sru.WebBasedEvaluations.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/*
 * JUnit test class intended to test the functionality of SecurityConfiguration class
 * Includes multiple test cases depending on user authorities
 * 
 * Authored by Elliot McIlwain
 */

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@WithMockUser(username = "testuser", authorities = "USER")
	public void testUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
			   .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
			   .andReturn();
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	public void testAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/login")) // Change redirect if port traffic is changed
			   .andExpect(MockMvcResultMatchers.status().isOk())
			   .andReturn();
	}
}