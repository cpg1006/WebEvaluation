package edu.sru.WebBasedEvaluations.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCrypt;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;

/**Class for methods of a user object, almost exclusively made out of getters and setters.
 * @author Tanuj Rane, Dalton Stenzel, Logan Racer, David Gillette
 *
 */
@Entity
@Table(name = "User")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", suffixName='" + suffixName + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", resetP=" + resetP +
				", companyName='" + companyName + '\'' +
				", roleName='" + roleName + '\'' +
				", companySuperUser=" + companySuperUser +
				", superUser=" + superUser +
				", adminEval=" + adminEval +
				", activated=" + activated +
				", evaluator=" + evaluator +
				", company=" + company +
				", locations=" + locations +
				", role=" + role +
				", subDepartments=" + subDepartments +
				", departments=" + departments +
				", dateOfHire='" + dateOfHire + '\'' +
				", jobTitle='" + jobTitle + '\'' +
				", divisionBranch='" + divisionBranch + '\'' +
				", departmentName='" + departmentName + '\'' +
				", supervisor='" + supervisor + '\'' +
				'}';
	}

	@NonNull
	@Column(unique=true) 
	private String name;

	@NonNull
	private String firstName;

	@NonNull
	private String lastName;

	private String suffixName;

	@NonNull
	private String email;
	
	@NonNull
	private String password;

	@NonNull
	private boolean resetP;

	@NonNull
	private String companyName;

	@NonNull
	private String roleName;



	private boolean isReviewee;

	private boolean isReviewer;

//	@OneToMany(mappedBy = "user")
//	private List<Evaluator> evaluator = new ArrayList<>();


	@NonNull
	private boolean companySuperUser;
	
	@NonNull
	private boolean superUser;
	
	@NonNull
	private boolean adminEval;

	private Boolean activated = true;
	
	//Deactivated user
	private Boolean deactivated = false;
	
	//	@NonNull
	//	private List<User> evaluatees;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Reviewee reviewee;

	@OneToMany(mappedBy = "user")
	private Set<Evaluator> evaluator = new HashSet<Evaluator>();


	
	@ManyToOne()
	@JoinColumn(name = "company_id")
	private Company company;


	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_location",
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "location_id"))
	private Set<Location> locations;

	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "role_id")
	private Role role;



	//departments which this user is the head of (essentially this user is a supervisor over this lis tof users). 
	@OneToMany(mappedBy = "deptHead", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Department> subDepartments;



	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Department> departments;

	// Preload
	//private long employeeId;
	private String dateOfHire;
	private String jobTitle;
	
	private String divisionBranch;
	private String departmentName;
	
	private String supervisor;

	/**
	 * default constructor
	 */
	public User() {
		this.locations = new HashSet<Location>();
		this.departments = new HashSet<Department>(); 
		this.subDepartments = new HashSet<Department>(); 
		this.companySuperUser = false;
		this.superUser = false;
		this.activated = true;
		
	}

	//adds user to a no location
	/**
	 * @param name of user
	 * @param firstName 
	 * @param lastName
	 * @param email
	 * @param password
	 * @param employeeId
	 * @param dateOfHire
	 * @param jobTitle	 
	 */
	public User(String name, String firstName, String lastName, String email, String password, /*String role,*/
			int employeeId, String dateOfHire, String jobTitle, String supervisor,
			String divisionBranch, String deptName, Company co, Role role, boolean companySuperUser, boolean superUser) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.name = name;
		this.email = email;
		this.password = password;
		this.departmentName = deptName;
		this.resetP = true;
		//this.companyID = companyID;
		this.companyName = co.getCompanyName();
		this.company = co;

		//this.employeeId = employeeId;
		this.dateOfHire = dateOfHire;
		this.jobTitle = jobTitle;
		this.supervisor = supervisor;
		this.divisionBranch = divisionBranch;
		this.locations = new HashSet<Location>();
		this.departments = new HashSet<Department>(); 
		if(role == null) {
			this.role = new Role (co.getDefaultRoleName(),co);
		}
		else {
			this.role = role;					
		}
		this.roleName = role.getName();

		if(role.getName().contains("ADMIN")) {
			this.companySuperUser = true;
		}else {
			this.companySuperUser = companySuperUser;
		}
		this.superUser = superUser;
		this.adminEval = false;
		this.activated = true;


	}

	//adds user to a single location
	/**
	 * @param name
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 * @param employeeId
	 * @param dateOfHire
	 * @param jobTitle
	 * @param supervisor
	 * @param divisionBranch name of locatoin
	 * @param co company the user is in
	 * @param location location the use ris in
	 * @param dept dept the user is in
	 * @param role the user has
	 * @param companySuperUser if user is companySU
	 * @param superUser if user is SSU
	 */
	public User(String name, String firstName, String lastName, String email, String password, /*String roleStr,*/
			int employeeId, String dateOfHire, String jobTitle, String supervisor, 
			String divisionBranch, Company co, Location location, Department dept, Role role, boolean companySuperUser, boolean superUser) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.name = name;
		this.email = email;
		this.password = password;
		this.resetP = true;
		//this.companyID = companyID;
		this.companyName = co.getCompanyName();
		this.company = co;
		// Preload
		//this.employeeId = employeeId;
		this.dateOfHire = dateOfHire;
		this.jobTitle = jobTitle;
		this.supervisor = supervisor;
		this.divisionBranch = divisionBranch;
		this.locations = new HashSet<Location>();
		this.locations.add(location);
		this.departments = new HashSet<Department>(); 
		this.departments.add(dept);
		if(role == null) {
			this.role = new Role (co.getDefaultRoleName(),co);
		}
		else {
			this.role = role;
		}
		this.roleName = role.getName();
		if(role.getName().contains("ADMIN")) {
			this.companySuperUser = true;
		}else {
			this.companySuperUser = companySuperUser;
		}
		this.superUser = superUser;
		this.adminEval = false;
		this.activated = true;

	}

	//adds user to multiple locations. 
	/**
	 * @param name
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 * @param employeeId
	 * @param dateOfHire
	 * @param jobTitle
	 * @param supervisor name of supervisors
	 * @param divisionBranch name of location
	 * @param co company the user is in
	 * @param locations the user is in
	 * @param depts the user is a part of
	 * @param role the user has
	 * @param companySuperUser if user is companySU
	 * @param superUser if user is SSU
	 */
	public User(String name, String firstName, String lastName, String email, String password, /*String role,*/
			int employeeId, String dateOfHire, String jobTitle, String supervisor,
			String divisionBranch, Company co, Set<Location> locations, Set<Department> depts, Role role, boolean companySuperUser, boolean superUser) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.name = name;
		this.email = email;
		this.password = password;

		this.resetP = true;
		//this.companyID = companyID;
		this.companyName = co.getCompanyName();
		this.company = co;
		// Preload
		//this.employeeId = employeeId;
		this.dateOfHire = dateOfHire;
		this.jobTitle = jobTitle;
		this.supervisor = supervisor;
		this.divisionBranch = divisionBranch;
		this.locations = locations;
		this.departments = depts;
		if(role == null) {
			this.role = new Role (co.getDefaultRoleName(),co);
		}
		else {
			this.role = role;
		}
		this.roleName = role.getName();
		if(role.getName().contains("ADMIN")) {
			this.companySuperUser = true;
		}else {
			this.companySuperUser = companySuperUser;
		}
		this.superUser = superUser;
		this.adminEval = false;
		this.activated = true;

	}	
	
	public String getDepartmentNames() {
		String names = "";
		for(Department dept : this.departments) {
			if(names.length() == 0) {
				names +=  dept.getName();
			}
			else {
				names +=  ", " + dept.getName();
			}
			
		}
		return departmentName;
	}

	/**
	 * @param dept to be added
	 */
	public void addSubDept(Department dept) {
		this.subDepartments.add(dept);
	}

	/**
	 * @param depts to be added
	 */
	public void addSubDepts(Set<Department> depts) {
		for(Department dept : depts) {
			this.subDepartments.add(dept);
		}
	}

	/**
	 * @param dept to be removed
	 * @return true if found/removed
	 */
	public boolean removesubDept(Department dept) {
		if(this.subDepartments.contains(dept)) {
			this.subDepartments.remove(dept);
			return true;
		}
		return false;
	}


	/**
	 * @return true if the user has a privilege with read access
	 */
	public boolean hasRead() {
		for(Privilege priv : this.role.getPrivileges()) {
			if(priv.getRead() == true)
				return true;
		}
		return false;
	}
	
	

	public boolean isAdminEval() {
		if(this.role.getName().contains("ADM_EVAL")) {
			return true;
		}
		return adminEval;
	}

	public void setAdminEval(boolean adminEval) {
		this.adminEval = adminEval;
	}

	/**
	 * @return true if the user has a privilege with write access
	 */
	public boolean hasWrite() {
		for(Privilege priv : this.role.getPrivileges()) {
			if(priv.getWrite() == true)
				return true;
		}
		return false;
	}

	/**
	 * @return true if the user has a privilege with delete access
	 */
	public boolean hasDelete() {
		for(Privilege priv : this.role.getPrivileges()) {
			if(priv.getDelete() == true)
				return true;
		}
		return false;
	}

	
	
	/**
	 * @return true if the user has an evaluator
	 */
	public boolean hasEvaluator() {
		return this.evaluator.size() > 0;
	}

	
	/**
	 * @return has permission to give evaluator permission. 
	 */
	public boolean hasEditEvalPerm() {
		for(Privilege priv : this.role.getPrivileges()) {
			if(priv.getEditEvaluate() == true)
				return true;
		}
		return false;
	}


	

	/**
	 * @param loc to add
	 */
	public void addLocation(Location loc) {
		this.locations.add(loc);
	}

	/**
	 * @param locations to add
	 */
	public void addLocations(List<Location> locations) {
		for(Location loc : locations) {
			this.locations.add(loc);
		}
	}

	/**
	 * @param loc to remove
	 * 
	 * @return true if removed
	 */
	public boolean removeLocation(Location loc) {
		if(this.locations.contains(loc)) {
			this.locations.remove(loc);
			return true;
		}
		return false;
	}
	
	
	/**
	 * @return names of the privileges
	 */
	public String getPrivilegeNames(){
		String list = "";
		for(Privilege priv : role.getPrivileges()) {
			list += priv.getName()+",";
		}
		if(list.length()>0)
			return list.substring(0,list.length()-1);		
		return list;

	}
	

	/**
	 * @param dept to be removed
	 * @return true if removed
	 */
	public boolean removeDepartment(Department dept) {
		if(this.departments.contains(dept)) {
			this.departments.remove(dept);
			return true;
		}
		return false;
	}

	/**
	 * @param dept to add
	 */
	public void addDepartment(Department dept) {
		this.departments.add(dept);
	}



	//getters and setters. 
	
	public Role getRole() {
		if(this.role != null)
			return role;
		return new Role("NO ROLL ASSIGNED", this.getCompany());
	}

	public void setRole(Role role) {
		this.role = role;
		if(role == null) {
			this.roleName = "none";
		}
		else {
			this.roleName = role.getName();
			if(role.getName().contains("COMPANYSUPERUSER")) {
				this.companySuperUser = true;
			}
		}		
		
	}

	public void setDepartments(Set<Department> departments) {
		this.departments = departments;
	}
	
	public boolean isCompanySuperUser() {
		return companySuperUser;
	}

	public void setCompanySuperUser(boolean superUser) {
		this.companySuperUser = superUser;
	}	
	
	public boolean isSuperUser() {
		return superUser;
	}

	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}

	public Set<Evaluator> getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(Set<Evaluator> evaluator) {
		this.evaluator = evaluator;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {

		// String encodPass = BCrypt.hashpw(password, BCrypt.gensalt());
		this.password = password/* .replaceAll("\\s", "") */;
	}

	public void setEncryptedPassword(String password) {
		String encodPass = BCrypt.hashpw(password/* .replaceAll("\\s", "") */, BCrypt.gensalt());
		this.setPassword(encodPass);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email/*.replaceAll("\\s", "")*/;
	}
	public boolean isEvaluator() {
		return this.evaluator.size() >0;
	}
	
	public Set<Department> getDepartments() {
		return departments;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}


	public Set<Location> getLocations() {
		return locations;
	}

	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}

	

	public String getRoleName() {
		if(this.superUser) {
			return "SuperUser";
		}
		if(this.companySuperUser) {
			return "CompanySuperUser";
		}
		return roleName;
	}

	/*
	public List<Evaluator> getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(List<Evaluator> evaluator) {
		this.evaluator = evaluator;
	}
	 */
	
	
	public String getFirstName() {
		return firstName;
	}

	

	public void setFirstName(String firstName) {
		this.firstName = firstName/* .replaceAll("\\s", "") */;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName/* .replaceAll("\\s", "") */;
	}

	public String getSuffixName() {
		return suffixName;
	}

	public void setSuffixName(String suffixName) {
		if (suffixName == null) {
			this.suffixName = suffixName;
		} else if (suffixName.replaceAll("\\s", "") == "") {
			this.suffixName = null;
		}

		else {

			this.suffixName = suffixName.replaceAll("\\s", "");
		}
	}

	public boolean getReset() {
		return resetP;
	}

	public void setReset(boolean reset) {
		this.resetP = reset;
	}

	public boolean isResetP() {
		return resetP;
	}

	public void setResetP(boolean resetP) {
		this.resetP = resetP;

	}

	// Preload =======================================
	/*
	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}
	 */

	public String getDateOfHire() {
		return dateOfHire;
	}

	public Set<Department> getSubDepartments() {
		return subDepartments;
	}

	public void setSubDepartments(Set<Department> subDepartments) {
		this.subDepartments = subDepartments;
	}

	public void setDateOfHire(String dateOfHire) {
		if(dateOfHire.matches("[0-9]+")) {
			dateOfHire = LocalDate.of( 1899 , Month.DECEMBER , 30 ).plusDays((long) Double.parseDouble(dateOfHire)).toString();
		}
		if(dateOfHire.matches("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]")) {
			this.dateOfHire = dateOfHire;
		}
		else if(dateOfHire.matches("[0-9][0-9]/[0-9][0-9]/[0-9][0-9][0-9][0-9]")) {
			this.dateOfHire = dateOfHire;
		}
		else {
			this.dateOfHire = "N/A";
		}
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDivisionBranch() {
		return divisionBranch;
	}

	public void setDivisionBranch(String divisionBranch) {
		this.divisionBranch = divisionBranch;
	}


	public Long getCompanyID() {
		return this.company.getId();
	}
	
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public String getLocationsAsString() {
		
		String result = "";
		
		for (Location l : this.locations) {
			
			result += l.getLocationName() + " ";
		}
		return result;
	}
	
	public void setActivation(Boolean activation) {
		this.activated = activation;
	}

	public Boolean getActivation() {
		return this.activated;
	}

	public Reviewee getReviewee() {
		return reviewee;
	}

	public void setReviewee(Reviewee reviewee) {
		this.reviewee = reviewee;
	}

	public boolean isReviewee() {
		return isReviewee;
	}

	public void setReviewee(boolean reviewee) {
		isReviewee = reviewee;
	}

	public boolean isReviewer() {
		return isReviewer;
	}

	public void setReviewer(boolean reviewer) {
		isReviewer = reviewer;
	}

	public boolean isDeactivated() {
		return deactivated;
	}

	public void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
	}
}