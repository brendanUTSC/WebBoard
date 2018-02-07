package core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MultipleChoiceOptionsTest {
	MultipleChoiceOptions multipleChoiceOptions;
	private static final String OPTION_ONE = "Option 1";
	private static final String OPTION_TWO = "Option 2";

	@Before
	public void setUp() {
		List<String> options = new ArrayList<String>();
		options.add(OPTION_ONE);
		options.add(OPTION_TWO);
		multipleChoiceOptions = new MultipleChoiceOptions(options);
	}

	@Test
	public void testGetOptions() {
		Assert.assertEquals(OPTION_ONE, multipleChoiceOptions.getOptions().get(0));
		Assert.assertEquals(OPTION_TWO, multipleChoiceOptions.getOptions().get(1));
	}
}
