package acceptance;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.athaydes.automaton.FXer;

import database.DatabaseTest;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/*
 * This test checks if a professor can edit an assignment.
 * It assumes an assignment has already been created, and edits it.
 * It edits the assignment name and deadline.
 */
public class EditAssignmentAcceptanceTest extends DatabaseTest {
	private FXer fxer;
	private static final String ASSIGNMENT_NAME = "TestEditting";
	private static final String DATE = "2020-01-01";
	private static final String EDITED_ASSIGNMENT_NAME = "EdittedAssignment";
	private static final String EDITED_DATE = "2020-02-02";

	@Before
	public void setUp() {
		fxer = AcceptanceTestUtils.openLoginScreen();
		AcceptanceTestUtils.login(fxer);
	}

	@Test
	public void acceptanceTest() {
		AcceptanceTestUtils.addAssignment(fxer, ASSIGNMENT_NAME, DATE);
		testEditAssignmentButtonDirectsToNewScene();
		testEditingAssignment();
		testAssignmentsWereEdited();
		AcceptanceTestUtils.deleteAssignment(fxer);
	}

	public void testEditAssignmentButtonDirectsToNewScene() {
		fxer.clickOn("text:Edit").waitForFxEvents();
		fxer.pause(AcceptanceTestUtils.LONG_DELAY);
		Label headerLabel = (Label) fxer.getAt("#header-label");
		Assert.assertEquals("Edit an Assignment", headerLabel.getText());
		fxer.pause(AcceptanceTestUtils.LONG_DELAY);
	}

	public void testEditingAssignment() {
		fxer.doubleClickOn("#assignment-name-textfield").pause(AcceptanceTestUtils.SHORT_DELAY)
				.type(EDITED_ASSIGNMENT_NAME).waitForFxEvents();
		DatePicker datePicker = (DatePicker) fxer.getAt("#assignment-deadline-datepicker");
		datePicker.setValue(LocalDate.parse(EDITED_DATE));
		fxer.clickOn("#submit-button").pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:OK").pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:View Assignments").pause(AcceptanceTestUtils.LONG_DELAY);
	}

	public void testAssignmentsWereEdited() {
		fxer.clickOn("text:Edit").waitForFxEvents();
		fxer.pause(AcceptanceTestUtils.LONG_DELAY);
		TextField nameTextField = (TextField) fxer.getAt("#assignment-name-textfield");
		DatePicker datePicker = (DatePicker) fxer.getAt("#assignment-deadline-datepicker");
		Assert.assertTrue(nameTextField.getText().contains(EDITED_ASSIGNMENT_NAME));
		Assert.assertEquals(EDITED_DATE, datePicker.getValue().toString());
		fxer.clickOn("text:View Assignments").pause(AcceptanceTestUtils.LONG_DELAY);
	}

}
