package database;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import core.Assignment;
import core.MultipleChoiceOptions;
import core.question.MultipleChoiceQuestion;

public class OptionRepositoryTest extends DatabaseTest {
	private QuestionOptionRepository optionRepository;
	private MultipleChoiceQuestion multipleChoiceQuestion;
	private MultipleChoiceOptions multipleChoiceOptions;
	private static final String OPTION_ONE = "Option 1";
	private static final String OPTION_TWO = "Option 2";
	
	@Before
	public void setUp() {
		optionRepository = new QuestionOptionRepository();
		List<String> options = new ArrayList<String>();
		options.add(OPTION_ONE);
		options.add(OPTION_TWO);
		multipleChoiceOptions = new MultipleChoiceOptions(options);
		
		multipleChoiceQuestion = new MultipleChoiceQuestion("Question", options, 1);
		Assignment assignment = new Assignment();
		assignment.setName(this.getClass().getSimpleName());
		multipleChoiceQuestion.setAssignment(assignment);
	}
	
	@Test
	public void testInsert() {
		multipleChoiceQuestion.saveToLocalMachine();
		optionRepository.insert(1, multipleChoiceOptions.getOptionsFilePaths());
	}
}
