package database;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.Assignment;
import core.AssignmentElement.Format;
import core.answer.AssignmentAnswer;
import core.answer.StringAssignmentAnswer;

public class AnswerRepositoryTest extends DatabaseTest {
	private AnswerRepository answerRepository;
	private StringAssignmentAnswer answerOne;
	private StringAssignmentAnswer answerTwo;
	private StringAssignmentAnswer answerThree;

	@Before
	public void setUp() throws Exception {
		answerRepository = new AnswerRepository();

		answerOne = new StringAssignmentAnswer("abc", 20);
		answerTwo = new StringAssignmentAnswer("def", 25);
		answerThree = new StringAssignmentAnswer("ghi", 30);
	}

	@Test
	public void testInsert() {
		Assignment assignment = new Assignment();
		assignment.setName("Assignment testUpdate");
		assignment.setAssignmentId(1);
		assignment.addAnswer(answerOne);
		answerOne.saveToLocalMachine();
		int result = answerRepository.insert(answerOne);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testInsertMultipleAnswers() {
		Assignment assignment = new Assignment();
		assignment.setName("Assignment testInsertMultipleQuestions");
		assignment.setAssignmentId(1);

		assignment.addAnswer(answerOne);
		assignment.addAnswer(answerTwo);
		assignment.addAnswer(answerThree);

		answerOne.saveToLocalMachine();
		answerTwo.saveToLocalMachine();
		answerThree.saveToLocalMachine();

		int result = answerRepository.insert(answerOne);
		int resultTwo = answerRepository.insert(answerTwo);
		int resultThree = answerRepository.insert(answerThree);

		Assert.assertEquals(1, result);
		Assert.assertEquals(1, resultTwo);
		Assert.assertEquals(1, resultThree);
	}

	@Test
	public void testGetAnswersForAssignment() {
		Assignment assignment = new Assignment();
		assignment.setName("Assignment testGetAnswersForAssignment");
		assignment.setDeadline(LocalDateTime.now());
		assignment.setAssignmentId(12345);

		assignment.addAnswer(answerOne);
		assignment.addAnswer(answerTwo);
		assignment.addAnswer(answerThree);

		answerOne.saveToLocalMachine();
		answerTwo.saveToLocalMachine();
		answerThree.saveToLocalMachine();

		answerRepository.insert(answerOne);
		answerRepository.insert(answerTwo);
		answerRepository.insert(answerThree);

		List<AssignmentAnswer> retrievedAssignments = answerRepository
				.getAnswersForAssignment(assignment.getAssignmentId());

		Assert.assertEquals(Format.STRING, retrievedAssignments.get(0).getFormat());
		Assert.assertEquals(answerOne.getValue(), ((StringAssignmentAnswer) retrievedAssignments.get(0)).getValue());
		Assert.assertEquals(answerTwo.getValue(), ((StringAssignmentAnswer) retrievedAssignments.get(1)).getValue());
		Assert.assertEquals(answerThree.getValue(), ((StringAssignmentAnswer) retrievedAssignments.get(2)).getValue());
	}

	@Test
	public void testUpdate() {
		String updatedValue = "Updated Value";

		Assignment assignment = new Assignment();
		assignment.setName("Assignment testUpdate");
		assignment.setDeadline(LocalDateTime.now());

		assignment.addAnswer(answerOne);
		assignment.addAnswer(answerTwo);
		assignment.addAnswer(answerThree);

		answerOne.saveToLocalMachine();
		answerTwo.saveToLocalMachine();
		answerThree.saveToLocalMachine();

		answerRepository.insert(answerOne);
		answerRepository.insert(answerTwo);
		answerRepository.insert(answerThree);

		answerOne.setValue(updatedValue);
		answerOne.saveToLocalMachine();
		answerRepository.update(assignment.getAssignmentId(), 0, answerOne);

		List<AssignmentAnswer> retrievedAssignments = answerRepository
				.getAnswersForAssignment(assignment.getAssignmentId());
		Assert.assertEquals(0, retrievedAssignments.get(0).getIndex());
		Assert.assertEquals(updatedValue, ((StringAssignmentAnswer) retrievedAssignments.get(0)).getValue());
	}
}
