package edu.sru.WebBasedEvaluations.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/*
 * JUnit test class intended to test the functionality of SecurityConfiguration class
 * 
 * Authored by Elliot McIlwain
 */

@SpringBootTest
@WebMvcTest
@EnableWebSecurity
public class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@WithMockUser(username = "testuser", authorities = "USER")
	public void testUnauthenticated() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
			   .andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	@WithMockUser(username = "admin", authorities = "ADMIN")
	public void testAdmin() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/h2-console"))
			   .andExpect(MockMvcResultMatchers.status().isOk());
	}
}