package edu.sru.WebBasedEvaluations.domain;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;


/*
 * This class is adapted from https://www.baeldung.com/role-and-privilege-for-spring-security-registration.
 * This class includes a role and holds the users that have that role and the privileges associated with this role. 
 * @Author David Gillette
 */
@Entity
@Table(name = "role")
public class Role {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;


	@NonNull
	private String name;

	private Boolean activated = true;

	@OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<User> users;	

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "roles_privileges", 
			joinColumns = @JoinColumn(name = "role_id"), 
			inverseJoinColumns = @JoinColumn(name = "privilege_id"))
	private Set<Privilege> privileges;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", nullable=false)
	private Company company;

	/**
	 * default constructor
	 */
	public Role() {

	}

	/**
	 * @param name name of role
	 * @param co company the role is associated with  
	 */
	public Role(String name, Company co) {
		this.name = name;
		this.users = new HashSet<User>();
		this.privileges = new HashSet<Privilege>();
//		this.companies = new HashSet<Company>();
//		companies.add(co);
		this.company = co;
		this.activated = true;

	}



	/**
	 * @param name of role
	 * @param priv privilege in role
	 * @param co company role is associated with 
	 */
	public Role(String name,  Privilege priv, Company co) {
		this.name = name;
		this.privileges = new HashSet<Privilege>();
		this.privileges.add(priv);
		this.users = new HashSet<User>();
//		this.companies = new HashSet<Company>();
		this.company = co;
		this.activated = true;

	}

	/**
	 * @param name of role	
	 * @param user user role is assigned to
	 * @param priv privilege of user
	 * @param co company role is assigned
	 */
	public Role(String name, User user, Privilege priv, Company co) {
		this.name = name;
		this.privileges = new HashSet<Privilege>();
		this.privileges.add(priv);
		this.users = new HashSet<User>();
		this.users.add(user);
		if(this.name.contains("ADMIN")) {
			user.setCompanySuperUser(true);
		}
//		this.companies = new HashSet<Company>();
		this.company = co;
		this.activated = true;
	}

	/**
	 * @param name of role	
	 * @param users users role is assigned to
	 * @param privs privileges of user
	 * @param co company role is assigned
	 */
	public Role(String name, Set<User> users, Set<Privilege> privs, Company co) {
		this.name = name;
		this.privileges = privs;
		this.users = users;
		
		for(User us : users) {
			if(this.name.contains("ADMIN")) {
				us.setCompanySuperUser(true);
			}
		}
		
		
//		this.companies = new HashSet<Company>();
		this.company = co;
		this.activated = true;
	} 


	/**
	 * @return the set of companies the user has read access to. 
	 */
	public Set<Company> readableCompanies(){
		HashSet<Company> readableCos = new HashSet<Company>();

		for(Privilege priv : this.privileges) {
			if(priv.getRead()) {
				readableCos.addAll(priv.getCompanies());
			}
		}
		return readableCos;
	}
	
	/**
	 * @return set of companies the user has write access to. 
	 */
	public Set<Company> writableCompanies(){
		HashSet<Company> writeableCos = new HashSet<Company>();

		for(Privilege priv : this.privileges) {
			if(priv.getWrite()) {
				writeableCos.addAll(priv.getCompanies());
			}
		}
		return writeableCos;
	}

	/**
	 * @return set of companies the user has delete access to. 
	 */
	public Set<Company> deletableCompanies(){
		HashSet<Company> deletableCos = new HashSet<Company>();

		for(Privilege priv : this.privileges) {
			if(priv.getDelete()) {
				deletableCos.addAll(priv.getCompanies());
			}
		}
		return deletableCos;
	}
	
	/**
	 * @return set of companies the user has eval access to. 
	 */
	public Set<Company> evalableCompanies(){
		HashSet<Company> evalableCos = new HashSet<Company>();

		for(Privilege priv : this.privileges) {
			if(priv.getEditEvaluate()) {
				evalableCos.addAll(priv.getCompanies());
			}
		}
		return evalableCos;
	}
	
	/**
	 * @return set of users the user has read access to. 
	 */
	public Set<User> readableUsers(){
		HashSet<User> readableUsers = new HashSet<User>();

		for(Privilege priv : this.privileges) {
			if(priv.getRead()) {
				for(Department dept : priv.getDepts()) {
					readableUsers.addAll(dept.getUsers());
				}
			}
		}
		return readableUsers;
	}

	/**
	 * @return set of users the user has write access to. 
	 */
	public Set<User> writableUsers(){
		HashSet<User> writableUsers = new HashSet<User>();

		for(Privilege priv : this.privileges) {
			if(priv.getWrite()) {
				for(Department dept : priv.getDepts()) {
					writableUsers.addAll(dept.getUsers());
				}
			}
		}
		return writableUsers;
	}

	/**
	 * @return set of users the user has delete access to. 
	 */
	public Set<User> deletableUsers(){
		HashSet<User> deletableUsers = new HashSet<User>();

		for(Privilege priv : this.privileges) {
			if(priv.getDelete()) {
				for(Department dept : priv.getDepts()) {
					deletableUsers.addAll(dept.getUsers());
				}
			}
		}
		return deletableUsers;
	}

	/**
	 * @return set of users the user has eval access to. 
	 */
	public Set<User> evalableUsers(){
		HashSet<User> evalableUsers = new HashSet<User>();

		for(Privilege priv : this.privileges) {
			if(priv.getEditEvaluate()) {
				for(Department dept : priv.getDepts()) {
					evalableUsers.addAll(dept.getUsers());
				}
			}
		}
		return evalableUsers;
	}    

	/**
	 * @return set of depts the user has read access to. 
	 */
	public Set<Department> readableDepartments(){
		HashSet<Department> readableDepts = new HashSet<Department>();

		for(Privilege priv : this.privileges) {
			if(priv.getRead()) {
				readableDepts.addAll(priv.getDepts());
			}
		}
		return readableDepts;
	}

	/**
	 * @return set of depts the user has write access to. 
	 */
	public Set<Department> writableDepartments(){
		HashSet<Department> writableDepts = new HashSet<Department>();

		for(Privilege priv : this.privileges) {
			if(priv.getWrite()) {
				writableDepts.addAll(priv.getDepts());
			}
		}
		return writableDepts;
	}

	/**
	 * @return set of depts the user has delete access to. 
	 */
	public Set<Department> deletableDepartments(){
		HashSet<Department> deletableDepts = new HashSet<Department>();

		for(Privilege priv : this.privileges) {
			if(priv.getDelete()) {
				deletableDepts.addAll(priv.getDepts());
			}
		}
		return deletableDepts;
	}

	/**
	 * @return set of depts the user has eval access to. 
	 */
	public Set<Department> evalableDepartments(){
		HashSet<Department> evalableDepts = new HashSet<Department>();

		for(Privilege priv : this.privileges) {
			if(priv.getEditEvaluate()) {
				evalableDepts.addAll(priv.getDepts());
			}
		}
		return evalableDepts;
	}


	/**
	 * @return set of locs the user has read access to. 
	 */
	public Set<Location> readableLocations(){
		HashSet<Location> readableLocations = new HashSet<Location>();

		for(Privilege priv : this.privileges) {
			if(priv.getRead()) {
				for(LocationGroup locGroup : priv.getLocationGroups()) {
					readableLocations.addAll(locGroup.getLocations());
				}
			}
		}
		return readableLocations;
	}

	/**
	 * @return set of locs the user has write access to. 
	 */
	public Set<Location> writableLocations(){
		HashSet<Location> writableLocations = new HashSet<Location>();

		for(Privilege priv : this.privileges) {
			if(priv.getWrite()) {
				for(LocationGroup locGroup : priv.getLocationGroups()) {
					writableLocations.addAll(locGroup.getLocations());
				}
			}
		}
		return writableLocations;
	}

	/**
	 * @return set of locs the user has delete access to. 
	 */
	public Set<Location> deletableLocations(){
		HashSet<Location> deletableLocations = new HashSet<Location>();

		for(Privilege priv : this.privileges) {
			if(priv.getDelete()) {
				for(LocationGroup locGroup : priv.getLocationGroups()) {
					deletableLocations.addAll(locGroup.getLocations());
				}
			}
		}
		return deletableLocations;
	}

	/**
	 * @return set of locs the user has eval access to. 
	 */
	public Set<Location> evalableLocations(){
		HashSet<Location> evalableLocations = new HashSet<Location>();

		for(Privilege priv : this.privileges) {
			if(priv.getEditEvaluate()) {
				for(LocationGroup locGroup : priv.getLocationGroups()) {
					evalableLocations.addAll(locGroup.getLocations());
				}
			}
		}
		return evalableLocations;
	}

	/**
	 * @return set of locGroups the user has read access to. 
	 */
	public Set<LocationGroup> readableLocationGroups(){
		HashSet<LocationGroup> readableLocGroups = new HashSet<LocationGroup>();

		for(Privilege priv : this.privileges) {
			if(priv.getRead()) {
				readableLocGroups.addAll(priv.getLocationGroups());
			}
		}
		return readableLocGroups;
	}

	/**
	 * @return set of locGroups the user has write access to. 
	 */
	public Set<LocationGroup> writableLocationGroups(){
		HashSet<LocationGroup> writableLocGroups = new HashSet<LocationGroup>();

		for(Privilege priv : this.privileges) {
			if(priv.getWrite()) {
				writableLocGroups.addAll(priv.getLocationGroups());
			}
		}
		return writableLocGroups;
	}

	/**
	 * @return set of locGroups the user has Delete access to. 
	 */
	public Set<LocationGroup> deletableLocationGroups(){
		HashSet<LocationGroup> deletableLocGroups = new HashSet<LocationGroup>();

		for(Privilege priv : this.privileges) {
			if(priv.getDelete()) {
				deletableLocGroups.addAll(priv.getLocationGroups());
			}
		}
		return deletableLocGroups;
	}

	/**
	 * @return set of locGroups the user has eval access to. 
	 */
	public Set<LocationGroup> evalableLocationGroups(){
		HashSet<LocationGroup> evalableLocGroups = new HashSet<LocationGroup>();

		for(Privilege priv : this.privileges) {
			if(priv.getEditEvaluate()) {
				evalableLocGroups.addAll(priv.getLocationGroups());
			}
		}
		return evalableLocGroups;
	}

	
	

	
	/**
	 * @param roles set of roles to be tested
	 * @return the set of roles in the passed in roles that have privilege below this role. 
	 */
	public Set<Role> containsRoles(Collection<Role> roles){
		HashSet<Role> containsRoles = new HashSet<Role>();
		for(Role role : roles) {
			if(this.contains(role)) {
				containsRoles.add(role);
			}			
		}
		return containsRoles;
	}

	
	/**
	 * this method checks to see if the role being passed in is contained in the current role. 
	 * Essentially checking if this role has higher privilege than the role being passed in.
	 * 
	 * @param role The role that is being checked for privilege level (is it lower than this role)
	 * @return whether the role passed in is a subrole of this.
	 */
	public boolean contains(Role role) {

		if(!this.privileges.containsAll(role.getPrivileges())) {
			return true;
		}
		if(!this.readableDepartments().containsAll(role.readableDepartments())) {
			return false;
		}
		if(!this.writableDepartments().containsAll(role.writableDepartments())) {
			return false;
		}
		if(!this.deletableDepartments().containsAll(role.deletableDepartments())) {
			return false;
		}
		if(!this.evalableDepartments().containsAll(role.evalableDepartments())) {
			return false;
		}			
		if(!this.readableLocationGroups().containsAll(role.readableLocationGroups())) {
			return false;
		}
		if(!this.writableLocationGroups().containsAll(role.writableLocationGroups())) {
			return false;
		}
		if(!this.deletableLocationGroups().containsAll(role.deletableLocationGroups())) {
			return false;
		}
		if(!this.evalableLocationGroups().containsAll(role.evalableLocationGroups())) {
			return false;
		}
		if(!this.readableLocations().containsAll(role.readableLocations())) {
			return false;
		}
		if(!this.writableLocations().containsAll(role.writableLocations())) {
			return false;
		}
		if(!this.deletableLocations().containsAll(role.deletableLocations())) {
			return false;
		}
		if(!this.evalableLocations().containsAll(role.evalableLocations())) {
			return false;
		}
		if(!this.readableUsers().containsAll(role.readableUsers())) {
			return false;
		}
		if(!this.writableUsers().containsAll(role.writableUsers())) {
			return false;
		}
		if(!this.deletableUsers().containsAll(role.deletableUsers())) {
			return false;
		}
		if(!this.evalableUsers().containsAll(role.evalableUsers())) {
			return false;
		}		
		if(!this.readableCompanies().containsAll(role.readableCompanies())) {
			return false;
		}
		if(!this.writableCompanies().containsAll(role.writableCompanies())) {
			return false;
		}
		if(!this.deletableCompanies().containsAll(role.deletableCompanies())) {
			return false;
		}
		if(!this.evalableCompanies().containsAll(role.evalableCompanies())) {
			return false;
		}
		return true;
	}

	
	
	/**
	 * @param p privilege to be added
	 * @return true if added. 
	 */
	public boolean addPrivilege(Privilege p) {
		this.privileges.add(p);
		return true;
	}

	/**
	 * @param p privilege to be removed
	 * @return true if removed
	 */
	public boolean removePrivilege(Privilege p) {
		if(this.privileges.contains(p)) {
			this.privileges.remove(p);
			return true;
		}
		return false;

	}

	

	/**
	 * @param u user to be added
	 * @return true if added
	 */
	public boolean addUser(User u) {
		this.users.add(u);
		if(this.name.contains("ADMIN")) {
			u.setCompanySuperUser(true);
		}
		return true;
	}

	/**
	 * @param u user to be removed
	 * @return true if removed.
	 */
	public boolean removeUser(User u) {
		if(this.users.contains(u)) {
			this.users.remove(u);
			return true;
		}
		return false;

	}


//	public boolean addCompany(Company co) {
//		this.companies.add(co);
//		return true;
//	}
//
//	public boolean removeCompany(Company co) {
//		if(this.companies.contains(co)) {
//			this.companies.remove(co);
//			return true;
//		}
//		return false;
//
//	}
	//getters and setters. 


	public Company getCompany() {
		return company;
	}

	public void setCompany(Company companies) {
		this.company = companies;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
		for(User user : users) {
			if(this.name.contains("ADMIN")) {
				user.setCompanySuperUser(true);
			}
		}
	}

	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}
	
	
	public String getNameAndCompany() {
		String outString = this.getName() + ", " + this.getCompany().getCompanyName();
		return outString;
	}


	/**
	 * @param role to be compared
	 * @return if the role passed in is the same as this.
	 */
	public boolean compareTo(Role role) {
		return (this.id ==role.id && this.name == role.name && this.privileges.equals(role.privileges) && this.users.equals(role.users));
	}

	public void setActivation(Boolean activation) {
		this.activated = activation;
	}

	public Boolean getActivation() {
		return this.activated;
	}


}
