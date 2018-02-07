package core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SubmissionTest {
	private Submission testSubmission;

	@Before
	public void setUp() {
		int userId = 1;
		int assignmentId = 1;
		int submissionId = 2;
		int mark = 3;
		testSubmission = new Submission(userId, assignmentId, submissionId, mark);
	}

	@Test
	public void testGetValues() {
		int userId = testSubmission.getUserId();
		int assignmentId = testSubmission.getAssignmentId();
		int submissionId = testSubmission.getSubmissionId();
		int mark = testSubmission.getMark();

		int expectedArray[] = { 1, 1, 2, 3 };
		int actualArray[] = { userId, assignmentId, submissionId, mark };

		Assert.assertArrayEquals(expectedArray, actualArray);
	}

	@Test
	public void testSetNewValues() {

		testSubmission.setUserId(4);
		testSubmission.setAssignmentId(5);
		testSubmission.setSubmissionId(6);
		testSubmission.setMark(7);

		int userId = testSubmission.getUserId();
		int assignmentId = testSubmission.getAssignmentId();
		int submissionId = testSubmission.getSubmissionId();
		int mark = testSubmission.getMark();

		int expectedArray[] = { 4, 5, 6, 7 };
		int actualArray[] = { userId, assignmentId, submissionId, mark };

		Assert.assertArrayEquals(expectedArray, actualArray);
	}
}
