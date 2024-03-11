package edu.sru.WebBasedEvaluations;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import edu.sru.WebBasedEvaluations.company.City;
import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Continent;
import edu.sru.WebBasedEvaluations.company.Country;
import edu.sru.WebBasedEvaluations.company.Department;
import edu.sru.WebBasedEvaluations.company.Location;
import edu.sru.WebBasedEvaluations.company.LocationGroup;
import edu.sru.WebBasedEvaluations.company.Province;
import edu.sru.WebBasedEvaluations.company.World;
import edu.sru.WebBasedEvaluations.controller.HomePage;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.CityRepository;
import edu.sru.WebBasedEvaluations.repository.CompanyRepository;
import edu.sru.WebBasedEvaluations.repository.ContinentRepository;
import edu.sru.WebBasedEvaluations.repository.CountryRepository;
import edu.sru.WebBasedEvaluations.repository.DepartmentRepository;
import edu.sru.WebBasedEvaluations.repository.LocationGroupRepository;
import edu.sru.WebBasedEvaluations.repository.LocationRepository;
import edu.sru.WebBasedEvaluations.repository.PrivilegeRepository;
import edu.sru.WebBasedEvaluations.repository.ProvinceRepository;
import edu.sru.WebBasedEvaluations.repository.RoleRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;
import edu.sru.WebBasedEvaluations.repository.WorldRepository;

@SpringBootApplication
@EnableScheduling
//@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class WebBasedEvaluationsApplication  extends SpringBootServletInitializer{
//adding a comment to test merge. 
	public static void main(String[] args) {
		//set to true if application properties is set to 'create-drop', false if set to 'none'
		final Boolean CREATE_FRESH_DATABASE = true;
		final Logger log = LoggerFactory.getLogger(WebBasedEvaluationsApplication.class);
		
		System.setProperty("spring.devtools.restart.enabled", "false");
		
		System.out.println("STARTING WEB APP\n");
		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(WebBasedEvaluationsApplication.class, args);
		System.out.println("\nFINISHED STARTING WEB APP app\n");
		
		
		if(CREATE_FRESH_DATABASE) {
			System.out.println("STARTING LOADING OF TEST ADMIN USER\n");
			InitUsers startingUsers = new InitUsers();
			startingUsers.createBaseUsers(configurableApplicationContext);
			System.out.println("\nFINISHED LOADING TEST ADMIN USER");
		} else {
			System.out.println("\nDATABASE LOADED USING SCHEMA");
		}
		
		log.info("Link: https://localhost:8443/");
	}
	
}


class InitUsers{
	
	private Logger log = LoggerFactory.getLogger(InitUsers.class);
	private boolean userExistsByEmail(ConfigurableApplicationContext configurableApplicationContext, String email) {
		
		UserRepository userRepo=configurableApplicationContext.getBean(UserRepository.class);
	    return userRepo.findByEmail(email) != null;
	}
	
	public InitUsers() {
		
	}
	public void createBaseUsers(ConfigurableApplicationContext configurableApplicationContext) {
		
		//create company structure
		
		//repos
		LocationRepository locationRepo=configurableApplicationContext.getBean(LocationRepository.class);
		
		UserRepository userRepo=configurableApplicationContext.getBean(UserRepository.class);
		LocationGroupRepository locGroupRepo = configurableApplicationContext.getBean(LocationGroupRepository.class);
		DepartmentRepository deptRepo =  configurableApplicationContext.getBean(DepartmentRepository.class);
		RoleRepository roleRepo =  configurableApplicationContext.getBean(RoleRepository.class);
		PrivilegeRepository privRepo = configurableApplicationContext.getBean(PrivilegeRepository.class);
		//making instances to add to the tables. 
		
		Company co = new Company("Thangiah Manufacturing LLC");
		Company co2 = new Company("Test Company 2");
		
		World world = new World("World");
//		worldRepo.save(world);
		
		Continent continent = new Continent("NA", world);
//		continentRepo.save(continent);
		
		world.addContinent(continent);
//		worldRepo.save(world);
	
		Country country = new Country("USA", continent);
//		countryRepo.save(country);
		
		continent.addCountry(country);
//		continentRepo.save(continent);
		

		Province province = new Province("PA", country);
//		provinceRepo.save(province);
		
		country.addProvince(province);
//		countryRepo.save(country);
		
		
		City city = new City("Slippery Rock", province);
//		cityRepo.save(city);
		
		province.addCity(city);
		
		
		LocationGroup locGroup = new LocationGroup();
		LocationGroup locGroup2 = new LocationGroup();
		LocationGroup locGroup3 = new LocationGroup();
		locGroup3.setCompany(co2);

		locGroup.setCompany(co);
		locGroup2.setCompany(co2);
		
		Location loc = new Location("none", city, co, locGroup);
		Location loc2 = new Location("testLocation2", city, co2, locGroup2);
		Location loc3 = new Location("testLocation2", city, co2, locGroup3);
//		locationRepo.save(loc);
			
		
		//this role name of "ADMIN*" is also used to assign the company super user value dynamically. 
		Role adminRole = new Role("SUPERUSER",co);
		
		Role USER = new Role("SUPERUSER",co);
		Role adminRole2 = new Role("ADMIN",co2);


		Role Eval_admin=new Role("EVALUATOR_ADMIN",co2);

		Role baseUser = new Role("USER", co2);


//		Role testRole1 = new Role("TEST_ROLE_1",co);
//		Role testRole2 = new Role("TEST_ROLE_2",co2);
//		
	
		
		co.addRole(adminRole);
		co.addRole(USER);
//		co.addRole(testRole1);
//		co.addRole(testRole2);
		co2.addRole(adminRole2);
		co2.addRole(baseUser);
		co2.addRole(Eval_admin);
//		co2.addRole(testRole1);
//		co2.addRole(testRole2);
//		companyRepo.save(co);
//		adminRole.addCompany(co);
		
		
		
		
		User use1 = new User("jimmy neutron","fname","lname","admin@gmail.com","$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ", 999991, "N/A", "N/A", null, "N/A","admin dept", co2, adminRole,false,true);
		User use2 = new User("jimmy2 neutron2","fname2","lname2","admin2@gmail.com","$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ", 999991, "N/A", "N/A", null, "N/A","admin dept", co2, adminRole2,true,false);


		User user3=new User("jimmy2 neutron3","fname2","lname2","admin3@gmail.com","$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ", 999991, "N/A", "N/A", null, "N/A","admin dept", co2, Eval_admin,false,false);

		User use3 = new User("frank nziza","nziza","frank","user1@gmail.com","$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ", 999991, "N/A", "N/A", null, "N/A","admin dept", co2, baseUser,false,false);
		User use4 = new User("frank smith","smith","frank","user2@gmail.com","$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ", 999991, "N/A", "N/A", null, "N/A","admin dept", co2, baseUser,false,false);
		User use5 = new User("frank ferdinand","ferdinand","frank","admine@gmail.com","$2y$12$.ahxo5UdngIuZdKSu91Jn.VtHjjYCh04.lpM5LNFdICjEjechMDQ", 999991, "N/A", "N/A", null, "N/A","admin dept", co2, Eval_admin,false,false);


		if(!userExistsByEmail(configurableApplicationContext, "admin@gmail.com"))
		{
//			adminRole.addUser(use1);
//			roleRepo.save(adminRole);

//			use1.setReviewee(true);
//			use1.setReviewer(true);
	//
			use4.setReviewer(false);
			use4.setReviewee(true);

			use1.setEncryptedPassword("test");
			use1.setReset(false);
			use2.setEncryptedPassword("test");
			use2.setReset(true);
			use3.setEncryptedPassword("test");
			use3.setReset(false);
			use3.setReviewee(true);
			use4.setEncryptedPassword("test");
			use4.setReset(false);
			use4.setReviewer(true);
			use5.setEncryptedPassword("test");
			use5.setReset(false);
//			userRepo.save(use1);
			
			
			
			
			
			Department dept = new Department(use1, loc, "none", null,null,co);
			Department dept2 = new Department(use2, loc2, "testing dept2", null,null,co2);
			Department dept3 = new Department(use2, loc3, "testing dept2", null,null,co2);
			
			use1.addDepartment(dept);
			loc.addDept(dept);
			use2.addDepartment(dept2);
			use3.addDepartment(dept2);
			use4.addDepartment(dept2);
			use5.addDepartment(dept2);
			loc2.addDept(dept2);
//			userRepo.save(use1);
//			locationRepo.save(loc);

			user3.addDepartment(dept3);

			
			
			
			locGroup.addLocation(loc);
			locGroup2.addLocation(loc2);
			locGroup3.addLocation(loc3);
//			locGroupRepo.save(locGroup);
			
			city.addLocation(loc);
			city.addLocation(loc2);
//			
		
			
			
			co.addLocation(loc);
			co2.addLocation(loc2);
			co2.addLocation(loc3);
//			companyRepo.save(co);
			
			
//			Role adminRole = new Role("Global Admin");
//			roleRepo.save(adminRole);
			
			Privilege priv = new Privilege("ADMIN",adminRole, locGroup, dept, co, true,true,true,false);

			Privilege priv2 = new Privilege("ADMIN2", adminRole2, locGroup2, dept2, co2, true,true,true,false);
//			privRepo.save(priv);
			
			adminRole.addPrivilege(priv);
			adminRole2.addPrivilege(priv2);
			Eval_admin.addPrivilege(priv);
			priv.addRole(adminRole);
			priv2.addRole(adminRole2);
			
//			roleRepo.save(adminRole);
			
			co.addUser(use1);
			use1.addLocation(loc);
			
			co2.addUser(use2);
			use2.addLocation(loc2);

			co2.addUser(user3);
			user3.addLocation(loc3);
			
			co2.addUser(use3);
			use3.addLocation(loc2);
			
			co2.addUser(use4);
			use4.addLocation(loc2);
			
			co2.addUser(use5);
			use5.addLocation(loc2);
			
			
			locGroupRepo.save(locGroup);		
			deptRepo.save(dept);
			privRepo.save(priv);
			userRepo.save(use1);
			locationRepo.save(loc);
//			roleRepo.save(testRole1);
//			roleRepo.save(testRole2);
			
			
			locGroupRepo.save(locGroup2);
			deptRepo.save(dept2);
			privRepo.save(priv2);
			userRepo.save(use2);
			userRepo.save(use3);
			userRepo.save(use4);
			userRepo.save(use5);
			locationRepo.save(loc2);
			
//			companyRepo.save(co2);

			co.addUser(user3);
			user3.addLocation(loc3);
			locGroupRepo.save(locGroup3);
			deptRepo.save(dept3);
			privRepo.save(priv);
			userRepo.save(user3);
			locationRepo.save(loc3);
		}
		else
		{
			log.info("Users already in Database");
		}

	}
}