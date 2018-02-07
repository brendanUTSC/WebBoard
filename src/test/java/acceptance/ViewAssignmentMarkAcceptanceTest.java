package acceptance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.athaydes.automaton.FXer;

import database.DatabaseTest;
import javafx.scene.control.Label;

/* 
 * This class tests that after a student submits an assignment, his mark is displayed.
 * 
 * For this test to work, the first assignment which appears when starting up the app as a student
 * should have a deadline which has not passed.
 * 
 */
public class ViewAssignmentMarkAcceptanceTest extends DatabaseTest {
	private FXer fxer;
	private static final String ASSIGNMENT_NAME = "ViewAssignmentMarksTest";
	private static final String DATE = "01/01/2020";

	@Before
	public void setUp() {
		fxer = AcceptanceTestUtils.openLoginScreen();
		AcceptanceTestUtils.login(fxer);
	}

	/*
	 * Test that when the student submits an assignment, the assignment results
	 * scene is displayed.
	 */
	@Test
	public void testSubmitAssignmentButtonDirectsToNewScene() {
		AcceptanceTestUtils.addAssignment(fxer, ASSIGNMENT_NAME, DATE);

		Label label = (Label) fxer.getAt(".clickable-label");
		Assert.assertNotNull(label);
		Assert.assertTrue(label.getText().contains(ASSIGNMENT_NAME));

		fxer.clickOn(".clickable-label").pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("#submit-button").pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("text:OK").pause(AcceptanceTestUtils.SHORT_DELAY);

		Label headerLabel = (Label) fxer.getAt("#header-label");
		fxer.pause(AcceptanceTestUtils.SHORT_DELAY);
		Assert.assertEquals("Assignment Results", headerLabel.getText());
		fxer.pause(AcceptanceTestUtils.SHORT_DELAY);

		Label scoreLabel = (Label) fxer.getAt("#score-label");
		fxer.pause(AcceptanceTestUtils.SHORT_DELAY);
		Assert.assertNotNull(scoreLabel);
		fxer.pause(AcceptanceTestUtils.SHORT_DELAY);
		Assert.assertTrue(scoreLabel.getText().contains("Your score on this assignment:"));
		fxer.pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:View Assignments").pause(AcceptanceTestUtils.LONG_DELAY);

		AcceptanceTestUtils.deleteAssignment(fxer);
	}

	/*
	 * Test that the AssignmentResultScene displays the student's mark. Tested by
	 * checking if the label "Your score on this assignment:" exists. The label
	 * appends "Your score on this assignment" with variables score / maxScore.
	 */
	@Test
	public void testSubmitAssignmentButtonDisplaysMark() {
		AcceptanceTestUtils.addAssignment(fxer, ASSIGNMENT_NAME, DATE);

		fxer.clickOn(".clickable-label").pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("#submit-button").pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:OK").pause(AcceptanceTestUtils.SHORT_DELAY);

		Label scoreLabel = (Label) fxer.getAt("#score-label");
		fxer.pause(AcceptanceTestUtils.SHORT_DELAY);
		Assert.assertNotNull(scoreLabel);
		fxer.pause(AcceptanceTestUtils.SHORT_DELAY);
		Assert.assertTrue(scoreLabel.getText().equals("Your score on this assignment: 1/1"));
		fxer.pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:View Assignments").pause(AcceptanceTestUtils.LONG_DELAY);

		AcceptanceTestUtils.deleteAssignment(fxer);
	}
}
