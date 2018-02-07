package database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.Submission;

public class SubmissionRepositoryTest extends DatabaseTest {
	private SubmissionRepository submissionRepository;
	private Submission submission;

	@Before
	public void setUp() throws Exception {
		submissionRepository = new SubmissionRepository();
		submission = new Submission(1, 1, 1, 30);
	}

	@Test
	public void testInsert() {
		int result = submissionRepository.insert(submission);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testGet() {
		submissionRepository.insert(submission);
		Submission retrievedSubmission = submissionRepository.getSubmission(1, 1).get(0);
		Assert.assertEquals(30, retrievedSubmission.getMark());
	}

	@Test
	public void testGetUser() {
		submissionRepository.insert(submission);
		Submission retrievedSubmission = submissionRepository.getSubmission(1, null).get(0);
		Assert.assertEquals(30, retrievedSubmission.getMark());
	}

	@After
	public void tearDown() {
		submissionRepository.destroy();
	}
}
