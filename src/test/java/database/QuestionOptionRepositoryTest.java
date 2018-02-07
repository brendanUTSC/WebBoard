package database;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.Assignment;
import core.MultipleChoiceOptions;
import core.question.MultipleChoiceQuestion;

public class QuestionOptionRepositoryTest extends DatabaseTest {
	private static final String OPTION_ONE = "Option 1";

	private Assignment assignment;
	private QuestionOptionRepository questionOptionRepository;
	private MultipleChoiceQuestion question;
	private MultipleChoiceOptions multipleChoiceOptions;

	@Before
	public void setUp() {
		assignment = new Assignment();
		assignment.setName("Assignment Name");

		questionOptionRepository = new QuestionOptionRepository();
		multipleChoiceOptions = new MultipleChoiceOptions(new ArrayList<String>());
		multipleChoiceOptions.getOptions().add(OPTION_ONE);
		question = new MultipleChoiceQuestion("Question", multipleChoiceOptions, 1);
		assignment.addQuestion(question);
	}

	@Test
	public void testInsert() {
		question.saveToLocalMachine();
		questionOptionRepository.insert(1, multipleChoiceOptions.getOptionsFilePaths());
		Assert.assertEquals(OPTION_ONE, questionOptionRepository.getOptionsForQuestion(1).getOptions().get(0));
	}

	@Test
	public void testUpdate() {
		question.saveToLocalMachine();
		questionOptionRepository.insert(1, multipleChoiceOptions.getOptionsFilePaths());
		Assert.assertEquals(OPTION_ONE, questionOptionRepository.getOptionsForQuestion(1).getOptions().get(0));

		String updatedValue = "Updated Option";
		multipleChoiceOptions.getOptions().set(0, updatedValue);
		question.saveToLocalMachine();

		questionOptionRepository.update(1, multipleChoiceOptions.getOptionsFilePaths());
		Assert.assertEquals(updatedValue, questionOptionRepository.getOptionsForQuestion(1).getOptions().get(0));
	}

	@After
	public void tearDown() {
		questionOptionRepository.destroy();
	}
}
