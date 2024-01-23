package edu.sru.WebBasedEvaluations.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;
/**
 * Controls the  Reviewee behavior  of the application 
 *
 */
@Controller
public class RevieweeController {	
	
	private EvaluatorRepository evaluatorRepository;
	private UserRepository userRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private RevieweeRepository revieweeRepository;
	private EvaluationRepository evalFormRepo;
	private EvalRoleRepository roleRepository;
	private GroupRepository groupRepository;
	private Logger log = LoggerFactory.getLogger(RevieweeController.class);

	
	//create an UserRepository instance - instantiation (new) is done by Spring
    public RevieweeController (GroupRepository groupRepository,EvaluatorRepository evaluatorRepository,UserRepository userRepository ,EvaluationLogRepository evaluationLogRepository,RevieweeRepository revieweeRepository,EvaluationRepository evalFormRepoprivate, EvalRoleRepository roleRepository,EvaluationRepository  evalFormRepo) {
    	this.revieweeRepository= revieweeRepository;
		this.evaluatorRepository  = evaluatorRepository;
		this.userRepository  = userRepository;
		this.evaluationLogRepository= evaluationLogRepository;
		this.evalFormRepo =evalFormRepo;
		this.roleRepository =roleRepository;
		this.groupRepository = groupRepository;
	}
	
  
  	/**
	 * gets all the evaluation made on the selected reviewee and display for the reviewee
	 * @param model
	 * model:
	 * groups:list of groups assciated with the reviewee
	 * reviewee is the reviewee associated with the user 
	 * @param authentication user details
	 * @return myEval page
	 */
    @GetMapping("/myEval")
    public Object getreviewee(Model model, Authentication authentication) {
    	
    	
    	User user2;
		
		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();

		Long id = userD.getID();

		user2 = this.userRepository.findById(id).orElse(null);

		
		boolean showUserScreen = true;
		boolean rolesNeedAdded = false;
		
		Set<Role> companyRoles = user2.getCompany().getRoles();
		List<Role> rolesList = new ArrayList<Role>();
		Set<Department> companyDepartments = user2.getCompany().getDepartments();
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
		
    	System.out.println("");
    	List<Reviewee> reviewee = revieweeRepository.findByuser_Id(id);
    	List<Group> grouplist = (List<Group>) groupRepository.findByevaluatorUserId(userD.getID());
    	model.addAttribute("groups", grouplist);

    	List<EvalRole> roles = (List<EvalRole>) roleRepository.findAll();
    	
    	model = AdminMethodsService.pageNavbarPermissions(user2, model, evaluatorRepository, evalFormRepo);

    	model.addAttribute("myRole", userD.getRole());
    	model.addAttribute("role", roles);
    	model.addAttribute("id", userD.getID());
    	model.addAttribute("groups", grouplist);
    	model.addAttribute("reviewee", reviewee);
    	model.addAttribute("showUserScreen", showUserScreen);
    	model.addAttribute("rolesNeedAdded", rolesNeedAdded);
    	
    	//hides "Manage Roles" from navbar
    	if(user2.isSuperUser()) {
    		model.addAttribute("COMPANY_ADMIN", false);
    	}
    	
    	
    	
    	System.out.println(user2.getName());
    	System.out.println(reviewee.size());
    	System.out.println(grouplist.size());
    	System.out.println(roles.size());
    	return "myEval";

    }

	
	/**
	 * gets all the evaluation made on the selected reviewee and display them for the admin
	 * @param model
	 * model:
	 * reviewee: list of reviewee associated with the id 
	 * @param id the reviewee id 
	 * @return adminEval page
	 */
	@GetMapping("/admineval/{id}")
	public Object getrevieweegroup(Model model,@PathVariable("id") long id, Authentication auth) {
		
		
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);
		String UserRole=currentUser.getRole().getName();
		List<Reviewee> reviewee = revieweeRepository.findByuser_Id(id);
		
			
		List<EvalRole>roles = (List<EvalRole>) roleRepository.findAll();
			
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);	
		model.addAttribute("group", roles);
		model.addAttribute("role", roles);
		model.addAttribute("id",id);
		model.addAttribute("UserRole",UserRole);
		model.addAttribute("User",currentUser);
		model.addAttribute("reviewee", reviewee);
		return "adminEval";
		
	}
	
	@GetMapping("/admineval/{id}/{gid}")
	public Object getrevieweeGroup(Model model,@PathVariable("id") long id, @PathVariable("gid") long gid, Authentication auth) {
		
		
		User currentUser;
		Group g = groupRepository.findById(gid);
		
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);
		String UserRole=currentUser.getRole().getName();
		Reviewee rev = revieweeRepository.findById(id);
				
			
		List<EvalRole>roles = (List<EvalRole>) roleRepository.findAll();
			
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);	
		model.addAttribute("group", g);
		model.addAttribute("role", roles);
		model.addAttribute("id",id);
		model.addAttribute("UserRole",UserRole);
		model.addAttribute("User",currentUser);
		model.addAttribute("reviewee", rev);
		return "adminEval";
		
	}


}
