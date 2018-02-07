package database;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.Assignment;
import core.question.AssignmentQuestion;
import core.question.StringQuestion;
import utils.ApplicationDataDirectoryUtils;

public class QuestionRepositoryTest extends DatabaseTest {
	private Assignment assignment;
	private QuestionRepository questionRepository;
	private StringQuestion questionOne;
	private StringQuestion questionTwo;
	private StringQuestion questionThree;
	private AssignmentRepository assignmentRepository;

	@Before
	public void setUp() throws Exception {
		assignment = new Assignment();
		assignment.setName("Assignment Name");

		assignmentRepository = new AssignmentRepository();

		questionRepository = new QuestionRepository();
		questionOne = new StringQuestion("abc", 1);
		questionTwo = new StringQuestion("def", 3);
		questionThree = new StringQuestion("ghi", 5);
	}

	@Test
	public void testInsert() {
		assignment.addQuestion(questionOne);
		questionOne.saveToLocalMachine();
		int result = questionRepository.insert(questionOne);
		Assert.assertNotEquals(-1, result);
	}

	@Test
	public void testInsertMultipleQuestions() {
		assignment.addQuestion(questionOne);
		assignment.addQuestion(questionTwo);
		assignment.addQuestion(questionThree);

		questionOne.saveToLocalMachine();
		questionTwo.saveToLocalMachine();
		questionThree.saveToLocalMachine();

		int result = questionRepository.insert(questionOne);
		int resultTwo = questionRepository.insert(questionTwo);
		int resultThree = questionRepository.insert(questionThree);

		Assert.assertNotEquals(-1, result);
		Assert.assertNotEquals(-1, resultTwo);
		Assert.assertNotEquals(-1, resultThree);
	}

	@Test
	public void testGetQuestionsForAssignment() {
		assignment.setDeadline(LocalDateTime.now());
		ApplicationDataDirectoryUtils.createAssignment(assignment);
		assignment.saveToDatabase();

		assignment.addQuestion(questionOne);
		assignment.addQuestion(questionTwo);
		assignment.addQuestion(questionThree);

		questionOne.saveToLocalMachine();
		questionTwo.saveToLocalMachine();
		questionThree.saveToLocalMachine();

		questionRepository.insert(questionOne);
		questionRepository.insert(questionTwo);
		questionRepository.insert(questionThree);

		List<AssignmentQuestion> retrievedQuestions = questionRepository
				.getQuestionsForAssignment(assignment.getAssignmentId());

		Assert.assertEquals(0, retrievedQuestions.get(0).getIndex());

		Assert.assertEquals(questionOne.getValue(), ((StringQuestion) retrievedQuestions.get(0)).getValue());
		Assert.assertEquals(questionTwo.getValue(), ((StringQuestion) retrievedQuestions.get(1)).getValue());
		Assert.assertEquals(questionThree.getValue(),
				((StringQuestion) retrievedQuestions.get(2)).getValue());

		assignmentRepository.delete(assignment.getAssignmentId());
	}

	@Test
	public void testUpdate() {

		String updatedValue = "Update value";
		int updatedWeight = 2;

		Assignment assignment = new Assignment();
		assignment.setName("Assignment testUpdate");
		assignment.setDeadline(LocalDateTime.now());
		assignment.setAssignmentId(1099981);

		assignment.addQuestion(questionOne);
		assignment.addQuestion(questionTwo);
		assignment.addQuestion(questionThree);

		questionOne.saveToLocalMachine();
		questionTwo.saveToLocalMachine();
		questionThree.saveToLocalMachine();

		questionRepository.insert(questionOne);
		questionRepository.insert(questionTwo);
		questionRepository.insert(questionThree);

		questionOne.setValue(updatedValue);
		questionOne.setWeight(updatedWeight);
		questionOne.saveToLocalMachine();
		questionRepository.update(assignment.getAssignmentId(), 0, questionOne);

		List<AssignmentQuestion> retrievedAssignments = questionRepository
				.getQuestionsForAssignment(assignment.getAssignmentId());
		Assert.assertEquals(0, retrievedAssignments.get(0).getIndex());

		Assert.assertEquals(updatedValue, ((StringQuestion) retrievedAssignments.get(0)).getValue());
		Assert.assertEquals(updatedWeight, retrievedAssignments.get(0).getWeight());
	}

	@Test
	public void testDeleteAllQuestionsForAssignment() {
		ApplicationDataDirectoryUtils.createAssignment(assignment);
		assignment.saveToDatabase();

		assignment.addQuestion(questionOne);
		assignment.addQuestion(questionTwo);
		assignment.addQuestion(questionThree);

		questionOne.saveToLocalMachine();
		questionTwo.saveToLocalMachine();
		questionThree.saveToLocalMachine();

		questionRepository.insert(questionOne);
		questionRepository.insert(questionTwo);
		questionRepository.insert(questionThree);

		int questionsDeleted = questionRepository.deleteAllQuestionsForAssignment(assignment.getAssignmentId());
		Assert.assertEquals(3, questionsDeleted);

		List<AssignmentQuestion> retrievedQuestions = questionRepository
				.getQuestionsForAssignment(assignment.getAssignmentId());

		Assert.assertEquals(0, retrievedQuestions.size());

		assignmentRepository.delete(assignment.getAssignmentId());
	}
}
