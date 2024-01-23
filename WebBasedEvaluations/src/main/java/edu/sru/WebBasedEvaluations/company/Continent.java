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

/**Class for methods of a Continent object, almost exclusively made out of getters and setters.
 * also includes mapping for sql tables. 
 * @author David Gillette
 *
 */
@Entity
@Table(name = "continent")
public class Continent {

	
	
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NonNull
	String continentName;

	@NonNull
	private int numCountries;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "world_id", nullable=false)
	private World parentWorld;

	@NonNull
	@OneToMany(mappedBy = "parentContinent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Country> childCountries;
	
	
	
	/**
	 * default constructor
	 */
	public Continent() {
		
	}
	
	
	/**
	 * @param continentName name of the continent 
	 * @param parentWorld the parent world object
	 */
	public Continent(String continentName, World parentWorld) {
		this.continentName = continentName;
		this.numCountries = 0;
		this.parentWorld = parentWorld;
		this.childCountries = new ArrayList<Country>();;
	}
	
	/**
	 * @param continentName name of continent
	 * @param numCountries number of countries in the continent
	 * @param parentWorld world object that this continent is a child of
	 * @param childCountries countries in this continent. 
	 */
	public Continent(String continentName, int numCountries, World parentWorld, List<Country> childCountries) {
		this.continentName = continentName;
		this.numCountries = numCountries;
		this.parentWorld = parentWorld;
		this.childCountries = childCountries;
	}

	/**
	 * @param continentName name of continent
	 * @param numCountries number of countries in the continent
	 * @param parentWorld world object that this continent is a child of
	 * @param childCountry country in this continent. 
	 */
	public Continent(String continentName, int numCountries, World parentWorld, Country childCountry) {
		this.continentName = continentName;
		this.numCountries = numCountries;
		this.parentWorld = parentWorld;
		this.childCountries = new ArrayList<>();
		this.childCountries.add(childCountry);
	}
	
	
	/**
	 * adds a location
	 * @param childCountry country to add
	 * @return true if added. 
	 */
	public boolean addCountry(Country childCountry) {
		this.childCountries.add(childCountry);
		this.numCountries++;
		return true;
	}


	/**
	 * adds a list of locations
	 * @param childCountries to add
	 * @return true if added
	 */
	public boolean addCountry(List<Country> childCountries) {
		
		this.childCountries.addAll(childCountries);
		
		numCountries += childCountries.size();
		
		return true;
	}
	
	/*
	 * removes a country, should not be needed though. 
	 */
	/**
	 * @param id of country to remove
	 * @return true if removed. 
	 */
	public boolean removeCountry(Long id) {
		for(int i = 0; i < childCountries.size(); i++)
		{
			if(this.childCountries.get(i).getId() == id) {
				this.childCountries.remove(childCountries.get(i));
				this.numCountries--;
				return true;
			}
			
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

	public String getContinentName() {
		return continentName;
	}

	public void setContinentName(String continentName) {
		this.continentName = continentName;
	}

	public int getNumCountries() {
		return numCountries;
	}

	public void setNumCountries(int numCountries) {
		this.numCountries = numCountries;
	}

	public World getParentWorld() {
		return parentWorld;
	}

	public void setParentWorld(World parentWorld) {
		this.parentWorld = parentWorld;
	}

	public List<Country> getChildCountries() {
		return childCountries;
	}

	public void setChildCountries(List<Country> childCountries) {
		this.childCountries = childCountries;
	}
	
	
	
}
