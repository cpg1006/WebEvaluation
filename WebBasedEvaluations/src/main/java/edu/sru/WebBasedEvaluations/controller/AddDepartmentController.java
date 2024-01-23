package edu.sru.WebBasedEvaluations.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
 * @author Michael Mirabito
 *
 */
@Controller
public class AddDepartmentController {
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

	public AddDepartmentController(UserRepository userRepository,RoleRepository roleRepository,CompanyRepository companyRepo,LocationRepository locationRepo, DepartmentRepository deptRepo) {
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

	/**
	 * Method for manually adding departments from the adminCompanyUpdate page
	 * 
	 * @param dept     is a department object used that holds the information submitted
	 *                 from the "Add Department" button
	 * @param model    is a Model object used for adding attributes to a webpage,
	 *                 mostly used for adding messages and lists to the page.
	 * @param auth	   The authentication of the currently logged in user.
	 * 
	 * @param id       The id of the current company
	 * 
	 * @param locid	   The id of the current location
	 * 
	 * @return adminCompanyUpdate html webpage.
	 */
	@RequestMapping(value = "/addDept/{id}/{locid}/", method = RequestMethod.POST)
	public String addDepartment(@PathVariable("id") long id, @PathVariable("locid") long locID, @Validated Department dept, BindingResult result, Model model, Authentication auth, HttpSession session) {
		Company currentCompany = companyRepo.findById(id);
		User currentUser;
		Location loc = locationRepo.findById(locID);
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		
		
		Long idNum = userD.getID();

		currentUser = userRepository.findById(idNum).orElse(null);
		
		List<Company> companies	= new ArrayList<Company>();
		if(currentUser.isSuperUser()) {
			companies = companyRepo.findAll();
		}
		else {
			companies.add(currentUser.getCompany());
		}
		dept.addLocation(loc);
		dept.setCompany(currentCompany);
		if(!dept.getName().isBlank()) {
			deptRepo.save(dept);
		} else {
			log.info("Invalid Name. Not saving");
		}
		
		
		log.info("Adding deptartment '" + dept.getName() + "' (ID:" + dept.getId() + ") to company " + currentCompany.getCompanyName() + " (ID:" 
				+ currentCompany.getId() + ") at location " + loc.getLocationName() + " (ID:" + loc.getId() + ")");
//		log.info(currentCompany.getCompanyName());
//		log.info(loc.getLocationName());
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		model.addAttribute("companies", companies);

		model.addAttribute("company", currentCompany);
		model.addAttribute("id", id);
		model.addAttribute("SUPERUSER", currentUser.isSuperUser());
		model.addAttribute("location", loc);
		return "/adminCompanyUpdate";

	}

}
