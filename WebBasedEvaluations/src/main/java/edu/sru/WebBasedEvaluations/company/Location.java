package edu.sru.WebBasedEvaluations.company;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import org.springframework.lang.NonNull;

import edu.sru.WebBasedEvaluations.domain.User;

/**Class for methods of a location object, almost exclusively made out of getters and setters.
 * also includes mapping for sql tables. 
 * @author David Gillette
 *
 */
/**
 * @author david
 *
 */
@Entity
@Table(name = "location")
public class Location {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private Boolean activated;
	
	@NonNull
	private String locationName;
	
	
	
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id")
	private Company company;

	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "location_group_id")
	private LocationGroup locGroup;

	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "city_id")
	private City parentCity;
	
	
	@NonNull
	private int numEmployees;
	
	
	
	@ManyToMany(mappedBy = "locations", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private List<Department> departments;
	
	
	 
	@ManyToMany(mappedBy = "locations", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private List<User> users;

	
	/**
	 *  DEFAULT CONSTRUCTOR
	 */
	public Location() {
		this.activated = true;
	}
	
	/**
	 * @param locationName name of the location
	 * @param parentCity city locatoin is in. 
	 * @param co company the company is a part of
	 * @param locGroup locationgroup this location is in. 
	 */
	public Location(String locationName, City parentCity, Company co, LocationGroup locGroup) {
		this.numEmployees = 0;
		this.parentCity = parentCity;
		this.locationName = locationName;
		this.users = new ArrayList<User>();
		this.departments = new ArrayList<Department>();
		this.company = co;
		this.locGroup = locGroup;
		this.activated = true;
	}
	
	/**
	 * @param parentCity city locatoin is in. 
	 * @param numEmployees number of employees in the location
	 * @param users users to add to location
	 * @param locationName name of the location
	 * @param co company the company is a part of
	 * @param locGroup locationgroup this location is in. 
	 * @param dept that is a part of this location
	 */
	public Location(City parentCity, int numEmployees, List<User> users, String locationName, Company co, LocationGroup locGroup, Department dept) {
		this.numEmployees = numEmployees;
		this.parentCity = parentCity;
		this.locationName = locationName;
		this.users = users;
		this.company = co;
		this.locGroup = locGroup;
		this.departments = new ArrayList<Department>();
		this.departments.add(dept);
		this.activated = true;
	}
	
	/**
	 * @param parentCity city locatoin is in. 
	 * @param numEmployees number of employees in the location
	 * @param user to add to the locatoin
	 * @param locationName name of the location
	 * @param co company the company is a part of
	 * @param locGroup locationgroup this location is in. 
	 * @param depts that are a part of this location
	 */
	public Location(City parentCity, int numEmployees, User user, String locationName, Company co, LocationGroup locGroup, List<Department> depts) {
		this.numEmployees = numEmployees;
		this.parentCity = parentCity;
		this.locationName = locationName;
		this.users = new ArrayList<User>();
		this.addUser(user);
		this.company = co;
		this.locGroup = locGroup;
		this.departments = depts;
		this.activated = true;
	}
		
	
	/**
	 * @param dept to add
	 * @return true if added
	 */
	public boolean addDept(Department dept) {
		this.departments.add(dept);
		return true;
	}
	
	
	/*
	 * removes a dept from a location
	 */
	/**
	 * @param id id of the dept to remove
	 * @return true if dept is removed. 
	 */
	public boolean removeDept(long id) {
		
		for (int i = 0; i < departments.size(); i++)
		{
			if (departments.get(i).getId() == id);
			{
				departments.remove(i);
				return true;
			}
		}
		
		return false;
	}	
	
	
	



	/**
	 * @param user to add
	 * @return true if added. 
	 */
	public boolean addUser(User user) {
		this.users.add(user);
		this.numEmployees++;
		return true;
	}
	
	
	
	/**
	 * @param users users to add
	 * @return true if added
	 */
	public boolean addUsers(List<User> users) {
		
		this.users.addAll(users);	
		return true;
	}
	
	
	/**
	 * @param user to remove
	 * @return true if removed
	 */
	public boolean removeUser(User user) {
		if(this.users.contains(user)) {
			this.users.remove(user);
			this.numEmployees--;
			return true;
		}
		return false;
	}	
	
	
	
	//standard getters and setters 
	
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public City getParentCity() {
		return parentCity;
	}

	public void setParentCity(City parentCity) {
		this.parentCity = parentCity;
	}
	
	public int getNumEmployees() {
		return numEmployees;
	}


	public void setNumEmployees(int numEmployees) {
		this.numEmployees = numEmployees;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public City getHomeCity() {
		return parentCity;
	}



	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public LocationGroup getLocGroup() {
		return locGroup;
	}

	public void setLocGroup(LocationGroup locGroup) {
		this.locGroup = locGroup;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}
//	public List<User> getUsers() {
//		return users;
//	}
//
//
//	public void setUsers(List<User> users) {
//		this.users = users;
//	}
	
	public void setActivation(Boolean activation) {
		this.activated = activation;
	}

	public Boolean getActivation() {
		return this.activated;
	}
	
	
}
