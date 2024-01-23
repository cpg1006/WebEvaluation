package edu.sru.WebBasedEvaluations.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import edu.sru.WebBasedEvaluations.company.City;
import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Continent;
import edu.sru.WebBasedEvaluations.company.Country;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;
import edu.sru.WebBasedEvaluations.company.Province;
import edu.sru.WebBasedEvaluations.company.World;
import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.excel.ExcelRead_group;
import edu.sru.WebBasedEvaluations.repository.CityRepository;
import edu.sru.WebBasedEvaluations.repository.CompanyRepository;
import edu.sru.WebBasedEvaluations.repository.ContinentRepository;
import edu.sru.WebBasedEvaluations.repository.CountryRepository;
import edu.sru.WebBasedEvaluations.repository.DepartmentRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.LocationGroupRepository;
import edu.sru.WebBasedEvaluations.repository.LocationRepository;
import edu.sru.WebBasedEvaluations.repository.ProvinceRepository;
import edu.sru.WebBasedEvaluations.repository.RoleRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.repository.WorldRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;


@Controller
public class CompanyController {



	@Autowired
	private UserRepository userRepo;
	@Autowired
	private EvaluatorRepository evaluatorRepo;
	@Autowired
	private CompanyRepository companyRepo;
	@Autowired
	private LocationRepository locationRepo;
	@Autowired
	private LocationGroupRepository locGroupRepo;
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private WorldRepository worldRepo;
	@Autowired
	private ContinentRepository continentRepo;
	@Autowired
	private CountryRepository countryRepo;
	@Autowired
	private ProvinceRepository provinceRepo;
	@Autowired
	private CityRepository cityRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private EvaluationRepository evalFormRepo;
	@Autowired
	private AdminMethodsService adminMethodsService;


	private final String TEMP_FILES_PATH = "src\\main\\resources\\temp\\";
	private Logger log = LoggerFactory.getLogger(CompanyController.class);




	public CompanyController() {

	}


	@GetMapping("/adminCompanies")
	public String addRoles(Model model, Authentication auth) {


		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);
		String UserRole=currentUser.getRole().getName();
		model.addAttribute("UserRole",UserRole);

		// Create the directories if they do not exist, delete any existing files
		try {
			Files.createDirectories(Paths.get(TEMP_FILES_PATH));
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + TEMP_FILES_PATH + "' could not be created or cleaned.");
		}


		List<Company> companies = companyRepo.findAll();
		
		boolean showUserScreen = true;
		
		Set<Department> companyDepartments = currentUser.getCompany().getDepartments();
		List<Department> departmentsList = new ArrayList<Department>();
		final int MIN_DEPARTMENT_SIZE = 2;
		
		departmentsList.addAll(companyDepartments);
		
		Department firstDepartment = departmentsList.get(0);

		//will check to see if companies have been added before allowing users to be added
		if(departmentsList.size() < MIN_DEPARTMENT_SIZE && firstDepartment.getName().equals("none")) {
			showUserScreen = false;
		}
		
		//Creating new instances of a company and locationgroup.
		//Company is currently used to add a company.
		//Working out how to add attributes that are not necessarily just connected to the company object
		model.addAttribute("company", new Company());
		model.addAttribute("locationGrp", new LocationGroup());

		model.addAttribute("companies", companies);
		model.addAttribute("showUserScreen", showUserScreen);
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo,evalFormRepo);
		
		//hides "Manage Roles" from navbar
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
		}
		model.addAttribute("UserRole",currentUser.getRole().getName());
		return "/adminCompanies";
	}
	
	/*The addcompany sections that I added to this file are still under development. I got the html
	  to work but have not added the functionality to add the value to the database*/

	@RequestMapping(value = "/add_company", method = RequestMethod.POST)
	public String addCompany(@Validated @ModelAttribute("company") Company company, BindingResult result, Model model, Authentication auth) {
		User currentUser;
		Company currentCompany;
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = this.userRepo.findById(idnum).orElse(null);
		currentCompany = currentUser.getCompany();
		
		model.addAttribute("company", company);
		String UserRole=currentUser.getRole().getName();
		model.addAttribute("UserRole",UserRole);
		
		String mess ="";
		String ansr = "";
		boolean success = false;
		
		if (result.hasErrors()) {
			return "adminCompanies";
		}
		
		//hides "Manage Roles" from navbar
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
		}
		
		return "adminCompanies";
	}

	
	@RequestMapping(value = "/upload_company", method = RequestMethod.POST)
	public Object uploadCompany(@RequestParam("file") MultipartFile file, RedirectAttributes redir, Authentication auth,Model model) throws Exception {

		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);
		String UserRole=currentUser.getRole().getName();
		model.addAttribute("UserRole",UserRole);
		Company currentCompany = currentUser.getCompany();


		XSSFSheet sheet1 = null;
		XSSFSheet sheet2 = null;
		String mess ="";
		String ansr = "";
		boolean success = false;


		

		if(!UserRole.equalsIgnoreCase("SUPERUSER")) {
			RedirectView redirectView = new RedirectView("/adminCompanies", true);
			redir.addFlashAttribute("error", "User must be a Super User to add a company");
			return redirectView;
		}

		try {
			sheet1 = ExcelRead_group.loadFile(file).getSheetAt(0);
			sheet2 = ExcelRead_group.loadFile(file).getSheetAt(1);

		} catch (Exception e) {				
			RedirectView redirectView = new RedirectView("/adminCompanies", true);
			redir.addFlashAttribute("error", "Invalid file");
			return redirectView;		
		}


		try{
			String filetype = ExcelRead_group.checkStringType(sheet2.getRow(0).getCell(5));	
			if(!filetype.equalsIgnoreCase("Company Template")) {
				throw new Exception("Wrong file type, please upload the upload_company file.");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			success = false;	
			log.error(e.getMessage());
			RedirectView redirectView = new RedirectView("/adminCompanies", true);
			redir.addFlashAttribute("error", e.getMessage());
			return redirectView;
		}
		Company company = null;
		Location location = null;
		LocationGroup locationGroup = null;
		Department dept = null;

		World world = null;
		Continent continent = null;
		Country country = null;
		Province province = null;
		City city = null;

		String companyName = "";


		String locationName = "";
		String locationGroupName = "";
		String deptName = "";

		String worldName = "";
		String continentName = "";
		String countryName = "";
		String provinceName = "";
		String cityName = "";
		String hierarchy = "";
		String parentName = "";

		HashSet<Company> companies = new HashSet<>();
		//add the locations/company structure
		for (int i = 1; sheet1.getRow(i) != null; i++) {

			try {


				companyName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(0));
				locationGroupName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(1));
				locationName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(2));				
				cityName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(3));					
				provinceName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(4));		
				countryName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(5));		
				continentName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(6));		
				worldName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(7));		
				hierarchy = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(8));
				parentName = ExcelRead_group.checkStringType(sheet1.getRow(i).getCell(9));

				company = companyRepo.findByCompanyName(companyName);				
				
				//prevents company with no name from being added
				if(companyName.equals("")) {
					log.warn("A company with no name was prevented from being added.");
					break;
				}
				
				//check if company exists, if not create it. 
				if(company == null) {
					company = new Company(companyName);
					log.info("created company: " + companyName);
					company.setHierarchy(hierarchy);
					if(parentName != null && hierarchy.equals("Child")) {
						Company myParent = companyRepo.findByCompanyName(parentName);
						company.setParent(myParent);
						myParent.addChildCompany(company);
						companyRepo.save(myParent);
					}
//					Role adminRole = new Role("COMPANYSUPERUSER",company);
//					User use1 = new User("jimmy nutron","fuckname","lname","admin3@gmail.com","$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ", 999991, "N/A", "N/A", null, "N/A","admin dept", company, adminRole,false,true);

					companyRepo.save(company);
					
				}
				

				world = worldRepo.findByName(worldName);
				if(world == null) {
					world = new World(worldName);
					log.info("created world: " + worldName);

				}
				continent = continentRepo.findByContinentName(continentName);
				if(continent == null) {
					continent = new Continent(continentName,world);
					log.info("created continent: " + continentName);
				}

				country = countryRepo.findByCountryName(countryName);
				if(country == null) {
					country = new Country(countryName,continent);
					log.info("created country: " + countryName);
				}

				province = provinceRepo.findByProvinceName(provinceName);
				if(province == null) {
					province = new Province(provinceName, country);
					log.info("created province: " + provinceName);

				}				

				city = cityRepo.findByCityName(cityName);
				if(city == null) {
					city = new City(cityName, province);
					log.info("created city: " + cityName);
				}

				location = locationRepo.findByLocationNameAndCompany(locationName, company);
				if(location == null) {
					location = new Location();
					location.setLocationName(locationName);
					location.setCompany(company);
					location.setParentCity(city);
					company.addLocation(location);
					log.info("created location: " + locationName);
				}

				locationGroup = locGroupRepo.findByCompanyAndName(company, locationGroupName);	
				if(locationGroup ==null) {
					locationGroup = new LocationGroup();
					locationGroup.setName(locationGroupName);
					locationGroup.setCompany(company);
					location.setLocGroup(locationGroup);
					log.info("added location: " + locationName + " to new location group " +locationGroupName);
				}
				else if(!locationGroup.getLocations().contains(location)){
					location.setLocGroup(locationGroup);
					log.info("added location: " + locationName + " to location group " +locationGroupName);
				}

				try {
					
					locGroupRepo.save(locationGroup);
					locationRepo.save(location);
					log.info("added/updated location " + location.getLocationName() + " to company " + companyName);
					success = true;
				}
				catch(Exception e) {
					e.printStackTrace();
					success = false;	
					log.error("Could not add company/location in row: " + (sheet1.getRow(i).getRowNum() + 1) + " from "
							+ file.getOriginalFilename() + "\n" + e.getStackTrace().toString());
					RedirectView redirectView = new RedirectView("/adminCompanies", true);
					redir.addFlashAttribute("error", "error saving company: " + companyName);
					return redirectView;
				}
				

			}
			catch(Exception e) {
				log.error("Could not add company/location in row: " + (sheet1.getRow(i).getRowNum() + 1) + " from "
						+ file.getOriginalFilename() + "\n" + e.getMessage());
				redir.addFlashAttribute("log", "error");
			}


		}




		
		//add the depts and add them to locs int he process. 

		for (int i = 1; sheet2.getRow(i) != null; i++) {

			try {
				companyName = ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(0));
				deptName = ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(1));
				locationName = ExcelRead_group.checkStringType(sheet2.getRow(i).getCell(2));
				
				
				
				company = companyRepo.findByCompanyName(companyName);				
				//check if company exists, if not create it. 
				if(company == null) {
					throw new Exception("please create company " + companyName + " before adding a dept to it.");
				}					
				
				location = locationRepo.findByLocationNameAndCompany(locationName, company);
				if(location == null) {
					throw new Exception("please create location " + locationName + " before adding it to a dept.");
				}
				
				dept = deptRepo.findByNameAndCompany(deptName, company);
				if(dept == null) {
					dept = new Department();
					dept.setName(deptName);
					dept.setCompany(company);
					dept.addLocation(location);
					log.info("added dept " + deptName + " in company " + companyName);
					log.info("added location " + locationName + "to dept " + deptName + " in company " + companyName);
				}
				
				
				if(!dept.getLocations().contains(location)) {
					dept.addLocation(location);
				}
				
				if(!company.getDepartments().contains(dept)) {
					company.addDepartment(dept);
				}
				
				//Create default admin if one does not exist
				Department adminDept = new Department();
				adminDept.setName("admin dept");
				adminDept.setCompany(company);
				adminDept.addLocation(location);
				Set<Department> depts = company.getDepartments();
				Boolean deptHasAdminDept = false;
				for(Department checkDept : depts) {
					if(checkDept.getName().equals("admin dept")) {
						deptHasAdminDept = true;
						break;
					}
				}
				
				if(deptHasAdminDept) {
					company.addDepartment(adminDept);
				}
				
				
				if(location.getDepartments() != null) {
					List<Department> depts2 = location.getDepartments();
					Boolean locHasAdminDept = false;
					for(Department checkDept : depts2) {
						if(!checkDept.getName().equals("admin dept")) {
							locHasAdminDept = true;
							break;
						}
					}
					
					if(locHasAdminDept) {
						location.addDept(adminDept);
					}
				}
				
//				Role adminRole = roleRepo.findByNameAndCompany("COMPANYSUPERUSER", company);
//				if(adminRole == null) {
//					adminRole = new Role("COMPANYSUPERUSER",company);
//					company.addRole(adminRole);
//				}
//				
//				User adminUser = getAdminUser(company);
//				if(adminUser == null) {
//					adminUser = new User(company.getCompanyName() + " admin","fname","lname","admin" + company.getId().toString() + "@gmail.com","$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ", 999991, "N/A", "N/A", null, "N/A","admin dept", company, adminRole,true,false);
//					adminUser.setEncryptedPassword("test");
//					adminUser.setReset(false);
//					adminUser.addDepartment(adminDept);
//					Privilege priv = new Privilege("ADMIN2", adminRole, location.getLocGroup(), adminDept, company, true,true,true,false);
//					adminRole.addPrivilege(priv);
//					priv.addRole(adminRole);
//					company.addUser(adminUser);
//					adminUser.addLocation(location);
//				}
//				
				
				try {
					deptRepo.save(dept);
					locationRepo.save(location);
					log.info("added location " + location.getLocationName() + " to dept " + dept.getName() + " in company " + companyName);
					success = true;
				}
				catch(Exception e) {
					e.printStackTrace();
					success = false;	
					log.error("Could not add dept in row: " + (sheet1.getRow(i).getRowNum() + 1) + " to company " + companyName + " in file "
							+ file.getOriginalFilename() + "\n" + e.getStackTrace().toString());
					RedirectView redirectView = new RedirectView("/adminCompanies", true);
					redir.addFlashAttribute("error", "error saving company: " + companyName);
					return redirectView;
				}				


			}
			catch(Exception e) {
				success = false;
				log.error("Could not add dept/company relationship in row: " + (sheet1.getRow(i).getRowNum() + 1) + " from "
						+ file.getOriginalFilename() + " sheet 2\n" + e.getMessage());
				redir.addFlashAttribute("log", "error");
				mess = "File failed to be uploaded!";
				ansr = "addFail";
				log.error("error saving Company from file " + file.getOriginalFilename());
				redir.addFlashAttribute("mess", mess);
				redir.addFlashAttribute("ansr", ansr);
				RedirectView redirectView = new RedirectView("/adminCompanies", true);

				return redirectView;
			}

		}
		if(success) {
			mess = "File uploaded! Company successfully added!";
			ansr = "addPass";
			
		
			
			log.info(file.getOriginalFilename() + " Company added successfuly");
			redir.addFlashAttribute("mess", mess);
			redir.addFlashAttribute("ansr", ansr);
		}
		else
		{
			mess = "File failed to be uploaded!";
			ansr = "addFail";
			log.error("error saving Company from file " + file.getOriginalFilename());
			redir.addFlashAttribute("mess", mess);
			redir.addFlashAttribute("ansr", ansr);
		}
		

		RedirectView redirectView = new RedirectView("/adminCompanies", true);

		return redirectView;
	}
	

	
	@GetMapping("/edit_company/{id}/{locid}/")
	public String showUpdateForm(@PathVariable("id") long id, @PathVariable("locid") long locID, Company company, Model model, Authentication auth) {
		Company currentCompany = companyRepo.findById(id);
		User currentUser;
		Location loc = locationRepo.findById(locID);
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		
		Long idNum = userD.getID();

		currentUser = userRepo.findById(idNum).orElse(null);
		log.info("Editing Company " + currentCompany.getCompanyName() + " at Location " + loc.getLocationName());
		List<Company> companies	= new ArrayList<Company>();
		if(currentUser.isSuperUser()) {
			companies = companyRepo.findAll();
		}
		else {
			companies.add(currentUser.getCompany());
		}
		
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		model.addAttribute("companies", companies);

		model.addAttribute("company", currentCompany);
		model.addAttribute("id", id);
		model.addAttribute("SUPERUSER", currentUser.isSuperUser());
		model.addAttribute("location", loc);
		return "adminCompanyUpdate";

	}
	
	
	@PostMapping("/update_company/{id}/{locid}")
	public String updateCompany(@PathVariable("id") long id, @PathVariable("locid") long locID, Model model, Authentication auth, @Validated Company comp) {
		
		Company currentCompany = companyRepo.findById(id);
		User currentUser;
		Location loc = locationRepo.findById(locID);
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idNum = userD.getID();
		
		currentUser = userRepo.findById(idNum).orElse(null);
		log.info("Editing Company " + currentCompany.getCompanyName() + " at Location " + loc.getLocationName());
		
		List<Company> companies	= new ArrayList<Company>();
		if(currentUser.isSuperUser()) {
			companies = companyRepo.findAll();
		}
		else {
			companies.add(currentUser.getCompany());
		}
		
		if(comp.getParentCompanyName() != null && !comp.getParentCompanyName().equals("0")) {

			Company parentComp = companyRepo.findByCompanyName(comp.getParentCompanyName());
			log.info("Updating parent company of " + currentCompany.getCompanyName() + " (ID:" + currentCompany.getId() + ") to " + parentComp.getCompanyName() + " (ID:" + parentComp.getId() + "}");

			comp.setParent(parentComp);
		}

		companyRepo.save(comp);
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		model.addAttribute("companies", companies);
		model.addAttribute("company", currentCompany);
		model.addAttribute("id", id);
		model.addAttribute("SUPERUSER", currentUser.isSuperUser());
		model.addAttribute("location", loc);
		return "adminCompanyUpdate";
		
	}
	
	@GetMapping("/company_remove_user/{id}/{userid}/{locid}/")
	public String removeUser(@PathVariable("id") long id, @PathVariable("userid") long userid, @PathVariable("locid") long locID,  Model model, Authentication auth) {
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = userRepo.findById(idnum).orElse(null);
		Company thisComp = companyRepo.findById(id);
		User theUser = userRepo.findByid(userid);
		theUser.setActivation(false);
		Location loc = locationRepo.findById(locID);

		System.out.println("DEACTIVATING USER " + theUser.getName());
		userRepo.save(theUser);
		log.info("Deactivated User " + theUser.getName() + " (ID:" + theUser.getId() + ") For Company " + thisComp.getCompanyName() + " (ID:" + thisComp.getId() + ")");

		
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingCompany(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
			model.addAttribute("ADMIN", true);
			model.addAttribute("showUserScreen", true);
		}
		model.addAttribute("location", loc);
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		model.addAttribute(thisComp);
		return "adminCompanyUpdate";
	}
	
	@GetMapping("/company_reactivate_user/{id}/{userid}/{locid}/")
	public String reactivateUser(@PathVariable("id") long id, @PathVariable("userid") long userid,@PathVariable("locid") long locID, Model model, Authentication auth) {
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = userRepo.findById(idnum).orElse(null);
		Company thisComp = companyRepo.findById(id);
		User theUser = userRepo.findByid(userid);
		theUser.setActivation(true);
		Location loc = locationRepo.findById(locID);

		userRepo.save(theUser);
		log.info("Reactivated User " + theUser.getName() + " (ID:" + theUser.getId() + ") For Company " + thisComp.getCompanyName() + " (ID:" + thisComp.getId() + ")");

		
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingCompany(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
			model.addAttribute("ADMIN", true);
			model.addAttribute("showUserScreen", true);
		}
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		model.addAttribute("location", loc);
		model.addAttribute(thisComp);
		return "adminCompanyUpdate";
	}
	
	@GetMapping("/company_remove_location/{id}/{locid}/")
	public String removeLocation(@PathVariable("id") long id, @PathVariable("locid") long locid, Model model, Authentication auth) {
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = userRepo.findById(idnum).orElse(null);
		Company thisComp = companyRepo.findById(id);
		Location theLoc = locationRepo.findById(locid);
		theLoc.setActivation(false);
		locationRepo.save(theLoc);

		log.info("Deactivated Location " + theLoc.getLocationName() + " (ID:" + theLoc.getId() + ") For Company " + thisComp.getCompanyName() + " (ID:" + thisComp.getId() + ")");
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingCompany(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
			model.addAttribute("ADMIN", true);
			model.addAttribute("showUserScreen", true);
		}
		model.addAttribute(thisComp);
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		return "adminCompanies";
	}
	
	@GetMapping("/company_reactivate_location/{id}/{locid}/")
	public String reactivateLocation(@PathVariable("id") long id, @PathVariable("locid") long locid, Model model, Authentication auth) {
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = userRepo.findById(idnum).orElse(null);
		Company thisComp = companyRepo.findById(id);
		Location theLoc = locationRepo.findById(locid);
		theLoc.setActivation(true);
		locationRepo.save(theLoc);
		log.info("Reactivated Location " + theLoc.getLocationName() + " (ID:" + theLoc.getId() + ") For Company " + thisComp.getCompanyName() + " (ID:" + thisComp.getId() + ")");

		
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingCompany(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
			model.addAttribute("ADMIN", true);
			model.addAttribute("showUserScreen", true);
		}
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		model.addAttribute(thisComp);
		return "adminCompanies";
	}
	
	@GetMapping("/company_remove_department/{id}/{deptid}/{locid}/")
	public String removeDepartment(@PathVariable("id") long id, @PathVariable("deptid") long deptid, @PathVariable("locid") long locID, Model model, Authentication auth) {
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = userRepo.findById(idnum).orElse(null);
		Company thisComp = companyRepo.findById(id);
		Department theDept = deptRepo.findById(deptid);
		theDept.setActivation(false);
		deptRepo.save(theDept);
		Location loc = locationRepo.findById(locID);
		log.info("Deactivated Department " + theDept.getName() + " (ID:" + theDept.getId() + ") For Company " + thisComp.getCompanyName() + " (ID:" + thisComp.getId() + ")");

		
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingCompany(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
			model.addAttribute("ADMIN", true);
			model.addAttribute("showUserScreen", true);
		}
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		model.addAttribute("location", loc);
		model.addAttribute(thisComp);
		return "adminCompanyUpdate";
	}
	
	@GetMapping("/company_reactivate_department/{id}/{deptid}/{locid}/")
	public String reactivateDepartment(@PathVariable("id") long id, @PathVariable("deptid") long deptid, @PathVariable("locid") long locID, Model model, Authentication auth) {
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = userRepo.findById(idnum).orElse(null);
		Company thisComp = companyRepo.findById(id);
		Department theDept = deptRepo.findById(deptid);
		theDept.setActivation(true);
		deptRepo.save(theDept);
		Location loc = locationRepo.findById(locID);
		log.info("Reactivated Department " + theDept.getName() + " (ID:" + theDept.getId() + ") For Company " + thisComp.getCompanyName() + " (ID:" + thisComp.getId() + ")");

		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingCompany(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
			model.addAttribute("ADMIN", true);
			model.addAttribute("showUserScreen", true);
		}
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		model.addAttribute("location", loc);
		model.addAttribute(thisComp);
		return "adminCompanyUpdate";
	}
	
	@GetMapping("/company_deactivate/{id}")
	public String deactivateCompany(@PathVariable("id") long id, Model model, Authentication auth) {
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = userRepo.findById(idnum).orElse(null);
		Company thisComp = companyRepo.findById(id);
		thisComp.setActivation(false);
		companyRepo.save(thisComp);
		log.info("Deactivated Company " + thisComp.getCompanyName()  + " (ID:" + thisComp.getId() + ")");


		Iterable allUsers = userRepo.findAll();
		Iterator<User> userIterator = allUsers.iterator();
		while(userIterator.hasNext()) {
			User user = userIterator.next();
			if(user.getCompany().equals(thisComp)) {
			if(!(user.equals(currentUser)) && !(user.isCompanySuperUser()) ) {
				user.setActivation(false);
				userRepo.save(user);
				log.info("Deactivated User " + user.getName() + " (ID:" + user.getId() + ") For Company " + thisComp.getCompanyName() + " (ID:" + thisComp.getId() + ")");

			}
			}
		}
		
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingCompany(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
			model.addAttribute("ADMIN", true);
			model.addAttribute("showUserScreen", true);
		}
		Department newDept = new Department();
		
		model.addAttribute("newDepartment",newDept);
		return "adminCompanies";
	}
	
	@GetMapping("/company_reactivate/{id}")
	public String reactivateCompany(@PathVariable("id") long id, Model model, Authentication auth) {
		User currentUser;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();
		Long idnum = userD.getID();
		currentUser = userRepo.findById(idnum).orElse(null);
		Company thisComp = companyRepo.findById(id);
		thisComp.setActivation(true);
		companyRepo.save(thisComp);
		log.info("Reactivated Company " + thisComp.getCompanyName()  + " (ID:" + thisComp.getId() + ")");


		Iterable allUsers = userRepo.findAll();
		Iterator<User> userIterator = allUsers.iterator();
		while(userIterator.hasNext()) {
			User user = userIterator.next();
			if(user.getCompany().equals(thisComp)) {
			if(!(user.equals(currentUser))) {
				user.setActivation(true);
				userRepo.save(user);
				log.info("Reactivated User " + user.getName() + " (ID:" + user.getId() + ") For Company " + thisComp.getCompanyName() + " (ID:" + thisComp.getId() + ")");

			}
			}
		}
		
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo, evalFormRepo);
		model = AdminMethodsService.addingOrEditingCompany(currentUser, this.locationRepo, this.deptRepo, this.roleRepo, this.companyRepo, model);
		if(currentUser.isSuperUser()) {
			model.addAttribute("COMPANY_ADMIN", false);
			model.addAttribute("ADMIN", true);
			model.addAttribute("showUserScreen", true);
		}
		return "adminCompanies";
	}


	public Set<Company> getChildList(Company parentComp){
		Long parentId = parentComp.getId();
		Set<Company> children = new HashSet<Company>();
		List<Company> allCompanies = companyRepo.findAll();
		ListIterator<Company> compIterator = allCompanies.listIterator();
		while(compIterator.hasNext()) {
			Company nextComp = compIterator.next();
			if(nextComp.getParentId() == parentId) {
				children.add(nextComp);
			}
		}
		return children;
	}
	
	
	/**
	 * @param comp Company to search for admin
	 * @return returnUser The admin user of the company if found, null otherwise
	 */
	public User getAdminUser(Company comp) {
		List<User> companyUsers = userRepo.findByCompany(comp);
		User returnUser = null;
		for(User user : companyUsers) {
			returnUser = user;
			System.out.println(user.getName());
		}
		return returnUser;
	}
	
	
	

}
