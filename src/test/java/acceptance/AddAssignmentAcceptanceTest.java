package acceptance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.athaydes.automaton.FXer;

import database.DatabaseTest;
import javafx.scene.control.Label;

public class AddAssignmentAcceptanceTest extends DatabaseTest {
	public static final String ASSIGNMENT_NAME = "TestAssignment";
	private FXer fxer;

	@Before
	public void setUp() {
		fxer = AcceptanceTestUtils.openLoginScreen();
		AcceptanceTestUtils.login(fxer);
	}

	@Test
	public void testViewAssignmentsButtonDirectsToNewScene() {
		fxer.clickOn("text:Add an Assignment").waitForFxEvents();
		fxer.pause(AcceptanceTestUtils.LONG_DELAY);
		Label headerLabel = (Label) fxer.getAt("#header-label");
		Assert.assertEquals("Create an Assignment", headerLabel.getText());
	}

	@Test
	public void testAddingAssignment() {
		String date = "01/01/2020";

		AcceptanceTestUtils.addAssignment(fxer, ASSIGNMENT_NAME, date);

		Label label = (Label) fxer.getAt(".clickable-label");
		Assert.assertNotNull(label);
		Assert.assertTrue(label.getText().contains(ASSIGNMENT_NAME));

		AcceptanceTestUtils.deleteAssignment(fxer);
	}
}
