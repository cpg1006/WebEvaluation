package edu.sru.WebBasedEvaluations.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import edu.sru.WebBasedEvaluations.service.GroupService;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SerializationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.domain.Archive;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.EvaluatorId;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.SelfEvaluation;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.WebBasedEvaluations.evalform.GenerateEvalReport;
import edu.sru.WebBasedEvaluations.evalform.GenerateEvalReportPoi;
import edu.sru.WebBasedEvaluations.excel.ExcelRead_group;
import edu.sru.WebBasedEvaluations.repository.ArchiveRepository;
import edu.sru.WebBasedEvaluations.repository.CompanyRepository;
import edu.sru.WebBasedEvaluations.repository.DepartmentRepository;
import edu.sru.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;

/**
 * Group controller determines the group behavior in the application
 *
 */
@Controller
public class GroupController {

	private Logger log = LoggerFactory.getLogger(GroupController.class);

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupService groupService;

	@Autowired
	private DepartmentRepository deptRepo;

	private EvaluatorRepository evaluatorRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private RevieweeRepository revieweeRepository;
	private EvalRoleRepository evalRoleRepository;
	private EvaluationRepository evaluationRepository;
	private EvaluationRepository evalFormRepo;
	private ArchiveRepository archiveRepository ;
	private CompanyRepository companyRepo;
	private UserRepository userRepo;

	@Autowired
	private AdminMethodsService adminMethodsService;

	private final String TEMP_FILES_PATH = "src\\main\\resources\\temp\\";

	public GroupController(GroupRepository groupRepository, UserRepository userRepository,
			EvaluatorRepository evaluatorRepository, RevieweeRepository revieweeRepository,
			EvaluationLogRepository evaluationLogRepository, EvalRoleRepository roleRepository,
			EvaluationRepository evaluationRepository, EvaluationRepository evalFormRepo,
			ArchiveRepository archiveRepository, CompanyRepository companyRepo, UserRepository	userRepo
			) {
		this.evaluatorRepository = evaluatorRepository;
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
		this.revieweeRepository = revieweeRepository;
		this.evaluationLogRepository = evaluationLogRepository;
		this.evalRoleRepository = roleRepository;
		this.evaluationRepository = evaluationRepository;
		this.evalFormRepo = evalFormRepo;
		this.archiveRepository=archiveRepository;
		this.companyRepo = companyRepo;
		this.userRepo = userRepo;
	}


	@RequestMapping(value = "/addgroup", method = RequestMethod.POST)
	public String addSave(@ModelAttribute("group") Group group,
			@RequestParam(value = "rev", required = false) long[] rev,
			@RequestParam(value = "lone", required = false) long lone,
			@RequestParam(value = "ltwo", required = false) long ltwo,
			@RequestParam(value = "facetoface", required = false) long facetoface, BindingResult bindingResult,
			Model model, Authentication auth) {
		if (rev != null) {

			Reviewee reviewee = null;
			User user = null;
			for (int i = 0; i < rev.length; i++) {
				user = userRepository.findByid(rev[i]);
				reviewee = new Reviewee(group, user.getName(), user);
				group.appendReviewee(reviewee);
			}
			for (int i = 0; i < group.getReviewee().size(); i++) {

			}
			long id = group.getId();
			groupRepository.save(group);
			Company currentCompany = group.getCompany();

			Evaluator eval1 = new Evaluator(userRepository.findByid(lone), group,
					evalRoleRepository.findById((long)1).orElse(null),currentCompany);
			Evaluator eval2 = new Evaluator(userRepository.findByid(ltwo), group,
					evalRoleRepository.findById((long)1).orElse(null),currentCompany);
			Evaluator eval3 = new Evaluator(userRepository.findByid(facetoface), group,
					evalRoleRepository.findById((long)1).orElse(null),currentCompany);

			evaluatorRepository.save(eval1);
			evaluatorRepository.save(eval2);
			evaluatorRepository.save(eval3);
			List<Reviewee> revieweelist = revieweeRepository.findBygroup_Id(id);
			for (int a = 0; a < revieweelist.size(); a++) {

				evaluationLogRepository.save(new EvaluationLog(eval1, revieweelist.get(a)));
				evaluationLogRepository.save(new EvaluationLog(eval2, revieweelist.get(a)));
				evaluationLogRepository.save(new EvaluationLog(eval3, revieweelist.get(a)));
			}

		}
		
		

		return "home";

	}
	
	@GetMapping("/archivegroup/{id}")
	public Object archiveGroup(RedirectAttributes redirect, @PathVariable("id") long id) {
		Group group = groupRepository.findById(id);
		
		
		if(group.isArchived()) {
			group.setArchived(false);
		}else {
			group.setArchived(true);
		}
		
		groupRepository.save(group);
		
		System.out.println("Archiving group");
		
		return "redirect:/adminGroups";
	}
	
	/*
	 * This finds the list of groups that share the same year, iterates through the list and archives or unarchives each group
	 */
	@GetMapping("/archiveGroupYears/{year}")
	public Object archiveGroup(RedirectAttributes redirect, @PathVariable("year") int year) {
		List<Group> groups = groupRepository.findByYear(year);


		if(!groups.isEmpty()) {
			for(Group group : groups) {
				if(group != null) {
					
					if(group.isArchived()) {
						group.setArchived(false);
					}
					else
					{
						group.setArchived(true);
					}
				}
				
			}
		}
		else {
			System.out.println("No groups to archive");
			return "redirect:/adminGroups";
		}
		
		groupRepository.saveAll(groups);
		
		System.out.println("Archiving Groups by Year");
		
		return "redirect:/adminGroups";
	}
	
	@GetMapping("/usergroups/{id}")
	public Object userGroups(RedirectAttributes redirect, @PathVariable("id") long id, Model model,Authentication auth) {
		User user = userRepo.findByid(id);
		String userRole=user.getRole().getName();
		List<Group> groups = groupRepository.findByCompany(user.getCompany());
		List<Group> userGroups = new ArrayList<Group>();
		List<Reviewee> revs = revieweeRepository.findByUser_Id(id);
		Company currentCompany = user.getCompany();
		List<EvalRole> roles = (List<EvalRole>) evalRoleRepository.findByCompany(currentCompany);
		
		for(Group g : groups) {
			if(g.getUsers().contains(user)) {
				userGroups.add(g);
			}
		}
		if(userGroups.isEmpty()) {
			redirect.addFlashAttribute("groupWarning", "This user does not belong to any group.");
			return "redirect:/home/";
		}
		
		// groups.removeAll(removeG);
		
		//groups.sort(Comparator.comparing(Group::getGroupName));
		//revs.sort(Comparator.comparing(reviewee -> reviewee.getGroup().getGroupName()));
		
		model.addAttribute("revs",revs);
		model.addAttribute("UserRole",userRole);
		model.addAttribute("User", user);
	    model.addAttribute("groups", userGroups);
	    model.addAttribute("roles", roles);
		
		return "userGroups";
	}
	
	@GetMapping("/groupInformation/{id}")
	public Object groupInformation(Authentication auth, RedirectAttributes redirect, @PathVariable("id") long id, Model model) {
		MyUserDetails user = (MyUserDetails) auth.getPrincipal();
		User user2 = userRepository.findByid(user.getId());
		Group group = groupRepository.findById(id);
		Company currentCompany = group.getCompany();
		List<EvalRole> roles = (List<EvalRole>) evalRoleRepository.findByCompany(currentCompany);
		Calendar calendar = Calendar.getInstance();
		Date currentDateAndTime = calendar.getTime();
		
		
		
		user2.setLastLoginDateTime(currentDateAndTime);
		userRepository.save(user2);
		
		
		Date userDateTime = user2.getLastLoginDateTime();
		
		
		
		model = AdminMethodsService.pageNavbarPermissions(user2, model, evaluatorRepository, evalFormRepo);
		model.addAttribute("dateTime", userDateTime);
		model.addAttribute("group", group);
		model.addAttribute("roles", roles);
		return "groupInformation";
	}
	
	@GetMapping("/groupInformation/{id}/{uid}")
	public Object userGroupInformation(RedirectAttributes redirect, @PathVariable("id") long id, @PathVariable("uid") long uid,
			Model model) {
		Group group = groupRepository.findById(id);
		User user = userRepo.findByid(uid);
		Company currentCompany = group.getCompany();
		List<EvalRole> roles = (List<EvalRole>) evalRoleRepository.findByCompany(currentCompany);
		
		
		
		
		model.addAttribute("group", group);
		model.addAttribute("roles", roles);
		return "groupInformation";
	}
	
	@PostMapping("/changeEvaluator/{id}/{eid}")
	public Object changeEvaluator(RedirectAttributes redirect, @PathVariable("id") long id, @PathVariable("eid") long evalId,
			@RequestParam("Elevel") long roleId, @RequestParam("newEval") long newEid, Model model) {
		Group group = groupRepository.findById(id);
		Company currentCompany = group.getCompany();
		Evaluator currentEval = evaluatorRepository.findById(evalId);
		
		User newEvalUser = userRepository.findByid(newEid);
		EvalRole newEvalRole = evalRoleRepository.findByEvalRoleId(roleId);
		
		
		
		List<Evaluator> groupEvals = group.getEvaluator();
		
		for(Evaluator eval: groupEvals) {
			if(eval.getId() == evalId) {
				groupEvals.remove(eval);
				break;
			}
		}
		
		Evaluator newEval = new Evaluator(newEvalUser, group, newEvalRole, currentCompany);
		groupEvals.add(newEval);
		evaluatorRepository.save(newEval);
		
		evaluationLogRepository.deleteByEvaluatorId(evalId);
		evaluatorRepository.deleteByIdAndGroupId(evalId, group.getId());
		
		group.setEvaluator(groupEvals);
		groupRepository.save(group);
		
		
		return "redirect:/editgroup/{id}";
	}
	
	@PostMapping("/editEvaluator/{id}/{eid}")
	public Object editEvaluator(@PathVariable("id") long groupId, 
            @PathVariable("eid") long evalId, 
            @RequestParam("newLevel") long levelId, 
            @RequestParam("newSync") boolean sync,
            @RequestParam("newPreview") boolean preview, 
            @RequestParam("newDeadline") @DateTimeFormat(pattern = "yyyy-MM-dd") Date deadline,
            @RequestParam("newDeadlineReminder") int deadlineReminderDays,
            Model model) {
		
		Group group = groupRepository.findById(groupId);
		Evaluator eval = evaluatorRepository.findById(evalId);
		EvalRole newLevel = evalRoleRepository.findByEvalRoleId(levelId);
		List<Evaluator> gevals = group.getEvaluator(); 
		
		eval.setLevel(newLevel);
		eval.setSync(sync);
	    eval.setPreview(preview); 
	    eval.setDeadline(deadline);
	    eval.setDeadlineReminderDays(deadlineReminderDays);
	    gevals.add(eval);
		
		
		group.setEvaluator(gevals);
		evaluatorRepository.save(eval);
		groupRepository.save(group);


	
		return "redirect:/editgroup/{id}";
	}
	
	@GetMapping("/removeEvaluator/{id}/{eid}")
	public Object removeEvaluator(RedirectAttributes redirect, @PathVariable("id") long id, @PathVariable("eid") long evalId,
			  Model model) {
		Group group = groupRepository.findById(id);
		Company currentCompany = group.getCompany();
		Evaluator currentEval = evaluatorRepository.findById(evalId);
		

		List<Evaluator> groupEvals = group.getEvaluator();
		
		groupEvals.remove(currentEval);

		
		evaluationLogRepository.deleteByEvaluatorId(evalId);
		evaluatorRepository.deleteByIdAndGroupId(evalId, group.getId());
		
		group.setEvaluator(groupEvals);
		groupRepository.save(group);
		
		
		return "redirect:/editgroup/{id}";
	}
	
	
	
	/**getChangeEvaluator
	 * @param id of group
	 * @param id of evaluator
	 * @param model to send to web page
	 * @return user to the change evaluator page
	 *
	 */
	@GetMapping("/getchangeEvaluator/{id}/{eid}")
	public Object getChangeEvaluator(RedirectAttributes redirect, @PathVariable("id") long id, @PathVariable("eid") long evalId,
			Model model) {
		Group group = groupRepository.findById(id);
		Company currentCompany = group.getCompany();
		Evaluator currentEval = evaluatorRepository.findById(evalId);
		
		Iterator<Department> deptList = deptRepo.findByCompany(currentCompany).iterator();
		List<Evaluator> groupEvals = group.getEvaluator();
		List<User> evals = userRepository.findByRoleNameEqualsOrRoleNameEquals("ADMIN", "EVALUATOR_ADMIN");
		List<EvalRole> levels = evalRoleRepository.findByCompany(currentCompany);
		

		
		model.addAttribute("deptList", deptList);
		model.addAttribute("group", group);
		model.addAttribute("levels", levels);
		model.addAttribute("currentEval", currentEval);
		model.addAttribute("groupEvals", groupEvals);
		model.addAttribute("evals", evals);
		return "changeEvaluator";
	}

		
	/**searchUserGroup
	 * @param id of group
	 * @param keyword or the string searched
	 * @param model to send to web page
	 * @return user to the group edit page
	 *
	 */
	@GetMapping("/editgroupsearch/{id}")
	public Object searchUserGroup(RedirectAttributes redir ,@PathVariable("id") long id, @RequestParam("searchKey") String uSearch,
			 @RequestParam("department") String department, Model model, Authentication auth) {

		User currentUser;
		Company currentCompany;

		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);
		currentCompany = currentUser.getCompany();

		Group group = groupRepository.findById(id);
		User user = userRepository.findById(id).orElse(null);
		if(group.getEvalstart()) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "Can not  edit group "+id +", evaluation have started");
			return redirectView;
		}


		List<Boolean> synclist = new ArrayList<Boolean>();
		List<Boolean> prevlist = new ArrayList<Boolean>();
		List<Evaluator> evallist = evaluatorRepository.findByGroupId(id);
		List<User> evaluatorsList = new ArrayList<User>(); //list to be used to display evaluators at each level for a given group
		List<User> userList = userRepository.findByCompany(currentCompany); //used for tables of users


		List<EvalRole> roles = (List<EvalRole>) evalRoleRepository.findAll();
		List<Reviewee> rev = revieweeRepository.findBygroup_Id(id);

		List<Reviewee> reviewees = revieweeRepository.findByCompany(currentCompany);
		Iterator<Department> depts = deptRepo.findByCompany(currentCompany).iterator();

		List<User> userRemove = new ArrayList<User>();
		
		
		if (!department.equalsIgnoreCase("all")) {
			for (int i = 0; i < userList.size(); i++) { // remove users not in specified department

				if (!userList.get(i).getDepartmentName().equals(department)) {

					userRemove.add(userList.get(i));
				}
			}
		}

		userList.removeAll(userRemove);		//remove users from table

		for (int i = 0; i < userList.size(); i++) { // filters out users already in group
			for (int j = 0; j < groupRepository.getGroupSize(id); j++) {
				if (userList.get(i).getId() == group.getReviewees().get(j).getUser().getId()) {

					userRemove.add(userList.get(i));
					break;
				}
			}
		}
		
		

		userList.removeAll(userRemove);

		for (int i = 0; i < userList.size(); i++) { 		// remove users without search key

			if (userList.get(i).getId() == currentUser.getId()) {

				userRemove.add(userList.get(i));
			}

			if (!userList.get(i).getFirstName().contains(uSearch) && !userList.get(i).getLastName().contains(uSearch)) {

				userRemove.add(userList.get(i));
			}
		}

		userList.removeAll(userRemove);		//remove users from table

		Collections.sort(reviewees, Comparator.comparing(Reviewee::getName));

		List<Long> revid = new ArrayList<Long>();
		for (int x = 0; x < rev.size(); x++) {
			revid.add(rev.get(x).getUser().getId());
		}
		Map<EvalRole, List<Long>> evals = new LinkedHashMap<EvalRole, List<Long>>();

		for (int x = 0; x < roles.size(); x++) {

			evals.put(roles.get(x), null);
			List<Long> temp = new ArrayList<Long>();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {
					temp.add((long) evallist.get(y).getUser().getId());

				}

			}
			evals.put(roles.get(x), temp);
		}
		for (int x = 0; x < roles.size(); x++) {
			int size = synclist.size();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {

					synclist.add(evallist.get(y).isSync());

					break;
				}
			}
			if(size == synclist.size()) {
				synclist.add(false);
			}

		}
		for (int x = 0; x < roles.size(); x++) {
			int size = prevlist.size();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {

					prevlist.add(evallist.get(y).isPreview());

					break;
				}
			}
			if(size == prevlist.size()) {
				prevlist.add(false);
			}


		}


		for(int i = 0; i < evallist.size(); i++) {
			//ensures only unique users are added to the list of evaluators
			if(!evaluatorsList.contains(evallist.get(i).getUser())) {
				evaluatorsList.add(evallist.get(i).getUser());
			}
		}

		synclist.add(false);
		EvalRole temprole = roles.get(0);
		//System.out.println(evals.get(temprole));
		//System.out.println(evallist.get(0).getId());
		model.addAttribute("group", group);
		model.addAttribute("user", user);
		model.addAttribute("evalList", evallist);
		model.addAttribute("sync", synclist);
		model.addAttribute("prev", prevlist);
		model.addAttribute("revedit", revid);
		model.addAttribute("evaluators", evaluatorsList);
		model.addAttribute("roles", roles);
		model.addAttribute("forms", evalFormRepo.findAll());
		model.addAttribute("userlist", userList);
		model.addAttribute("reviewees", reviewees);
		model.addAttribute("deptList", depts);
		log.info("editGroupSearch open");
		return "editGroupSearch";

	}




		/**addUserToGroup
		 * @param id of the group
		 * @param userId id of the user
		 * @param model to send in model to web page
		 * @return user to the group edit page
		 * 
		 * still working on implementation
		 */
		 @GetMapping("/editgroup/{id}/{uid}")
		 public Object addUserGroup(@PathVariable("id") long groupId, 
				 @PathVariable("uid") long userId, Model model, Authentication auth) {
				

			 	 Group group = groupRepository.findById(groupId);
				 User revUser = userRepository.findByid(userId);
				 Reviewee newRev = new Reviewee(group, revUser.getName(), revUser);
				 newRev.setGroup(group);
				 revieweeRepository.save(newRev);
				 
				 List<Evaluator> evaluators = evaluatorRepository.findByGroupId(groupId);
				    for (Evaluator evaluator : evaluators) {
				        EvaluationLog log = new EvaluationLog(evaluator, newRev);
				        log.setAuth(evaluator.getLevel().getLevel() == 1); // Set auth based on level, adjust as needed
				        evaluationLogRepository.save(log);
				    }


				 return"redirect:/editgroup/{id}";


		 }


	/**editgroup
	 * takes user to the edit group page where use can make changes to evaluator, reviewee and group attributes.
	 * @param redir
	 * @param id  of the group being edited 
	 * @param model model is the a model object use to add attributes to a web page
	 * @return user to the group edit page
	 */
	@GetMapping("/editgroup/{id}")

	public Object editgroup(RedirectAttributes redir ,@PathVariable("id") long id, Model model, Authentication auth) {


		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();
		
		
		User currentUser = userRepository.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		Iterator<Department> depts = deptRepo.findByCompany(currentCompany).iterator();
		
		Group currGroup=this.groupRepository.findById(id);
		User user = userRepository.findById(id).orElse(null);
		if(currGroup.getEvalstart()) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "Can not  edit group "+id +", evaluation have started");
			return redirectView;
		}

		List<Boolean> synclist = new ArrayList<Boolean>();
		List<Boolean> prevlist = new ArrayList<Boolean>();
		List<Evaluator> evallist = evaluatorRepository.findByGroupId(id);
		List<User> evaluatorsList = new ArrayList<User>(); //list to be used to display evaluators at each level for a given group
		List<User> userList = userRepository.findByCompany(currentCompany); //used for tables of users

		
		List<EvalRole> roles = (List<EvalRole>) evalRoleRepository.findAll();
		List<Reviewee> rev = revieweeRepository.findBygroup_Id(id);
		
		List<Reviewee> reviewees = revieweeRepository.findByCompany(currentCompany);

		List<User> userRemove = new ArrayList<User>();

		for (int i = 0; i < userList.size(); i++) { 		// filters out users already in group
			for (int j = 0; j < groupRepository.getGroupSize(id); j++) {
				if (userList.get(i).getId() == currGroup.getReviewees().get(j).getUser().getId()) {

					userRemove.add(userList.get(i));
				}
			}
		}
		
		if(userList.get(0).getId()== currentUser.getId()) {
			userRemove.add(userList.get(0));
		}

		userList.removeAll(userRemove);		//removes user from table

		Collections.sort(reviewees, Comparator.comparing(Reviewee::getName));
		
		List<Long> revid = new ArrayList<Long>();
		for (int x = 0; x < rev.size(); x++) {
			revid.add(rev.get(x).getUser().getId());
		}
		Map<EvalRole, List<Long>> evals = new LinkedHashMap<EvalRole, List<Long>>();

		for (int x = 0; x < roles.size(); x++) {

			evals.put(roles.get(x), null);
			List<Long> temp = new ArrayList<Long>();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {
					temp.add((long) evallist.get(y).getUser().getId());

				}

			}
			evals.put(roles.get(x), temp);
		}
		for (int x = 0; x < roles.size(); x++) {
			int size = synclist.size();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {

					synclist.add(evallist.get(y).isSync());

					break;
				}
			}
			if(size == synclist.size()) {
				synclist.add(false);
			}

		}
		for (int x = 0; x < roles.size(); x++) {
			int size = prevlist.size();
			for (int y = 0; y < evallist.size(); y++) {

				if (roles.get(x).getId() == evallist.get(y).getLevel().getId()) {

					prevlist.add(evallist.get(y).isPreview());

					break;
				}
			}
			if(size == prevlist.size()) {
				prevlist.add(false);
			}


		}
		
		
		for(int i = 0; i < evallist.size(); i++) {
			//ensures only unique users are added to the list of evaluators
			if(!evaluatorsList.contains(evallist.get(i).getUser())) {
				evaluatorsList.add(evallist.get(i).getUser());
			}
		}
		
		synclist.add(false);
		EvalRole temprole = roles.get(0);
		//System.out.println(evals.get(temprole));
		//System.out.println(evallist.get(0).getId());
		model.addAttribute("group", currGroup);
		model.addAttribute("evalList", evallist);
		model.addAttribute("user", user);
		model.addAttribute("evallist", evals);
		model.addAttribute("sync", synclist);
		model.addAttribute("prev", prevlist);
		model.addAttribute("revedit", revid);
		model.addAttribute("evaluators", evaluatorsList);
		model.addAttribute("roles", roles);
		model.addAttribute("forms", evalFormRepo.findAll());
		model.addAttribute("userlist", userList);
		model.addAttribute("reviewees", reviewees);
		model.addAttribute("deptList", depts);
		log.info("groupEdit open");
		return "groupEdit";

	}
	
	/**changegroup
	 * takes user to the edit group page where use can make changes to evaluator, reviewee and group attributes.
	 * @param id  of the group being edited 
	 * @param redir
	 * @param group data being passed from the form to the controller
	 * @param model model is the a model object use to add attributes to a web page 
	 * @return user to the group edit page
	 */
	@RequestMapping(value= "/changegroup{id}", method = RequestMethod.POST)
	public String change(@PathVariable("id") long id, RedirectAttributes redir, @Validated Group group, Model model, Authentication auth) {
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		
		currentUser = this.userRepository.findById(idnum).orElse(null);
		currentCompany = currentUser.getCompany();
		
		String ansr = null;
		String mess = null;
		
		//get pre-group info
		Group group2 = groupRepository.findById(id);
		//check for values being set
		boolean check = false;
		log.info("User Pre Changes- Group Name: " + group2.getGroupName() + "Group Number: " + group2.getGroupNumber() + "Year: " + 
				group2.getYear());
		log.info("User Post Changes- Group Name: " + group.getGroupName() + "Group Number: " + group.getGroupNumber() + "Year: " + 
				group.getYear());
		
		//changing group name
		if (true) {
			if (group.getGroupName() == "" || group.getGroupName() == null) {
				group.setGroupName(group2.getGroupName());
				log.info("Group Name was null");
			} 
			else if ((groupRepository.findByGroupName(group.getGroupName()) != null)
					&& (groupRepository.findByGroupName(group.getGroupName())) != groupRepository.findById(id)) {
				group.setGroupName(group2.getGroupName());
				log.info("Group Name was equal to another existing group");
			}
			else if (group.getGroupName().equals(group2.getGroupName())) {
				group2.setGroupName(group.getGroupName());
			} else {
				group2.setGroupName(group.getGroupName());
				check = true;
			}
		}
		//changing group number
		if (true) {
			if (group.getNumber() == 0) {
				group.setNumber(group2.getNumber());
				log.info("Group Number was 0");
			} 
			else if (group.getNumber() == group2.getNumber()) {
				group2.setNumber(group.getNumber());
			} else {
				group2.setNumber(group.getNumber());
				check = true;
			}
		}
		//changing group year
		if (true) {
			if (group.getYear() == 0) {
				group.setYear(group2.getYear());
				log.info("Group Year was 0");
			} 
			else if (group.getYear() == group2.getYear()) {
				group2.setYear(group.getYear());
			} else {
				group2.setYear(group.getYear());
				check = true;
			}
		}
		
		//check if check was successful
		if (check) {
			log.info("Changes have been made!");
			
			//save changes to the repository
			try {
				mess = "Changes have been made successfully";
				ansr = "pass";
				redir.addFlashAttribute("mess", mess);
				redir.addFlashAttribute("ansr", ansr);
				groupRepository.save(group2);
				log.info("Changes saved!");
			}
			catch(Exception e){
				e.printStackTrace();
				log.info("Changes have not been made");
				log.error(e.getStackTrace().toString());	
			}
		}
		if (!check) {
			mess = "Changes have not been made successfully";
			ansr = "fail";
			redir.addFlashAttribute("mess", mess);
			redir.addFlashAttribute("ansr", ansr);
			log.info("Changes have not been made.");
		}
			
		return "redirect:/editgroup/" + id;
	}
	
	@PostMapping("/addEvaluator/{id}")
	public String addEvaluator(@PathVariable("id") long groupId, @RequestParam("newE") long userId, 
	        @RequestParam("newLevel") long levelId, @RequestParam("sync") boolean sync,
	        @RequestParam("preview") boolean preview, @RequestParam("deadline") @DateTimeFormat(pattern = "yyyy-MM-dd") Date deadline , 
	        @RequestParam("deadlineReminder") int deadlineReminderDays, Model model) {
		
		Group group = groupRepository.findById(groupId);
		Company company = group.getCompany();
		User user = userRepo.findByid(userId);
		EvalRole level = evalRoleRepository.findByEvalRoleId(levelId);
		
		List<Evaluator> gevals = group.getEvaluator(); 
		
		Evaluator eval = new Evaluator(user, group, level, company);
		
		eval.setSync(sync);
	    eval.setPreview(preview); 
	    eval.setDeadline(deadline);
	    eval.setDeadlineReminderDays(deadlineReminderDays);
	    gevals.add(eval);
	    
	    
	    List<Reviewee> reviewees = revieweeRepository.findBygroup(group);
	    for (Reviewee reviewee : reviewees) {
	        EvaluationLog log = new EvaluationLog(eval, reviewee);
	        log.setAuth(eval.getLevel().getLevel() == 1); // Set auth based on level, adjust as needed
	        evaluationLogRepository.save(log);
	    }
		
		
		group.setEvaluator(gevals);
		evaluatorRepository.save(eval);
		groupRepository.save(group);

		return "redirect:/editgroup/{id}";
	}
	
	@GetMapping("/getAddEvaluator/{id}")
	public String addEvalToGroup(@PathVariable("id") long groupId, Model model) {
		
		Group group = groupRepository.findById(groupId);
		Company company = group.getCompany();
		
		List<Evaluator> evals = evaluatorRepository.findByCompanyId(company.getId());
		List<Evaluator> evalList = evaluatorRepository.findByGroupId(groupId);
		List<User> newEvals = new ArrayList<User>();
		List<EvalRole> levels = evalRoleRepository.findByCompany(company);
		
		
		
		for(Evaluator  eval : evals) {
			if(!newEvals.contains(eval.getUser())) {
				newEvals.add(eval.getUser());
			}
		}
		
		
		
		
	
		
		model.addAttribute("levels", levels);
		model.addAttribute("evalList" , evalList);
		model.addAttribute("group", group);
		model.addAttribute("newEvals", newEvals);
		return "/AddEvalGroup";
	}
	
	@PostMapping("addUserToGroup/{groupID}")
	public String addUserToGroup(@RequestParam(name = "rev") Reviewee rev, @PathVariable("groupID") long groupID) {
		Iterable<Group> groupList = groupRepository.findAll();
		Group group = null;
		
		for(Group g: groupList) {
			if(g.getId() == groupID) {
				group = g;
				break;
			}
		}
		
		rev.setGroup(group);
		revieweeRepository.save(rev);
		
		return "redirect:/editgroup/" + group.getId();
	}
	 /**moveUserToGroup
	 * @param id of the group
	 * @param userId id of the user
	 * @param model to send in model to web page
	 * @return user to the group edit user move page
	 * 
	 * s
	 */
	@GetMapping("/editgroupmoveuser/{id}/{uid}")
	public Object getUserGroupmove(@PathVariable("id") long groupId, @PathVariable("uid") long userId,
			Model model, Authentication auth) {

		User currentUser;
		Company currentCompany;

		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);
		currentCompany = currentUser.getCompany();

		Group group = groupRepository.findById(groupId);
		Reviewee revUser = revieweeRepository.findByUserIdGroupId(userId, groupId);
		Group agroup = new Group();
		
		Set<Department> deptList = deptRepo.findByCompany(currentCompany);
		List<Evaluator> groupEvals = evaluatorRepository.findByGroupId(groupId);
		Iterable<Group> allGroups = groupRepository.findAll();
		Iterator<Department> depts = deptRepo.findByCompany(currentCompany).iterator();
		
		model.addAttribute("deptList", deptList);
		model.addAttribute("groupEvals", groupEvals);
		model.addAttribute("agroup", agroup);
		model.addAttribute("allgroups", allGroups);
		model.addAttribute("group", group);
		model.addAttribute("revuser", revUser);
		model.addAttribute("deptList", depts);

		return "/editGroupUserMove";

	}
	
	/**moveUserToGroup
	 * @param id of the group
	 * @param userId id of the user
	 * @param model to send in model to web page
	 * @return user to the group edit page
	 * 
	 * 
	 */
	@PostMapping("/editgroupmove/{id}/{uid}")
	public Object editGroupmove(@PathVariable("id") long groupId, @PathVariable("uid") long userId, 
			@RequestParam("ngid") long newGroupId, Model model, Authentication auth) {

		Group group = groupRepository.findById(newGroupId);
		Reviewee revUser = revieweeRepository.findByUserIdGroupId(userId, groupId);

		List<Reviewee> revList = group.getReviewee();
		
		for(Reviewee rev: revList) {
			if(rev.getId() == revUser.getId()) {
				System.out.print("User already in group");
				return "redirect:/editgroup/{id}";
			}
		}
		
		revUser.setGroup(group);
		revList.add(revUser);

		group.setReviewees(revList);
		groupRepository.save(group);
		revieweeRepository.save(revUser);

		return "redirect:/editgroup/{id}";

	}
	

	/**update group 
	 * save changes to the group 
	 * @param id of the group being edited 
	 * @param rev return a new list of reviewee
	 * @param eval list of evaluators 
	 * @param roles list of roles
	 * @param issync determines if a evaluator is issync or not 
	 * @param isprev is the evaluation able to be previewed 
	 * @param form what form is associated  with the group
	 * @param self  is self evaluation needed 
	 * @param model
	 * @return  user back to the edit group page 
	 */
	/**@RequestMapping(value = "/updategroup{id}", method = RequestMethod.POST)
	public String update(@PathVariable("id") long id, @RequestParam(value = "rev", required = false) long[] rev,
			@RequestParam(value = "eval", required = false) long[] eval,
			@RequestParam(value = "role", required = false) int[] roles,
			@RequestParam(value = "issync", required = false) boolean[] issync,
			@RequestParam(value = "isprev", required = false) boolean[] isprev,
			@RequestParam(value = "form", required = false) EvalTemplates form,
			@RequestParam(value = "selfeval", required = false) boolean self, Model model, Authentication auth) {

		//System.out.println("start");
		//for (int x = 0; x < isprev.length; x++) {
		//	System.out.println(isprev[x]);
		//}
		//System.out.println("end");
		//System.out.println(isprev.length);
		//System.out.println(roles.length);
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		
		
		//  edits group 
		Group group = groupRepository.findById(id);

		group.getReviewee().clear();
		group.getEvaluator().clear();
		Reviewee reviewee = null;
		User user = null;
		for (int i = 0; i < rev.length; i++) {
			user = userRepository.findByid(rev[i]);
			reviewee = new Reviewee(group, user.getName(), user);
			group.appendReviewee(reviewee);
		}
		//for (int i = 0; i < group.getReviewee().size(); i++) {
		//	System.out.println(group.getReviewee().get(i).getName());
		//}
		group.setSelfeval(self);
		group.setEvalTemplates(form);
		groupRepository.save(group);
		group = groupRepository.findById(id);


		List<Reviewee> revieweelist = revieweeRepository.findBygroup_Id(id);





		for (int i = 0; i < roles.length; i++) {
			Evaluator temp = new Evaluator(userRepository.findByid(eval[i]), group,
					evalRoleRepository.findById(roles[i]).orElse(null),currentCompany);
			temp.setSync(issync[roles[i] - 1]);
			temp.setPreview(isprev[roles[i] - 1]);
			List<Evaluator> eval2 = (evaluatorRepository.findByLevelLevelAndGroupId(roles[i] - 1, group.getId()));
			List<Evaluator> eval3 = (evaluatorRepository.findByLevelLevelAndGroupId(roles[i] + 1, group.getId()));
			for (int a = 0; a < revieweelist.size(); a++) {
				EvaluationLog eltemp = new EvaluationLog(temp, revieweelist.get(a));

				for (int k = 0; k < eval2.size(); k++) {
					EvaluationLog log1 = evaluationLogRepository.findByEvaluatorIdAndRevieweeId(eval2.get(k).getId(),
							revieweelist.get(a).getId());
					if ((eval2.get(k).isSync() != true) && log1.getAuth()) {
						eltemp.setAuth(true);

					} else {
						eltemp.setAuth(false);

					}
				}
				temp.appendEvalutationLog(eltemp);
				for (int k = 0; k < eval3.size(); k++) {
					EvaluationLog log2 = evaluationLogRepository.findByEvaluatorIdAndRevieweeId(eval3.get(k).getId(),
							revieweelist.get(a).getId());
					if ((temp.isSync() != true) && eltemp.getAuth()) {
						log2.setAuth(true);

						evaluationLogRepository.save(log2);

					}

				}



			}
			evaluatorRepository.save(temp);
		}


		return "redirect:/editgroup/" + id;

	} **/

	@ResponseBody
	@RequestMapping(value = "/creategroup", method = RequestMethod.POST)
	public Object createGroup(@ModelAttribute("groupName") String name, RedirectAttributes redir, Authentication auth) {
		System.out.println("am i running?");
		//@RequestParam("groupName") String name,
		
		Group group;
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		
		Long idNum = userD.getId();
		
		currentUser = this.userRepository.findById(idNum).orElse(null);
		currentCompany = currentUser.getCompany();

		//ensure user has correct permissions
		if(!currentUser.getRole().getName().equalsIgnoreCase("EVALUATOR_ADMIN")) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "invalid permissions on user "+currentUser.getName());
			System.out.println(currentUser.getRole().getName());
			log.error("invalid permissions on user "+currentUser.getName()+ " does not have permission to create a eval group or assign evaluator role.");
			return redirectView;
		}
		
		group = new Group(currentCompany);
		
		//defaults
		long lastGroupNumber = groupRepository.count();
		int groupNumber = (int) lastGroupNumber + 1;
		int level = 0;
		int defaultTemplate = 0;
		EvalTemplates evalTemplate = evaluationRepository.findByCompany(currentCompany).get(defaultTemplate);
		
		List<EvalRole> roles = (List<EvalRole>) evalRoleRepository.findByCompany(currentCompany);
		//Add default evaluator roles if list is empty
		if(roles.isEmpty()) {
			EvalRole level1 = new EvalRole("Level 1", level, currentCompany);
			EvalRole level2 = new EvalRole("Level 2", level++, currentCompany);
			EvalRole consistencyReview = new EvalRole("Consistency Review", level++, currentCompany);
			EvalRole faceToFace = new EvalRole("Face to Face", level++, currentCompany);
			
			roles.add(level1);
			roles.add(level2);
			roles.add(consistencyReview);
			roles.add(faceToFace);	
		}
		
		
		group.setNumber(groupNumber);
		group.setEvalTemplates(evalTemplate);
		group.setSelfeval(false);
		group.setGroupName(name);
		
		try {
			groupRepository.save(group);
			evalRoleRepository.saveAll(roles);
		} catch(Exception e) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "There was an error saving the group. Please try again.");
			log.error("Error saving group: "+e.getMessage());
			return redirectView;
		}
		
		
		redir.addFlashAttribute("Group "+group.getGroupNumber()+" was successfully added.", true);
		RedirectView redirectView = new RedirectView("/adminGroups", true);
		if(group.getCompany() != null) {
			log.info("Added Group " + group.getGroupNumber() + " (ID:" + group.getId() + ") For Company " + group.getCompany().getCompanyName() + " (ID:" + group.getCompany().getId() + ")");
		} else {
			log.info("Added Group " + group.getGroupNumber() + " (ID:" + group.getId() + ") [No Company]");

		}
		return redirectView;
	}


	//@RequestMapping(value="/manageGroups", method = RequestMethod.POST)
	@GetMapping("/gotoman")
	public String manGroupsRedir(Model model, RedirectAttributes redir, Authentication auth) {
		Group group = new Group();
		model.addAttribute("group", group);

		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idNum = userD.getId();
		User currentUser = new User();
		currentUser = this.userRepository.findById(idNum).orElse(null);
		model.addAttribute("currentUser", currentUser);

		Company currentCompany=currentUser.getCompany();
		List<User> userList = userRepository.findByCompany(currentCompany);
		model.addAttribute("userlist", userList);

		//will contain users to be added to the group.  may use group.reviewees instead
		List<User> usersToAdd = new ArrayList<>();
		model.addAttribute("usersToAdd", usersToAdd);

		List<EvalTemplates> evaltemplates = this.evaluationRepository.findByCompany(currentCompany);
		model.addAttribute("evaltemplates", evaltemplates);

		Map<Integer, List<String>> syncmap = new HashMap<Integer, List<String>>();
		model.addAttribute("syncmap", syncmap);


		//initializes all activities as Synchronous for a newly created group.
		//this is changed by the user upon creation.
//		boolean synctrue[] = new boolean[]{true, true, true, true};
//		boolean strueL1 = true;
//		boolean strueL2 = true;
//		boolean strueCR = true;
//		boolean strueFTF = true;
//		List<String> synclist = new ArrayList<String>();
//		for (int i = 0; i < 4; i++) {
//			synclist.add("Sync");
//		}
//		model.addAttribute("strueL1", strueL1);
//		model.addAttribute("strueL2", strueL2);
//		model.addAttribute("strueCR", strueCR);
//		model.addAttribute("strueFTF", strueFTF);
		String[] levellist = new String[] {"Level 1", "Level 2", "Consistency Review", "Face to Face"};
		Evaluator eval1 = new Evaluator(currentUser, group,
					evalRoleRepository.findByNameAndCompany(levellist[0], currentUser.getCompany()), currentUser.getCompany());
		Evaluator eval2 = new Evaluator(currentUser, group,
				evalRoleRepository.findByNameAndCompany(levellist[1], currentUser.getCompany()), currentUser.getCompany());
		Evaluator eval3 = new Evaluator(currentUser, group,
				evalRoleRepository.findByNameAndCompany(levellist[2], currentUser.getCompany()), currentUser.getCompany());
		Evaluator eval4 = new Evaluator(currentUser, group,
				evalRoleRepository.findByNameAndCompany(levellist[3], currentUser.getCompany()), currentUser.getCompany());
		model.addAttribute("eval1", eval1);
		model.addAttribute("eval2", eval2);
		model.addAttribute("eval3", eval3);
		model.addAttribute("eval4", eval4);
		return "manageGroups";
	}

	///////////////////////////////////////////////////////////////////////////////



	@ResponseBody
	@RequestMapping(value = "/addGroup", method = RequestMethod.POST)
	public Object manCreateGroup(@ModelAttribute("group") Group group, 
            RedirectAttributes redir, 
            Authentication auth,
            @RequestParam Map<String, String> allParams
            
			){

		User currentUser;
		Company currentCompany;



		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idNum = userD.getId();

		currentUser = this.userRepository.findById(idNum).orElse(null);
		currentCompany = currentUser.getCompany();

		//ensure user has correct permissions
		if(!currentUser.getRole().getName().equalsIgnoreCase("Evaluator_admin")) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "invalid permissions on user "+currentUser.getName());
			log.error("invalid permissions on user "+currentUser.getName()+ " does not have permission to create a eval group or assign evaluator role.");
			return redirectView;
		}

		//test for duplicates not group name and year
		//groupRepository.findByGroupName(group.getGroupName());
		boolean duplicated = false;
		if (!(groupRepository.findGroupsByGroupNameAndYear(group.getGroupName(), group.getYear()).isEmpty())) {
			 duplicated=true;
		}

		if(duplicated){
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "duplicate name and year entered: " + group.getGroupName()
					+ ", " + group.getYear());
			log.error("duplicate name and year entered: " + group.getGroupName()
					+ ", " + group.getYear() + ", this group already exists..");
			return redirectView;
		}

		//group = new Group(currentCompany);
		//set group company to curr user company
		currentCompany = currentUser.getCompany();
		group.setCompany(currentCompany);

		//defaults
		long lastGroupNumber = groupRepository.count();
		int groupNumber = (int) lastGroupNumber + 1;
		int level = 0;
		int defaultTemplate = 0;
		EvalTemplates evalTemplate = evaluationRepository.findByCompany(currentCompany).get(defaultTemplate);

		//deleted redundant cast
		List<EvalRole> roles = evalRoleRepository.findByCompany(currentCompany);

		//Add default evaluator roles if list is empty
		if(roles.isEmpty()) {
			EvalRole level1 = new EvalRole("Level 1", level, currentCompany);
			EvalRole level2 = new EvalRole("Level 2", level++, currentCompany);
			EvalRole consistencyReview = new EvalRole("Consistency Review", level++, currentCompany);
			EvalRole faceToFace = new EvalRole("Face to Face", level++, currentCompany);

			roles.add(level1);
			roles.add(level2);
			roles.add(consistencyReview);
			roles.add(faceToFace);
		}

		List<Evaluator> evaluatorlist = new ArrayList<Evaluator>();
		 for (int i = 1; i <= 4; i++) {
		        boolean sync = allParams.containsKey("syncLevel" + i);
		        boolean preview = allParams.containsKey("previewLevel" + i);
		        Evaluator evaluator = new Evaluator();
		        evaluator.setSync(sync);
		        evaluator.setPreview(preview);
		        evaluatorRepository.save(evaluator);
		    }
		
		 
		 
		 
		 //sync list setting:
		//boolean synctrue[] = new boolean[] {strueL1, strueL2, strueCR, strueFTF};
//		List<String> synclist = new ArrayList<String>();
//
//		for (int j = 0; j < synctrue.length; j++) {
//			if (synctrue[j]) {
//				synclist.add("Sync");
//			}
//			else if (!synctrue[j]){
//				synclist.add("Async");
//			}
//			else {
//				RedirectView redirectView = new RedirectView("/adminGroups", true);
//				redir.addFlashAttribute("error", " in new group Sync layout");
//				log.error("error in new group Sync layout");
//				return redirectView;
//			}
//		}
//		String[] levellist = new String[] {"Level 1", "Level 2", "Consistency Review", "Face to Face"};
//		for (int k = 0; k < levellist.length; k++) {
//			Evaluator eval = new Evaluator(currentUser, group,
//					evalRoleRepository.findByNameAndCompany(levellist[k], currentUser.getCompany()), currentUser.getCompany());
			//unnecessary?
//			int evallevel = eval.getLevel().getLevel();
//
//			if (synclist.get(k).equals("Sync")) {
//				eval.setSync(true);
//			}
//			else {
//				eval.setSync(false);
//			}
//
//
//			evaluatorlist.add(eval);
//			evaluatorRepository.save(eval);
//		}


		log.info("self eval:" + group.getSelfeval());
		log.info("deadline:" + group.getDeadline());
		group.setNumber(groupNumber);
		group.setEvalTemplates(evalTemplate);
		//group.setSelfeval(false);
//		group.setGroupName(name);

		try {
			groupRepository.save(group);
			evalRoleRepository.saveAll(roles);
		} catch(Exception e) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "There was an error saving the group. Please try again.");
			log.error("Error saving group: "+e.getMessage());
			return redirectView;
		}


		redir.addFlashAttribute("Group "+group.getGroupNumber()+" was successfully added.", true);
		RedirectView redirectView = new RedirectView("/adminGroups", true);
//		RedirectView redirectView = new RedirectView("/adminGroups", true);
		if(group.getCompany() != null) {
			log.info("Added Group " + group.getGroupNumber() + " (ID:" + group.getId() + ") For Company " + group.getCompany().getCompanyName() + " (ID:" + group.getCompany().getId() + ")");
		} else {
			log.info("Added Group " + group.getGroupNumber() + " (ID:" + group.getId() + ") [No Company]");

		}
//		ArrayList<Long> idfront = new ArrayList<>();
//		idfront.add(group.getId());
//		return idfront;
		return redirectView;
	}




	/////////////////////////////////////////////////////////////////////////////////

//get rid of this
	@ResponseBody
	@RequestMapping(value = "/mancreategroup", method = RequestMethod.POST)
	public Object manCreateGroupv1(@ModelAttribute("group") Group group, RedirectAttributes redir, Authentication auth) {

		User currentUser;
		Company currentCompany;

		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idNum = userD.getId();

		currentUser = this.userRepository.findById(idNum).orElse(null);
		currentCompany = currentUser.getCompany();

		//ensure user has correct permissions
		if(!currentUser.getRole().getName().equalsIgnoreCase("Evaluator_admin")) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "invalid permissions on user "+currentUser.getName());
			log.error("invalid permissions on user "+currentUser.getName()+ " does not have permission to create a eval group or assign evaluator role.");
			return redirectView;
		}

		//group = new Group(currentCompany);
		//set group company to curr user company
		currentCompany = currentUser.getCompany();
		group.setCompany(currentCompany);

		//defaults
		long lastGroupNumber = groupRepository.count();
		int groupNumber = (int) lastGroupNumber + 1;
		int level = 0;
		int defaultTemplate = 0;
		EvalTemplates evalTemplate = evaluationRepository.findByCompany(currentCompany).get(defaultTemplate);

		//deleted redundant cast
		List<EvalRole> roles = evalRoleRepository.findByCompany(currentCompany);

		//Add default evaluator roles if list is empty
		if(roles.isEmpty()) {
			EvalRole level1 = new EvalRole("Level 1", level, currentCompany);
			EvalRole level2 = new EvalRole("Level 2", level++, currentCompany);
			EvalRole consistencyReview = new EvalRole("Consistency Review", level++, currentCompany);
			EvalRole faceToFace = new EvalRole("Face to Face", level++, currentCompany);

			roles.add(level1);
			roles.add(level2);
			roles.add(consistencyReview);
			roles.add(faceToFace);
		}

		log.info("self eval:" + group.getSelfeval());
		group.setNumber(groupNumber);
		group.setEvalTemplates(evalTemplate);
		//group.setSelfeval(false);
//		group.setGroupName(name);

		try {
			groupRepository.save(group);
			evalRoleRepository.saveAll(roles);
		} catch(Exception e) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "There was an error saving the group. Please try again.");
			log.error("Error saving group: "+e.getMessage());
			return redirectView;
		}


		redir.addFlashAttribute("Group "+group.getGroupNumber()+" was successfully added.", true);
		RedirectView redirectView = new RedirectView("/adminGroups", true);
		if(group.getCompany() != null) {
			log.info("Added Group " + group.getGroupNumber() + " (ID:" + group.getId() + ") For Company " + group.getCompany().getCompanyName() + " (ID:" + group.getCompany().getId() + ")");
		} else {
			log.info("Added Group " + group.getGroupNumber() + " (ID:" + group.getId() + ") [No Company]");

		}
//		ArrayList<Long> idfront = new ArrayList<>();
//		idfront.add(group.getId());
//		return idfront;
		return redirectView;
	}

	/**generateName
	 * For use in the situation where groups are imported, but an imported group is a duplicate of a current created group by name and year.
	 *
	 * @param duplName represents the name of an imported group which must be changed to avoid duplicates
	 * @param year represents the year of the offending group.
	 * @return a similar but adjusted name for the offending imported group
	 */
	private String generateName(String duplName, int year) {

		boolean duplicate = true;
		int duplNum = 1;
		while (duplicate) {
			duplName = duplName + "(" + duplNum + ")";

			if ((groupRepository.findGroupsByGroupNameAndYear(duplName, year).isEmpty())) {
				duplicate=false;
			}
		}
		return duplName;
	}


	/**uploadgroup
	 * takes an excel file and takes the data from it and creates groups 
	 * @param reapExcelDataFile read the excel file 
	 * @param redir hold redirection attributes 
	 * 
	 * @return admingroup page 
	 */
	@RequestMapping(value = "/uploadgroup", method = RequestMethod.POST)
	public Object uploadgroup(@RequestParam("file") MultipartFile reapExcelDataFile, RedirectAttributes redir, Authentication auth, Model model) {

		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		//checks if user can assign evaluator. 
		if(!currentUser.getRole().getName().equalsIgnoreCase("Evaluator_admin")) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "invalid permissions on user "+currentUser.getName());
			log.error("invalid permissions on user "+currentUser.getName()+ " does not have permission to create a eval group or assign evaluator role.");
			return redirectView;
		}
		
		

		XSSFSheet sheet = null;
		XSSFSheet sheet2 = null;
		XSSFSheet sheet3 = null;
		List<Group> grouplist = new ArrayList<Group>();
		List<Evaluator> evaluatorlist = new ArrayList<Evaluator>();
		List<EvalRole> rolelist = new ArrayList<EvalRole>();
		Map<Integer, List<String>> syncmap = new HashMap<Integer, List<String>>();
		Map<Integer, List<String>>  previewmap = new HashMap<Integer, List<String>>();
		try {
			sheet = ExcelRead_group.loadFile(reapExcelDataFile).getSheetAt(0);
			sheet2 = ExcelRead_group.loadFile(reapExcelDataFile).getSheetAt(1);
			sheet3 = ExcelRead_group.loadFile(reapExcelDataFile).getSheetAt(2);
		} catch (Exception e) {

			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "invalid file");
			return redirectView;
		}
		String type = ExcelRead_group.checkStringType(sheet3.getRow(0).getCell(1));
		if (!type.equals("Groups")) {
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "wrong file type");
			//System.out.println(ExcelRead_group.checkStringType(sheet3.getRow(0).getCell(1)));
			return redirectView;
		}

		// roles
		for (int i = 1; sheet3.getRow(i) != null; i++) {
			int level = ExcelRead_group.checkIntType(sheet3.getRow(i).getCell(1));
			String evalRoleName = ExcelRead_group.checkStringType(sheet3.getRow(i).getCell(0));	
			String companyName = ExcelRead_group.checkStringType(sheet3.getRow(i).getCell(2));
			Company co = companyRepo.findByCompanyName(companyName);
			if(co == null) {
				RedirectView redirectView = new RedirectView("/adminGroups", true);
				redir.addFlashAttribute("error", "company " + companyName + "does not exist, cannot add it to a group.");
				log.error("company " + companyName + "does not exist, cannot add it to a group.");
				return redirectView;
			}
			else if(!co.equals(currentCompany)) {
				RedirectView redirectView = new RedirectView("/adminGroups", true);
				redir.addFlashAttribute("error", "User with company access to  " + currentCompany.getCompanyName() + " doesnt have access to " + co.getCompanyName());
				log.error("User with company access to  " + currentCompany.getCompanyName() + " doesnt have access to " + co.getCompanyName());
				return redirectView;
			}
			EvalRole evalRole = evalRoleRepository.findByNameAndCompany(evalRoleName,currentUser.getCompany());

			if(evalRole != null && evalRole.getCompany().getCompanyName().equals(currentUser.getCompanyName())) {
//				RedirectView redirectView = new RedirectView("/adminGroups", true);
//				redir.addFlashAttribute("error", "Eval Role with Name " + evalRoleName + " already exists");
//				log.error("Eval Role with Name " + evalRoleName + " already exists");
//				return redirectView;
				rolelist.add(evalRole);
			}
			else {
				//took this out role,
				rolelist.add(new EvalRole(evalRoleName,level, currentCompany));
				//System.out.println(roll + " " + roll_name);
			}
			

		}
		int totalrole = rolelist.size();
		evalRoleRepository.saveAll(rolelist);
		
		/* 
		 *	Groups Section
		 *
		 */
		int existingGroups = (int)groupRepository.count();
		System.out.println("\n\n EXISTING GROUPS IN REPO: " + existingGroups + "\n\n");


		for (int i = 0; sheet.getRow(0).getCell(i) != null; i++) {
			List<String> synclist = new ArrayList<String>();
			List<String> previewlist = new ArrayList<String>();
			
			Group group = new Group(currentUser.getCompany());

			// long id = (Long) null;

			for (int x = 0; sheet.getRow(x) != null; x++) {
				// System.out.print(sheet.getRow(x).getCell(i));
				if (x == 0) {
					//duplicates caught once year is set
					group.setGroupName(ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)));

					String groupstringid = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i))
							.replaceAll("\\s", "").replace("Group", "");
					int grstrIDint = Integer.parseInt(groupstringid)+existingGroups;
					group.setGroupNumber(grstrIDint);

					Group tempGroup = this.groupRepository.findByNumberAndCompany(grstrIDint, currentCompany);
					if(tempGroup != null) {
						RedirectView redirectView = new RedirectView("/adminGroups", true);
						redir.addFlashAttribute("error", "group " +  grstrIDint + " already exists.");
						//System.out.println("user doesn't not exist1 " + evaltemplateid);
						log.error("group " +  grstrIDint + " already exists.");
						return redirectView;
					}
				}

				else if (x == 1) {
					String evaltemplateid = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i));
					EvalTemplates evaltemp = evaluationRepository.findByNameAndCompany(evaltemplateid,currentCompany);
					if (evaltemp == null) {
						RedirectView redirectView = new RedirectView("/adminGroups", true);
						redir.addFlashAttribute("error", "template " + evaltemplateid + " does not exist, please upload it before trying to create groups that use that template");
						//System.out.println("user doesn't not exist1 " + evaltemplateid);
						log.error("user does not exist " + evaltemplateid);
						return redirectView;
					}

					boolean found = true;
					//group should not contain users yet, unsure what this is doing
					for(User user : group.getUsers()) {
						for(Department dept : evaltemp.getDepts()) {
							if(!user.getDepartments().contains(dept)) {
								found = false;
							}
							else {
								found = true;
								break;
							}							
						}
						if(!found) {
							break;
						}
					}
					if(found) {
						group.setEvalTemplates(evaltemp);
					}
					else {
						RedirectView redirectView = new RedirectView("/adminGroups", true);
						redir.addFlashAttribute("error", "user not found in any of the listed departments, could not user template on group");
						//System.out.println("user doesn't not exist1 " + evaltemplateid);
						log.error("user not found in any of the listed departments, could not user template on group");
						return redirectView;
					}


					

				}
				else if (x==2) {
					String year = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i));
					group.setYear(Integer.parseInt(year));

					//CATCH the instance where this group is a duplicate of an already created group by year+name.
					boolean duplicate = false;
					if (!(groupRepository.findGroupsByGroupNameAndYear(group.getGroupName(), group.getYear()).isEmpty())) {
						duplicate=true;
					}

					if(duplicate){
						redir.addFlashAttribute("warn", "duplicate name and year imported: " + group.getGroupName()
								+ ", " + group.getYear());
						log.warn("duplicate name and year imported: " + group.getGroupName()
								+ ", " + group.getYear() + ", this group already exists.");
						//unsure if this flash warn is quite right yet
						//Name Generation: e.g. Group1(1) instead of Group1
						String newname = generateName(group.getGroupName(), group.getYear());
						group.setGroupName(newname);
					}
				}
				// is self eval needed
				else if (x == 3) {
					String self = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)).replaceAll("\\s", "");
					if (self.equals("NoSelf-Eval")) {
						group.setSelfeval(false);
					} else if (self.equals("Self-Eval")) {
						group.setSelfeval(true);
					} else {
						RedirectView redirectView = new RedirectView("/adminGroups", true);
						redir.addFlashAttribute("error", "in group  " + group.getId()+ " layout");
						log.error("error in group  " + group.getId()+ " layout");
						return redirectView;
					}

				} else if (x < (3 + totalrole)) {
					String sync = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)).replaceAll("\\s", "");
					//System.out.print(sync);
					if(sync.equals("Sync")||sync.equals("Async")) {

						synclist.add(sync);
					}else
					{
						RedirectView redirectView = new RedirectView("/adminGroups", true);
						redir.addFlashAttribute("error", " in group " + group.getId()+ " Sync layout");
						log.error("error in group " + group.getId()+ " Sync layout");
						return redirectView;
					}

				}
				else if (x < (3 + totalrole+totalrole)) {
					String preview = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)).replaceAll("\\s", "");
					//System.out.print(preview);
					if(preview.equals("preview")||preview.equals("nopreview")) {

						previewlist.add(preview);
					}else {
						RedirectView redirectView = new RedirectView("/adminGroups", true);
						redir.addFlashAttribute("error", "in group  " + group.getId()+ " preview layout");
						log.error("error in group  " + group.getId()+ " preview layout");
						return redirectView;
					}

				}


				else {
					if (ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)) != null) {
						String name = ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i));
//						System.out.println("user/deptname:" + name);
						User user = userRepository.findUserByEmail(name);
//						System.out.println(user.getDepartmentName());
						Department dept = deptRepo.findByNameAndCompany(name, currentCompany);

						//						System.out.println((currentUser.isCompanySuperUser() && currentUser.getCompanyID() == user.getCompanyID()));
						//						System.out.println(currentUser.isCompanySuperUser() );
						//						System.out.println(user.getCompanyID());
						//						System.out.println(currentUser.getCompanyID());
						//						System.out.println(currentUser.getRole().writableUsers().contains(user));
						if (user == null && dept == null) {

							redir.addFlashAttribute("error", "user or dept "
									+ name + " does not exist");

							RedirectView redirectView = new RedirectView("/adminGroups", true);
							//System.out.println("user dosnt not exist " + x + " " + i
							//		+ ExcelRead_group.checkStringType(sheet.getRow(x).getCell(i)));
							log.error("user or dept " + name + "does not not exist");
							return redirectView;
							
						} else if(user != null) {
							if(currentUser.getRole().getName().equalsIgnoreCase("Evaluator_admin") || ((currentUser.getCompanyID() == user.getCompanyID()) || currentUser.getRole().writableUsers().contains(user))) {
								Reviewee reviewee = new Reviewee(group, user.getName(), user);
								user.setReviewee(true);
								group.appendReviewee(reviewee);
							}
						}
						else if((dept != null && currentUser.getRole().getName().equalsIgnoreCase("Evaluator_admin")) || ((currentUser.isCompanySuperUser() && currentUser.getCompanyID() == dept.getCompany().getId()) || currentUser.isAdminEval() ||currentUser.getRole().writableDepartments().contains(dept))){
							for(User u : dept.getUsers()) {
								Reviewee reviewee = this.revieweeRepository.findByNameAndCompany(u.getName(), u.getCompany());
								if(reviewee == null) {
									reviewee = new Reviewee(group, u.getName(), u);
								}								
								group.appendReviewee(reviewee);
								reviewee = null;
							}
						}
						else {
							RedirectView redirectView = new RedirectView("/adminGroups", true);
							redir.addFlashAttribute("error", "User/dept " + currentUser.getName() + " cannot add user/dept " + name + " to a group");
							log.error("User/dept " + currentUser.getName() + " cannot add user/dept " + name + " to a group");
							return redirectView;
						}
						user = null;
						dept = null;
					}
				}

			}
			syncmap.put(group.getGroupNumber(), synclist);
			previewmap.put(group.getGroupNumber(), previewlist);
			grouplist.add(group);

		}
		try {
			currentCompany.addGroups(grouplist);
//			System.out.println("added groups to cos");
			groupRepository.saveAll(grouplist);
		}
		catch(Exception e) {
			e.printStackTrace(System.out);
			RedirectView redirectView = new RedirectView("/adminGroups", true);
			redir.addFlashAttribute("error", "problem saving grouplist");
			log.error("problem saving grouplist");
			return redirectView;
		}
		//// Evaluator
		for (int i = 0; sheet2.getRow(i) != null; i += 2) {
			User user = userRepository.findByEmail(ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(0)));
			if (user != null) {			

				if(!currentUser.getRole().getName().equalsIgnoreCase("EVALUATOR_ADMIN")) {
					RedirectView redirectView = new RedirectView("/adminGroups", true);
					redir.addFlashAttribute("error", "User " + currentUser.getName() + " cannot give user " + user.getName() + " Evaluator permissions");
					log.error("User " + currentUser.getName() + " cannot give user " + user.getName() + " Evaluator permissions");
					return redirectView;

				} 
				for (int x = 1; sheet2.getRow(i).getCell(x) != null; x++) {
					String groupids = ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(x));
					String level = ExcelRead_group.checkStringType(sheet2.getRow(i + 1).getCell(x));
					List<String> levellist = Stream.of(level.split(",")).map(String::trim).collect(Collectors.toList());
					List<String> groupNumberlist = Stream.of(groupids.split(",")).map(String::trim)
							.collect(Collectors.toList());
					int size = rolelist.size();

					for (int y = 0; y < groupNumberlist.size(); y++) {
						int groupNum = Integer.parseInt(groupNumberlist.get(y)) + existingGroups;
						int groupIdx= Integer.parseInt(groupNumberlist.get(y));
						for (int z = 0; z < levellist.size(); z++) {
							// if evaluator is sync or not 
							Evaluator eval = new Evaluator(user, groupRepository.findByNumberAndCompany(groupNum, currentCompany),
									evalRoleRepository.findByNameAndCompany(levellist.get(z), user.getCompany()),user.getCompany());
							user.setReviewer(true);
							int num = eval.getLevel().getLevel();

							if (num==0) {num = 1;}

							if (num != size && (syncmap.get(groupNum).get(num - 1).equals("Async"))) {
								eval.setSync(false);
							} else if (num != size && syncmap.get(groupNum).get(num - 1).equals("Sync")) {
								eval.setSync(true);
							} else if (num == size) {
								eval.setSync(true);
							}

							if ((previewmap.get(groupNum).get(num - 1).equals("preview"))) {
								eval.setPreview(true);

							} 
							else if (previewmap.get(groupNum).get(num - 1).equals("nopreview")) {
								eval.setPreview(false);
							}

							List<Reviewee> rev = revieweeRepository.findByGroupNumberAndCompany(groupNum, currentCompany);
							List<Evaluator> eval2 = (evaluatorRepository.findByLevelLevelAndGroupNumberAndCompany(num - 1, groupNum, currentCompany));
							List<Evaluator> eval3 = (evaluatorRepository.findByLevelLevelAndGroupNumberAndCompany(num + 1, groupNum, currentCompany));
//							System.out.println(rev.size());
							for (int a = 0; a < rev.size(); a++) {
								EvaluationLog etemp = new EvaluationLog(eval, rev.get(a));


								for (int k = 0; k < eval2.size(); k++) {
									EvaluationLog log1 = evaluationLogRepository.findByEvaluatorIdAndRevieweeId(eval2.get(k).getId(), rev.get(a).getId());
									if ((eval2.get(k).isSync() != true) && log1.getAuth()) {
										etemp.setAuth(true);

									} else {
										etemp.setAuth(false);

									}
								}
								eval.appendEvalutationLog(etemp);
								for (int k = 0; k < eval3.size(); k++) {
									EvaluationLog log2 = evaluationLogRepository.findByEvaluatorIdAndRevieweeId(eval3.get(k).getId(), rev.get(a).getId());
									if ((eval.isSync() != true) && etemp.getAuth()) {
										log2.setAuth(true);

										evaluationLogRepository.save(log2);

									}

								}

							}

							evaluatorlist.add(eval);
							evaluatorRepository.save(eval);

						}
					}

				}
			}
			else {
				String name = ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(0));
				groupRepository.deleteAll(grouplist);
				evalRoleRepository.deleteAll(rolelist);
				redir.addFlashAttribute("error", "user " + name + " dosnt not exist");
				RedirectView redirectView = new RedirectView("/adminGroups", true);
				return redirectView;
			}


		}
		
		
		Iterable<User> allUsersIterable = userRepository.findAll();
		List<User> allUsers = StreamSupport.stream(allUsersIterable.spliterator(), false)
                .collect(Collectors.toList());
		
		for (User user2 : allUsers) {
		    Calendar calendar = Calendar.getInstance();
		    Date currentDate = calendar.getTime();
		    
		    user2.setStartingDate(currentDate);
		    userRepository.save(user2);
		    
		    calendar.add(Calendar.MONTH, 1);
		    Date newDate = calendar.getTime();
		    
		    user2.setEndingDate(newDate);
		    userRepository.save(user2);
		}
		
		if (!allUsers.isEmpty()) {
		    User user2 = allUsers.get(0); 
		    Date userStarting = user2.getStartingDate();
		    Date userEnding = user2.getEndingDate();
		    
		    model = AdminMethodsService.pageNavbarPermissions(user2, model, evaluatorRepository, evalFormRepo);
		    model.addAttribute("startDate", userStarting);
		    model.addAttribute("endDate", userEnding);
		}
		
		


		redir.addFlashAttribute("completed", true);
		RedirectView redirectView = new RedirectView("/adminGroups", true);
		log.info("group was added ");
		return redirectView;
	}

	@GetMapping("/uploading")
	public String uploadgroup(Model model) {

		return "redirect:/adminGroups";
	}

	@GetMapping("/Evaluationgroups")
	public String evalGroups(Model model, Authentication authentication) {

		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepository.findById(idnum).orElse(null);

		boolean showUserScreen = true;
		boolean rolesNeedAdded = false;
		
		Set<Role> companyRoles = currentUser.getCompany().getRoles();
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
		
		List<Group> grouplist = (List<Group>) groupRepository.findByevaluatorUserId(userD.getID(),Sort.by(Sort.Direction.ASC, "Id"));
		
		grouplist = new ArrayList<Group>(new LinkedHashSet<Group>(grouplist));

		model.addAttribute("groups", grouplist);

		List<EvalRole> roles = (List<EvalRole>) evalRoleRepository.findAll();



		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);
		
		
		model.addAttribute("id", userD.getID());
		model.addAttribute("roles", roles);
		model.addAttribute("evalu", currentUser);
		model.addAttribute("groups", grouplist);
		model.addAttribute("showUserScreen", showUserScreen);
		model.addAttribute("rolesNeedAdded", rolesNeedAdded);
		log.info("evaluationView was opened ");
		
		return "evaluationView";
	}


	
	
	/**
	 * takes  an load information for the admin group age 
	 * @param model
	 * @return admin group page 
	 */
	@GetMapping("/adminGroups")
	public String Groups(Model model, Authentication auth, RedirectAttributes ra) {
		
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();
		

		currentUser = this.userRepository.findById(idnum).orElse(null);
		String UserRole= currentUser.getRole().getName();
		model.addAttribute("UserRole",UserRole);

		currentCompany = currentUser.getCompany();
		
		
		
		Group group = new Group();
		List<Group> grouplist = (List<Group>) groupRepository.findByCompany(currentCompany);
		List<EvalRole> roles = (List<EvalRole>) evalRoleRepository.findByCompany(currentCompany);
		List<EvaluationLog> evalLog = (List<EvaluationLog>) evaluationLogRepository.findByEvaluatorCompany(currentCompany);
		List<User> companyEvaluators = (List<User>) userRepository.findByCompany(currentCompany);
		
		boolean showUserScreen = true;
		boolean rolesNeedAdded = false;
		
		Set<Role> companyRoles = currentUser.getCompany().getRoles();
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
		

		if(evalFormRepo.findByCompany(currentCompany).size() == 0) {
			
			ra.addFlashAttribute("message", "Upload Evaluation Form Templates first");
			return "redirect:/home";
		}

		model.addAttribute("evaluation", evalLog);
		model.addAttribute("roles", roles);

		List<String>warnings =new ArrayList<String>();
		for(int x=0; x<roles.size();x++) {
			for(int y=0; y<grouplist.size();y++) {
				Boolean temp = evaluatorRepository.existsBylevelAndGroup(roles.get(x),grouplist.get(y));
				if(temp ==false) {
					warnings.add("Group:"+" "+ grouplist.get(y).getNumber()+" is missing "+ roles.get(x).getName()+" Evaluator");
				}
			}
		}

		for(int y=0; y<grouplist.size();y++) {
			if(grouplist.get(y).getReviewee().isEmpty()) {
				warnings.add("Group:"+" "+ grouplist.get(y).getNumber()+" has no reviewee");
			}
		}
		
		//filter evalList to only show evaluators
		for(int i = 0; i < companyEvaluators.size(); i++) {
			if(!companyEvaluators.get(i).getRoleName().equals("EVALUATOR") || !companyEvaluators.get(i).getRoleName().equals("EVALUATOR_EVAL")) {
				companyEvaluators.remove(i);
			}
		}
		
		List<Group> archiveGroup = new ArrayList();
		List<Group> removeG = new ArrayList();
		
		for(Group g: grouplist) {
			
			if(g.isArchived()) {
				archiveGroup.add(g);
				removeG.add(g);
			}
			
		}
		
		grouplist.removeAll(removeG);
		
		model.addAttribute("archiveGroup", archiveGroup);
		
		model.addAttribute("warnings",warnings);
		if(grouplist.isEmpty()) {
			grouplist=null;
			model.addAttribute("groupsAreEmpty", true);
		} else {
			model.addAttribute("groupsAreEmpty", false);
			grouplist=groupRepository.findAllByOrderByGroupNameAsc();
			model.addAttribute("groups", grouplist);
		}

		model.addAttribute("group", group);
		log.info("admin group was open ");
		
		model.addAttribute("formslist", evalFormRepo.findAll());
		model.addAttribute("companyevaluators", companyEvaluators);
		model.addAttribute("showUserScreen", showUserScreen);
		model.addAttribute("rolesNeedAdded", rolesNeedAdded);
		
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);
		
		
		return "adminGroups";
	}





	/**
	 * this method will delete a group and store it competed evaluation in the archive 
	 * @param id of the group being deleted
	 * @param model
	 * @return
	 */
	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable("id") long id, Model model) {

		Group group = groupRepository.findById(id);
		List<Archive> Archivelist = new ArrayList<Archive>();
		for(int x =0; x< group.getEvaluator().size();x++) {
			List<EvaluationLog> temp = group.getEvaluator().get(x).getEvalutationLog();
			for(int y =0; y< temp.size();y++) {
				if(temp.get(y).getCompleted()) {
					Archive temp2 = new Archive(temp.get(y));
					Archivelist.add(temp2);
				}

			}
		}
		for(int x =0; x< group.getReviewee().size();x++) {
			SelfEvaluation temp = group.getReviewee().get(x).getSelfEvaluation();
			if(temp !=null) {
				if(temp.getCompleted()) {
					Archive temp2 = new Archive(temp);
					Archivelist.add(temp2);
				}
			}

		}
		groupRepository.delete(group);
		archiveRepository.saveAll(Archivelist);
		if(group.getCompany() != null) {
			log.info("Deleted Group " + group.getGroupNumber() + " (ID:" + group.getId() + ") For Company " + group.getCompany().getCompanyName() + " (ID:" + group.getCompany().getId() + ")");
		} else {
			log.info("Deleted Group " + group.getGroupNumber() + " (ID:" + group.getId() + ") [No Company]");

		}		return "redirect:/adminGroups";
	}



	/**
	 * Processes the request for the download of the Evaluation Results excel file for a given group.
	 * 
	 * @param groupId - ID of the Group
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/download_eval_group_results/{groupId}")
	public ResponseEntity<Resource> downloadEvalGroupResults(@PathVariable("groupId") long groupId) throws Exception {

		Group group = groupRepository.findById(groupId);
		String evalId = group.getEvalTemplates().getName();

		// Name of download file
		final String FILE_NAME = "Group " + groupId + " Evaluation Summary - " + evalId + ".xlsx";

		log.info("File '" + FILE_NAME + "' requested for download.");

		// Create the temp directory if it does not exist
		Files.createDirectories(Paths.get(TEMP_FILES_PATH));

		//Get Evaluation template
		List<EvalTemplates> evalTemps = (List<EvalTemplates>) evalFormRepo.findAll();
		byte[] evalTempByte = null;

		for (int i = 0; i<evalTemps.size();i++) {
			String name = evalTemps.get(i).getName();

			if (name.equals(evalId)) {
				evalTempByte = evalTemps.get(i).getEval();
			}
		}

		Evaluation evalTemp = (Evaluation) SerializationUtils.deserialize(evalTempByte);

		//Get Completed evaluations
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evaluationLogRepository.findAll();
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();

		byte[] evalLogByte = null;

		for (int i = 0; i < evalLogs.size();i++) {
			if (evalLogs.get(i).getCompleted()) {
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);

				if(completeEval.getEvalID().equals(evalId)) {

					String evalGroupNum = "";

					for (int j = 0; j < completeEval.getSection(0).getQuestionCount(); j++) {
						if (completeEval.getSection(0).getQuestion(j).getQText().equals("GROUP NO.")) {
							evalGroupNum = completeEval.getSection(0).getQuestion(j).getQResponse();
						}
					}

					if (evalGroupNum.matches(".*[0-9].*")) {
						evalGroupNum = evalGroupNum.replaceAll("\\D+","");
						if (Long.parseLong(evalGroupNum) == groupId) {
							completedEvals.add(completeEval);
						}
					}	
				}
			}
		}

		//System.out.println("FOUND " + completedEvals.size() + " COMPLETED EVALS FOR GROUP: " + groupId);
		log.info("Found " + completedEvals.size() + " completed evals for group " + groupId);

		// Create the excel report file
		//GenerateEvalReport.generateReport(evalTemp, completedEvals, TEMP_FILES_PATH, FILE_NAME);
		GenerateEvalReportPoi.generateReport(evalTemp, completedEvals, TEMP_FILES_PATH, FILE_NAME);
		//Download the file
		FileSystemResource resource = new FileSystemResource(TEMP_FILES_PATH + FILE_NAME);
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
	@GetMapping("/edit/{gid}/{id}")
	public String editGroupUser(@PathVariable("gid") long groupId,@PathVariable("id") long userId, Model model,Authentication auth){
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		User currentUser = userRepository.findById(idnum).orElse(null);

		System.out.println("this is user logged in  "+currentUser);
		
		List<Evaluator> evalList = evaluatorRepository.findByGroupId(groupId);
		model.addAttribute("evalList", evalList);
		
		Group group = groupRepository.findById(groupId);
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
		model.addAttribute("user",user);
		model.addAttribute("group",group);
		long uid=(long) userId;
		model.addAttribute("uid",uid);
		System.out.println(userId);
		model.addAttribute("id",userId);
		return "EditUserGroup";
	}
	@PostMapping("/group/userUpdate/{id}/{gid}")
	public String updateUser(@PathVariable("id") long id, @Validated User user,@PathVariable("gid") long groupId,
			/* BindingResult result, */ Model model, Authentication auth) {


		User currentUser;

		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = userRepository.findById(idnum).orElse(null);

		String ansr = null;
		String mess = null;
//		model.addAttribute("perPage", perPage);

		User user2 = userRepository.findByid(id);



		// Performs comparison between old and new user values for changes
//		User user3 = adminMethodsService.comparingMethod(id, user, user2, model);
//		System.out.println(user3.toString());
		// Checks if email already used by another user, if not then the user selected
		// will be updated.
//		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepository, evalFormRepo);
//		model = AdminMethodsService.addingOrEditingUser(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo,  model);


//		Department dept = deptRepo.findByNameAndCompany(user.getDepartmentName(), currentUser.getCompany());
//
//
//		if(dept != null) {
//			System.out.println("passed null");
//			if(currentUser.getRole().writableDepartments().contains(dept) || currentUser.isCompanySuperUser() || currentUser.isSuperUser() && !user3.getDepartments().contains(dept)) {
//				user3.addDepartment(dept);
//				dept.addUser(user3);
//				System.out.println("added dept");
//			}
//			else if(!user3.getDepartments().contains(dept))
//			{
//				System.out.println("already had it");
//				mess = "User " + user2.getName() + " already has the dept: " + dept.getName();
//			}
//			else{
//				System.out.println("no permissions");
//				mess = "User " + user2.getName() + " does not have permission to add a user to department " + dept.getName();
//			}
//		}


		if ((userRepository.findByEmail(user.getEmail()) == null)
				|| (userRepository.findByEmail(user.getEmail())) == userRepository.findByid(id)) {

			user2.setFirstName(adminMethodsService.capitalize(user.getFirstName()));
			user2.setLastName(adminMethodsService.capitalize(user.getLastName()));
			user2.setEmail(adminMethodsService.capitalize(user.getEmail()));
			user2.setJobTitle(adminMethodsService.capitalize(user.getJobTitle()));


			try {
				userRepository.save(user2);
				ansr = "pass";
				mess = "User successfully edited!";

			}
			catch(Exception e){
				e.printStackTrace();
				ansr = "fail";
				mess = "problem occured editing user.";
				log.error(e.getStackTrace().toString());

			}
//			adminMethodsService.adminUserPageItems(ansr, mess, model, auth);
			log.info("Updated User " + user2.getName() + " (ID:" + user2.getId() + ")");
			System.out.println("in if condition");
			return "redirect:/edit/{gid}/{id}";
		} else {
//			adminMethodsService.adminUserPageItems(ansr, keyword, mess, perPage, model, sort, currPage, sortOr, auth);
			log.info("Updated User " + user2.getName() + " (ID:" + user2.getId() + ")");
			System.out.println("in else condition");
			return "redirect:/edit/{gid}/{id}";

		}
	}

	/*
	 * add review to group
	 * set group reviewee list to newList
	 * update group
	 * */

	@GetMapping("/group/deleteUser/{id}/{gid}")
	public String removeUserGroup(@PathVariable("id") long id,@PathVariable("gid") long groupId,Model model,Authentication auth){

		Group group=groupRepository.findById(groupId);
		Optional<Reviewee> review=revieweeRepository.findByGroupIdAndUserId(groupId,id);
		Reviewee rev = null;
		if (review.isPresent()){
			rev=review.get();
		}
		groupService.updateDeletedUser(groupId,rev);
		Group updatedGroup=groupRepository.findById(groupId);
		System.out.println("after save in database \n"+updatedGroup.getReviewees());
		model.addAttribute("group",updatedGroup);
		return "DeleteUser";
	}
}
