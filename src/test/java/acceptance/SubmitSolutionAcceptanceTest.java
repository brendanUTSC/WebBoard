package acceptance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.athaydes.automaton.FXer;

import database.DatabaseTest;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class SubmitSolutionAcceptanceTest extends DatabaseTest {
	private FXer fxer;
	private static final String ASSIGNMENT_NAME = "ViewAssignmentMarksTest";
	private static final String DATE = "01/01/2020";

	@Before
	public void setUp() {
		fxer = AcceptanceTestUtils.openLoginScreen();
		AcceptanceTestUtils.login(fxer);
	}

	/**
	 * Tests to make sure that the user can login, click on an assignment and submit
	 * their answers
	 */
	@Test
	public void testSubmit() {
		AcceptanceTestUtils.addAssignment(fxer, ASSIGNMENT_NAME, DATE);

		fxer.clickOn(".clickable-label").pause(AcceptanceTestUtils.SHORT_DELAY);

		Button nextButton = (Button) fxer.getAt("#next-question-button");
		// Click through all the questions to get to the section where the user can
		// submit
		while (nextButton.isDisabled() == false) {
			fxer.clickOn("#next-question-button").waitForFxEvents();
			fxer.pause(AcceptanceTestUtils.SHORT_DELAY);
			nextButton = (Button) fxer.getAt("#next-question-button");
		}
		fxer.clickOn("#submit-button").pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:OK").pause(AcceptanceTestUtils.LONG_DELAY);
		Label headerLabel = (Label) fxer.getAt("#header-label");

		Assert.assertEquals("Assignment Results", headerLabel.getText());

		fxer.clickOn("text:View Assignments").pause(AcceptanceTestUtils.LONG_DELAY);
		AcceptanceTestUtils.deleteAssignment(fxer);
	}
}
