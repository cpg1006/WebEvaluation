package edu.sru.WebBasedEvaluations.domain;


import java.util.stream.Collectors;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;

/**
 * Domain class for obtaining the currently logged in user
 * @author Dalton
 *
 */
public class MyUserDetails implements UserDetails {
	private User user;
	private String userName;
	private String password;
	private Role role;
	private long Id;
	//private boolean active;
	private List<GrantedAuthority> authorities;
	private List<User> usersUnderAuth;
	private List<Department> deptsUnderAuth;
	private List<Location> locsUnderAuth;

	public MyUserDetails(User user) {
		this.Id = user.getId(); 
		this.userName = user.getEmail();
		this.password = user.getPassword();
		this.role = user.getRole();
		//	        this.companyID = user.getCompany().getId();

		//this.active = user.isActive();
//		System.out.println("this is the thing: " + user.getRole().getName().split(","));
		this.authorities = new ArrayList<GrantedAuthority>();
		
		ArrayList<String> roleNames = new ArrayList<String>();
		roleNames.add("USER");
		if((user.hasRead() || user.hasWrite() || user.hasDelete()) && user.hasEditEvalPerm()) {
			roleNames.add("EVAL_ADMIN");
		}		
		if(user.hasEvaluator()) {
			roleNames.add("EVALUATOR");
			roleNames.add("EVALUATOR_EVAL");
		}		
		if((user.hasRead() || user.hasWrite() || user.hasDelete())) {
			roleNames.add("ADMIN");
		}
		
		for(String roleName : roleNames) {
			authorities.add(new SimpleGrantedAuthority(roleName));
		}
		
		
//		this.authorities = Arrays.stream(user.getRoleName().split(","))
//				.map(SimpleGrantedAuthority::new)
//				.collect(Collectors.toList());
		this.usersUnderAuth = new ArrayList<User>();
		this.deptsUnderAuth = new ArrayList<Department>();
		this.locsUnderAuth = new ArrayList<Location>();
		//makes a list of the users that this user has privilege over. 
		//		for(Role role : roles) {
		for(Privilege priv : role.getPrivileges()) {

			//gets the depts this user has auth over. 
			this.deptsUnderAuth = priv.getDepts();
			for(Department dept : this.deptsUnderAuth) {
				for(User deptUser : dept.getUsers()) {
					this.usersUnderAuth.add(deptUser);
				}
			}
			for(LocationGroup locGrp : priv.getLocationGroups()) {

				//gets the locations this user has auth over
				this.locsUnderAuth = locGrp.getLocations();
				for(Location loc : this.locsUnderAuth) {
					for(User locUser : loc.getUsers()) {
						if(!this.usersUnderAuth.contains(locUser)) {
							this.usersUnderAuth.add(locUser);
						}
					}
				}
			}
		}
	}
	

	/**
	 *A method for obtaining authority based of relation from user's role
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	public List<User> getUsers(){
		return this.usersUnderAuth;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public List<User> getUsersUnderAuth() {
		return usersUnderAuth;
	}

	public void setUsersUnderAuth(List<User> usersUnderAuth) {
		this.usersUnderAuth = usersUnderAuth;
	}

	public List<Department> getDeptsUnderAuth() {
		return deptsUnderAuth;
	}

	public void setDeptsUnderAuth(List<Department> deptsUnderAuth) {
		this.deptsUnderAuth = deptsUnderAuth;
	}

	public List<Location> getLocsUnderAuth() {
		return locsUnderAuth;
	}

	public void setLocsUnderAuth(List<Location> locsUnderAuth) {
		this.locsUnderAuth = locsUnderAuth;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}
	public long getID() {

		return Id;
	}

//	public List<Role> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(List<Role> roles) {
//		this.roles = roles;
//	}
	

	public String getUsername() {
		// TODO Auto-generated method stub
		return userName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

}
