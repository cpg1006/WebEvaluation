package edu.sru.WebBasedEvaluations.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;

import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.WebBasedEvaluations.evalform.GenerateEvalReport;
import edu.sru.WebBasedEvaluations.evalform.GenerateEvalReportPoi;
import edu.sru.WebBasedEvaluations.evalform.ParseEvaluation;
import edu.sru.WebBasedEvaluations.excel.ExcelRead_group;
import edu.sru.WebBasedEvaluations.repository.DepartmentRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;

/**
 * Controller for functionality of the 'evalTemplates.html' web page for 'adminEval' users.
 * Includes uploading, saving, deleting, and downloading excel files and Evaluation forms.
 * 
 * @author Logan Racer
 */
@Controller
public class EvalFormController {

	private EvaluationRepository evalFormRepo;
	private EvaluationLogRepository evalLogRepo;
	private GroupRepository groupRepo;
	private UserRepository userRepo;
	private DepartmentRepository deptRepo;

	@Autowired
	private EvaluatorRepository evaluatorRepo;

	private Evaluation eval;
	private XSSFWorkbook apacheWorkbook;
	private Set<Department> depts = new HashSet<Department>();

	
	private final String WINDOWS_FILES_PATH = "src\\main\\resources\\temp\\"; //windows uses '\'
	private final String MAC_FILES_PATH = "src//main//resources//temp//"; //macOS and Linux uses '/'
	
	private String workingFilesPath;
	
	private Logger log = LoggerFactory.getLogger(EvalFormController.class);

	// Constructor
	EvalFormController(EvaluationRepository evalFormRepo, EvaluationLogRepository evalLogRepo, GroupRepository groupRepo,UserRepository userRepo, DepartmentRepository deptRepo) {
		this.evalFormRepo = evalFormRepo;
		this.evalLogRepo = evalLogRepo;
		this.groupRepo = groupRepo;
		this.userRepo = userRepo;
		this.deptRepo = deptRepo;

		this.eval= null;
		this.apacheWorkbook = null;
		
		
		//sets appropriate file path depending on operating system
		if(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
			this.workingFilesPath = MAC_FILES_PATH;
		}
		else {
			this.workingFilesPath = WINDOWS_FILES_PATH; 
		}
	}



	/**
	 * Controller method for @{/adminEvaluations}. Loads the "Evaluation Templates" page
	 * for the 'EVAL_ADMIN' users.
	 * 
	 * @param model
	 * @return evalTemplates.html
	 */
	@GetMapping("/adminEvaluations")
	public String adminEvaluations(Model model, Authentication auth) {
		
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);
		String userRole= currentUser.getRole().getName();
		model.addAttribute("UserRole",userRole);

		currentCompany = currentUser.getCompany();
		
		
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
		
		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(workingFilesPath));
			FileUtils.cleanDirectory(new File(workingFilesPath));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + workingFilesPath + "' could not be created or cleaned.");
		}

		String hasEvals = "no";
		if (evalFormRepo.count() > 0) {
			hasEvals = "yes";

			List <EvalTemplates> evalTempList = (List<EvalTemplates>) evalFormRepo.findByCompany(currentCompany);
			Set<Department> depts = new HashSet<Department>();
			
			if(!currentUser.isCompanySuperUser() || !currentUser.isAdminEval()) {
				
				depts.addAll(currentUser.getRole().readableDepartments());
				depts.addAll(currentUser.getDepartments());
			}
			List <Evaluation> evalList = new ArrayList<Evaluation>();

			for (int i = 0; i < evalTempList.size(); i++) {

				// Deserialize
				byte[] data = evalTempList.get(i).getEval();
				Evaluation eval;
				eval = (Evaluation) SerializationUtils.deserialize(data);
				eval.clearGroupsList();

				// Checking which groups the evaluation is assigned
				List<Group> groupList = (List<Group>) groupRepo.findAll();

				for (int j = 0; j < groupList.size(); j++) {
					byte[] groupData = groupList.get(j).getEvalTemplates().getEval();

					if(data == groupData) {
						eval.addGroup("Group #" + groupList.get(j).getId());
					}
				}

				//adds the dept names to the list of depts. 
				for(Department dept :evalTempList.get(i).getDepts()) {
					eval.addDeptName(dept.getName());
				}
				evalList.add(eval);
				model.addAttribute("evalTemplates", evalTempList);
			}

			model.addAttribute("evalList", evalList);
		}
		model.addAttribute("showUserScreen", showUserScreen);
		model.addAttribute("rolesNeedAdded", rolesNeedAdded);

		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model.addAttribute("hasEvals", hasEvals);


		return "evalTemplates";
	}





	/**
	 * Controller method for @{/upload_eval}. Process the uploaded file.
	 * Redirects to the "Evaluation Templates" page for the 'EVAL_ADMIN' users
	 * updated with the evaluation preview or error messages.
	 * 
	 * @param file - Excel file uploaded
	 * @param redir - Redirect attributes
	 * @return RedirectView to @{/adminEvaluations}
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload_eval", method = RequestMethod.POST)
	public Object uploadEvalTemplate(@RequestParam("file") MultipartFile file, RedirectAttributes redir, Authentication auth) throws Exception {

		boolean showLog = false;
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		XSSFSheet sheet2;

		//deptartmentList
		try {
			sheet2 = ExcelRead_group.loadFile(file).getSheetAt(1);
		}
		catch(Exception e) {
			RedirectView redirectView = new RedirectView("/adminEvaluations", true);
			redir.addFlashAttribute("error", "invalid file");
			return redirectView;
		}
		//all departments available for this users company checked before attempting to add
		//used to compare against departments being added from Excel sheet
		Iterable<Department> companyDeptsAvalList = this.deptRepo.findAll();
		String companyDeptsAval = companyDeptsAvalList.toString();
		
		depts = new HashSet<Department>();
		for (int i = 1; sheet2.getRow(i) != null; i++) {
			String deptName = ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(0));
			Department dept = this.deptRepo.findByNameAndCompany(deptName, currentCompany);
			
			//current department to be check for users company
			Object newDept = dept;
			
			//check to see if dept name is found in current users current company
			try {
					boolean isFound = false;
					String newDepartmentCompare = newDept.toString();
				
					if(companyDeptsAval.contains(newDepartmentCompare)) {
						isFound = true; 
					}}catch (Exception e) {
						RedirectView redirectView = new RedirectView("/adminEvaluations", true);
						redir.addFlashAttribute("error", "the department " + deptName + " on the form is not present in the company");
						log.error("User's company doesn't have one or more of the requested departments");
						return redirectView;
				}
			
			if(dept != null) {
				if(currentUser.getRole().writableDepartments().contains(dept) || currentUser.getRole().getName().equalsIgnoreCase("EVALUATOR_ADMIN")) {
					depts.add(dept);
					
				}	
				else {
					redir.addFlashAttribute("PermissionError", "User does not have permission to create/add departments");

					log.error("User does not have permission to create/add departments");
				}
			}
			
			else {
				Department dept2 = new Department(currentCompany);
				dept2.setName(deptName);
				depts.add(dept2);
				this.deptRepo.save(dept2);
				
			}
			
		
			
		}
		
		


		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			redir.addFlashAttribute("error", "Wrong file type or no file selected");
		} else {
			
			// Temp XML file name
			final String XML_FILE_NAME = "eval.xml";

			// Load stream into new workbook
			InputStream stream = file.getInputStream();
			Workbook wb = new Workbook(stream);

			// Remove all but worksheet 0
			while(wb.getWorksheets().getCount() > 1) {
				wb.getWorksheets().removeAt(1);
			}

			// Save .xlsx as .xml file
			wb.save(workingFilesPath + XML_FILE_NAME, SaveFormat.AUTO);

			// Save excel file as apache workbook
			XSSFWorkbook workbook = new XSSFWorkbook(new BufferedInputStream(file.getInputStream()));	
			this.apacheWorkbook = workbook;

			// Parse data from XML file into Evaluation object
			this.eval = new Evaluation(currentCompany.getCompanyName());
			
			
			
//			this.eval.addAllDepts(depts);
			this.eval = ParseEvaluation.parseEvaluation(this.eval, workingFilesPath + XML_FILE_NAME);
			
			//adds the dept names to the list of depts. 
			for(Department dept : depts) {
				eval.addDeptName(dept.getName());
			}

			// Check file ID and check for duplicates
			String id = this.eval.getEvalID();
			String companyName = this.eval.getCompanyName();
			boolean duplicate = false;

			if (evalFormRepo.count() > 0) {

				List <EvalTemplates> evalTempList = (List<EvalTemplates>) evalFormRepo.findAll();
				for (int i = 0; i < evalTempList.size(); i++) {

					// Deserialize
					byte[] data = evalTempList.get(i).getEval();
					Evaluation eval;
					eval = (Evaluation) SerializationUtils.deserialize(data);
					if (eval.getEvalID().equals(id) && eval.getCompanyName().equals(companyName)) {
						duplicate = true;
					}
				}
			}

			// Check for duplicates
			if (duplicate) {
				this.eval.addError("Evaluation template with keyword '<code>ID</code>': &nbsp'<u>" + id + "</u>'&nbsp already exists.");
			}

			//Process tool tips
			this.eval.processToolTips();

			//Check for errors
			this.eval.checkErrors();

			if (this.eval.getWarningCount() > 0) {
				redir.addFlashAttribute("warningList", this.eval.getWarnings());

				log.warn("Evaluation file uploaded contains the following warnings:");
				for (int i = 0; i < this.eval.getWarningCount(); i++) {
					String warn = this.eval.getWarning(i);
					warn = warn.replaceAll("<code>", "");
					warn = warn.replaceAll("</code>", "");
					warn = warn.replaceAll("<u>", "");
					warn = warn.replaceAll("</u>", "");
					warn = warn.replaceAll("&nbsp", "");
					log.warn("\t" + warn);
				}
				showLog = true;
			}
			if (this.eval.getErrorCount() > 0) {
				redir.addFlashAttribute("errorList", this.eval.getErrors());

				log.error("Evaluation file uploaded failed with the following errors:");
				for (int i = 0; i < this.eval.getErrorCount(); i++) {
					String error = this.eval.getError(i);
					error = error.replaceAll("<code>", "");
					error = error.replaceAll("</code>", "");
					error = error.replaceAll("<u>", "");
					error = error.replaceAll("</u>", "");
					error = error.replaceAll("&nbsp", "");
					log.error("\t" + error);
				}
				showLog = true;
			} else {

				
				redir.addFlashAttribute("eval", this.eval);
				redir.addFlashAttribute("completed", "Preview the template below and click 'Save Evaluation Template' if satisfied.");

				log.info("Evaluation template '" + this.eval.getEvalID() + "' uploaded successfully.");
			}
		}
		if (showLog) {
			if (this.eval != null) {
				if (!this.eval.getEvalID().isBlank()) {
					redir.addFlashAttribute("showLog", this.eval.getEvalID());
				} else {
					redir.addFlashAttribute("showLog", "NoID");
				}
			}

		}

		RedirectView redirectView = new RedirectView("/adminEvaluations", true);

		return redirectView;
	}



	/**
	 * Saves the uploaded evaluation in the database. Redirects back to @{/adminEvaluations} with the normal view.
	 * 
	 * @param eval - Evaluation object
	 * @param model
	 * @return RedirectView to @{/adminEvaluations}
	 * @throws Exception
	 */
	@RequestMapping("/eval_form")
	public RedirectView saveEvalTemplate(@Validated Evaluation eval, Model model, Authentication auth) throws Exception {

		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		
		Set<Department> tempDepts = new HashSet<Department>();
		tempDepts.addAll(depts);
		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(workingFilesPath));
			FileUtils.cleanDirectory(new File(workingFilesPath));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + workingFilesPath + "' could not be created or cleaned.");
		}

		// Temp file name
		final String FILE_NAME = "eval_temp.xlsx";

		// Serialize eval
		byte[] evalByte;
		evalByte = SerializationUtils.serialize(this.eval);

		try (OutputStream out = new FileOutputStream(workingFilesPath + FILE_NAME)) {
			this.apacheWorkbook.write(out);
		} catch (IOException e){
			e.printStackTrace();
			log.error("File '" + workingFilesPath + FILE_NAME + "' could not be created.");
		}

		// Serialize apache workbook excel file
		byte[] excelByte = Files.readAllBytes(Paths.get(workingFilesPath + FILE_NAME));

		try {
			FileUtils.cleanDirectory(new File(workingFilesPath));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + workingFilesPath + "' could not be cleaned.");
		}

		// Save to database
		EvalTemplates evalTemp = new EvalTemplates(this.eval.getEvalID(), evalByte, excelByte, currentCompany);
		evalTemp.addDepts(depts);
		evalFormRepo.save(evalTemp);
		log.info("Evaluation template '" + this.eval.getEvalID() + "' saved.");

		// Redirect
		RedirectView redirectView = new RedirectView("/adminEvaluations", true);
		return redirectView;
	}



	/**
	 * Processes the request for the download of the Evaluation Results excel file for a given evaluation ID.
	 * 
	 * @param evalId - ID of the Evaluation
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/download_eval_results/{evalId}")
	public ResponseEntity<Resource> downloadEvalResults(@PathVariable("evalId") String evalId) throws Exception {

		// Name of download file
		final String FILE_NAME = "Evaluation Summary - " + evalId + ".xlsx";

		log.info("File '" + FILE_NAME + "' requested for download.");

		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(workingFilesPath));
			FileUtils.cleanDirectory(new File(workingFilesPath));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + workingFilesPath + "' could not be created or cleaned.");
		}

		//Get Evaluation template
		List<EvalTemplates> evalTemps = (List<EvalTemplates>) evalFormRepo.findAll();
		byte[] evalTempByte = null;

		for (int i = 0; i<evalTemps.size();i++) {
			String name = evalTemps.get(i).getName();
			//System.out.println("name: " + name + ", evalId: " + evalId);

			if (name.equals(evalId)) {
				//System.out.println("Eval template found");
				evalTempByte = evalTemps.get(i).getEval();
			}
		}

		Evaluation evalTemp = (Evaluation) SerializationUtils.deserialize(evalTempByte);

		//Get Completed evaluations
		List<EvaluationLog> evalLogs = (List<EvaluationLog>) evalLogRepo.findAll();
		List<Evaluation> completedEvals = new ArrayList<Evaluation>();

		byte[] evalLogByte = null;

		for (int i = 0; i < evalLogs.size();i++) {
			if (evalLogs.get(i).getCompleted()) {
				evalLogByte = evalLogs.get(i).getPath();
				Evaluation completeEval = (Evaluation) SerializationUtils.deserialize(evalLogByte);

				if(completeEval.getEvalID().equals(evalId)) {
					completedEvals.add(completeEval);
				}
			}
		}

		log.info("Found " + completedEvals.size() + " completed evals for Evaluation form ID: '" + evalId + "'.");

		// Create the excel report file

		// Uncomment to generate using Aspose.Cells
		//GenerateEvalReport.generateReport(evalTemp, completedEvals, workingFilesPath, FILE_NAME);

		// Generate excel using Apache POI
		GenerateEvalReportPoi.generateReport(evalTemp, completedEvals, workingFilesPath, FILE_NAME);

		//Download the file
		FileSystemResource resource = new FileSystemResource(workingFilesPath + FILE_NAME);
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



	/**
	 * Processes the request for the download of the original Evaluation template excel file for a given evaluation ID.
	 * 
	 * @param evalId - ID of the Evaluation
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/download_eval_excel/{evalId}")
	public ResponseEntity<Resource> downloadEvalExcel(@PathVariable("evalId") String evalId) throws Exception {

		// Name of download file
		final String FILE_NAME = "Evaluation File - " + evalId + ".xlsx";

		log.info("File '" + FILE_NAME + "' requested for download.");

		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(workingFilesPath));
			FileUtils.cleanDirectory(new File(workingFilesPath));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + workingFilesPath + "' could not be created or cleaned.");
		}

		//Get Evaluation template
		List<EvalTemplates> evalTemps = (List<EvalTemplates>) evalFormRepo.findAll();
		byte[] excelByte = null;

		for (int i = 0; i<evalTemps.size();i++) {
			String name = evalTemps.get(i).getName();

			if (name.equals(evalId)) {
				excelByte = evalTemps.get(i).getExcelFile();
			}
		}

		Path path = Paths.get(workingFilesPath + FILE_NAME);
		Files.write(path, excelByte);

		//Download the file
		FileSystemResource resource = new FileSystemResource(workingFilesPath + FILE_NAME);
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



	/**
	 * Processes the request for the download of the uploaded Evaluation template
	 * excel file with Errors and Warnings appended to a new sheet.
	 * 
	 * @param evalId - ID of the Evaluation
	 * @return ResponseEntity containing the download resource
	 * @throws Exception
	 */
	@GetMapping("/dl_error_log/{evalId}")
	public ResponseEntity<Resource> downloadErrorLog(@PathVariable("evalId") String evalId) throws Exception {

		// Name of download file
		final String FILE_NAME = "Uploaded Evaluation - " + evalId + ".xlsx";

		log.info("File '" + FILE_NAME + "' requested for download.");

		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(workingFilesPath));
			FileUtils.cleanDirectory(new File(workingFilesPath));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + workingFilesPath + "' could not be created or cleaned.");
		}

		XSSFWorkbook errorLogFile;
		errorLogFile = this.apacheWorkbook;
		errorLogFile.createSheet("Error & Warning Log");
		XSSFSheet warnSheet = errorLogFile.getSheet("Error & Warning Log");
		int row = 0;
		for (int i = 0; i < this.eval.getErrorCount(); i++) {
			String error = this.eval.getError(i);
			error = "ERROR: " + error;
			error = error.replaceAll("<code>", "");
			error = error.replaceAll("</code>", "");
			error = error.replaceAll("<u>", "");
			error = error.replaceAll("</u>", "");
			error = error.replaceAll("&nbsp", "");

			XSSFRow curRow = warnSheet.createRow(i);
			curRow.createCell(0).setCellValue(error);
			row++;
		}

		for (int i = 0; i < this.eval.getWarningCount(); i++) {
			String warn = this.eval.getError(i);
			warn = "WARNING: " + warn;
			warn = warn.replaceAll("<code>", "");
			warn = warn.replaceAll("</code>", "");
			warn = warn.replaceAll("<u>", "");
			warn = warn.replaceAll("</u>", "");
			warn = warn.replaceAll("&nbsp", "");

			XSSFRow curRow = warnSheet.createRow(row);
			curRow.createCell(0).setCellValue(warn);
			row++;
		}

		try (OutputStream out = new FileOutputStream(workingFilesPath + FILE_NAME)) {
			errorLogFile.write(out);
		} catch (IOException e){
			e.printStackTrace();
			log.error("File '" + workingFilesPath + FILE_NAME + "' could not be created.");
		}

		//Download the file
		FileSystemResource resource = new FileSystemResource(workingFilesPath + FILE_NAME);
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



	/**
	 * Processes the request to deactivate an evaluation template from a given evaluation ID.
	 * 
	 * @param evalId - ID of the Evaluation
	 * @param auth - authentication
	 * @return RedirectView to @{/adminEvaluations}
	 */
	@GetMapping("/deactivateEvalForm/{evalId}")
	public Object deactivateEvalTemplate(@PathVariable("evalId") String evalId, Authentication auth) {

		User currentUser;
		Company currentCompany;
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);
		currentCompany = currentUser.getCompany();
		
		List<EvalTemplates> evalTemplates = evalFormRepo.findByCompany(currentCompany);
		
		for(EvalTemplates template: evalTemplates) {
			byte[] data = template.getEval();
			Evaluation eval = (Evaluation) SerializationUtils.deserialize(data);

			if(eval.getEvalID().equals(evalId)) {
				eval.setActivated(false);
				byte[] updatedData = SerializationUtils.serialize(eval);
				template.setEval(updatedData);
				template.setActivated(false);
			
				evalFormRepo.save(template);

				break;
			}
		}
		
		RedirectView redirectView = new RedirectView("/adminEvaluations", true);

		return redirectView;
	}
	
	/**
	 * Reactivates an evaluation form to be used for evaluations
	 * @param evalId - ID of the evaluation
	 * @param auth - authentication
	 * @return RedirectView to @{/adminEvaluations}
	 */
	@GetMapping("/reactivateEvalTemplate/{evalId}")
	public Object reactivateEvalTemplate(@PathVariable("evalId") String evalId, Authentication auth) {

		User currentUser;
		Company currentCompany;
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);
		currentCompany = currentUser.getCompany();
		
		List<EvalTemplates> evalTemplates = evalFormRepo.findByCompany(currentCompany);
		
		for(EvalTemplates template: evalTemplates) {
			byte[] data = template.getEval();
			Evaluation eval = (Evaluation) SerializationUtils.deserialize(data);

			if(eval.getEvalID().equals(evalId)) {
				eval.setActivated(true);
				byte[] updatedData = SerializationUtils.serialize(eval);
				template.setEval(updatedData);
				template.setActivated(true);
			
				evalFormRepo.save(template);	
				break;
			}
		}
		
		RedirectView redirectView = new RedirectView("/adminEvaluations", true);

		return redirectView;
	}
}