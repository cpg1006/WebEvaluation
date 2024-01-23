package edu.sru.WebBasedEvaluations.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.lang.NonNull;

import edu.sru.WebBasedEvaluations.company.Company;

/**
 * Eval roles is the roles being used in the evaluation 
 *
 */
@Entity
public class EvalRole {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private int level;
	
	private String name;
	
	@OneToMany(mappedBy = "level",
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private Set<Evaluator> evaluator= new HashSet<>();
	
	
	@NonNull
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "company_id", nullable=false)
	private Company company;
	
	
	public EvalRole() {
		
	}
	
	/**
	 * @param name is the name of the role 
	 * @param id the the level this role is at
	 */
	public EvalRole(String name, int level, Company co) {		
		this.level = level;
		this.name =name;	
		this.company = co;
	}
	
	
	
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<Evaluator> getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(Set<Evaluator> evaluator) {
		this.evaluator = evaluator;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}


}
