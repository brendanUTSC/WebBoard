package core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.answer.MultipleChoiceAnswer;
import utils.FileReader;

public class MultipleChoiceAnswerTest {
	MultipleChoiceAnswer multipleChoiceAnswer;
	private static final int ANSWER = 0;
	
	@Before
	public void setUp() {
		multipleChoiceAnswer = new MultipleChoiceAnswer(ANSWER);
		Assignment assignment = new Assignment();
		assignment.setName(this.getClass().getSimpleName());
		multipleChoiceAnswer.setAssignment(assignment);
	}
	
	@Test
	public void testSaveToLocalMachine() {
		multipleChoiceAnswer.saveToLocalMachine();
		
		Assert.assertEquals(String.valueOf(ANSWER), FileReader.readFile(multipleChoiceAnswer.getFilePath()));
	}
}
