package edu.sru.WebBasedEvaluations.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.lang.NonNull;

import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.User;


/**
 * this class represents a department in a company. It contains a list of users, locations and the associated objects. 
 * @author David Gillette
 *
 */
@Entity
@Table(name = "department")
public class Department {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Boolean activated = true;
	
	//department name
	private String name;
	
	@ManyToOne()
	@JoinColumn(name = "company_id")
	private Company company;

	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "deptHead_user_id")
	private User deptHead;
	
	//locations
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "dept_locations", 
			joinColumns = @JoinColumn(name = "dept_id"), 
			inverseJoinColumns = @JoinColumn(name = "location_id"))
	private List<Location> locations;
	
	
	
	
	@ManyToMany(mappedBy = "departments", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Group> groups = new ArrayList<>();
	
	
	@ManyToMany(mappedBy = "depts", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private List<EvalTemplates> evalTemplates;
	
	//users
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "dept_users", 
			joinColumns = @JoinColumn(name = "dept_id"), 
			inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> users;
	
	
	@ManyToMany(mappedBy = "depts", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Privilege> privileges;
	
	
	/**
	 * default constructor
	 */
	public Department() {
		users = new ArrayList<User>();
		locations = new ArrayList<Location>();
		this.name = "";
		this.activated = true;
	}
	
	/**
	 * @param co company object to associate with this dept
	 */
	public Department(Company co) {
		users = new ArrayList<User>();
		locations = new ArrayList<Location>();
		this.name = "";
		this.company = co;
		this.activated = true;
	}
	
	/**
	
	 */
	public Department(User user, Location loc, String name, Privilege priv, User deptHead, Company company) {
		users = new ArrayList<User>();
		users.add(user);
		locations = new ArrayList<Location>();
		this.privileges = new ArrayList<Privilege>();
		this.privileges.add(priv);
		locations.add(loc);
		this.name = name;
		this.deptHead = deptHead;
		this.company = company;
		this.activated = true;
	}
	
	/**
	 * @param users users to add
	 * @param locs locations to add
	 * @param name name of dept
	 * @param privs privileges associated with this dept
	 * @param company that this dept is in. 
	 */
	public Department(List<User> users, List<Location> locations, String name, List<Privilege> privs, Company company) {
		this.users = users;
		this.locations = locations;
		this.name = name;
		this.privileges = privs;
		this.company = company;
		this.activated = true;
	}
	
	
	
	
	
	
	
	/**
	 * @param priv privilege to add. 
	 */
	public void addPrivilege(Privilege priv) {
		this.privileges.add(priv);
	}
	
	/**
	 * @param priv to remove
	 * @return true if it is removed. 
	 */
	public boolean removePrivilege(Privilege priv) {
		if(this.privileges.contains(priv)) {
			this.privileges.remove(priv);
			return true;
		}
		return false;
	}
	
	/**
	 * @param user to add
	 * @return true if added. 
	 */
	public boolean addUser(User user) {
		this.users.add(user);
		return true;
	}
	
	
	
	/**
	 * @param users to add
	 * @return true if all users are added. 
	 */
	public boolean addUsers(List<User> users) {
		
		this.users.addAll(users);
			
		return true;
	}
	
	
	/**
	 * @param user to remove
	 * @return true if removed. 
	 */
	public boolean removeUser(User user) {
		if(this.users.contains(user)) {
			this.users.remove(user);
			return true;
		}
		return false;
	}	
	
	
	/**
	 * @param evalTemplate to add
	 * @return true if added
	 */
	public boolean addTemplate(EvalTemplates evalTemplate) {
		this.evalTemplates.add(evalTemplate);
		return true;
	}
	
	
	
	/**
	 * @param evalTemplates to add
	 * @return true if all are added. 
	 */
	public boolean addTemplates(List<EvalTemplates> evalTemplates) {
		
		this.evalTemplates.addAll(evalTemplates);
			
		return true;
	}
	
	/*
	 * removes a user from a location
	 */
	/**
	 * @param evalTemplate to remove
	 * @return true if removed. 
	 */
	public boolean removeTemplate(EvalTemplates evalTemplate) {
		if(this.evalTemplates.contains(evalTemplate)) {
			this.evalTemplates.remove(evalTemplate);
			return true;
		}
		return false;
	}
	
	/**
	 * @param loc location to add
	 */
	public void addLocation(Location loc) {
		this.locations.add(loc);
	}
	
	/**
	 * @param locations to add. 
	 */
	public void addLocations(List<Location> locations) {
		
		this.locations.addAll(locations);
		
	}
	
	/**
	 * @param loc location to remove
	 * @return true if removed
	 */
	public boolean removeLocation(Location loc) {
		if(this.locations.contains(loc)) {
			this.locations.remove(loc);
			return true;
		}
		return false;
	}
	
	
	//getters and setters
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<EvalTemplates> getEvalTemplates() {
		return evalTemplates;
	}

	public void setEvalTemplates(List<EvalTemplates> evalTemplates) {
		this.evalTemplates = evalTemplates;
	}
	
	public User getDeptHead() {
		return deptHead;
	}

	public void setDeptHead(User deptHead) {
		this.deptHead = deptHead;
	}

	public String getName() {
		return name;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	public void setActivation(Boolean activation) {
		this.activated = activation;
	}

	public Boolean getActivation() {
		return this.activated;
	}
	
}
