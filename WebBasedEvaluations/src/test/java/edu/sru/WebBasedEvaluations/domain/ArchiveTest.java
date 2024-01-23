package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import org.mockito.junit.jupiter.MockitoExtension;

import edu.sru.WebBasedEvaluations.domain.Archive;

@Suite
@SuiteDisplayName("Archive Methods")
@IncludeClassNamePatterns(".*Tests")
@ExtendWith(MockitoExtension.class)
public class ArchiveTest {

	static Archive archive;
	static Archive invalidArchive;
	static Date date = new Date(100);

	@BeforeAll
	static void setup() {

		archive = new Archive();
		archive.setReviewee("Bob");
		archive.setRole("Manager");
		archive.setEvaluator("Steve");
		archive.setDateEdited(date);

	}

	@Test
	public void getIdTest() {
		long actual = archive.getId();
		assertEquals(archive.getId(), actual);
	}

	@Test
	public void getDateEditedTest() {
		Date actual = new Date(100);
		assertEquals(archive.getDateEdited(), actual);
	}

	@Test
	public void getPathTest() {
		byte[] actual = new byte[1];
		actual[0] = (byte) 4;
		archive.setPath(actual);
		assertEquals(archive.getPath(), actual);
	}

	@Test
	public void getRevieweeTest() {
		String actual = "Bob";
		assertEquals(archive.getReviewee(), actual);
	}

	@Test
	public void getRoleTest() {
		String actual = "Manager";
		assertEquals(archive.getRole(), actual);
	}

	@Test
	public void getEvaluatorTest() {
		String actual = "Steve";
		assertEquals(archive.getEvaluator(), actual);
	}

	@Test
	public void nullValuesTest() {
		invalidArchive = new Archive();
		invalidArchive.setReviewee(null);
		invalidArchive.setRole(null);
		invalidArchive.setEvaluator(null);
		invalidArchive.setDateEdited(null);
		invalidArchive.setPath(null);

		assertNotNull(invalidArchive.getId());
		assertNull(invalidArchive.getReviewee());
		assertNull(invalidArchive.getRole());
		assertNull(invalidArchive.getEvaluator());
		assertNull(invalidArchive.getDateEdited());
		assertNull(invalidArchive.getPath());
	}

	@Test
	public void Test() {

	}
}
