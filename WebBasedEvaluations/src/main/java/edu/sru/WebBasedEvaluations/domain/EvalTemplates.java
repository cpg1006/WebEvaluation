package edu.sru.WebBasedEvaluations.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;


/**
 * Class used to hold uploaded Evaluation Templates
 * 
 * @author Logan Racer, Tanuj Rane
 */
@Entity
public class EvalTemplates {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	long id;
	
	String name;
	
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "company", nullable=false)
    private Company company;
	
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(
			name = "EvalTemplates_depts", 
			joinColumns = @JoinColumn(name = "eval_template_id"), 
			inverseJoinColumns = @JoinColumn(name = "dept_id"))
	private Set<Department> depts = new HashSet<Department>();
	
	@Lob
	byte[] eval;
	@Lob
	byte[] excelFile;
	
	private boolean activated;
	
	// Constructors
	public EvalTemplates() {
		this.name = null;
		this.eval = null;
		this.excelFile = null;
		this.activated = true;
		
	}
	public EvalTemplates(Company company) {
		this.name = null;
		this.eval = null;
		this.excelFile = null;
		this.company = company;
		this.activated = true;
	}
	public EvalTemplates(String name, byte[] eval, byte[] excelFile, Company company) {
		this.name = name;
		this.eval = eval;
		this.excelFile = excelFile;
		this.company = company;
		this.activated = true;
	}
	public EvalTemplates(String name, byte[] eval, byte[] excelFile, Company company, Department dept) {
		this.name = name;
		this.eval = eval;
		this.excelFile = excelFile;
		this.company = company;
		this.depts.add(dept);
		this.activated = true;
	}
	public EvalTemplates(String name, byte[] eval, byte[] excelFile, Company company, Set<Department> dept) {
		this.name = name;
		this.eval = eval;
		this.excelFile = excelFile;
		this.company = company;
//		System.out.println("added the depts tothe eval template");
		this.depts.addAll(dept);
		this.activated = true;
	}
	
	public EvalTemplates(String name, byte[] eval, Company company) {
		this.name = name;
		this.eval = eval;
		this.excelFile = null;
		this.company = company;
		this.activated = true;
	}
	
	public boolean addDept(Department dept) {
		this.depts.add(dept);
		return true;
	}

	public boolean addDepts(Collection<Department> depts) {
		this.depts.addAll(depts);
		return true;
	}

	public boolean removeDept(Department dept) {

		if(this.depts.contains(dept)) {
			this.depts.remove(dept);			
			return true;
		}
		return false;
	}

	// Setters and Getters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Department> getDepts() {
		return depts;
	}
	public void setDepts(Set<Department> depts) {
		this.depts = depts;
	}
	public void setId(long id) {
		this.id = id;
	}
	public byte[] getEval() {
		return eval;
	}

	public void setEval(byte[] eval) {
		this.eval = eval;
	}
	
	public byte[] getExcelFile() {
		return this.excelFile;
	}

	public void setExcelFile(byte[] excelFile) {
		this.excelFile = excelFile;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public long getId() {
		return id;
	}
	public boolean isActivated() {
		return activated;
	}
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	
}
