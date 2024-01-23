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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.lang.NonNull;





/**Class for methods of a City object, almost exclusively made out of getters and setters.
 * also includes mapping for sql tables. 
 * @author David Gillette
 *
 */
/**
 * @author david
 *
 */
@Entity
@Table(name = "city")
public class City {

	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NonNull
	String cityName;

	@lombok.NonNull
	private int numLocations;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "province_id")
	private Province parentProvince;

	
	@NonNull
	@OneToMany(mappedBy = "parentCity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Location> childLocations;

	
	/**
	 * default constructor
	 */
	public City() {
		
	}
	
	/**
	 * @param cityName name of the city
	 * @param parentProvince the province object that this city is located in. 
	 */
	public City(String cityName, Province parentProvince) {
		this.cityName = cityName;
		this.numLocations = 0;
		this.parentProvince = parentProvince;
		this.childLocations = new ArrayList<Location>();
	}
	
	
	/**
	 * @param cityName name of the city
	 * @param numLocations number of locations in this city
	 * @param parentProvince the province object that this city is located in. 
	 * @param locations list of locations to add. 
	 */
	public City(String cityName, int numLocations, Province parentProvince, List<Location> locations) {
		this.cityName = cityName;
		this.numLocations = numLocations;
		this.parentProvince = parentProvince;
		this.childLocations = locations;
	}

	/**
	 * @param cityName name of the city
	 * @param numLocations number of locations in this city
	 * @param parentProvince the province object that this city is located in. 
	 * @param location location to add to the server. 
	 */
	public City(String cityName, int numLocations, Province parentProvince, Location location) {
		this.cityName = cityName;
		this.numLocations = numLocations;
		this.parentProvince = parentProvince;
		this.childLocations = new ArrayList<Location>();
		this.childLocations.add(location);
	}

	/**
	 * adds a location
	 * @param loc location to add. 
	 * @return if location was successfully added. 
	 */
	public boolean addLocation(Location loc) {
		this.childLocations.add(loc);
		this.numLocations++;
		return true;
	}


	/*
	 * 
	 */
	/**
	 * adds a list of locations
	 * @param locs list of locations to add. 
	 * @return true if locations are added.
	 */
	public boolean addLocations(List<Location> locs) {
		for(Location loc : locs) { 
			this.childLocations.add(loc);
			this.numLocations++;
		}
		return true;
	}
	
	
	/**
	 *  removes a location
	 * @param loc location to remove
	 * @return true if location is removed. 
	 */
	public boolean removeLocation(Location loc) {
		if(this.childLocations.contains(loc)) {
			this.childLocations.remove(loc);
			this.numLocations--;
			return true;
		}
		return false;
	}

	
	//getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getNumLocations() {
		return numLocations;
	}

	public void setNumLocations(int numLocations) {
		this.numLocations = numLocations;
	}

	public Province getParentProvince() {
		return parentProvince;
	}

	public void setParentProvince(Province parentProvince) {
		this.parentProvince = parentProvince;
	}

	public List<Location> getChildLocations() {
		return childLocations;
	}

	public void setChildLocations(List<Location> childLocations) {
		this.childLocations = childLocations;
	}
	

	
	
	
	
}
