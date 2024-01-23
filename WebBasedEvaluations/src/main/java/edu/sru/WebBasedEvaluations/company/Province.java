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


/**Class for methods of a Province object, almost exclusively made out of getters and setters.
 * also includes mapping for sql tables. 
 * @author David Gillette
 *
 */
@Entity
@Table(name = "province")
public class Province {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NonNull
	String provinceName;

	@NonNull
	private int numCities;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "country_id")
	private Country parentCountry;

	@NonNull
	@OneToMany(mappedBy = "parentProvince", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<City> childCities;


	/**
	 *  default constructor
	 */
	public Province() {
		
	}
	
	/**
	 * @param provinceName name of province
	 * @param parentCountry country province is located in. 
	 */
	public Province(String provinceName, Country parentCountry) {
		this.provinceName = provinceName;
		this.numCities = 0;
		this.parentCountry = parentCountry;
		this.childCities = new ArrayList<City>();;
	}
	
	
	/**
	 * @param provinceName name of province
	 * @param numCities number of cities in the province
	 * @param parentCountry country province is in
	 * @param cities cities in the province
	 */
	public Province(String provinceName, int numCities, Country parentCountry, List<City> cities) {
		this.provinceName = provinceName;
		this.numCities = numCities;
		this.parentCountry = parentCountry;
		this.childCities = cities;
	}

	/**
	 * @param provinceName namer of province
	 * @param numCities number of cities
	 * @param parentCountry country province is in
	 * @param city province has
	 */
	public Province(String provinceName, int numCities, Country parentCountry, City city) {
		this.provinceName = provinceName;
		this.numCities = numCities;
		this.parentCountry = parentCountry;
		this.childCities = new ArrayList<City>();
		this.childCities.add(city);
	}


	
	/**
	 * @param city to add
	 * @return true if added. 
	 */
	public boolean addCity(City city) {
		this.childCities.add(city);
		this.numCities++;
		return true;
	}


	
	/**
	 * @param cities to add
	 * @return true 
	 */
	public boolean addCities(List<City> cities) {
		
		this.childCities.addAll(cities);
		
		return true;
	}
	
	/*
	 * removes a city, should not be needed though. 
	 */
	/**
	 * @param id of city to remove
	 * @return true if removed. 
	 */
	public boolean removeCity(long id) {
		
		for(int i = 0; i < childCities.size(); i++) {
			if(childCities.get(i).getId() == id) {
				
				childCities.remove(i);
				return true;
				
			}
		}
		
		return false;
	}
	
	
	
	
	//getters and setters.

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public int getNumCities() {
		return numCities;
	}

	public void setNumCities(int numCities) {
		this.numCities = numCities;
	}

	public Country getParentCountry() {
		return parentCountry;
	}

	public void setParentCountry(Country parentCountry) {
		this.parentCountry = parentCountry;
	}

	public List<City> getChildCities() {
		return childCities;
	}

	public void setChildCities(List<City> childCities) {
		this.childCities = childCities;
	}
	
	
	
}
