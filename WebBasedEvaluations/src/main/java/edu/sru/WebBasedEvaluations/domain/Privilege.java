package edu.sru.WebBasedEvaluations.domain;

import lombok.NonNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;



/*
 * This class is adapted from https://www.baeldung.com/role-and-privilege-for-spring-security-registration
 * @Author David Gillette
 * This class holds the specific privileges that can be given to a role. they include read, write, delete (RWD) and 
 * what group they have this power over. 
 * 
 * contains a lists of departments and location groups that this privilege has the given RWD over. 
 */
@Entity
@Table(name = "privilege")
public class Privilege {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
		
	//read
	@NonNull
	private boolean r;
		
	//write
	@NonNull
	private boolean w;
		
	//delete
	@NonNull
	private boolean d;
	
	@NonNull
	private boolean editEvaluator;

	
	
	
	@NonNull
    private String name;
	
	@ManyToMany(mappedBy = "privileges")
    private List<Role> roles;   
    
    
    @ManyToMany
	@JoinTable(
			name = "privilege_dept", 
			joinColumns = @JoinColumn(name = "privilege_id"), 
			inverseJoinColumns = @JoinColumn(name = "dept_id"))
    private List<Department> depts;
    
    
    @ManyToMany(mappedBy = "privileges", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LocationGroup> locationGroups;
    
    
    //list of companies the privilege has access to. 
    @ManyToMany
   	@JoinTable(
		name = "privilege_companies", 
		joinColumns = @JoinColumn(name = "privilege_id"), 
		inverseJoinColumns = @JoinColumn(name = "company_id"))
   private Set<Company> companies;
   
   private HashSet<User> users;			//HashSet to store unique users
   private HashSet<Location> locations;	//HasSet to store unique locations
   
   
    public Privilege() {
    	
    }
    
    
    public Privilege(String name, Role role, boolean read, boolean write, boolean delete, boolean evaluate) {
    	this.name = name;
    	this.roles = new ArrayList<Role>();
    	this.roles.add(role);
    	this.depts = new ArrayList<Department>();
    	this.locationGroups = new ArrayList<LocationGroup>(); 
    	this.companies = new HashSet<Company>();
    	this.r = read;
    	this.w = write;
    	this.d = delete;
    	this.editEvaluator = evaluate;
    }
    
    
	public Privilege(String name, Role role, LocationGroup locGroup, Department dept, Company co, boolean read, boolean write, boolean delete, boolean evaluate) {
    	this.name = name;
    	this.roles = new ArrayList<Role>();
    	this.roles.add(role);
    	this.depts = new ArrayList<Department>();
    	this.locationGroups = new ArrayList<LocationGroup>();
    	this.companies = new HashSet<Company>();
    	companies.add(co);
    	this.depts.add(dept);
    	this.locationGroups.add(locGroup);
    	this.r = read;
    	this.w = write;
    	this.d = delete;
    	this.editEvaluator = evaluate;
    }
	
	
    public Privilege(String name, List<Role> roles, List<LocationGroup> locGroups, List<Department> depts,Set<Company> cos, boolean read,boolean write,boolean delete, boolean evaluate) {
    	this.name = name;
    	this.roles = roles;
    	this.locationGroups = locGroups;
    	this.depts = depts;
    	this.r = read;
    	this.w = write;
    	this.d = delete;
    	this.editEvaluator = evaluate;
    	this.companies = cos;
    }
    
    /*
     * Attempt to allow for the use of collections of users and locations for privileges.
     * 
     * Rather than removing previous functionality, it was recommended to add new functionality
     * in addition to the prior methods.
     */
    
    public Privilege(String name, List<User> users, List<Location> locations, Set<Company> cos, boolean read, boolean write, boolean delete, boolean evaluate) {
    	this.name = name;
    	this.users = new HashSet<User>();
    	this.users.addAll(users);
    	this.locations = new HashSet<Location>();
    	this.locations.addAll(locations);
    	this.companies = cos;
    	this.r = read;
    	this.w = write;
    	this.d = delete;
    	this.editEvaluator = evaluate;
    	
    }
    
    /**
     * @param loc the loc we are searching for 
     * @return true if the location is found in any location group
     */
    public boolean containsLoc(Location loc){
    	for(LocationGroup locGroup : this.locationGroups) {
    		if(locGroup.getLocations().contains(loc)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * @return the set of locations contained in the location groups. 
     */
    public Set<Location> getLocations(){
    	
    	HashSet<Location> locs = new HashSet<>();
    	for(LocationGroup locGroup : this.locationGroups) {
    		locs.addAll(locGroup.getLocations());
    	}
    	return locs;
    }
    
    
    public void addCompany(Company co) {
		this.companies.add(co);
	}
	
	public boolean removeCompany(Company co) {
		if(this.companies.contains(co)) {
			this.companies.remove(co);
			return true;
		}
		return false;
	}
    
    
    
    public void addLocGroup(LocationGroup locGroup) {
		this.locationGroups.add(locGroup);
	}
	
	public boolean removeLocGroup(LocationGroup locGroup) {
		if(this.locationGroups.contains(locGroup)) {
			this.locationGroups.remove(locGroup);
			return true;
		}
		return false;
	}
    
    
    public void addDept(Department dept) {
		this.depts.add(dept);
	}
	
	public boolean removeDept(Department dept) {
		if(this.depts.contains(dept)) {
			this.depts.remove(dept);
			return true;
		}
		return false;
	}
    
    public boolean addRole(Role role) {
    	this.roles.add(role);
    	return true;
    }
    
    public boolean removeRole(Role role) {
    	if(this.roles.contains(role)) {
    		this.roles.remove(role);
    		return true;
    	}
    	return false;
    }
    
    
    //getters and setters.
    
    public boolean getRead() {
		return r;
	}
    
	public boolean getEditEvaluator() {
		return editEvaluator;
	}


	public void setEditEvaluator(boolean editEvaluator) {
		this.editEvaluator = editEvaluator;
	}


	public Set<Company> getCompanies() {
		return companies;
	}


	public void setCompanies(Set<Company> companies) {
		this.companies = companies;
	}


	public boolean isR() {
		return r;
	}


	public void setR(boolean r) {
		this.r = r;
	}


	public boolean isW() {
		return w;
	}


	public void setW(boolean w) {
		this.w = w;
	}


	public boolean isD() {
		return d;
	}


	public void setD(boolean d) {
		this.d = d;
	}


	public boolean getEditEvaluate() {
		return editEvaluator;
	}


	public void setEvaluate(boolean evaluate) {
		this.editEvaluator = evaluate;
	}


	public void setRead(boolean read) {
		this.r = read;
	}
	public boolean getWrite() {
		return w;
	}
	public void setWrite(boolean write) {
		this.w = write;
	}
	public boolean getDelete() {
		return d;
	}
	public void setDelete(boolean delete) {
		this.d = delete;
	}
	public List<Department> getDepts() {
		return depts;
	}
	public void setDepts(List<Department> depts) {
		this.depts = depts;
	}
	public List<LocationGroup> getLocationGroups() {
		return locationGroups;
	}
	public void setLocationGroups(List<LocationGroup> locationGroups) {
		this.locationGroups = locationGroups;
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

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
    
    public HashSet<User> getUsers() {
    	return users;
    }
   
    public void setUsers(HashSet<User> users) {
    	this.users = users;
    }
    
    public void addUser(User user) {
    	this.users.add(user);
    }
    
    public void addLocation(Location location) {
    	this.locations.add(location);
    }
    
    
    
    
	
    
}
