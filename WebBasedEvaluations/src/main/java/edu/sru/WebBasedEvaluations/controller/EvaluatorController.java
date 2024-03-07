package edu.sru.WebBasedEvaluations.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import edu.sru.WebBasedEvaluations.domain.EmailService;
import edu.sru.WebBasedEvaluations.domain.EmailService.EmailType;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.EvaluatorId;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.SelfEvaluation;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.WebBasedEvaluations.evalform.ParseEvaluation;
import edu.sru.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.WebBasedEvaluations.repository.SelfEvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
/** EvaluatorController
 * Controls the evaluation behavior  of the application 
 *
 */
@Controller
public class EvaluatorController {
	
	
	private EvaluatorRepository evaluatorRepository;
	private UserRepository userRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private RevieweeRepository revieweeRepository;
	private EvaluationRepository evalFormRepo;
	private EvalRoleRepository roleRepository;
	private SelfEvaluationRepository selfEvalRepo;
	private EmailService emailService;
	
	private Logger log = LoggerFactory.getLogger(EvaluatorController.class);
	
	//create an UserRepository instance - instantiation (new) is done by Spring
    public EvaluatorController (EvaluatorRepository evaluatorRepository,UserRepository userRepository ,EvaluationLogRepository evaluationLogRepository,RevieweeRepository revieweeRepository,EvaluationRepository evalFormRepoprivate, EvalRoleRepository roleRepository,EvaluationRepository  evalFormRepo, SelfEvaluationRepository selfEvalRepo, EmailService emailService) {
    	this.revieweeRepository= revieweeRepository;
		this.evaluatorRepository  = evaluatorRepository;
		this.userRepository  = userRepository;
		this.evaluationLogRepository= evaluationLogRepository;
		this.evalFormRepo =evalFormRepo;
		this.roleRepository =roleRepository;
		this.selfEvalRepo=selfEvalRepo;
		this.emailService = emailService;
		
	}

    
    /**Method for Starting an evaluation 
     * method will takes  take the the user to a evaluation form to edit and submit for the for reviewee 
     * @param log is the id the evaluationLog, the evaluationlog stores the evaluation of the reviewee made by the evaluator 
     * @param authentication hold the current user data 
     * @param model is the a model object use to add attributes to a web page 
     * The model attributes
     * eval: is the evaluator template which will be either be a new blank template or if the evaluator has all ready started one  it will be the previously saved template 
     * log: is the evaluationlog  id
     * @return evalFormEdit takes you to the evalFormEdit where the user makes or edit and evaluation 
     */
    @RequestMapping({"/eval/{log}"})
    public Object submiteval(@PathVariable("log") long log, Authentication authentication, Model model) {

    	MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();

    	Evaluation evall;

    	EvaluationLog evaluationLog = evaluationLogRepository.findById(log).orElse(null);
    	
    	//RedirectView redirectView = new RedirectView("/evalFormEdit", true);
    	
    	Group group = evaluationLog.getReviewee().getGroup();
    	
    	Date deadline = group.getDeadline();
    	
    	Date todaysDate = new Date();
    	
    	if(deadline == todaysDate) {
    		model.addAttribute("error, you are past the deadline");
    		return "evalFormEdit";
    	}else if(evaluationLog.getPath() == null) {
			System.out.println(evaluationLog);
    		//Deserialize
    		EvalTemplates evalTemp = evalFormRepo.findById(evaluationLog.getEvaluator().getGroup().getEvalTemplates().getId());
			System.out.println(evalTemp.getId());
    		evall = (Evaluation) SerializationUtils.deserialize(evalTemp.getEval());
    		
    		//Populate preload
    		User reviewee = evaluationLog.getReviewee().getUser();
    		evall.populatePreload(reviewee, group);
    		
    		//Serialize
    		byte[] data;
        	data = SerializationUtils.serialize(evall);
        	
        	//save to database
        	evaluationLog.setPath(data);
        	evaluationLogRepository.save(evaluationLog);
        	
    		this.log.info("Evaluator '" + evaluationLog.getEvaluator().getUser().getEmail() + "' began a new evaluation for user '" + evaluationLog.getReviewee().getUser().getEmail() + "'.");

   
    	} else {
    		evall = (Evaluation) SerializationUtils.deserialize(evaluationLog.getPath());
    	
    		//Populate preload
    		User reviewee = evaluationLog.getReviewee().getUser();
    		evall.populatePreload(reviewee, group);
    		
    		this.log.info("Evaluator '" + evaluationLog.getEvaluator().getUser().getEmail() + "' continued evaluation for user '" + evaluationLog.getReviewee().getUser().getEmail() + "'.");

    	}

    	model.addAttribute("eval", evall);
    	model.addAttribute("log", log);
    	System.out.println(log);
    	


    	return "evalFormEdit";
    }
    
    
    /**Method for previewing a evaluation
     * 
     * the user is able to preview evaluation based on the logId
     * @param redir is the a RedirectAttributes model object use to add attributes to a Redirect web page
     * @param log is the id the evaluationLog, the evaluationlog stores the evaluation of the reviewee made by the evaluator 
     * @param authentication hold the current user data 
     * @param model is the a model object use to add attributes to a web page
     * the model attributes:
     * eval: is the filled out evaluation form that will be displayed 
     * log: is the evaluationlog  id
     * Address: stores the back buttons link to return to the previous page 
     * @return previewEval.html page where the use can view the evaluation 
     * if user user is an eval_adim and is not the evaluationlog  evaluator the will:
     * have to have a higher role to preview evaluation
     * and the evuationlog will have to allow other evaluator to preview it  
     */
    @RequestMapping({"/previeweval/{log}"})
    public Object previeweval(RedirectAttributes redir ,@PathVariable("log") long log, Authentication authentication, Model model) {

    	MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
    	
    	Evaluation evall;

    	EvaluationLog evaluationLog = evaluationLogRepository.findById(log).orElse(null);
    	List<Evaluator>evaluator=evaluatorRepository.findByUserIdAndGroupId(userD.getID(),evaluationLog.getEvaluator().getGroup().getId());
    	int rolenum=0;
    	for(int x =0; x< evaluator.size();x++) {
    		if(rolenum < evaluator.get(x).getLevel().getId()) {
    			rolenum = evaluator.get(x).getLevel().getLevel();
    		}
    	}
    	if(userD.getRole().getName().equals("EVAL_ADMIN") ){
    		model.addAttribute("address", "/admineval/"+evaluationLog.getReviewee().getUser().getId());
    	}
    	else if(evaluationLog.getReviewee().getUser().getId() == userD.getID()) {
    		model.addAttribute("address", "/myEval");
    		
    	}
    	else if( userD.getID() == evaluationLog.getEvaluator().getUser().getId()) {
    		model.addAttribute("address", "/Evaluationgroups");
    		
    	}
    	else if(!evaluationLog.getEvaluator().isPreview()) {
    		RedirectView redirectView = new RedirectView("/Evaluationgroups", true);
			redir.addFlashAttribute("error", "User does not have permission ");
			return redirectView;
    	}
    	else if(evaluationLog.getEvaluator().getLevel().getId()> rolenum) {
    		RedirectView redirectView = new RedirectView("/Evaluationgroups", true);
			redir.addFlashAttribute("error", "User does not have permission ");
			return redirectView;
    	}
    	else if(evaluationLog.getEvaluator().getLevel().getId()<= rolenum) {
    		model.addAttribute("address", "/Evaluationgroups");
			
    	}
    	if(evaluationLog.getPath() == null) {
    		//Deserialize
    		EvalTemplates evalTemp = evalFormRepo.findById(evaluationLog.getEvaluator().getGroup().getEvalTemplates().getName()).orElse(null);

    		evall = (Evaluation) SerializationUtils.deserialize(evalTemp.getEval());
    	}else {

    		evall = (Evaluation) SerializationUtils.deserialize(evaluationLog.getPath());
    		//evall.printEvaluation();
    	}

    	model.addAttribute("eval", evall);
    	model.addAttribute("log", log);
    	if(evaluationLog.getAttach()!= null) {
    		
    		model.addAttribute("attach", true);
    	}
    	
    	return "previewEval";
    }
    
    
    @GetMapping({"/downloada/{log}"})
    public  ResponseEntity<byte[]> download(RedirectAttributes redir ,@PathVariable("log") long log, Authentication authentication, Model model) throws Exception {
    final String TEMP_FILES_PATH = "src\\main\\resources\\temp\\";
    	EvaluationLog evaluationLog = evaluationLogRepository.findById(log).orElse(null);
    	
    	//final String FILE_NAME = evaluationLog.getAttachname();
    	
    	//Path path = Paths.get(TEMP_FILES_PATH + FILE_NAME);
		//Files.write(path, evaluationLog.getAttach());

		//Download the file
		/*
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
		*/
		 return ResponseEntity.ok()
			        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + evaluationLog.getAttachname() + "\"")
			        .body(evaluationLog.getAttach());
	
    }
    
    
  
    /**Method for saving evaluation
     * this method review the the answer from the evaluator and either save it for later or set it as complete if its being submitted. 
     * if question with the required tag are not answer the method will redirect the user back to the form and highlight the required question that sill need to be answered 
     * @param eval is the evaluation object 
     * @param response  is an array  that hold the answer for question the the user answered 
     * @param completed is a boolean variable that determines if the sue is saving  or submitting their evaluation 
     * @param log is the evaluationlog  id that the use is saving to 
     * @param authentication is the user details 
     * @param redir is the a RedirectAttributes model object use to add attributes to a Redirect web page
     * @return will return user to the evaluation group page or redirect them  back to the evaluation form if there was an error 
     * @throws Throwable 
     * @throws Exception if parameters are missing 
     */
    @RequestMapping({"/save_eval/{log}"})
    public String saveEvalForm(@Validated Evaluation eval,
    		@RequestParam(value="response", required=false) String[] response,
    		@RequestParam(value="completed", required=true) boolean completed,
    		@PathVariable("log") long log,
    		Authentication authentication,
    		RedirectAttributes redir,
    		@RequestParam(value="file", required=false) MultipartFile file) {
    	
   

    	EvaluationLog evaluationLog = evaluationLogRepository.findById(log).orElse(null);
 
    	EvalTemplates evalTemp = evalFormRepo.findById(evaluationLog.getEvaluator().getGroup().getEvalTemplates().getId());

    	
    	Evaluation evalform;

    	if (evaluationLog.getPath() == null) {
    		// Deserialize
    		evalform = (Evaluation) SerializationUtils.deserialize(evalTemp.getEval());
    	} else {
    		// Deserialize
    		evalform = (Evaluation) SerializationUtils.deserialize(evaluationLog.getPath());
    	}

    	// Getting and saving responses
    	evalform.saveResponses(response);

    	// Updating compute sections
    	evalform.updateCompute();

    	// Determine if evaluation is complete
    	List <Integer> incompQuests = new ArrayList<Integer>();
    	incompQuests.addAll(evalform.verifyCompleted());

    	// Serialize
    	byte[] data;
    	data = SerializationUtils.serialize(evalform);

    	EvalRole level = evaluationLog.getEvaluator().getLevel();
    	long revid = evaluationLog.getReviewee().getId();
    	long groupid = evaluationLog.getEvaluator().getGroup().getId();
    	Date date = new Date();
    	//System.out.println(date);
    	evaluationLog.setDateEdited(date);
    	evaluationLog.setPath(data);
    		
    	if(file != null){
    		file.getContentType();
        	System.out.print(file.getOriginalFilename());
    		try {
				evaluationLog.setAttach(file.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		evaluationLog.setAttachname(file.getOriginalFilename());
    	}
    	List<Evaluator> eval2;


    	if (completed == false) {
    		evaluationLog.setCompleted(false);
    		evaluationLogRepository.save(evaluationLog);
    		this.log.info("Evaluator '" + evaluationLog.getEvaluator().getUser().getEmail() + "' saved an evaluation for later.");
    		return "redirect:/Evaluationgroups";
    	}

    	// If evaluation is complete
    	if (evalform.getCompleted()) {

    		evaluationLog.setCompleted(true);
    		evaluationLog.getEvaluator().getGroup().setEvalstart(true);
    		evaluationLogRepository.save(evaluationLog);
    		this.log.info("Evaluator '" + evaluationLog.getEvaluator().getUser().getEmail() + "' submitted a completed evaluation.");

    		List<Evaluator> eval1 = evaluatorRepository.findByLevelLevelAndGroupId(level.getLevel(), groupid);
    		//other evaluation
    		for(int i =0; i< eval1.size();i++) {
    			EvaluationLog  evaluationLog1 = evaluationLogRepository.findByEvaluatorIdAndRevieweeId(eval1.get(i).getId(),revid);
    			if(evaluationLog1.getCompleted()== false) {
    				return "redirect:/Evaluationgroups";
    				
    			}
    		}

    		long rolenum =level.getLevel();
    		// is evaluator is not sync it will  run back to the evaluation page
    		if(evaluationLog.getEvaluator().isSync() != true) {
    			return "redirect:/Evaluationgroups";
    		}
    		while(true) {
    			rolenum = rolenum +1;
    			EvalRole role = roleRepository.findById(rolenum).orElse(null);
    			if(role == null) {
    				eval2=null;
    			}
    			else {
    				eval2 = evaluatorRepository.findByLevelLevelAndGroupId(role.getLevel(), groupid);
    			}

    			if(eval2!=null) {


    				for(int i =0; i< eval2.size();i++) {
    					EvaluationLog  evaluationLog2 = evaluationLogRepository.findByEvaluatorIdAndRevieweeId(eval2.get(i).getId(),revid);

    					evaluationLog2.setAuth(true);
    					evaluationLogRepository.save(evaluationLog2);
    				}
    			}
    			if (eval2 == null ||eval2.get(0).isSync()){
    				
    				
    				return "redirect:/Evaluationgroups";
    			}
    		}
    		// If evaluation is not complete
    	}
    	else {

    		evaluationLog.setCompleted(false);
    		evaluationLogRepository.save(evaluationLog);

    		redir.addFlashAttribute("error", incompQuests.size() + " required question(s) are blank and must be answered.");
    		redir.addFlashAttribute("incompQuests", incompQuests);
    		
    		this.log.info("Evaluator '" + evaluationLog.getEvaluator().getUser().getEmail() + "' attempted to submit an evaluation but " + incompQuests.size() + " required questions were left blank.");

    		return "redirect:/eval/" + log;
    	}

    }
    
    @GetMapping({"/requestSelfEval/{id}"})
    public Object requestEval(@PathVariable("id") long revId, Authentication authentication, Model model) {
    	
    	Reviewee rev = revieweeRepository.findById(revId);
    	MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
		Long userid = userD.getID() ;
		User user = userRepository.findByid(userid);
    	Evaluation evall;
    	
    	
    	
    	//Deserialize
		EvalTemplates evalTemp = rev.getGroup().getEvalTemplates();
		//evalFormRepo.findById(reviewee.getGroup().getEvalTemplates().getId()).orElse(null);
		evall = (Evaluation) SerializationUtils.deserialize(evalTemp.getEval());

		//Populate preload
		Group group = rev.getGroup();
		evall.populatePreload(user, group);	

		//Serialize
		byte[] data;
		data = SerializationUtils.serialize(evall);

		//save to database
		SelfEvaluation selfEval = new SelfEvaluation(rev);
		selfEval.setPath(data);
		log.info("Submitted Self Evaluation (ID:" + evall.getEvalID() + ") for " + user.getName() + " (ID:" + user.getId() + ")");
	
		selfEvalRepo.save(selfEval);

		emailService.sendEmail(user.getEmail(), "", EmailService.EmailType.SELFEVALUATION);
		
    	return "redirect:/Evaluationgroups";

    }

}
