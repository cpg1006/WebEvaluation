package edu.sru.WebBasedEvaluations.domain;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;

/**
 * Groups class creates a group for evaluation the id is used to id identify the
 * group Class variables to state if a group evaluation have stared or if self
 * evaluation are needed Class variables to determine what Evaluation template
 * the group is using has a list of reviewee and evaluators. and able to append
 * to the lists
 *
 */
@Entity
@Table(name = "Groupeval")
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int number;
	
	private String groupName;
	
	private int year;
	
	private boolean archived;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date deadline;

	@ManyToOne()
	@JoinColumn(name = "company_id")
	private Company company;
	
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "dept_groups", 
			joinColumns = @JoinColumn(name = "group_id"), 
			inverseJoinColumns = @JoinColumn(name = "dept_id"))
	private List<Department> departments = new ArrayList<>();
	
	private Boolean evalstart;
	private Boolean selfeval;
	
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
	private List<Reviewee> reviewees=new ArrayList<>();

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
	private List<Evaluator> evaluator = new ArrayList<>();
	@OneToOne
	private EvalTemplates evalTemplates;

	/**
	 * default Group constructor sets evalstart false
	 */
	public Group() {

		this.evalstart = false;
		this.archived=false;
	}
	
	public Group(Company co) {

		this.company = co;
		this.evalstart = false;
		this.archived=false;
	}

	/**
	 * Group constructor
	 * 
	 * @param id       is the number associated with the group
	 * @param selfeval determines if self evaluation are needed
	 */
	public Group(Boolean selfeval, Company co,int groupNum) {
		this.company = co;
		this.evalstart = false;
		this.selfeval = selfeval;
		this.number = groupNum;
		this.archived=false;
	}
	
//	/**
//	 * @return a unique set of all the depts in this group
//	 */
//	public HashSet<Department> getExisitingDepartments() {
//		HashSet<Department> depts = new HashSet<Department>();
//		
//		for(Reviewee rev : this.reviewees) {
//			depts.addAll(rev.getUser().getDepartments());
//		}
//		
//		return depts;
//	}
	
	
	
	
	
// Delete this? Is the exact same as setReviewee
	public void setGroup(List<Reviewee> reviewee) {
		this.reviewees = reviewee;
		for(Reviewee rev : reviewees) {
			for(Department dept : rev.getUser().getDepartments()) {
				if(!this.departments.contains(dept)) {
					this.departments.add(dept);
				}
			}
		}

	}


	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<Reviewee> getReviewees() {
		return reviewees;
	}

	public void setReviewees(List<Reviewee> revieweesList) {
		reviewees = revieweesList;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<User> getUsers(){
		HashSet<User> users = new HashSet<User>();
		
		for(Reviewee rev : reviewees) {
			users.add(rev.getUser());
		}
		
		return users;
	}
	
	//why duplicates?
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getGroupNumber() {
		return number;
	}

	public void setGroupNumber(int groupNumber) {
		this.number = groupNumber;
	}


	public String getGroupName() {

		return groupName;
	}

	public void setGroupName(String groupName) {
//		String[] name=groupName.split(" ");
		this.groupName = groupName;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Long getId() {
		return id;
	}

	public void setGroupNum(int groupNum) {
		this.number = groupNum;
	}

	public long getRevieweeId(int i) {
		return reviewees.get(i).getId();
	}
	
	public List<Reviewee> getReviewee() {
		return reviewees;
	}

	public void setReviewee(List<Reviewee> reviewee) {
		this.reviewees = reviewee;
	}

	public List<Evaluator> getEvaluator() {
		return evaluator;
	}

	/**
	 * appendReviewee append to the reviewee list
	 * 
	 * @param rev is the the new reviewee
	 */
	public void appendReviewee(Reviewee rev) {
		this.reviewees.add(rev);
		for(Department dept : rev.getUser().getDepartments()) {
			if(!this.departments.contains(dept)) {
				System.out.println(dept.getName());
				this.departments.add(dept);
			}
		}

	}

	public EvalTemplates getEvalTemplates() {
		return evalTemplates;
	}

	public void setEvalTemplates(EvalTemplates evalTemplates) {
		this.evalTemplates = evalTemplates;
	}

	public void setEvaluator(List<Evaluator> evaluator) {
		this.evaluator = evaluator;
	}

	public Boolean getEvalstart() {
		return evalstart;
	}

	public void setEvalstart(Boolean evalstart) {
		this.evalstart = evalstart;
	}

	public Boolean getSelfeval() {
		return selfeval;
	}

	public void setSelfeval(Boolean selfeval) {
		this.selfeval = selfeval;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public Date getDeadline() {
		return deadline;
	}
	
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	@Override
	public String toString() {
		return "Group{" +
				"id=" + id +
				", groupName='" + groupName + '\'' +
				", reviewees=" + reviewees +
				'}';
	}
}
