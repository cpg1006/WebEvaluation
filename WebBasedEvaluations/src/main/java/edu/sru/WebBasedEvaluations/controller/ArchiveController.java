package edu.sru.WebBasedEvaluations.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.domain.Archive;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.WebBasedEvaluations.repository.ArchiveRepository;
import edu.sru.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;
/**
 * Controls the Archive behavior  of the application 
 *
 */
@Controller
public class ArchiveController {
	
	private GroupRepository groupRepository;

	private UserRepository userRepository;
	
	
	
	private EvaluatorRepository evaluatorRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private RevieweeRepository revieweeRepository;
	private EvalRoleRepository roleRepository;
	private EvaluationRepository evaluationRepository;
	private EvaluationRepository evalFormRepo;
	private ArchiveRepository archiveRepository ;
	public ArchiveController(ArchiveRepository archiveRepository,GroupRepository groupRepository, UserRepository userRepository,
			EvaluatorRepository evaluatorRepository, RevieweeRepository revieweeRepository,
			EvaluationLogRepository evaluationLogRepository, EvalRoleRepository roleRepository,
			EvaluationRepository evaluationRepository, EvaluationRepository evalFormRepo) {
		this.evaluatorRepository = evaluatorRepository;
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
		this.revieweeRepository = revieweeRepository;
		this.evaluationLogRepository = evaluationLogRepository;
		this.roleRepository = roleRepository;
		this.evaluationRepository = evaluationRepository;
		this.evalFormRepo = evalFormRepo;
		this.archiveRepository=archiveRepository ;
	}


	/**
	 * Display all the achieved evaluation to the the admin 
	 * @param model
	 * Models:
	 * archive: list of archive evaluation
	 * @param authentication user details
	 * @return adminArchive page 
	 */
	@GetMapping("/Archivegroups")
	public String evalGroups(Model model, Authentication authentication) {
		
		boolean groupButton = false;
		
		
		User user;
		
		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();

		Long idnum = userD.getID();

		user = this.userRepository.findById(idnum).orElse(null);
		
		boolean showUserScreen = true;
		boolean rolesNeedAdded = false;
		
		Set<Role> companyRoles = user.getCompany().getRoles();
		List<Role> rolesList = new ArrayList<Role>();
		Set<Department> companyDepartments = user.getCompany().getDepartments();
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
		
		
		
		List<Archive> Archivelist = (List<Archive>) archiveRepository.findAll();
		System.out.print(Archivelist.size());
		
		if (evaluationRepository == null || evaluationRepository.count() == 0) {
			groupButton = false;
		} else {
			groupButton = true;
		}
		
		
		//navbar controls
		model = AdminMethodsService.pageNavbarPermissions(user, model, evaluatorRepository, evalFormRepo);
		
		

		model.addAttribute("archive", Archivelist);
		
		List<EvalRole> roles = (List<EvalRole>) roleRepository.findAll();
		model.addAttribute("groupButton", groupButton);

		model.addAttribute("role", roles);
		model.addAttribute("id", userD.getID());
		model.addAttribute("evalu", user);
		model.addAttribute("showUserScreen", showUserScreen);
		model.addAttribute("rolesNeedAdded", rolesNeedAdded);
		return "adminArchive";
	}
	/**
	 * @param id of the archive
	 * @param model
	 * @param authentication user details
	 * @return previewEval and displays selected evaluation 
	 */
	@GetMapping("/ViewArchive/{id}")
	public String ViewViewArchive(@PathVariable("id") long id, Model model, Authentication authentication) {

		User user;
		
		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();

		Long idnum = userD.getID();

		user = this.userRepository.findById(idnum).orElse(null);

		
		
		Archive	archive= archiveRepository.findById(id).orElse(null);
		 Evaluation evall;
		 evall = (Evaluation) SerializationUtils.deserialize(archive.getPath());

		
		
		List<EvalRole> roles = (List<EvalRole>) roleRepository.findAll();
	 model.addAttribute("eval", evall);
		model.addAttribute("role", roles);
		model.addAttribute("id", userD.getID());
		model.addAttribute("evalu", user);
		model.addAttribute("address", "/Archivegroups");
		return "previewEval";
	}
	
	
	
}
