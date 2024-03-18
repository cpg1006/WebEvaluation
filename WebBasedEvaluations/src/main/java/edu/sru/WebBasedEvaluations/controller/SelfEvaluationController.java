package edu.sru.WebBasedEvaluations.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.SerializationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
import edu.sru.WebBasedEvaluations.evalform.PdfGenarator;
import edu.sru.WebBasedEvaluations.excel.ExcelRead_group;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.WebBasedEvaluations.repository.SelfEvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
/**SelfEvaluationController
 *  Controls the self evaluation behavior
 *
 */
@Controller
public class SelfEvaluationController {

	private RevieweeRepository revieweeRepository;
	private UserRepository userRepository;
	private SelfEvaluationRepository selfEvaluationRepository;
	private EvaluationLogRepository evaluationLogRepository;
	private EvaluationRepository evalFormRepo;
	private GroupRepository groupRepo;
	private Logger log = LoggerFactory.getLogger(SelfEvaluationController.class);

	SelfEvaluationController (RevieweeRepository revieweeRepository, UserRepository userRepository,	SelfEvaluationRepository selfEvaluationRepository,EvaluationLogRepository evaluationLogRepository, EvaluationRepository evalFormRepo){
		this.revieweeRepository= revieweeRepository;
		this.userRepository = userRepository;
		this.selfEvaluationRepository =selfEvaluationRepository;
		this.evaluationLogRepository= evaluationLogRepository;
		this.evalFormRepo= evalFormRepo;
	}



	/**submitselfeval
	 * Generates a form from the user to to fill out and submit as a self evaluation
	 * @param id is the reviewee id 
	 * @param authentication contains the user details
	 * @param model is the a model object use to add attributes to a web page 
	 * The model attributes
	 * eval: is the evaluator template which will be either be a new blank template or if the evaluator has all ready started one  it will be the previously saved template 
	 * id: is the reviewee  id
	 * @param redir is the a RedirectAttributes model object use to add attributes to a Redirect web page
	 * @return selfEvalFormEdit page 
	 */
	@RequestMapping({"/selfeval/{id}"})
	public Object submitselfeval(@PathVariable("id") long id,Authentication authentication, Model model,RedirectAttributes redir) {

		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
		Long userid = userD.getID() ;
		User user = userRepository.findByid(userid);
//		Reviewee reviewee = revieweeRepository.findByNameAndCompany(user.getName(), user.getCompany());

		
		List<Reviewee> revieweeOpt = revieweeRepository.findByUser_Id(id);
		Evaluation evall;

		if(revieweeOpt == null) {
			RedirectView redirectView = new RedirectView("/home", true);
			System.out.println("user not being evaluated ");
			redir.addFlashAttribute("error","user not being evaluated ");
			return redirectView;
		}
		
		Reviewee reviewee = revieweeOpt.get(0);
		SelfEvaluation selfEvaluation = selfEvaluationRepository.findByReviewee(reviewee);

		if(selfEvaluation == null) {

			//Deserialize
			EvalTemplates evalTemp = reviewee.getGroup().getEvalTemplates();
			//evalFormRepo.findById(reviewee.getGroup().getEvalTemplates().getId()).orElse(null);
			evall = (Evaluation) SerializationUtils.deserialize(evalTemp.getEval());

			//Populate preload
			Group group = reviewee.getGroup();
			System.out.println(group);
			evall.populatePreload(user, group);	

			//Serialize
			byte[] data;
			data = SerializationUtils.serialize(evall);

			//save to database
			SelfEvaluation selfEval = new SelfEvaluation(reviewee);
			selfEval.setPath(data);
			log.info("Submitted Self Evaluation (ID:" + evall.getEvalID() + ") for " + user.getName() + " (ID:" + user.getId() + ")");

			selfEvaluationRepository.save(selfEval);


		}else {

			evall = (Evaluation) SerializationUtils.deserialize(selfEvaluation.getPath());

		}


		model.addAttribute("eval", evall);
		model.addAttribute("id", id);
		model.addAttribute("user", user);

		return "selfEvalFormEdit";
	}

	/**viewselfeval
	 * lets user view  self evaluation
	 * @param id is the reviewee id 
	 * @param type  is who is viewing the self evaluation 
	 * @param authentication contains the user details
	 * @param model is the a model object use to add attributes to a web page 
	 * The model attributes
	 * eval: is the evaluator template which will be either be a new blank template or if the evaluator has all ready started one  it will be the previously saved template 
	 * id: is the reviewee  id
	 * address: holds the back address
	 * @return previewEval
	 */
	@RequestMapping({"/viewselfeval/{id}/{type}"})
	public Object viewselfeval(@PathVariable("id") long id,@PathVariable("type") String type,Authentication authentication, Model model) {


		List<Reviewee> revieweeOpt = revieweeRepository.findByUser_Id(id);
		Reviewee reviewee = revieweeOpt.get(0);
		Evaluation evall;

		SelfEvaluation selfEvaluation = selfEvaluationRepository.findByReviewee(reviewee);
		
		System.out.println(selfEvaluation.getPath());



		evall = (Evaluation) SerializationUtils.deserialize(selfEvaluation.getPath());
		


		if(type.equals("eval")) {
			model.addAttribute("address", "/Evaluationgroups");
		}
		else if(type.equals("rev")) {
			model.addAttribute("address", "/usergroups/" + reviewee.getUser().getId());
		}
		else if(type.equals("admin")) {
			long eid = selfEvaluation.getReviewee().getUser().getId();
			model.addAttribute("address", "/admineval/"+eid);
		}
		
		System.out.println(evall);

		model.addAttribute("eval", evall);
		model.addAttribute("id", id);

		return "previewEval";
	}


	/** saveselfEvalForm
	 * method used to save self evaluation
	 * @param eval is the evaluation object 
	 * @param response is an array  that hold the answer for question the the user answered 
	 * @param completed is a boolean variable that determines if the sue is saving  or submitting their evaluation
	 * @param id holds the id of the self evaluation
	 * @param authentication is the user details 
	 * @param model
	 * @param redir is the a RedirectAttributes model object use to add attributes to a Redirect web page
	 * @return either redirect you to "redirect:/myEval" if your saving for later  or "redirect:/selfeval/" + id if your miss information 
	 * @throws Exception
	 */
	@RequestMapping({"/save_selfeval/{id}"})
	public String saveselfEvalForm(@Validated Evaluation eval, BindingResult results,
			@RequestParam(value="response", required=false) String[] response,
			@RequestParam(value="completed", required=true) boolean completed,
			@PathVariable("id") long id ,
			Authentication authentication,
			Model model,
			RedirectAttributes redir) throws Exception {

		MyUserDetails userD = (MyUserDetails) authentication.getPrincipal();
		Long userid = userD.getID() ;
		User user = userRepository.findByid(userid);
		
		System.out.println(id);
		
		List<Reviewee> revieweeOpt = revieweeRepository.findByUser_Id(id);
		
		
		Reviewee reviewee = revieweeOpt.get(0);
		
		EvalTemplates evalTemp = reviewee.getGroup().getEvalTemplates();
//		evalFormRepo.findById(reviewee.getGroup().getEvalTemplates().getId()  .getName()).orElse(null);
		System.out.println(id);
		
		Evaluation evalform;

		SelfEvaluation selfEvaluation = selfEvaluationRepository.findByReviewee(reviewee);

		if(selfEvaluation == null) {

			//Deserialize
			selfEvaluation = new SelfEvaluation(reviewee);
			evalform = (Evaluation) SerializationUtils.deserialize(evalTemp.getEval());

		} else {

			// Deserialize
			evalform = (Evaluation) SerializationUtils.deserialize(selfEvaluation.getPath());
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

		Date date = new Date();
		System.out.println(date);
		selfEvaluation.setDateEdited(date);
		selfEvaluation.setPath(data);

		if (completed == false) {
			selfEvaluation.setCompleted(false);
			selfEvaluationRepository.save(selfEvaluation);
			log.warn("Saved Incomplete Self Evaluation (ID:" + selfEvaluation.getId() + ")");
			
			return "redirect:/usergroups/" + user.getId();
		}

		// If evaluation is complete
		if (evalform.getCompleted()) {

			selfEvaluation.setCompleted(true);
			
			selfEvaluationRepository.save(selfEvaluation);
			log.info("Saved Self Evaluation (ID:" + selfEvaluation.getId() + ") with Form (ID:" + evalform.getEvalID() + ")");

			return "redirect:/usergroups/" + user.getId();

			// If evaluation is not complete
		} else {

			selfEvaluation.setCompleted(false);
			selfEvaluationRepository.save(selfEvaluation);
			
			redir.addFlashAttribute("error", incompQuests.size() + " required question(s) are blank and must be answered.");
			redir.addFlashAttribute("incompQuests", incompQuests);
			log.warn("Saved Incomplete Self Evaluation (ID:" + selfEvaluation.getId() + ") with Form (ID:" + evalform.getEvalID() + ")");

			return "redirect:/selfeval/" + id;
		}
	}
}
