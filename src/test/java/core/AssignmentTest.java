package core;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.answer.AssignmentAnswer;
import core.answer.StringAssignmentAnswer;
import core.question.AssignmentQuestion;
import core.question.StringQuestion;
import database.AssignmentRepository;
import database.DatabaseTest;

// This extends RepositoryTest because Assignment internally depends on AssignmentRepository
public class AssignmentTest extends DatabaseTest {
	private Assignment testAssignment;
	private AssignmentRepository assignmentRepository;
	private static final String ASSIGNMENT_NAME = "Test Assignment";
	private static final String QUESTION_ONE = "test123";
	private static final String ANSWER_ONE = "test124";
	private static final String QUESTION_TWO = "test145";
	private static final String ANSWER_TWO = "test136";

	@Before
	public void setUp() {
		testAssignment = new Assignment();
		testAssignment.setName(ASSIGNMENT_NAME);
		testAssignment.setDeadline(LocalDateTime.now());
		testAssignment.addQuestion(new StringQuestion(QUESTION_ONE, 1));
		testAssignment.addAnswer(new StringAssignmentAnswer(ANSWER_ONE));

		assignmentRepository = new AssignmentRepository();
	}

	@Test
	public void testSaveToDatabaseOneQuestion() {
		testAssignment.saveToLocalMachine();
		testAssignment.saveToDatabase();

		Assert.assertNotEquals(0, testAssignment.getAssignmentId());

		assignmentRepository.delete(testAssignment.getAssignmentId());
	}

	@Test
	public void testSaveToDatabaseManyQuestions() {
		testAssignment.addQuestion(new StringQuestion(QUESTION_TWO, 1));
		testAssignment.addAnswer(new StringAssignmentAnswer(ANSWER_TWO));
		testAssignment.saveToLocalMachine();
		testAssignment.saveToDatabase();

		Assert.assertNotEquals(0, testAssignment.getAssignmentId());

		Assignment retrievedAssignment = Assignment.retrieveFromDatabase(testAssignment.getAssignmentId());

		Assert.assertTrue(testValue(retrievedAssignment.getQuestions().get(0), QUESTION_ONE));
		Assert.assertTrue(testValue(retrievedAssignment.getQuestions().get(1), QUESTION_TWO));
		Assert.assertTrue(testValue(retrievedAssignment.getAnswers().get(0), ANSWER_ONE));
		Assert.assertTrue(testValue(retrievedAssignment.getAnswers().get(1), ANSWER_TWO));

		assignmentRepository.delete(testAssignment.getAssignmentId());
	}

	@Test
	public void testGetTotalMarksOneQuestion() {
		Assert.assertEquals(1, testAssignment.getTotalMarks());
	}

	@Test
	public void testGetTotalMarksNoQuestions() {
		testAssignment.getAnswers().clear();
		testAssignment.getQuestions().clear();
		Assert.assertEquals(0, testAssignment.getTotalMarks());
	}

	@Test
	public void testIsDeadlinePassedForFutureDeadline() {
		testAssignment.setDeadline(LocalDateTime.MAX);
		Assert.assertFalse(testAssignment.isDeadlinePassed());
	}

	@Test
	public void testIsDeadlinePassedForPassedDeadline() {
		testAssignment.setDeadline(LocalDateTime.MIN);
		Assert.assertTrue(testAssignment.isDeadlinePassed());
	}

	private boolean testValue(AssignmentQuestion elem, String expectedValue) {
		return ((StringQuestion) elem).getValue().equals(expectedValue);
	}

	private boolean testValue(AssignmentAnswer elem, String expectedValue) {
		return ((StringAssignmentAnswer) elem).getValue().equals(expectedValue);
	}
}
