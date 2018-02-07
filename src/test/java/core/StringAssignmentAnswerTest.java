package core;

import org.junit.Assert;
import org.junit.Test;

import core.answer.StringAssignmentAnswer;
import utils.FileReader;

public class StringAssignmentAnswerTest {
	private StringAssignmentAnswer assignmentElement;

	@Test
	public void testGetCleanValueWithPrefix() {
		assignmentElement = new StringAssignmentAnswer("Answer: x^2", 5);
		Assert.assertEquals("x^2", assignmentElement.getCleanValue());
	}

	@Test
	public void testGetCleanValueWithoutPrefix() {
		assignmentElement = new StringAssignmentAnswer("x^2", 5);
		Assert.assertEquals("x^2", assignmentElement.getCleanValue());
	}

	@Test
	public void testSaveToLocalMachine() {
		String answer = "Answer: x^2";
		Assignment assignment = new Assignment();
		assignment.setName(this.getClass().getSimpleName());

		assignmentElement = new StringAssignmentAnswer(answer, 5);
		assignmentElement.setAssignment(assignment);

		assignmentElement.saveToLocalMachine();
		Assert.assertEquals(answer, FileReader.readFile(assignmentElement.getFilePath()));
	}
}
