package core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.question.MultipleChoiceQuestion;
import utils.FileReader;

public class MultipleChoiceQuestionTest {
	MultipleChoiceQuestion multipleChoiceQuestion;
	private static final String QUESTION = "abc";
	private static final String OPTION_ONE = "Option 1";
	private static final String OPTION_TWO = "Option 2";
	
	@Before
	public void setUp() {
		List<String> options = new ArrayList<String>();
		options.add(OPTION_ONE);
		options.add(OPTION_TWO);
		multipleChoiceQuestion = new MultipleChoiceQuestion(QUESTION, options, 1);
		Assignment assignment = new Assignment();
		assignment.setName(this.getClass().getSimpleName());
		multipleChoiceQuestion.setAssignment(assignment);
	}
	
	@Test
	public void testSaveToLocalMachine() {
		multipleChoiceQuestion.saveToLocalMachine();
		
		Assert.assertEquals(QUESTION, FileReader.readFile(multipleChoiceQuestion.getFilePath()));
		Assert.assertEquals(OPTION_ONE, FileReader.readFile(multipleChoiceQuestion.getMultipleChoiceOptions().getOptionsFilePaths().get(0)));
		Assert.assertEquals(OPTION_TWO, FileReader.readFile(multipleChoiceQuestion.getMultipleChoiceOptions().getOptionsFilePaths().get(1)));
	}
	
}
