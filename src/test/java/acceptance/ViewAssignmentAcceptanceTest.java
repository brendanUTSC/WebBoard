package acceptance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.athaydes.automaton.FXer;

import database.DatabaseTest;
import database.UserCourseRepository;
import javafx.scene.control.Label;

public class ViewAssignmentAcceptanceTest extends DatabaseTest {
	public static final String ASSIGNMENT_NAME = "Test Assignment 2";
	private FXer fxer;

	@Before
	public void setUp() {
		fxer = AcceptanceTestUtils.openLoginScreen();
	}

	@Test
	public void acceptanceTest() {
		testAssignmentIsVisibleToProfessor();
		testAssignmentIsVisibleToStudent();
		removeAssignment();
	}

	private void testAssignmentIsVisibleToProfessor() {
		String date = "01/01/2020";
		AcceptanceTestUtils.login(fxer);

		AcceptanceTestUtils.addAssignment(fxer, ASSIGNMENT_NAME, date);

		Label label = (Label) fxer.getAt("text:\t" + ASSIGNMENT_NAME);
		Assert.assertNotNull(label);
		fxer.clickOn("text:Log Out").pause(AcceptanceTestUtils.LONG_DELAY);
	}

	public void testAssignmentIsVisibleToStudent() {
		UserCourseRepository repo = new UserCourseRepository();
		repo.insert(2, "CSCC01");
		fxer = AcceptanceTestUtils.openLoginScreen();
		AcceptanceTestUtils.studentLogin(fxer);

		Label label = (Label) fxer.getAt("text:\t" + ASSIGNMENT_NAME);
		Assert.assertNotNull(label);
		fxer.clickOn("text:Log Out").pause(AcceptanceTestUtils.LONG_DELAY);
	}

	private void removeAssignment() {
		fxer = AcceptanceTestUtils.openLoginScreen();
		AcceptanceTestUtils.login(fxer);
		AcceptanceTestUtils.deleteAssignment(fxer);
	}
}
