package edu.sru.WebBasedEvaluations.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking.ClearCommand;
import org.hibernate.event.spi.ClearEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders.Clear;
import com.aspose.cells.Name;

import edu.sru.WebBasedEvaluations.company.City;
import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Country;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;
import edu.sru.WebBasedEvaluations.company.Province;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.excel.ExcelRead_group;
import edu.sru.WebBasedEvaluations.repository.CityRepository;
import edu.sru.WebBasedEvaluations.repository.CompanyRepository;
import edu.sru.WebBasedEvaluations.repository.CountryRepository;
import edu.sru.WebBasedEvaluations.repository.DepartmentRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.LocationGroupRepository;
import edu.sru.WebBasedEvaluations.repository.LocationRepository;
import edu.sru.WebBasedEvaluations.repository.PrivilegeRepository;
import edu.sru.WebBasedEvaluations.repository.ProvinceRepository;
import edu.sru.WebBasedEvaluations.repository.RoleRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.service.AdminMethodsService;

@Controller
public class AddRolesController {

	//list of repositories used throughout the controller file 
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private EvaluatorRepository evaluatorRepo;
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private LocationRepository locRepo;
	@Autowired
	private LocationGroupRepository locGroupRepo;
	@Autowired
	private PrivilegeRepository privRepo;
	@Autowired
	private EvaluationRepository evalFormRepo;
	@Autowired
	private CompanyRepository compRepo;
	@Autowired
	private CityRepository cityRepo;
	@Autowired
	private ProvinceRepository provRepo;
	@Autowired
	private CountryRepository cntryRepo;
	@Autowired
	private AdminMethodsService adminMethodsService;
	
	private final String TEMP_FILES_PATH = "src\\main\\resources\\temp\\";
	private Logger log = LoggerFactory.getLogger(AddRolesController.class);

	//check admin permissions and availability to page 
	@GetMapping("/adminRoles")
	public String addRoles(Model model, Authentication auth) {


		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		String UserRole=currentUser.getRole().getName();
		model.addAttribute("UserRole",UserRole);
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
			Files.createDirectories(Paths.get(TEMP_FILES_PATH));
			FileUtils.cleanDirectory(new File(TEMP_FILES_PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
			log.error("Directory '" + TEMP_FILES_PATH + "' could not be created or cleaned.");
		}

		
		
		List<Role> roles = new ArrayList<Role>();
		
		List<Location> locationsList = new ArrayList<Location>();
		Set<Location> companyLocations = currentCompany.getLocations();
		
		
		
		roles.addAll(currentCompany.getRoles());
		
		System.out.println(roles.get(0).getName());
		locationsList.addAll(companyLocations);

		
//		for(int x = 0 ; x < roles.size();x++) {
//			if(roles.get(x).getName().contains("SUPERUSER")) {
//				roles.remove(x);
//				
//			}
//			
//			try {
//			
//			//only shows admins to supersuperuser
//			if(currentUser.isSuperUser() && !roles.get(x).getName().contains("ADMIN")) {
//				roles.remove(x);
//			}
//			}catch (Exception e) {
//				System.out.print(e);
//			}
//		}
//		
		HashMap<Role, Integer> usersWithRole = new HashMap<Role, Integer>();
		List<User> usersInCompany = userRepo.findByCompany(currentCompany);
		
		//remove admins/supersuperusers
		List<User> usersToRemove = new ArrayList<User>();
		for(User user: usersInCompany) {
			if(user.getRole().getName().contains("COMPANY")) {
				usersToRemove.add(user);
			}
		}
		
		for(User user: usersToRemove) {
			usersInCompany.remove(user);
		}
		
		
		for(Role role: currentCompany.getRoles()) {
			
			usersWithRole.put(role, 0);
			
			for(User user: usersInCompany) {
				if(user.getRole().equals(role)) {
					usersWithRole.put(role, usersWithRole.get(role) + 1);			
				}
			}
			
		}

		
		if(roles.isEmpty()) {
			model.addAttribute("rolesAreEmpty", true);
		} else {
			model.addAttribute("rolesAreEmpty", false);
		}
		
		Collections.sort(roles, Comparator.comparing(Role::getName));
		
		
		model.addAttribute("showUserScreen", showUserScreen);
		model.addAttribute("rolesNeedAdded", rolesNeedAdded);
		model.addAttribute("roles", roles);
		model.addAttribute("usersWithRole", usersWithRole);
		model = AdminMethodsService.pageNavbarPermissions(currentUser, model, evaluatorRepo,evalFormRepo);
		
		return "/adminRoles";
	}
	
	@GetMapping("/roleInformation/{id}")
	public Object roleInformation(RedirectAttributes redirect, @PathVariable("id") long id, Model model, Authentication auth) {
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		Iterable<Role> roles = roleRepo.findAll();
		Role role = null;
		
		for(Role x: roles) {
			if(x.getId() == id) {
				role = x;
				break;
			}
		}
		
		Set<Privilege> privileges = role.getPrivileges();
		List<Department> roleDepartments = new ArrayList<Department>();
		List<Privilege> rolePrivileges = new ArrayList<Privilege>();
		
		Set<Location> companyLocations = locRepo.findByCompany(currentCompany);
		List<Location> roleLocations = new ArrayList<Location>();
		
		roleLocations.addAll(companyLocations);
		
		
		rolePrivileges.addAll(privileges);
		
		for(Privilege priv: rolePrivileges) {
			
			roleDepartments.addAll(priv.getDepts());
		}
		
		for(int i = 0; i < rolePrivileges.size(); i++) {
			Privilege priv = rolePrivileges.get(i);
			
			for(int j = 0; j < roleLocations.size(); j++) {
				Location loc = roleLocations.get(j);
				if(priv.getName().contains(loc.getLocationName())) {
					rolePrivileges.remove(priv);
				}
			}
		}
		
		List<User> companyUsers = new ArrayList<User>();
		List<User> usersWithRole = new ArrayList<User>();
		
		companyUsers.addAll(userRepo.findByCompany(currentCompany));
		
		for(User user: companyUsers) {
			if(user.getRole().equals(role)) {
				usersWithRole.add(user);
			}
		}
		
		

		//sort the lists alphabetically
		Collections.sort(roleDepartments, Comparator.comparing(Department::getName));
		Collections.sort(rolePrivileges, Comparator.comparing(Privilege::getName));
		Collections.sort(usersWithRole, Comparator.comparing(User::getName));
		
		model.addAttribute("role", role);
		model.addAttribute("privsList", rolePrivileges);
		model.addAttribute("deptsList", roleDepartments);		
		model.addAttribute("usersWithRole", usersWithRole);
		return "roleInformation";
	}
	
	
	//current user information based on user trying to access upload roles 

	@RequestMapping(value = "/upload_roles", method = RequestMethod.POST)
	public Object uploadRoles(@RequestParam("file") MultipartFile file, RedirectAttributes redir, Authentication auth) throws Exception {
		
		User currentUser;
		Company currentCompany;
		
		MyUserDetails userD = (MyUserDetails) auth.getPrincipal();

		Long idnum = userD.getID();

		currentUser = this.userRepo.findById(idnum).orElse(null);

		currentCompany = currentUser.getCompany();
		
		XSSFSheet sheet = null;
		String mess ="";
		String ansr = "";
		boolean success = false;
		
		
		if(!currentUser.getRole().getName().equalsIgnoreCase("SUPERUSER")) {
			RedirectView redirectView = new RedirectView("/adminRoles", true);
			redir.addFlashAttribute("error", "User must be a super user to add roles");
			return redirectView;
		}

		//Check That file type is correct 
		//check the file type for upload_roles files, should be Role Template in column S
		try {
			sheet = ExcelRead_group.loadFile(file).getSheetAt(0);
			String name = ExcelRead_group.checkStringType(sheet.getRow(0).getCell(18));
			if(!name.equals("Role Template")) {
				throw new Exception("Invalid file");
			}

		} 
		//invalid file type 
		catch (Exception e) {				
			RedirectView redirectView = new RedirectView("/adminRoles", true);
			redir.addFlashAttribute("error", "invalid file");
			return redirectView;			
		}
		
		//list of options aspects for the Excel file 
		Department dept = null;
		Location loc = null;
		Company compName = null;
		LocationGroup locGroup = null;
		City city = null;
		Province prov = null;
		Country cntry = null;
		
		//permissions for the Excel column types
		boolean locRead = false;
		boolean locWrite = false;
		boolean locDelete = false;
		boolean locEditEvaluator = false;
		boolean deptRead = false;
		boolean deptWrite = false;
		boolean deptDelete = false;
		boolean deptEditEvaluator = false;
		String compNameName = "";
		String deptName = "";
		String locName = "";
		String locGroupName = "";
		String cityName = "";
		String provName = "";
		String cntryName = "";
		boolean hasLoc = false;
		boolean hasDept = false;
		boolean hasCompName = false; 
		boolean hasLocGroup = false;
		boolean hasCity = false;
		boolean hasProv = false;
		boolean hasCntry = false;
		boolean assignedLoc = false;
		boolean assignedDept = false;
		

		HashMap<Department, Set<Privilege>> departmentPrivileges = new HashMap<Department, Set<Privilege>>(); 
		
		//set all to false until checked that present in the Excel file
		for (int i = 1; sheet.getRow(i) != null; i++) {
			try {
				String roleName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(0));
				Role currentRole = roleRepo.findByNameAndCompany(roleName, currentCompany);
				hasLoc = false;
				hasDept = false;
				hasCompName = false; 
				hasLocGroup = false;
				hasCity = false;
				hasProv = false;
				hasCntry = false;
				hasProv = false;
				hasCntry = false;

				
				
				if(currentRole == null) {
					currentRole = new Role(roleName, currentCompany);
				}
				
				
				deptName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(1));
				
				if(deptName == null || deptName.length() == 0) {
					hasDept = false;
				}
				else {					
					hasDept = true;
					//department
					dept = deptRepo.findByNameAndCompany(deptName, currentCompany);
					
					
					if(dept == null) {
						RedirectView redirectView = new RedirectView("/adminRoles", true);
						redir.addFlashAttribute("error", "Please upload a company containing  the dept: " + deptName + " before associating it with a role.");
						return redirectView;	
					}
					
					//permissions for that department
					deptRead = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(2));
					deptWrite = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(3));
					deptDelete = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(4));
					deptEditEvaluator = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(5));
				}
				
				//Start of location based privilege
				//check that all aspects are in the Excel upload_roles files
				
				
				//individually check that all aspects are present in the Excel file upload_roles
				for (int j = 0; j < sheet.getLastRowNum() + 1; j++) 
					compNameName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(6));
				    locName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(7));
				    locGroupName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(8));
				    cityName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(9));
				    provName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(10));
				    cntryName = ExcelRead_group.checkStringType(sheet.getRow(i).getCell(11));
				    
				    //locationCheck
				    //set to false originally
				    if (locName == null|| locName.length() == 0) {
						hasLoc = false;
					}
					else {
					
					hasLoc = true;
					//location
					//first check to see if the location is present in the Excel file upload_roles 
					loc = locRepo.findByLocationNameAndCompany(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(7)), currentCompany);				

					if(loc == null) {
						RedirectView redirectView = new RedirectView("/adminRoles", true);
						redir.addFlashAttribute("error", "Please upload company containing location with name " + locName + " before attempting add a role to that location.");
						return redirectView;	
						}
					}
				    //CompanyName Check
				    //first check to see if the companyName is present in the Excel file upload_roles 
				    if (compNameName == null|| compNameName.length() == 0 ) {
						hasCompName = false;
					}
				    //if the file has locationGroup present then proceed with hasLocationGroup = true
				    else {
						
				    hasCompName = true;
					//companyName
				    compName = compRepo.findByCompanyName(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(6)));				
				    
				    
				    
				    //Throw error if company name is not in the upload_roles excel file 
				    //Throw error if company is not correct for current user's company
					if(compName == null || compName != currentCompany) {
						RedirectView redirectView = new RedirectView("/adminRoles", true);
						redir.addFlashAttribute("error", "Missing companyName in Excel file/ not assigned to this company");
						
						return redirectView;	
					}
					
				    //locationGroup Check 
				    //first check to see if the location group is present in the Excel file upload_roles 
				    if (locGroupName == null|| locGroupName.length() == 0) {
						hasLocGroup = false;
					}
				    //if the file has locationGroup present then proceed with hasLocationGroup = true
				    else {
						
						hasLocGroup = true;
						//companyName
						locGroup = locGroupRepo.findByCompanyAndName(compName, ExcelRead_group.checkStringType(sheet.getRow(i).getCell(8)));				

						if(locGroup == null) {
							RedirectView redirectView = new RedirectView("/adminRoles", true);
							redir.addFlashAttribute("error", "Please upload company containing company locationGroup " + compNameName + "" + locName + " before attempting add a role to that company.");
							return redirectView;	
							}
					}
				    if (cityName == null|| locName.length() == 0) {
						hasCity = false;
					}
				    //if the file has city present then proceed with hasCity = true
				    else {
						
						hasCity = true;
						//companyName
						city = cityRepo.findByCityName(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(9)));				

						if(city == null) {
							RedirectView redirectView = new RedirectView("/adminRoles", true);
							redir.addFlashAttribute("error", "Please upload company containing company with city " + compNameName + "" + locName + " before attempting add a role to that company.");
							return redirectView;	
							}
					}
				    if (provName == null|| locName.length() == 0) {
						hasLoc = false;
					}
				    //if the file has Province present then proceed with hasProv = true
				    else {
						
						hasProv = true;
						//companyName
						prov = provRepo.findByProvinceName(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(10)));				

						if(prov == null) {
							RedirectView redirectView = new RedirectView("/adminRoles", true);
							redir.addFlashAttribute("error", "Please upload company containing company with provinence " + compNameName + "" + locName + " before attempting add a role to that company.");
							return redirectView;	
							}
					}
				    //Country check
				    if (cntryName == null|| locName.length() == 0) {
						hasCntry = false;
					}
				    //if present proceed with hasCntry
				    else {
						
						hasCntry = true;
						//companyName
						cntry = cntryRepo.findByCountryName(ExcelRead_group.checkStringType(sheet.getRow(i).getCell(11)));				

						if(prov == null) {
							RedirectView redirectView = new RedirectView("/adminRoles", true);
							redir.addFlashAttribute("error", "Please upload company containing company with country " + compNameName + "" + locName + " before attempting add a role to that company.");
							return redirectView;	
							}
					}
				    //permissions for that company
				    //only allowed if all aspects are present 
				    if(hasCompName == true & hasLoc == true & hasLocGroup & hasCity & hasProv == true & hasCntry == true) {
						locRead = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(12));
						locWrite = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(13));
						locDelete = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(14));
						locEditEvaluator = ExcelRead_group.checkBooleanType(sheet.getRow(i).getCell(15));
				    }
				    
					
				//checks to see if there is a privilege that already contained the right read/write/delete/editevaluator perms that could be added to. 
				assignedLoc = false;
				assignedDept = false;
				
				
				//check the current users current role and their privilege
				for(Privilege priv : currentRole.getPrivileges()) {
					System.out.println(priv.getName() + (hasLoc && !assignedLoc));
					if(hasLoc && !assignedLoc) {						
						if(priv.getRead() == locRead && priv.getWrite() == locWrite && priv.getDelete() == locDelete && priv.getEditEvaluator() == locEditEvaluator && priv.containsLoc(loc)) {
							assignedLoc = true;
							break;						
						}
						else if(priv.getRead() == locRead && priv.getWrite() == locWrite && priv.getDelete() == locDelete && priv.getEditEvaluator() == locEditEvaluator) {
							assignedLoc = true;
							priv.addLocGroup(new LocationGroup(loc.getParentCity().getCityName(),loc,currentCompany));
//							privRepo.save(priv);
//							currentRole.addPrivilege(priv);
						}						
					}				
					
				}
				
				if(hasDept) {
					System.out.println("\n\n"+currentRole.getName() + " creating dept "+dept.getName());
					String name = this.privName(currentCompany, dept, deptRead, deptWrite, deptDelete, deptEditEvaluator);
					Privilege priv = new Privilege(name, currentRole, deptRead, deptWrite, deptDelete, deptEditEvaluator);
					
					//prevent duplicate departments from being added to a privilege
					boolean skipDept = false;
					
					Set<Privilege> rolePrivs = currentRole.getPrivileges();
					List<Privilege> rolePrivsList = new ArrayList<Privilege>();
					rolePrivsList.addAll(rolePrivs);
					
					for(int j = 0; j < rolePrivsList.size(); j++) {
						Privilege currentPriv = rolePrivsList.get(j);
						
						for(int z = 0; z < currentPriv.getDepts().size(); z++) {
							Department currentDept = currentPriv.getDepts().get(z);
							if(currentDept.getName().equals(dept.getName())) {
								skipDept = true;
								break;
							}
						}
						
						
					}
					
					if(!skipDept) {
						priv.addDept(dept);
						currentRole.addPrivilege(priv);
					}
				}
				
				//check to see if the role has any currentRoles assigned 
				try {
					roleRepo.save(currentRole);
					if(currentRole.getCompany() != null) {
						log.info("Uploaded Role " + currentRole.getName() + " (ID:" + currentRole.getId() + ") For Company " + currentRole.getCompany().getCompanyName() + " (ID:" + currentRole.getCompany().getId() + ")");

					} else {
						log.info("Uploaded Role " + currentRole.getName() + " (ID:" + currentRole.getId() + ")  [No Company]");
					}

					success = true;
					
				}
				//error saving the roles current worked on
				catch(Exception e) {
					e.printStackTrace();
					success = false;	
					
					RedirectView redirectView = new RedirectView("/adminRoles", true);
					redir.addFlashAttribute("error", "error saving role: " + currentRole.getName());
					return redirectView;
				}
			}
			}
			//error adding a role to a row 
			catch(Exception e) {
				log.error("Could not add role in row: " + (sheet.getRow(i).getRowNum() + 1) + " from "
						+ file.getOriginalFilename() + "\n" + e.getMessage());
				redir.addFlashAttribute("log", "error");
			}	
			
		}
		
		
		

		//success message if everything works correctly 
		if(success) {
			mess = "File uploaded! Role(s) successfully added!";
			ansr = "addPass";
			log.info(file.getOriginalFilename() + " roles added successfuly");
			redir.addFlashAttribute("mess", mess);
			redir.addFlashAttribute("ansr", ansr);
		}
		//failed to upload everything to the page
		else
		{
			mess = "File failed to be uploaded!";
			ansr = "addFail";
			log.error("error saving roles from file " + file.getOriginalFilename());
			redir.addFlashAttribute("mess", mess);
			redir.addFlashAttribute("ansr", ansr);
		}
		
		redir.addFlashAttribute("completed","File uploaded");
		
		RedirectView redirectView = new RedirectView("/adminRoles", true);
		
		return redirectView;
	}
	
	/**
	 * @param co company to add name of
	 * @param dept to add name of
	 * @param read permission of priv
	 * @param write permission of priv
	 * @param delete permission of priv
	 * @param editEvaluator permission of priv
	 * @return the unique name of the privilege
	 */
	
	//check priv based on outcome of above interpretation 
	public String privName(Company co, Department dept, boolean read, boolean write, boolean delete, boolean editEvaluator) {
		String name = "";
		name += co.getCompanyName() + "_";
		name += dept.getName() + "_";
		
		
		if(read) {
			name += "r";
		}
		else {
			name += "-";
		}
		
		if(write) {
			name += "w";
		}
		else {
			name += "-";
		}
		
		if(delete) {
			name += "d";
		}
		else {
			name += "-";
		}
		
		if(editEvaluator) {
			name += "e";
		}
		else {
			name += "-";
		}
		return name ;
	}
	
	
}
