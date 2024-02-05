package edu.sru.WebBasedEvaluations.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.sru.WebBasedEvaluations.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.repository.CompanyRepository;
import edu.sru.WebBasedEvaluations.repository.DepartmentRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.LocationRepository;
import edu.sru.WebBasedEvaluations.repository.RoleRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;
import edu.sru.WebBasedEvaluations.service.UserService;

/**
 * Class for handling user related changes other than adding users. Houses the
 * admin_user webpage.
 * 
 * @author Dalton Stenzel
 * @author David Gillette
 *
 */
@Controller
public class UserController {
	// set up a UserRepositoty variable
	private UserRepository userRepository;
	private EvaluatorRepository evaluatorRepository;
	private EvaluationRepository evaluationRepository;
	private DepartmentRepository departmentRepository;
	private Authentication auth;

	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private LocationRepository locationRepo;
	
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private EvaluationRepository evalFormRepo;

	@Autowired
	private CompanyRepository companyRepo;


	private AddUserController addUserController;

	private Logger log = LoggerFactory.getLogger(UserController.class);

	//	private static final String ADMIN = "ADMIN";
	//	private static final String COMPANY_ADMIN = "COMPANY_ADMIN";
	//	private static final String EVALUATOR_EVAL = "EVALUATOR_EVAL";
	//	private static final String EVAL_ADMIN = "EVAL_ADMIN";
	//	private static final String EVALUATOR = "EVALUATOR";
	//	private static final String USER = "USER";


	@Autowired
	private AdminMethodsService adminMethodsService;

	@Autowired
	private UserService service;

	// create an UserRepository instance - instantiation (new) is done by Spring
	public UserController(UserRepository userRepository, EvaluatorRepository evaluatorRepository,
			EvaluationRepository evaluationRepository, AddUserController addUserController) {
		this.userRepository = userRepository;
		this.evaluatorRepository = evaluatorRepository;
		this.evaluationRepository = evaluationRepository;
		this.addUserController = addUserController;

	}

	/**
	 * Method called upon loading the admin user page in order to
	 * add/edit/delete/view users. Calls several methods from the
	 * AdminMethodsService for performing sorting and the amount of pages needed to
	 * be viewable.
	 * 
	 * @param user     is a User object required for the webpage, or else an
	 *                 "IllegalStateException error occurs.
	 * @param model    is a Model object used to add attributes to a webpage.
	 * @param keyword  is a String used to hold a particular search term entered by
	 *                 the user.
	 * @param perPage  is a Integer used to store the value of how many users should
	 *                 be displayed on a page at once.
	 * @param sort     is a String used to hold the type of sort to be used on the
	 *                 users to be displayed.
	 * @param currPage is an Integer used to store the current page the user is on
	 *                 for viewing users.
	 * @param sortOr   is an Integer value used to determine the order in which the
	 *                 list of users will be displayed as: ascending(1) or
	 *                 descending(0).
	 * @return the admin_user html page.
	 */


	@GetMapping({ "/adminUsers/", "/search" })
	public String home(Authentication auth, Model model, String keyword, @RequestParam(value = "perPage",required = true) Integer perPage,
					   @RequestParam(value = "sort",required = true) String sort, @RequestParam(value = "currPage",required = true) Integer currPage,
					   @RequestParam(value = "sortOr",required = true) Integer sortOr) {

		User currentUser;
		Company currentCompany;

		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long id = userD.getID();

		currentUser = userRepository.findById(id).orElse(null);

		String UserRole=currentUser.getRole().getName();
		model.addAttribute("UserRole",UserRole);

		currentCompany = currentUser.getCompany();

		//list of all users in the repository
		Iterable<User> allUsers = userRepository.findAll();
		List<User> allUsersList = new ArrayList<User>();

		allUsersList.addAll((Collection<? extends User>) allUsers);

		String hasEvals;

		if(evalFormRepo.count() > 0) {
			hasEvals = "yes";
		} else {
			hasEvals = "no";
		}


		boolean showUserScreen = true;
		boolean rolesNeedAdded = false;

		Set<Role> companyRoles = currentCompany.getRoles();
		List<Role> rolesList = new ArrayList<Role>();
		Set<Department> companyDepartments = currentUser.getCompany().getDepartments();
		List<Department> departmentsList = new ArrayList<Department>();
		final int MIN_DEPARTMENT_SIZE = 2;
		final int MIN_ROLES_SIZE = 2;
		rolesList.addAll(companyRoles);
		departmentsList.addAll(companyDepartments);

		Department firstDepartment = departmentsList.get(0);
		Role firstRole = rolesList.get(0);

		//checks to ensure roles have been created before users can be uploaded or created
		if(rolesList.size() < MIN_ROLES_SIZE && firstRole.getName().equals("COMPANYSUPERUSER")) {
			showUserScreen = false;
			rolesNeedAdded = true;
		}

		//will check to see if companies have been added before allowing users to be added
		if(departmentsList.size() < MIN_DEPARTMENT_SIZE && firstDepartment.getName().equals("none")) {
			showUserScreen = false;
		}

		//list of all admins (for SuperSuperUser view)
		List<User> allCompanyAdmins = new ArrayList<User>();

		//list of all users in a company (company admin view)
		List<User> companyUsers = userRepository.findByCompany(currentCompany);

		 
		
		for(User userInList: allUsersList) {

			//add all admins to the admin list
			if(userInList.isCompanySuperUser()) {
				allCompanyAdmins.add(userInList);
			}

			//remove SuperSuperUser from list (should not be visible to company admins)
			if(userInList.isSuperUser() && companyUsers.contains(userInList)) {
				companyUsers.remove(userInList);
			}
		}

		//list to be sent to the model and shown to users
		List<User> list;

		if(currentUser.isSuperUser()) {
			list = allCompanyAdmins;
			list.add(currentUser);
		}
		else {
			list = companyUsers;
		}


		// No keyword
		if (keyword == null || keyword.equals("")/* && count !=null */) {
			list = adminMethodsService.sortCheck(sort, list, sortOr, model);

			list = adminMethodsService.pageCalc(list, currPage, perPage, sort, keyword, model);
		}
		// Has keyword
		else {

			// If showing all users
			if (perPage <= 0) {
				list = adminMethodsService.sortCheck(sort, service.getByKeyword(keyword,currentCompany), sortOr, model);
				list = adminMethodsService.pageCalc(list, currPage, perPage, sort, keyword, model);

				// If not showing all users
			} else {
				// sort list with parameters
				list = adminMethodsService.sortCheck(sort, service.getByKeyword(keyword, currentCompany), sortOr, model);
				// display current page + other page buttons
				list = adminMethodsService.pageCalc(list, currPage, perPage, sort, keyword, model);

			}

		}
		
		List<User> activeUsers = list.stream().filter(user -> !user.isDeactivated()).collect(Collectors.toList());
		List<User> deactivatedUsers = list.stream().filter(User::isDeactivated).collect(Collectors.toList());

		//for navbar
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);
		model = AdminMethodsService.addingOrEditingUser(currentUser, locationRepo, deptRepo, roleRepo, companyRepo, model);

		//companies the currently logged in user can add user to.



		//roles the currently logged in user can grant.
//		Set<Role> roles = currentUser.getCompany().getRoles();
//		Set<Role> grantableRoles = new HashSet<Role>();
//		Set<String> roleNames = new HashSet<String>();
//		roleNames.add(currentUser.getCompany().getDefaultRoleName());
//		//		grantableRoles.add(new Role currentUser.getCompany().getDefaultRoleName());
//		for(Role role : roles) {
//			if(currentUser.getRole().contains(role)) {
//				grantableRoles.add(role);
//				roleNames.add(role.getName());
//			}
//		}

//		model.addAttribute("roles", grantableRoles);
		
		
	    


		model.addAttribute("superUser",userRepository.findByRoleNameEquals("SUPERUSER"));
		model.addAttribute("adminUser",userRepository.findByRoleNameEquals("ADMIN"));
		List<User> EvalAdminList= userRepository.findByRoleNameEqualsOrRoleNameEquals("EVALUATOR_ADMIN","USER");
		System.out.println(EvalAdminList.toString());
		model.addAttribute("Eval_admin",userRepository.findByRoleNameEqualsOrRoleNameEquals("EVALUATOR_ADMIN","USER"));

		model.addAttribute("EVALUATOR_EVAL", false);
		model.addAttribute("SuperSuperUserList", currentUser.isSuperUser());
		model.addAttribute("CompanyAdminList", currentUser.isCompanySuperUser());

		model.addAttribute("keyword", keyword);

		model.addAttribute("list", list);

		model.addAttribute("perPage", perPage);
		model.addAttribute("sortOr", sortOr);

		model.addAttribute("sort", sort);
		model.addAttribute("hasEvals", hasEvals);
		model.addAttribute("showUserScreen", showUserScreen);
		model.addAttribute("user",new User());
		model.addAttribute("depts",companyDepartments);
		model.addAttribute("locations",currentUser.getCompany().getLocations());
	    model.addAttribute("activeUsers", activeUsers);
	    model.addAttribute("deactivatedUsers", deactivatedUsers);
		return "adminUsers";
	}
	/**
	 * Method called when a user decides to edit their own account with the "My
	 * Account" button on most pages.
	 * 
	 * @param authentication is an Authentication object used in this instance to
	 *                       get the current user logged in.
	 * @param model          is a Model object used to add attributes to a webpage.
	 * @return userSettings html webpage.
	 */
	@RequestMapping({ "/userSettings/" })
	public String editSettings(Authentication authentication, Model model) {

		boolean groupButton = false;
		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();

		Long id = userD.getID();

		User user1 = userRepository.findById(id).orElse(null);

		Company currentCompany = user1.getCompany();

		String userRole = user1.getRole().getName();

//		userDao user = new userDao();
//		user.setFname(user1.getFirstName());
//		user.setLname(user1.getLastName());
//		user.setSuffix(user1.getSuffixName());
//		user.setEmail(user1.getEmail());
//		user.setPassword(user1.getPassword());
		
		boolean showUserScreen = true;
		boolean rolesNeedAdded = false;
		
		Set<Role> companyRoles = currentCompany.getRoles();
		List<Role> rolesList = new ArrayList<Role>();
		Set<Department> companyDepartments = currentCompany.getDepartments();
		List<Department> departmentsList = new ArrayList<Department>();
		final int MIN_DEPARTMENT_SIZE = 2;
		final int MIN_ROLES_SIZE = 2;
		rolesList.addAll(companyRoles);
		departmentsList.addAll(companyDepartments);
		
		Department firstDepartment = departmentsList.get(0);
		Role firstRole = rolesList.get(0);
		
		//checks to ensure roles have been created before users can be uploaded or created
		if(rolesList.size() < MIN_ROLES_SIZE && firstRole.getName().equals("COMPANYSUPERUSER")) {
			showUserScreen = false;
			rolesNeedAdded = true;
		}
		
		//will check to see if companies have been added before allowing users to be added
		if(departmentsList.size() < MIN_DEPARTMENT_SIZE && firstDepartment.getName().equals("none")) {
			showUserScreen = false;
		}
		
		if (evaluationRepository == null || evaluationRepository.count() == 0) {
			groupButton = false;
		} else {
			groupButton = true;
		}




		//fornavbar
		model = AdminMethodsService.pageNavbarPermissions(user1, model, evaluatorRepository, evalFormRepo);
		model.addAttribute("id", user1.getId());
		model.addAttribute("groupButton", groupButton);

		model.addAttribute("user", user1);
//		model.addAttribute("user1", user1);
		model.addAttribute("showUserScreen", showUserScreen);
		model.addAttribute("rolesNeedAdded", rolesNeedAdded);
		model.addAttribute("UserRole", userRole);
		
		//hides "Manage Roles" from navbar
    	if(user1.isSuperUser()) {
    		model.addAttribute("COMPANY_ADMIN", false);
    	}
    	
		return "userSettings";
	}

	/*
	 * Maps to the adminUserUpdate.html when called, where an user is selected
	 * from the adminUsers.html page and that id is then sent and used in order to
	 * make changes to the user with the id selected.
	 */

	/**
	 * Method for editing other users from an edit button on the admin users page.
	 * 
	 * @param id       is a long that contains the id value of the user selected to
	 *                 be edited.
	 * @param user     is a User object that gets used to find the specific user
	 *                 being edited.
	 * @param model    is a Model object used to add attributes to a webpage.
	 * @param keyword  is a String used to hold a particular search term entered by
	 *                 the user.
	 * @param perPage  is a Integer used to store the value of how many users should
	 *                 be displayed on a page at once.
	 * @param sort     is a String used to hold the type of sort to be used on the
	 *                 users to be displayed.
	 * @param currPage is an Integer used to store the current page the user is on
	 *                 for viewing users.
	 * @param sortOr   is an Integer value used to determine the order in which the
	 *                 list of users will be displayed as: ascending or descending.
	 * @return adminUserUpdate html webpage.
	 */
	@GetMapping("/edit/{id}/")
	public String showUpdateForm(@PathVariable("id") long id, @RequestParam("perPage") Integer perPage,
			@RequestParam("sort") String sort, @RequestParam("keyword") String keyword,
			@RequestParam("currPage") Integer currPage, @RequestParam("sortOr") Integer sortOr, User user,
			Model model, Authentication auth) {
		model.addAttribute("perPage", perPage);
		
		
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idNum = userD.getID();

		currentUser = userRepository.findById(idNum).orElse(null);
		
		String ansr = null;
		String mess = null;

		// Likely has redunant code in this method

		// MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
		Long userId = id;
		
		
		
		
		User userCheck = userRepository.findById(userId).orElse(null);

		user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		
		
		Department dept = deptRepo.findByNameAndCompany(user.getDepartmentName(), user.getCompany());
		if(dept != null) {
			if(currentUser.getRole().writableDepartments().contains(dept) || currentUser.isCompanySuperUser() || currentUser.isSuperUser() && !user.getDepartments().contains(dept)) {
				user.addDepartment(dept);
			}
			else if(!user.getDepartments().contains(dept))
			{
				mess = "User " + user.getName() + " already has the dept: " + dept.getName();
			}
			else{
				mess = "User " + user.getName() + " does not have permission to add a user to department " + dept.getName();
			}
		}
		Company userCompany=user.getCompany();
		Set<Location> branches=userCompany.getLocations();
		model.addAttribute("branches",branches);
		model.addAttribute("user", user);
		model.addAttribute("id", id);
		model.addAttribute("UserRole", currentUser.getRole().getName());
		adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr,auth);

		
//		List<Role> roles = roleRepo.findByCompany(currentUser.getCompany());
//		if(!currentUser.isSuperUser() && !currentUser.isCompanySuperUser()) {
//			for(Role role : roles) {
//				if(!currentUser.getRole().contains(role)) {
//					roles.remove(role);
//				}
//			}
//		}
//		model.addAttribute("roles", roles);
//		
		

		

		//fornavbar
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);
		model = AdminMethodsService.addingOrEditingUser(currentUser, locationRepo, deptRepo, roleRepo, companyRepo, model);
		adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);

		return "adminUserUpdate";


	}

	/*
	 * Maps to the adminUsers.html when called, which continues what the
	 * showUpdateForm(or /edit/{id}) function started where the changeable
	 * information is either changed or not and sent here to update the user
	 * repository in order to save the changes made. This function prevents a user
	 * having their email manually changed to same email as another user without
	 * preventing the user to retain the same email as they previously did.
	 */

	/**
	 * @param id       is a long used to hold the value of the user that is in the
	 *                 process of having their information updated.
	 * @param perPage  is a Integer used to store the value of how many users should
	 *                 be displayed on a page at once.
	 * @param sort     is a String used to hold the type of sort to be used on the
	 *                 users to be displayed.
	 * @param keyword  is a String used to hold a particular search term entered by
	 *                 the user.
	 * @param currPage is an Integer used to store the current page the user is on
	 *                 for viewing users.
	 * @param sortOr   is an Integer value used to determine the order in which the
	 *                 list of users will be displayed as: ascending or descending.
	 * @param user     is a User object.
	 * @param model    is a Model object used to add attributes to a webpage.
	 * @return adminUserUpdate html webpage.
	 */
	@PostMapping("/update/{id}")
	public String updateUser(@PathVariable("id") long id, @RequestParam("perPage") Integer perPage,
			@RequestParam("sort") String sort, @RequestParam("keyword") String keyword,
			@RequestParam("currPage") Integer currPage, @RequestParam("sortOr") Integer sortOr, @Validated User user,
			/* BindingResult result, */ Model model, Authentication auth) {
		
		
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = userRepository.findById(idnum).orElse(null);

		String ansr = null;
		String mess = null;
		model.addAttribute("perPage", perPage);

		User user2 = userRepository.findByid(id);

		

		// Performs comparison between old and new user values for changes
		User user3 = adminMethodsService.comparingMethod(id, user, user2, model);

		// Checks if email already used by another user, if not then the user selected
		// will be updated.
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);
		model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo,  model);
	

		Department dept = deptRepo.findByNameAndCompany(user.getDepartmentName(), currentUser.getCompany());
		
		
		if(dept != null) {
			System.out.println("passed null");
			if(currentUser.getRole().writableDepartments().contains(dept) || currentUser.isCompanySuperUser() || currentUser.isSuperUser() && !user3.getDepartments().contains(dept)) {
				user3.addDepartment(dept);
				dept.addUser(user3);
				System.out.println("added dept");
			}
			else if(!user3.getDepartments().contains(dept))
			{
				System.out.println("already had it");
				mess = "User " + user2.getName() + " already has the dept: " + dept.getName();
			}
			else{
				System.out.println("no permissions");
				mess = "User " + user2.getName() + " does not have permission to add a user to department " + dept.getName();
			}
		}
		
		
		if ((userRepository.findByEmail(user.getEmail()) == null)
				|| (userRepository.findByEmail(user.getEmail())) == userRepository.findByid(id)) {

			user3.setFirstName(adminMethodsService.capitalize(user3.getFirstName()));
			user3.setLastName(adminMethodsService.capitalize(user3.getLastName()));

			try {
				userRepository.save(user3);
				ansr = "pass";
				mess = "User successfully edited!";
			}
			catch(Exception e){
				e.printStackTrace();
				ansr = "fail";
				mess = "problem occured editing user.";
				log.error(e.getStackTrace().toString());
				
			}
			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
			log.info("Updated User " + user3.getName() + " (ID:" + user3.getId() + ")");
			return "adminUserUpdate";
		} else {
			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
			log.info("Updated User " + user3.getName() + " (ID:" + user3.getId() + ")");
			return "adminUserUpdate";

		}
	}

	/**
	 * Method for deleting users when a "Delete User" button is pressed on the admin
	 * user page. Administrative users are prevented from deleting other users with
	 * the same role.
	 *
	 * 
	 * @param id          is a long used to store the id of the user being deleted.
	 * @param keyword     is a String used to hold the set of characters provided to
	 *                    be searched for.
	 * @param perPage     is a Integer used to store the value of how many users
	 *                    should be displayed on a page at once.
	 * @param model       is a Model object used to add attributes to a webpage.
	 * @param sort        is a String used to hold the type of sort to be used on
	 *                    the users to be displayed.
	 * @param currPage    is an Integer used to store the current page the user is
	 *                    on for viewing users.
	 * @param sortOr      is an Integer value used to determine the order in which
	 *                    the list of users will be displayed as: ascending or
	 *                    descending.
	 * @param redir       is a RedirectAttributes object used to send attributes to
	 *                    a webpage, similar to the model included.
	 * @param deletedUser is a User object that is needed for some reason, error
	 *                    "java.lang.IllegalStateException: Neither BindingResult
	 *                    nor plain target object for bean name 'user' available as
	 *                    request attribute" returns without a user.
	 * @return adminUsers html webpage.
	 */
	@GetMapping("/delete/{id}/")
	public Object deleteUser(@PathVariable("id") long id, @RequestParam("keyword") String keyword,
			@RequestParam("perPage") Integer perPage, Model model, @RequestParam("sort") String sort,
			@RequestParam("currPage") Integer currPage, @RequestParam("sortOr") Integer sortOr, User deletedUser,
			RedirectAttributes redir, Authentication auth) {
		String ansr = null;
		String mess = null;
		
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = userRepository.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();

		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		List<Evaluator> eval = evaluatorRepository.findByUser(user);
		//If user is in a group
		if (eval.size() > 0) {
			ansr = "addFail";
			model.addAttribute("ansr", ansr);

			redir.addFlashAttribute("mess", "User is currently in a group, you must remove them first! ");
			RedirectView redirectView = new RedirectView("/adminUsers/", true);
			return redirectView;
		} else {
			//if user to be deleted is an admin
			if(user.isCompanySuperUser() || user.isSuperUser()){
				// System.out.println("Detected Admin");

				ansr = "addFail";
				mess = "Can't delete administrative users";
				model.addAttribute("ansr", ansr);

				model.addAttribute("mess", mess);
				// model.addAttribute("user", user);

			} else {

				Set<Department> depts = user.getDepartments();
				Set<Department> subDepts = user.getSubDepartments();
				for(Department dept : depts) {
					dept.removeUser(user);					
				}
				for(Department dept : subDepts) {
					dept.removeUser(user);
				}
				user.setDepartments(null);
				user.setSubDepartments(null);
				user.getCompany().removeUser(user);
				this.companyRepo.save(user.getCompany());
				user.setCompany(null);				
				this.deptRepo.saveAll(depts);
				this.deptRepo.saveAll(subDepts);
				
				
				Role role = user.getRole();
				role.removeUser(user);
				user.setRole(null);
				this.roleRepo.save(role);
				
				Set<Location> locs = user.getLocations();
				
				for(Location loc : locs) {
					loc.removeUser(user);
				}
				this.locationRepo.saveAll(locs);
				user.setLocations(null);
				
				
				userRepository.save(user);
								
				
				
				userRepository.delete(user);
				log.info("Deleted User " + user.getName() + " (ID:" + user.getId() + ") For Company " + user.getCompany().getCompanyName() + " (ID:" + user.getCompany().getId() + ")");

				ansr = "addPass";
				mess = "User '" + user.getFirstName() + " " + user.getLastName() + "' has been deleted";
				model.addAttribute("ansr", ansr);
				model.addAttribute("mess", mess);
			}

			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
			model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo,  model);
			model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);
			return "adminUsers";
			// return "redirect:/adminUsers/?keyword=" + keyword + "&perPage=" +
			// perPage.toString() + "&sort=" + sort;
		}
	}

	/**
	 * Method for attempting to apply changes that a user has applied for on their
	 * "My Account" page. The method calls the comparingMethod from an instance of
	 * AdminMethodsService to do some checking and add messages to the webpage.
	 * 
	 * @param id    is a long used to hold the id of the user having the changes
	 *              applied to.
	 * @param user  is a User object used to hold the changes attempting to be
	 *              applied in the database.
	 * @param model is a Model object used to add attributes to a webpage.
	 * @return userSettings html webpage.
	 */
	@PostMapping("/change/{id}")
	public String changeUser(Authentication auth, @PathVariable("id") long id, @ModelAttribute("user") userDao user, Model model) { //@ModelAttribute ("user1") User user1,
		User user2 = userRepository.findByid(id);

		//System.out.println("user html object pw:" + user.getPassword());

		//User user3 = adminMethodsService.comparingMethod(id, user, user2, model);

		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = userRepository.findById(idnum).orElse(null);
		//System.out.println(currentUser.getPassword());

		String userRole = currentUser.getRole().getName();

		//		model.addAttribute("role", user2.getRole());

		model.addAttribute("id", user2.getId());

		//model.addAttribute("user", user2);

		model.addAttribute("UserRole", userRole);

		String checkemail = user.getEmail();
		if (userRepository.findByEmail(checkemail) != null && userRepository.findByEmail(checkemail).getId() != currentUser.getId()) {
			model.addAttribute("user", user2);
			log.warn("You cannot set your email to an email belonging to another user.");
			return "userSettings";
		}

//		user3.setFirstName(adminMethodsService.capitalize(user3.getFirstName()));
//		user3.setLastName(adminMethodsService.capitalize(user3.getLastName()));

		user2.setFirstName(user.getFirstName());
		user2.setLastName(user.getLastName());
		user2.setSuffixName(user.getSuffixName());
		user2.setEmail(user.getEmail());
		if (!user.getPassword().isBlank()) {
			user2.setEncryptedPassword(user.getPassword());
		}

		//System.out.println(user2);
		//System.out.println(user2.getPassword());

		userRepository.save(user2);

		model.addAttribute("user", user2);

//		userRepository.save(user2);
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);
		model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);

		return "userSettings";
	}

	@GetMapping("/user")
	public String revieweeDashboard(Authentication auth){
		User loggedInUser;

		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long userId = userD.getID();

		loggedInUser = userRepository.findById(userId).orElse(null);

		return "";
	}
	
	
	
	
	@GetMapping("/deactivateUser/{id}")
	public Object deactivateUser(RedirectAttributes redirect, @PathVariable("id") long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		
		
		if(user.isDeactivated()) {
			user.setDeactivated(false);
		}else {
			user.setDeactivated(true);
		}
		
		userRepository.save(user);
		
		System.out.println("Deactivated User");
		
		return "redirect:/adminUsers";
	}
	

}
