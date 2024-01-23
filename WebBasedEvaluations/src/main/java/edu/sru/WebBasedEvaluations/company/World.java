package edu.sru.WebBasedEvaluations.company;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.lang.NonNull;


/**Class for methods of a World object, almost exclusively made out of getters and setters.
 * also includes mapping for sql tables. 
 * @author David Gillette
 *
 */
@Entity
@Table(name = "world")
public class World {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NonNull
	private String name;
	
	@NonNull
	private int numContinents;
	
	@NonNull
	@OneToMany(mappedBy = "parentWorld", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Continent> childContinents;
	
	
	
	
	/**
	 * default constructor
	 */
	public World() {
		this.numContinents = 0;
		this.childContinents = new ArrayList<Continent>();
		this.name ="";
		
	}
	
	/**
	 * @param name name of world
	 */
	public World(String name) {
		this.numContinents = 0;
		this.childContinents = new ArrayList<Continent>();
		this.name = name;
		
	}
	
	/**
	 * @param numContinents number of continents in the world
	 * @param childContinents continents in this world 
	 */
	public World(int numContinents, List<Continent> childContinents, String name) {
		this.numContinents = numContinents;
		this.childContinents = childContinents;
		this.name = name;
	}

	/**
	 * @param numContinents number of continents in the world
	 * @param childContinents continents in the world.
	 */
	public World(int numContinents, Continent childContinents, String name) {
		this.numContinents = numContinents;
		this.childContinents = new ArrayList<Continent>();
		this.childContinents.add(childContinents);
		this.name=name;
	}

	
	/**
	 * @param childContinent to add
	 * @return true if added. 
	 */
	public boolean addContinent(Continent childContinent) {
		this.childContinents.add(childContinent);
		this.numContinents++;
		return true;
	}


	
	/**
	 * @param childContinents to add
	 * @return true if added
	 */
	public boolean addContinent(List<Continent> childContinents) {
		
		this.childContinents.addAll(childContinents);
		
		return true;
	}
	
	/*
	 * removes a continent, should not be needed though. 
	 */
	/**
	 * @param id of continent to remove
	 * @return true if added. 
	 */
	public boolean removeContinent(long id) {
		
		for(int i = 0; i < childContinents.size(); i++)
		{
			
			if (childContinents.get(i).getId() == id)
			{
				childContinents.remove(i);
				return true;
			}
			
		}
		
		return false;
	}
	
	
	
	
	//makes getters and setters. 

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getNumContinents() {
		return numContinents;
	}

	public void setNumContinents(int numContinents) {
		this.numContinents = numContinents;
	}

	public List<Continent> getChildContinents() {
		return childContinents;
	}

	public void setChildContinents(List<Continent> childContinents) {
		this.childContinents = childContinents;
	}



}
