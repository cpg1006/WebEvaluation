package edu.sru.WebBasedEvaluations.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.ui.Model;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.CompanyRepository;
import edu.sru.WebBasedEvaluations.repository.DepartmentRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;
import edu.sru.WebBasedEvaluations.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;


/**Class for controlling the starting sequence of users logging and taking them to the home page
 * @author Dalton Stenzel
 *
 */
@Controller
public class HomePage {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	private UserRepository userRepository;
	private EvaluatorRepository evaluatorRepository;
	private DepartmentRepository departmentRepository;
	private EvaluationRepository evaluationRepository;
	private Logger log = LoggerFactory.getLogger(HomePage.class);

	@Autowired
	private EvaluationRepository evalFormRepo;

	public HomePage(UserRepository userRepository, EvaluatorRepository evaluatorRepository,
			EvaluationRepository evaluationRepository) {
		this.userRepository = userRepository;
		this.evaluationRepository = evaluationRepository;
		this.evaluatorRepository = evaluatorRepository;
	}

	/*
	// Maps to the evaluator_home.html when called.
	@GetMapping("/evaluator_home")
	public String EvalHome(Authentication authentication, Model model) {
		return "evaluator_home";
	}
	*/

	// Maps to the base.html when called or when localhost:8080 is called.
	@GetMapping("/")
	public String base() {
		return "redirect:/login";
	}

	// Maps to the login.html when called.
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	// Maps to manageGroups.html when called.
	@GetMapping("/manageGroups")
	public String manageGroups() {
		return "manageGroups";
	}
	
	
	/**Method for logging users who have logged in.
	 * @param auth is an Authentication object used to identify who has logged in.
	 * @return home mapping.
	 */
	@GetMapping("/logging")
	public String loginLoging(Authentication auth, Model model) {
		MyUserDetails user = (MyUserDetails) auth.getPrincipal();
		User user2 = userRepository.findByid(user.getID());
		log.info("User logged in- ID:" + user2.getId() + " | First Name: " + user2.getFirstName() + " | Last Name: " +user2.getLastName() );
		
		log.info("Date:" + LocalDate.now());
		Calendar calendar = Calendar.getInstance();
		Date lastLogin = calendar.getTime();;
		
		
		user2.setLastLogin(lastLogin);
		userRepository.save(user2);
		
		
		Date userTwoDate = user2.getLastLogin();
		model = AdminMethodsService.pageNavbarPermissions(user2, model, evaluatorRepository, evalFormRepo);
		model.addAttribute("date", userTwoDate);
		return "redirect:/home";
	}	


	/**Method called upon being logged in and logged. Add's user attributes to show particular things based of user and evaluation form information.
	 * @param auth is an Authentication object used for identifying who is logged in.
	 * @param user3 is a User object needed in order to prevent an "IllegalStateException" error from occurring.
	 * @param model is a Model object used to add attributes to a webpage.
	 * @return home html webpage or redirect to firstReset mapping
	 */
//	@GetMapping("/home")
//	public String home(Authentication auth, User user3, /*BindingResult result,*/ Model model) {
//
//
//		User user2;
//
//		MyUserDetails user = (MyUserDetails) auth.getPrincipal();
//
//		Long idnum = user.getID();
//
//
//		user2 = this.userRepository.findById(idnum).orElse(null);
//
//		Company currentCompany = user2.getCompany();
//
//		boolean groupButton = false;
//
//		boolean showUserScreen = true;
//		boolean rolesNeedAdded = false;
//
//		Set<Role> companyRoles = currentCompany.getRoles();
//		List<Role> rolesList = new ArrayList<Role>();
//		Set<Department> companyDepartments = currentCompany.getDepartments();
//		List<Department> departmentsList = new ArrayList<Department>();
//		final int MIN_DEPARTMENT_SIZE = 2;
//		final int MIN_ROLES_SIZE = 2;
//		rolesList.addAll(companyRoles);
//		departmentsList.addAll(companyDepartments);
//
//		Department firstDepartment = departmentsList.get(0);
//		Role firstRole = rolesList.get(0);
//
//		//checks to ensure roles have been created before users can be uploaded or created
//		if(rolesList.size() < MIN_ROLES_SIZE && firstRole.getName().equals("COMPANYSUPERUSER")) {
//			showUserScreen = false;
//			rolesNeedAdded = true;
//		}
//
//		//will check to see if companies have been added before allowing users to be added
//		if(departmentsList.size() < MIN_DEPARTMENT_SIZE && firstDepartment.getName().equals("none")) {
//			showUserScreen = false;
//		}
//
//		if (evaluationRepository == null || evaluationRepository.count() == 0) {
//			groupButton = false;
//		} else {
//			groupButton = true;
//		}
//		if (user2.getReset() == true) {
//			return "redirect:/firstReset";
//		}
//		else {
//			//adds the permissions used for thenavbar to the model.
//			model = AdminMethodsService.pageNavbarPermissions(user2, model, evaluatorRepository, evalFormRepo);
//
//
//			model.addAttribute("EVALUATOR_EVAL", false);
//			model.addAttribute("groupButton", groupButton);
//			model.addAttribute("user", user.getUsername());
//			model.addAttribute("id", user.getID());
//			model.addAttribute("deptNames", user2.getDepartmentNames());
//			model.addAttribute("companyName", user2.getCompanyName());
//			model.addAttribute("firstName", user2.getFirstName());
//			model.addAttribute("lastName", user2.getLastName());
//			model.addAttribute("showUserScreen", showUserScreen);
//			model.addAttribute("rolesNeedAdded", rolesNeedAdded);
//			if(user2.isSuperUser()) {
//				model.addAttribute("roleName", "SuperSuperUser");
//				model.addAttribute("COMPANY_ADMIN", false);
//			}
//			else if(user2.isCompanySuperUser()) {
//				model.addAttribute("roleName",user2.getCompanyName() + " SuperUser");
//			}
//			else {
//				model.addAttribute("roleName", user2.getRoleName());
//			}
//			return "home";
//		}
//	}

	@GetMapping("/home")
	public String newHome(Authentication auth,Model model){
		User user;

		MyUserDetails logged_user = (MyUserDetails) auth.getPrincipal();

		Long idNum = logged_user.getID();


		user = this.userRepository.findById(idNum).orElse(null);

		Company currentCompany = user.getCompany();
		Role role=user.getRole();
		String userRole=role.getName();
		model.addAttribute("UserRole",userRole);
		model.addAttribute("User",user);
		return "newHome";
	}




	
	/**
	 * Processes the request to download the log.txt file.
	 * 
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/download_log_txt")
	public ResponseEntity<Resource> downloadEvalExcel() throws Exception {
		
		// Name of download file
		final String FILE_NAME = "log.txt";
		
		log.info("File '" + FILE_NAME + "' requested for download.");
		
		//Download the file
		FileSystemResource resource = new FileSystemResource(FILE_NAME);
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







//	@GetMapping("/processing")
//	public String processed() {
//		return "home";
//	}




}
