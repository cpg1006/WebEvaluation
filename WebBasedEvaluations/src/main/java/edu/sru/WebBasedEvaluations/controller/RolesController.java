package edu.sru.WebBasedEvaluations.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.DepartmentRepository;
import edu.sru.WebBasedEvaluations.repository.LocationRepository;
import edu.sru.WebBasedEvaluations.repository.PrivilegeRepository;
import edu.sru.WebBasedEvaluations.repository.RoleRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;

/**
 * Class that handles role functions such as editing the various attributes and deleting roles.
 * 
 * @author Mike Elias
 *
 */
@Controller
public class RolesController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private LocationRepository locRepo;
	
	@Autowired
	private DepartmentRepository deptRepo;
	
	@Autowired
	private PrivilegeRepository privRepo;
	
	/**
	 * Handles the roles "Edit" page.
	 * @param id - the ID of the role being edited
	 * @param model - the model for the HTML page
	 * @param auth - authentication
	 * @return
	 */
	@GetMapping("/roleEdit/{id}")
	public Object rolesEdit(@PathVariable("id") long id, Model model, Authentication auth) {
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		Iterable<Role> roles = roleRepo.findAll();
		Role role = null;
		
		for(Role x: roles) {
			if(x.getId() == id) {
				role = x;
				break;
			}
		}
		Set<Privilege> privileges = role.getPrivileges();
		List<Department> roleDepartments = new ArrayList<Department>();
		List<Department> otherDepartments =new ArrayList<Department>();
		List<Privilege> rolePrivileges = new ArrayList<Privilege>();
		
		Set<Location> companyLocations = locRepo.findByCompany(currentCompany);
		List<Location> roleLocations = new ArrayList<Location>();
		List<ImmutablePair<Department, Privilege>> departmentPrivileges = new ArrayList<ImmutablePair<Department, Privilege>>();
		
		
		roleLocations.addAll(companyLocations);
		
		
		rolePrivileges.addAll(privileges);
		otherDepartments.addAll(deptRepo.findByCompany(currentCompany));
		
		for(Privilege priv: rolePrivileges) {
			
			roleDepartments.addAll(priv.getDepts());
			otherDepartments.removeAll(priv.getDepts());
		}
		
		for(int i = 0; i < rolePrivileges.size(); i++) {
			Privilege priv = rolePrivileges.get(i);
			
			for(int j = 0; j < roleLocations.size(); j++) {
				Location loc = roleLocations.get(j);
				if(priv.getName().contains(loc.getLocationName())) {
					rolePrivileges.remove(priv);
				}
			}
		}
		
		List<User> companyUsers = new ArrayList<User>();
		List<User> usersWithRole = new ArrayList<User>();
		List<User> otherUsers = new ArrayList<User>();
		
		companyUsers.addAll(userRepo.findByCompany(currentCompany));
		
		for(User user: companyUsers) {
			if(user.getRole().equals(role)) {
				usersWithRole.add(user);
			} else {
				otherUsers.add(user);
			}
		}

		//sort the lists alphabetically
		Collections.sort(roleDepartments, Comparator.comparing(Department::getName));
		Collections.sort(rolePrivileges, Comparator.comparing(Privilege::getName));
		Collections.sort(usersWithRole, Comparator.comparing(User::getName));
		Collections.sort(otherUsers, Comparator.comparing(User::getName));

		model.addAttribute("role", role);
		model.addAttribute("privsList", rolePrivileges);
		model.addAttribute("deptsList", roleDepartments);		
		model.addAttribute("usersWithRole", usersWithRole);
		model.addAttribute("otherUsers", otherUsers);
		model.addAttribute("otherDepartments", otherDepartments);
		model.addAttribute("user", new User());
		model.addAttribute("department", new Department());
		
		return "roleEdit";
	}
	
	/**
	 * Handles the removal of users from a certain role
	 * @param id - the ID of the user being removed from the role
	 * @param model - the model of the HTML page
	 * @param auth - authentication
	 * @return Redirect back to the edit page
	 */
	@GetMapping("removeUser/{id}")
	public Object removeUser(@PathVariable("id") long id, Model model, Authentication auth) {
		User userToRemove = userRepo.findByid(id);
		Role role = userToRemove.getRole();
		
		userToRemove.setRole(null);
		userRepo.save(userToRemove);
		
		RedirectView redirectView = new RedirectView("/roleEdit/"+role.getId(), true);
		return redirectView;
	}
	
	/**
	 * Handles the removal of departments and their privileges for a specific role
	 * @param departmentID - the ID of the department being removed
	 * @param roleID - the ID of the role for the department being removed
	 * @param model - model of the HTML page
	 * @param auth - authentication
	 * @return Redirect back to the edit page
	 */
	@GetMapping("removeDepartment/{deptID}/{roleID}")
	public Object removeDepartment(@PathVariable("deptID") long departmentID, @PathVariable("roleID") long roleID, Model model, Authentication auth) {
		Department department = deptRepo.findById(departmentID);
		Iterable<Role> rolesList = roleRepo.findAll();
		Role role = null;
		
		for(Role r: rolesList) {
			if(r.getId() == roleID) {
				role = r;
				break;
			}
		}
		
		
		List<Privilege> departmentPrivileges = department.getPrivileges();
		
		for(Privilege priv: departmentPrivileges) {
			priv.removeDept(department);
			privRepo.save(priv);
			role.removePrivilege(priv);
		}
	
		RedirectView redirectView = new RedirectView("/roleEdit/"+roleID, true);
		return redirectView;
	}
	

	/**
	 * Add a user to the desired role
	 * @param user - the user the role is being added to
	 * @param roleID - the ID of the role the user will be set to
	 * @return Redirect back to the edit page
	 */
	@PostMapping("addUserToRole/{roleID}")
	public Object addUserToRole(@RequestParam(name = "user") User user, @PathVariable("roleID") long roleID) {
		Iterable<Role> rolesList = roleRepo.findAll();
		Role role = null;
		
		for(Role r: rolesList) {
			if(r.getId() == roleID) {
				role = r;
				break;
			}
		}

		user.setRole(role);
		userRepo.save(user);
		
		RedirectView redirectView = new RedirectView("/roleEdit/"+role.getId(), true);
		return redirectView;
	}
	
	/**
	 * Add a department to the desired role
	 * @param department - the department being added to the role 
	 * @param roleID - the ID of the role the department will be added to 
	 * @return Redirect back to the edit page
	 */
	@PostMapping("addDepartmentToRole/{roleID}")
	public Object addDepartmentToRole(@RequestParam(name = "department") Department department, @PathVariable("roleID") long roleID, Authentication auth) {
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		Iterable<Role> rolesList = roleRepo.findAll();
		Role role = null;
		
		for(Role r: rolesList) {
			if(r.getId() == roleID) {
				role = r;
				break;
			}
		}
		
		List<Privilege> privilegeList = new ArrayList<Privilege>();
		List<Department> deptsList = new ArrayList<Department>();
		deptsList.add(department);
		
		Privilege privilege = new Privilege();
		privilege.setRead(false);
		privilege.setWrite(false);
		privilege.setDelete(false);
		privilege.setEditEvaluator(false);
		
		String privName = "";
		privName += currentCompany.getCompanyName()+"_";
		privName += department.getName()+"_";
		privName += "----";
		
		privilege.setName(privName);
		privilege.setDepts(deptsList);
		
		privilegeList.add(privilege);
		department.setPrivileges(privilegeList);
		
		Set<Privilege> privilegeSet = new HashSet<Privilege>();
		privilegeSet.addAll(privilegeList);
		privilegeSet.addAll(role.getPrivileges());
		
		role.setPrivileges(privilegeSet);
		
		deptRepo.save(department);
		privRepo.save(privilege);
		roleRepo.save(role);
		
		RedirectView redirectView = new RedirectView("/roleEdit/"+role.getId(), true);
		return redirectView;
	}
	
	@GetMapping("/toggleRead/{roleID}/{privID}/{deptID}")
	public Object toggleRead(@PathVariable("roleID") long roleID, @PathVariable("privID") long privilegeID, @PathVariable("deptID") long deptID, Authentication auth) {
		Iterable<Privilege> allPrivs = privRepo.findAll();
		Department department = deptRepo.findById(deptID);
		Privilege privilege = null;
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		for(Privilege priv: allPrivs) {
			if(priv.getId() == privilegeID) {
				privilege = priv;
				break;
			}
		}
		
		
		boolean toggle = !privilege.getRead();
		privilege.setRead(toggle);
		
		String newName = setNewPrivName(currentCompany, department, privilege.getRead(), privilege.getWrite(), privilege.getDelete(), privilege.getEditEvaluator()); 
		privilege.setName(newName);
	
		privRepo.save(privilege);
			
		RedirectView redirectView = new RedirectView("/roleEdit/"+roleID, true);
		return redirectView;
	}
	
	@GetMapping("/toggleWrite/{roleID}/{privID}/{deptID}")
	public Object toggleWrite(@PathVariable("roleID") long roleID, @PathVariable("privID") long privilegeID, @PathVariable("deptID") long deptID, Authentication auth) {
		Iterable<Privilege> allPrivs = privRepo.findAll();
		Department department = deptRepo.findById(deptID);
		Privilege privilege = null;
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		for(Privilege priv: allPrivs) {
			if(priv.getId() == privilegeID) {
				privilege = priv;
				break;
			}
		}
		
		
		boolean toggle = !privilege.getWrite();
		privilege.setWrite(toggle);
		
		String newName = setNewPrivName(currentCompany, department, privilege.getRead(), privilege.getWrite(), privilege.getDelete(), privilege.getEditEvaluator()); 
		privilege.setName(newName);
		
		privRepo.save(privilege);
		
		
		RedirectView redirectView = new RedirectView("/roleEdit/"+roleID, true);
		return redirectView;
	}
	
	@GetMapping("/toggleEditEvaluator/{roleID}/{privID}/{deptID}")
	public Object toggleEditEvaluator(@PathVariable("roleID") long roleID, @PathVariable("privID") long privilegeID, @PathVariable("deptID") long deptID, Authentication auth) {
		Iterable<Privilege> allPrivs = privRepo.findAll();
		Privilege privilege = null;
		Department department = deptRepo.findById(deptID);
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		for(Privilege priv: allPrivs) {
			if(priv.getId() == privilegeID) {
				privilege = priv;
				break;
			}
		}
		
		
		boolean toggle = !privilege.getEditEvaluator();
		privilege.setEditEvaluator(toggle);
		
		String newName = setNewPrivName(currentCompany, department, privilege.getRead(), privilege.getWrite(), privilege.getDelete(), privilege.getEditEvaluator()); 
		privilege.setName(newName);
		
		privRepo.save(privilege);
		
		RedirectView redirectView = new RedirectView("/roleEdit/"+roleID, true);
		return redirectView;
	}
	
	/**
	 * Sets the name of a new Privilege added
	 * @param co - Company the privilege is for
	 * @param dept - Department the privilege is created for
	 * @param read - Read Privileges
	 * @param write - Write Privileges
	 * @param delete - Delete Privileges
	 * @param editEvaluator - EditEvaluator Privileges
	 * @return new name
	 */
	public String setNewPrivName(Company co, Department dept, boolean read, boolean write, boolean delete, boolean editEvaluator) {
		String name = "";
		name += co.getCompanyName() + "_";
		name += dept.getName() + "_";
		
		
		if(read) {
			name += "r";
		}
		else {
			name += "-";
		}
		
		if(write) {
			name += "w";
		}
		else {
			name += "-";
		}
		
		if(delete) {
			name += "d";
		}
		else {
			name += "-";
		}
		
		if(editEvaluator) {
			name += "e";
		}
		else {
			name += "-";
		}
		return name ;
	}
	
	@GetMapping("/toggleRoleActivation/{id}")
	public Object toggleRoleActivation(@PathVariable("id") long roleID, Authentication auth) {
		Role role = roleRepo.findById(roleID).get();
		boolean currentState = role.getActivation();
		User currentUser;
		Company currentCompany;
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);
		currentCompany = currentUser.getCompany();
		
		role.setActivation(!currentState);
		
		if(!role.getActivation()) {
			List<User> companyUsers = userRepo.findByCompany(currentCompany);
			List<User> usersWithRole = new ArrayList<User>();
			
			Set<Privilege> privileges = role.getPrivileges();
			
			for(User user: companyUsers) {
				if(user.getRole() == role) {
					usersWithRole.add(user);
				}
			}
			
			//remove users
			for(User user: usersWithRole) {
				user.setRole(null);
				userRepo.save(user);
			}
			
			
			for(Privilege priv: privileges) {
				List<Department> departments = priv.getDepts();
				
				for(Department dept: departments) {
					dept.removePrivilege(priv);
					deptRepo.save(dept);
				}
				
				for(int i = 0; i < departments.size(); i++) {
					priv.removeDept(departments.get(i));
					privRepo.save(priv);
				}
			}
			role.setPrivileges(null);
		}
		
		roleRepo.save(role);
		
		RedirectView redirectView = new RedirectView("/adminRoles", true);
		return redirectView;
	}
	
	
}
