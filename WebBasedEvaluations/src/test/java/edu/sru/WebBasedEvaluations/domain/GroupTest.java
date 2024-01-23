package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.UserRepository;


public class GroupTest {
	@Mock static int groupNum;
	@Mock static Boolean selfEval;
	static Reviewee rev;
	private static User user = new User();
	@Mock static Company co;
	static Group group;
	@BeforeAll
	static void setup() {
		group = new Group(selfEval,co,groupNum);
		rev = new Reviewee();
		rev.setName("Test");
		
		user.setFirstName("Dalton");
		user.setLastName("Stenzel");
		user.setEmail("daltonrstenzel@gmail.com");
		Company co = new Company("Thangiah Inc");
		user.setRole(new Role("USER",co));
		user.setEncryptedPassword("test");
		
		user.setCompanyName("Thangiah Inc");
		user.setDivisionBranch("Retroville");
		user.setSupervisor("Brandon");
		user.setDateOfHire("10/15/2022");
		user.setJobTitle("Assistant");
	}

	@Test
	public void getIdTest() {
		long actual = 5;
		group.setId(actual);
		assertEquals(group.getId(), actual);
	}

	@Test
	public void getRevieweeTest() {
		List<Reviewee> actual = null;
		group.setReviewee(actual);
		assertNull(group.getReviewee());
	}

	@Test
	public void getEvaluatorTest() {
		List<Evaluator> actual = null;
		group.setEvaluator(actual);
		assertNull(group.getEvaluator());
	}

	@Test
	public void appendRevieweeTest() {
		List<Reviewee> actual = new ArrayList<>();
		actual.add(rev);
		group.setReviewee(actual);
		rev.setUser(user);
		group.appendReviewee(rev);
		assertEquals(group.getReviewee(), actual);
		

	}

	@Test
	public void getEvalTemplatesTest() {
		EvalTemplates actual = new EvalTemplates(co);
		group.setEvalTemplates(actual);
		assertEquals(group.getEvalTemplates(), actual);
	}

	@Test
	public void getEvalstartTest() {
		group.setEvalstart(true);
		assertTrue(group.getEvalstart());
		group.setEvalstart(false);
		assertFalse(group.getEvalstart());
	}

	@Test
	public void getSelfevalTest() {
		group.setSelfeval(true);
		assertTrue(group.getSelfeval());
		group.setSelfeval(false);
		assertFalse(group.getSelfeval());
	}

}
