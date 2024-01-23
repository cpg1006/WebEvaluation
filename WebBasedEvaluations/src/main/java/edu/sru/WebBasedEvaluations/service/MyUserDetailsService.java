package edu.sru.WebBasedEvaluations.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.RoleRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

/**Service for finding/loading particular users.
 * @author Dalton Stenzel
 *
 */
@Service("userDetailsService")
@Transactional
public class MyUserDetailsService implements UserDetailsService {

	/*
	 * This is broken, I have no idea what is wrong with this and using MyUserDetailsService
	 * and UserDetails
	 */
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
    private UserService service;
 
    @Autowired
    private MessageSource messages;

	@Autowired
	private RoleRepository roleRepository;

	/**Method for returning a user found with a specific email if the user exists.
	 *
	 */
	@Override
	public UserDetails loadUserByUsername(String email) 
			throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);


		//            //This gets the currently logged in user. 
		//            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		//            String userName = ((UserDetails)principal).getUsername();
		//            User currentUser = userRepository.findByEmail(userName);
		//            



		/*
            if(currentUser == null) {
            	throw new UsernameNotFoundException("finding pricipal did not work.");
            }
            else */if (user == null) {
            	throw new UsernameNotFoundException("Could not find user");
            }
            //            else if (currentUser.getCompany().getId() != user.getCompany().getId()) {
            //                throw new UsernameNotFoundException("That user does not exist in your organization.");
            //            }

            return new MyUserDetails(user);
	}



//	private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
//		
//		return getGrantedAuthorities(getPrivileges(roles));
//		
//	}
//	
//	
//
//	private List<String> getPrivileges(Collection<Role> roles) {
//
//		List<String> privileges = new ArrayList<>();
//		List<Privilege> collection = new ArrayList<>();
//		
//		for (Role role : roles) {
//			privileges.add(role.getName());
//			collection.addAll(role.getPrivileges());
//		}
//		
//		for (Privilege item : collection) {
//			privileges.add(item.getName());
//		}
//		
//		return privileges;
//		
//	}
//	
//	
//
//	private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
//		
//		List<GrantedAuthority> authorities = new ArrayList<>();
//		for (String privilege : privileges) {
//			authorities.add(new SimpleGrantedAuthority(privilege));
//		}
//		return authorities;
//		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}