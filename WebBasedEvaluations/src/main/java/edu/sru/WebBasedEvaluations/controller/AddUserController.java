package edu.sru.WebBasedEvaluations.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.excel.ExcelRead_group;
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
 * Class for housing the methods for controlling how to add users via
 * manually/uploading
 * 
 * @author Dalton Stenzel
 *
 */
@Controller
public class AddUserController {
	// set up a UserRepositoty variable
	private UserRepository userRepository;
	private RoleRepository roleRepo;
	private CompanyRepository companyRepo;
	private LocationRepository locationRepo;
	private DepartmentRepository deptRepo;

	@Autowired
	private AdminMethodsService adminMethodsService;

	@Autowired
	private EvaluatorRepository evalRepo;


	@Autowired
	private EvaluationRepository evalFormRepo;

	private Logger log = LoggerFactory.getLogger(AddUserController.class);
	
	private final String WINDOWS_FILES_PATH = "src\\main\\resources\\temp\\"; //windows uses '\'
	private final String MAC_FILES_PATH = "src//main//resources//temp//"; //macOS and Linux uses '/'
	
	private String workingFilesPath;

	public AddUserController(UserRepository userRepository,RoleRepository roleRepository,CompanyRepository companyRepo,LocationRepository locationRepo, DepartmentRepository deptRepo) {
		this.deptRepo = deptRepo;
		this.userRepository = userRepository;
		this.roleRepo = roleRepository;
		this.companyRepo = companyRepo;
		this.locationRepo = locationRepo;
		
		//sets appropriate file path depending on operating system
		if(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
			this.workingFilesPath = MAC_FILES_PATH;
		}
		else {
			this.workingFilesPath = WINDOWS_FILES_PATH; 
		}
	}

	//	@PostMapping
	//	(value = "/adduser/")
	//    public ResponseEntity<User> addUserPost(HttpServletRequest request,
	//                                        UriComponentsBuilder uriComponentsBuilder) {
	//
	//        var content = request.getParameter("content");
	//
	//        String name = request.getParameter("name");
	//    	String firstName = request.getParameter("firstName");
	//    	String lastName = request.getParameter("lastName");
	//    	String email = request.getParameter("email");
	//    	String password = request.getParameter("password");
	//    	String role = request.getParameter("role");
	//		String dateOfHire = request.getParameter("dateOfHire");
	//		String jobTitle = request.getParameter("jobTitle");
	//		String supervisor = request.getParameter("supervisor");
	//		String divisionBranch = request.getParameter("divisonBranch");
	//		Company co = new Company(null);
	//        
	//        User user = new User(name, firstName, lastName, email, password, role, 1, dateOfHire, jobTitle, supervisor, divisionBranch, co);
	//        
	//        //this.addUser(user);
	//
	//        UriComponents uriComponents =
	//                uriComponentsBuilder.path("/adminUsers/{id}").buildAndExpand(user.getId());
	//        var location = uriComponents.toUri();
	//
	//        return ResponseEntity.created(location).build();
	//    }

	/**
	 * Method for manually adding users from the admin user page. It calls a few
	 * methods from the AdminMethodsService class for checking for any changes,
	 * capital letters, spaces, problems, etc,. This method is called when the "Add
	 * User" button is pressed.
	 * 
	 * @param user     is a User object used that holds the information submitted
	 *                 from the "Add User" button
	 * @param result   is a BindResult object used in conjunction with "@Validated"
	 *                 tag in order to bind the submission to an object.
	 * @param model    is a Model object used for adding attributes to a webpage,
	 *                 mostly used for adding messages and lists to the page.
	 * @param auth	   The authentication of the currently logged in user. 
	 * @param keyword  is a String used to hold a particular set of characters being
	 *                 sought after in a list of users.
	 * @param perPage  is an Integer value used to store the amount of users to be
	 *                 displayed per page.
	 * @param sort     is a String value user to contain the type of sorting to be
	 *                 used on a list of users such as: first name, last name,
	 *                 email, role.
	 * @param currPage is an Integer value used to store the current page number the
	 *                 user was on.
	 * @param sortOr   is an Integer value used to determine the order in which the
	 *                 sort will take place: ascending(1) or descending(0).
	 * @return adminUsers html webpage.
	 */
	@PostMapping("/adduser")
	public String  addUser(@Validated @ModelAttribute("user") User user, BindingResult result, Model model, Authentication auth,
			/* @RequestParam("keyword") */ String keyword, @RequestParam("perPage") Integer perPage,
			@RequestParam("sort") String sort, @RequestParam("currPage") Integer currPage,
			@RequestParam("sortOr") Integer sortOr, HttpSession session) {
		
		String ansr = null;
		String mess = null;		
		boolean check = false;
		user.setAdminEval(false);
		user.setCompanySuperUser(false);
		user.setSuperUser(false);
		User adminUser = user;

	
		
		User currentUser;
		Company currentCompany;
		
		
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);
		String UserRole=currentUser.getRole().getName();

		model.addAttribute("UserRole",UserRole);
		currentCompany = currentUser.getCompany();
		model.addAttribute("depts",currentCompany.getDepartments());
		System.out.println("this is incoming user: "+user.getLastName());

		if(currentUser.isSuperUser()) {
			user.setJobTitle(user.getCompanyName()+" Admin");
			user.setSupervisor("none"); //not sure what the default should be for admins
			user.setDepartmentName("admin dept");
			Company userCompany = companyRepo.findByCompanyName(user.getCompanyName());
			
			Role userRole = roleRepo.findByNameAndCompany("ADMIN", userCompany);
			
			if(Objects.isNull(userRole)){
				Role adminRole = new Role("ADMIN", currentCompany);
				user.setRole(adminRole);
			}
			
		}
		

		if (userRepository.findByEmail(user.getEmail()) == null) {

			check = adminMethodsService.checkAndUpdate(user, session);
//			boolean missedField = false;
			//check if admin has permission to add this user to the dept/location/role
			//departments of the user being added

			Department dept = deptRepo.findByNameAndCompany(user.getDepartmentName(), currentCompany);
			String locationDefaultMatch = "none"; 
			String locName = "none";
			Set<Location> allLocs = currentCompany.getLocations();
			Iterator<Location> locs = allLocs.iterator();
			while(locs.hasNext()) {
				String nextLoc = locs.next().getLocationName();
				if(nextLoc.equals(locationDefaultMatch)) {
					locName = nextLoc;
				} else {
					System.out.println("@@" + nextLoc);
				}
			}
			Location loc = locationRepo.findByLocationNameAndCompany(user.getDivisionBranch(), currentUser.getCompany());
			Company co = companyRepo.findByCompanyName(user.getCompanyName());
			user.setCompany(co);
			Role role ;

			if(currentUser.getRole().getName().equalsIgnoreCase("SUPERUSER")){
				role=new Role("ADMIN",currentCompany);
				user.setRole(role);
			}
			else if(currentUser.getRole().getName().equalsIgnoreCase("ADMIN")){
				role = new Role("EVALUATOR_ADMIN", currentCompany);
				user.setRole(role);
			}
			else{
				role=null;
			}
			try {
				System.out.println(user.getRoleName());
				System.out.println(user.getRole().getName());
			}
			catch (Exception e) {
				System.out.println("doesnt have a role");
			}

			if(role != null) {
				if(currentUser.getRole().contains(role) || currentUser.isCompanySuperUser() || currentUser.isSuperUser()) {
					user.setRole(role);
				}
				else {
					mess = "User " + adminUser.getName() + " does not have permission to grant a user the " + role.getName() + " role.";
					check = false;
				}
			}
			else {
				Role newRole = new Role(user.getRole().getName(), currentCompany);
				co.addRole(newRole);

				user.setRole(newRole);
				System.out.println("this is the user Role: "+user.getRoleName());
			}
			if(dept != null) {
				if((currentUser.getRole().writableDepartments().contains(dept) || currentUser.isCompanySuperUser() || currentUser.isSuperUser()) && !user.getDepartments().contains(dept)) {
					user.addDepartment(dept);
				}
				else if(!user.getDepartments().contains(dept)) //fires on fail
				{
					mess = "User " + adminUser.getName() + " already has the dept: " + dept.getName();
				}
				else{
					mess = "User " + adminUser.getName() + " does not have permission to add a user to department " + dept.getName();
					check = false;
				}
			}
			if(loc != null) {
				if(currentUser.getRole().writableLocations().contains(loc) || currentUser.getRole().getName().equalsIgnoreCase("COMPANYSUPERUSER") || currentUser.getRole().getName().equalsIgnoreCase("SUPERUSER")){
					user.addLocation(loc);
				}
				else if(currentUser.getRole().getName().equalsIgnoreCase("ADMIN")){
					user.addLocation(loc);
				}
				else {
					mess = "User " + currentUser.getName() + " does not have permission to add a user to location " + loc.getLocationName();
					check = false;
				}	
			}

			if (check == true) {

				user.setFirstName(adminMethodsService.capitalize(user.getFirstName()));
				user.setLastName(adminMethodsService.capitalize(user.getLastName()));
				if (user.getSuffixName() == " " || user.getSuffixName() == null) {

					user.setName(user.getFirstName() + " " + user.getLastName());

				} else {

					user.setName(user.getFirstName() + " " + user.getLastName() + " " + user.getSuffixName());

				}
				
				// was initially checking for "adminEval" and resulted in the condition always being false
				if(user.getRole().getName().contains("ADM_EVAL")) {
					user.setAdminEval(true);

				}
				user.setEncryptedPassword(user.getPassword());
				user.setReset(true);
				boolean worked = true;
				try {
					userRepository.save(user);
				}
				catch(Exception e) {
					
					
//					missedField = true;
//					model.addAttribute("missedField", missedField);
//					model.addAttribute("enteredEmail", user.getEmail());
//					
					ansr = "addFail";
					mess = "Error occured!";	
					worked=false;
					log.error(e.getStackTrace().toString());
				}

				if(worked) {
					log.info("ADMIN User - ID:" + adminUser.getId() + " First Name: " + adminUser.getFirstName()
					+ " Last Name: " + " added a " + user.getRole().getName() + " user");

					ansr = "addPass";
					mess = "User successfully added!";
					model.addAttribute("superUser",userRepository.findByRoleNameEquals("SUPERUSER"));
					model.addAttribute("adminUser",userRepository.findByRoleNameEquals("ADMIN"));
					model.addAttribute("Eval_admin",userRepository.findByRoleNameEquals("EVALUATOR_ADMIN"));

					adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
					return "redirect:/adminUsers/?keyword=&perPage=0&sort=id&currPage=1&sortOr=1";

				}
			} else {
				ansr = "addFail";
				if(mess == null) {
					
					mess = (String) session.getAttribute("error");
				}

				// adminUserPageItems(ansr, keyword, mess, perPage, model, sort);
				adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
				return "redirect:/adminUsers/?keyword=&perPage=0&sort=id&currPage=1&sortOr=1";
			}
			
			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
		
//			model = AdminMethodsService.pageNavbarPermissions(currentUser, model, this.evalRepo, evalFormRepo);//error on broken field
			model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
			Set<Location> locations=currentUser.getCompany().getLocations();
			model.addAttribute("locations",locations);

			System.out.println(result.toString());
			return "redirect:/adminUsers/?keyword=&perPage=0&sort=id&currPage=1&sortOr=1";
//			return "adminUsers"; //On fail hits this return
		}

//		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, this.evalRepo, evalFormRepo);
//		model = AdminMethodsService.addingOrEditingUser(adminUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);

		if(result.hasErrors()) {

			model.addAttribute("users", userRepository.findAll());
			return "redirect:/adminUsers/?keyword=&perPage=0&sort=id&currPage=1&sortOr=1";
		}

		else {
			ansr = "addFail";
			mess = "User email already taken!";

			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
			return "redirect:/adminUsers/?keyword=&perPage=0&sort=id&currPage=1&sortOr=1"; // if there is no error when adding a user run addedUser Html page

		}

	}



	/**
	 * Method called when the "Upload Users" button is pressed. It will attempt
	 * check the file uploaded, if there is one in the first place, and will log
	 * information about users unable to be added. It checks if there was a file
	 * selected and also adds messages about when users were or were not
	 * successfully added.
	 * 
	 * @param reapExcelDataFile is a MultipartFile object
	 * @param user              is a User object that is required for the page,
	 *                          other an IllegalStateException error will occur.
	 * 
	 * @param model             is a Model object used for adding attributes to a
	 *                          webpage, mostly used for adding messages and lists
	 *                          to the page.
	 * @param keyword           is a String used to hold a particular set of
	 *                          characters being sought after in a list of users.
	 * @param perPage           is an Interger value used to store the amount of
	 *                          users to be displayed per page.
	 * @param sort              is a String value user to contain the type of
	 *                          sorting to be used on a list of users such as: first
	 *                          name, last name, email, role.
	 * @param currPage          is an Integer value used to store the current page
	 *                          number the user was on.
	 * @param sortOr            is an Integer value used to determine the order in
	 *                          which the sort will take place: ascending(1) or
	 *                          descending(0).
	 * @return adminUsers html webpage.
	 * @throws IOException 
	 */
	@RequestMapping(value = "/uploaduser2", method = RequestMethod.POST)
	public Object uploaduser2(@RequestParam("file") MultipartFile reapExcelDataFile, RedirectAttributes redir,
			@RequestParam("perPage") Integer perPage, @Validated User users, /* BindingResult result, */ Model model,
			String keyword, @RequestParam("sort") String sort, @RequestParam("currPage") Integer currPage,
			@RequestParam("sortOr") Integer sortOr, Authentication auth) throws IOException {

		String ansr;
		String mess;
		
		
		User currentUser;
		Company currentCompany;
		
		List<ImmutablePair<User, Boolean>> skippedUsersList = new ArrayList<ImmutablePair<User, Boolean>>();
	
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);

		String UserRole=currentUser.getRole().getName();
		model.addAttribute("UserRole",UserRole);

		currentCompany = currentUser.getCompany();
		
		boolean check = false;
		XSSFSheet sheet = null;
		try {
			sheet = ExcelRead_group.loadFile(reapExcelDataFile).getSheetAt(0);

		} catch (Exception e) {
			if (e instanceof Exception) {

				mess = "No file selected!";
				ansr = "addFail";
				adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr,auth);
				model = AdminMethodsService.pageNavbarPermissions(currentUser, model, this.evalRepo, evalFormRepo);
				model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
				return "adminUsers";

			}
		}

		if (ExcelRead_group.checkStringType(sheet.getRow(0).getCell(1)).equals(null)
				|| !ExcelRead_group.checkStringType(sheet.getRow(0).getCell(1)).equals("User Upload")) {

		}

		else if (ExcelRead_group.checkStringType(sheet.getRow(0).getCell(1)).equals("User Upload")) {

			for (int i = 2; sheet.getRow(i) != null; i++) {
				try {
					User user2 = new User();
					User tempUser = userRepository
							.findByEmail(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(3)));
					if (tempUser == null) {
						if (ExcelRead_group.checkStringType(sheet.getRow(i).getCell(2)) == " "
								|| ExcelRead_group.checkStringType(sheet.getRow(i).getCell(2)) == null) {

							String str1 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(0)));
							String str2 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(1)));

							user2.setName(str1 + " " + str2);
							user2.setFirstName(str1);
							user2.setLastName(str2);

						} else {
							String str1 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(0)));
							String str2 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(1)));
							String str3 = adminMethodsService
									.capitalize(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(2)));

							user2.setName(str1 + " " + str2 + " " + str3);
							user2.setFirstName(str1);
							user2.setLastName(str2);
							user2.setSuffixName(str3);

						}
						user2.setEmail(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(3)));
						user2.setEncryptedPassword(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(0))+"23");

						//if the role exists add it to the user, otherwise create a role that has that name, but no permissions. 
						String roleName = (ExcelRead_group.checkStringType(sheet.getRow(i).getCell(5)));
						System.out.println(roleName);
						
						
						
						//prevents SuperSuperUser from uploading non-admin users
						if(!currentUser.getRole().getName().equals("ADMIN")) {
							if(!skippedUsersList.contains(user2)) {
								ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
								skippedUsersList.add(userPair);
							}
							throw new Exception("Only ADMIN can only add admins.");
							
						}
						
						user2.setDateOfHire(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(7)));
						user2.setJobTitle(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(8)));
						String deptName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(9));		
						user2.setDepartmentName(deptName);
						boolean setAsDeptManager = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10));
						String companyName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(11));
						user2.setCompanyName(companyName);
						String locationName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(12));

						Location loc = this.locationRepo.findByLocationNameAndCompany(locationName, currentCompany);


						user2.setDivisionBranch(locationName);


						Company co = companyRepo.findByCompanyName(companyName);

						//check that the company exists and that the logged in user has permission to add a user to it. 
						if(co != null ){

							if(co.getId() == currentCompany.getId()){
								user2.setCompany(co);
								System.out.println("set User to Company--------");
							}
							else {
								if(!skippedUsersList.contains(user2)) {
									ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
									skippedUsersList.add(userPair);
								}
								System.out.println("Failed to set User to company");
								throw new Exception("User:" + currentUser.getName() + " does not have access to company " + companyName);
							}
						}
						else {
							if(!skippedUsersList.contains(user2)) {
								ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
								skippedUsersList.add(userPair);
							}
							throw new Exception("Company" + companyName + " does not exist, please add it before attempting to add users to it.");
						}

						//check that the location exists and that the logged in user has permission to add a user to it. 
						if(loc != null ){
							if(currentUser.getRole().writableLocations().contains(loc) || currentUser.getRole().getName().equalsIgnoreCase("ADMIN")){
								user2.addLocation(loc);
							}
							else {
								if(!skippedUsersList.contains(user2)) {
									ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
									skippedUsersList.add(userPair);
								}
								throw new Exception("User:" + currentUser.getName() + " does not have access to location " + locationName);
							}
						}
						else {
							if(currentUser.getRole().writableLocations().contains(loc) || currentUser.isCompanySuperUser() || currentUser.isSuperUser()){
								loc = new Location(locationName,null,currentCompany,null);
								user2.addLocation(loc);
							}
							else {
								if(!skippedUsersList.contains(user2)) {
									ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
									skippedUsersList.add(userPair);
								}
								throw new Exception("User:" + currentUser.getName() + " does not have access to location " + locationName);
							}
						}
						System.out.println("About to Find Roles in Database");
						Role role;
						List<Role> roleOptional = roleRepo.findRolesByNameAndCompany(roleName, currentCompany);

						if (!roleOptional.isEmpty()) {
							role = roleOptional.get(0);

						} else {
							role=null;
						}

						System.out.println("Found Multiple results");
						if(!role.getActivation()) {
							ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
							skippedUsersList.add(userPair);
							throw new Exception("Role set for user "+user2.getName()+" is not activated.");
						}
						
						if(role != null) {
							if(currentUser.getRole().contains(role) || currentUser.isCompanySuperUser() || currentUser.isSuperUser()) {
								user2.setRole(role);
								System.out.println("Setting Role to User");
							}
							else {
								if(!skippedUsersList.contains(user2)) {
									ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
									skippedUsersList.add(userPair);
								}
								System.out.println("Failed to assign Role to user");
								throw new Exception("current user " + currentUser.getName() + " does not have permission to assign role "  +role.getName()+ " to a user.");
							}
						}
						else {							
							Role newRole = new Role(roleName,currentCompany);
							user2.setRole(newRole);
							ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
							skippedUsersList.add(userPair);
							throw new Exception(roleName+" does not exist. It needs uploaded before a user can be assigned to it.");
						}

						//sets the department and supervisor based on the dept we are adding the user to.

						Department dept = this.deptRepo.findByNameAndCompany(deptName, currentCompany);

						System.out.println(dept);
						System.out.println(user2.getDepartmentName());
						if(dept != null) {
							if(currentUser.getRole().writableDepartments().contains(dept) || currentUser.isCompanySuperUser() || currentUser.getRole().getName().equalsIgnoreCase("ADMIN")) {

								if(setAsDeptManager || dept.getDeptHead() == null) {
									dept.setDeptHead(user2);
									user2.addSubDept(dept);
									for(User user : dept.getUsers()) {
										user.setSupervisor(user2.getName());
									}
								}
								else {
									user2.setSupervisor(dept.getDeptHead().getName());
								}
								user2.addDepartment(dept);
								dept.addUser(user2);
							}
							else {
								if(!skippedUsersList.contains(user2)) {
									ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
									skippedUsersList.add(userPair);
								}
								throw new Exception("current user " + currentUser.getName() + " does not have permission to add user "  +user2.getName()+ " to a dept " + dept.getName());
							}
						}
						else {
							if(!skippedUsersList.contains(user2)) {
								ImmutablePair<User, Boolean> userPair = new ImmutablePair<User, Boolean>(user2, ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(10)));
								skippedUsersList.add(userPair);
							}
							throw new Exception(user2.getName()+" cannot be added, because the department they are in does not yet exist. The department '"+deptName+"' must be added to the company first.\n");
						}			

						user2.setReset(true);
						if(user2.getRole().getName().contains("ADMIN")) {
							user2.setAdminEval(true);
						}



						userRepository.save(user2);
						log.info("Uploaded User " + user2.getName() + " (ID:" + user2.getId() + ")");

						check = true;

					}
					else {
						log.error("User " + tempUser.getEmail() + " already exists.");
					}

				}

				catch (Exception e) {

					log.error("Could not add user in row: " + (sheet.getRow(i).getRowNum() + 1) + " from "
							+ reapExcelDataFile.getOriginalFilename()
							+ ". Either null, email already taken, or incorrect information!\n" + e.getMessage());
					model.addAttribute("log", "error");
				}
			}
		}
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, this.evalRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		
		if(!skippedUsersList.isEmpty()) {
			createSkippedUsersExcel(skippedUsersList);
		}
		

		if (check) {
			log.info("ADMIN User - ID:" + currentUser.getId() + " First Name: " + currentUser.getFirstName()
			+ " Last Name: " + " uploaded a file: " + reapExcelDataFile.getOriginalFilename());

			mess = "File uploaded! User(s) successfully added!";
			ansr = "addPass";

			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
			model = AdminMethodsService.pageNavbarPermissions(currentUser, model, this.evalRepo, evalFormRepo);
			model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);

			return "redirect:/adminUsers/?keyword=&perPage=0&sort=id&currPage=1&sortOr=1";

		} else {

			mess = "File failed to be uploaded!";
			ansr = "addFail";

			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
			model = AdminMethodsService.pageNavbarPermissions(currentUser, model, this.evalRepo, evalFormRepo);
			model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);

			return "redirect:/adminUsers/?keyword=&perPage=0&sort=id&currPage=1&sortOr=1";

		}

	}

	/**
	 * creates the file that contains users who were skipped during upload 
	 * @param skippedUsers list of users who were skipped during upload
	 * @throws IOException
	 */
	public void createSkippedUsersExcel(List<ImmutablePair<User, Boolean>> skippedUsers) throws IOException{
		File path = new File(workingFilesPath);
		
		if(!path.exists()) {
			Files.createDirectories(Paths.get(workingFilesPath));
		}
		
		FileUtils.cleanDirectory(new File(workingFilesPath));
	
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Non-Added Users");
		final String FILE_NAME = "Non-Added Users.xlsx";
		String filePath = workingFilesPath + FILE_NAME;
		File file = new File(filePath);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		
		//row 1
		sheet.createRow(0);
		XSSFRow workingRow = sheet.getRow(0);
		
		workingRow.createCell(0).setCellValue("File Type");
		workingRow.createCell(1).setCellValue("User Upload");
		
		//row 2
		sheet.createRow(1);
		workingRow = sheet.getRow(1);
		workingRow.createCell(0).setCellValue("FIRST NAME");
		workingRow.createCell(1).setCellValue("LAST NAME");
		workingRow.createCell(2).setCellValue("TITLE");
		workingRow.createCell(3).setCellValue("EMAIL");
		workingRow.createCell(4).setCellValue("PASSWORD");
		workingRow.createCell(5).setCellValue("ROLE");
		workingRow.createCell(6).setCellValue("RESET");
		workingRow.createCell(7).setCellValue("DATE OF HIRE");
		workingRow.createCell(8).setCellValue("JOB TITLE");
		workingRow.createCell(9).setCellValue("DEPT");
		workingRow.createCell(10).setCellValue("DEPT MANAGER");
		workingRow.createCell(11).setCellValue("COMPANY NAME");
		workingRow.createCell(12).setCellValue("DIVISION/BRANCH");
		
		//row 3 and beyond
		for(int i = 0; i < skippedUsers.size(); i++) {
			sheet.createRow(i+2);
			workingRow = sheet.getRow(i+2); 
			
			workingRow.createCell(0).setCellValue(skippedUsers.get(i).left.getFirstName());
			workingRow.createCell(1).setCellValue(skippedUsers.get(i).left.getLastName());
			workingRow.createCell(2).setCellValue(skippedUsers.get(i).left.getSuffixName());
			workingRow.createCell(3).setCellValue(skippedUsers.get(i).left.getEmail());
			workingRow.createCell(4).setCellValue(skippedUsers.get(i).left.getPassword());
			workingRow.createCell(5).setCellValue(skippedUsers.get(i).left.getRole().getName());
			workingRow.createCell(6).setCellValue(skippedUsers.get(i).left.getReset());
			workingRow.createCell(7).setCellValue(skippedUsers.get(i).left.getDateOfHire());
			workingRow.createCell(8).setCellValue(skippedUsers.get(i).left.getJobTitle());
			workingRow.createCell(9).setCellValue(skippedUsers.get(i).left.getDepartmentName());
			workingRow.createCell(10).setCellValue(skippedUsers.get(i).right);
			workingRow.createCell(11).setCellValue(skippedUsers.get(i).left.getCompanyName());
			workingRow.createCell(12).setCellValue(skippedUsers.get(i).left.getDivisionBranch());
		}
		
		workbook.write(fileOutputStream);
		workbook.close();
	}
	

	
	/**
	 * returns the skipped users excel file stored in resources/temp 
	 * 
	 * @return downloads the excel file
	 * @throws IOException
	 */
	@GetMapping("/download_skipped_users")
	public ResponseEntity<Resource> downloadSkippedUsersExcel() throws IOException {
		String skippedUsersExcelPath = workingFilesPath+ "Non-Added Users.xlsx";
		FileSystemResource resource = new FileSystemResource(skippedUsersExcelPath);
		MediaType mediaType = MediaTypeFactory
				.getMediaType(resource)
				.orElse(MediaType.APPLICATION_OCTET_STREAM);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		ContentDisposition disposition = ContentDisposition
				.attachment()
				.filename(resource.getFilename())
				.build();
		headers.setContentDisposition(disposition);

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		
	}
}
