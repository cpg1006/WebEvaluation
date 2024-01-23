package edu.sru.WebBasedEvaluations.integration;
/*package edu.sru.group3.WebBasedEvaluations.integration;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import edu.sru.group3.WebBasedEvaluations.company.Company;
import edu.sru.group3.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.group3.WebBasedEvaluations.domain.User;
import edu.sru.group3.WebBasedEvaluations.repository.UserRepository;

public class SecurityConfigIntegrationTest {

	@Configuration
	public class WebSecurityConfigurer {
		
		@Bean
		public InMemoryUserDetailsManager userDetailsService() {
			String name = "test";
			String firstName = "test";
			String lastName = "test";
			String email = "test";
			String password = "test";
			String role = "Admin";
			long employeeId = 69420;
			String dateOfHire = "test";
			String jobTitle = "test";
			String supervisor = "test";
			String divisionBranch = "test";
			Company co = new Company("test");
			UserDetails user;
			User newUser = new User(name, firstName, lastName, email, password, role, 
					employeeId, dateOfHire, jobTitle, supervisor, divisionBranch, co);
			user = (UserDetails) newUser;
			return new InMemoryUserDetailsManager(user);
		}
		
		@Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.authorizeRequests()
	            .antMatchers("/**")
	            .permitAll()
	            .and()
	            .httpBasic();
	        return http.build();
	    }
	}
}
*/
