package acceptance;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.athaydes.automaton.FXer;

import core.User;
import database.CourseRepository;
import database.DatabaseTest;
import database.UserCourseRepository;
import javafx.scene.control.TableView;

public class AddStudentToCourseAcceptanceTest extends DatabaseTest {
	private FXer fxer;
	private static final String FIRST_NAME = "Student";

	@Before
	public void setUp() {
		fxer = AcceptanceTestUtils.openLoginScreen();
		AcceptanceTestUtils.login(fxer);
		UserCourseRepository repo = new UserCourseRepository();
		repo.destroy();
		CourseRepository courseRepo = new CourseRepository();
		courseRepo.destroy();
	}

	@Test
	public void testAddAndRemove() {
		testAddStudentToCourse();
		testRemoveStudentFromCourse();
	}

	@SuppressWarnings("unchecked")
	private void testAddStudentToCourse() {
		String courseId = "CSCC01";

		fxer.clickOn("text:Add a Course").pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("#course-id-textfield").type(courseId).waitForFxEvents();
		fxer.clickOn("#course-name-textfield").type("Software Engineering").waitForFxEvents();
		fxer.clickOn("#submit-button").pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("text:OK").pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:View Assignments").pause(AcceptanceTestUtils.LONG_DELAY);

		UserCourseRepository repo = new UserCourseRepository();
		repo.insert(2, courseId);

		fxer.clickOn("text:View Students").pause(AcceptanceTestUtils.LONG_DELAY);
		TableView<User> enrolledStudents = (TableView<User>) fxer.getAt("#enrolled-students-table");
		Assert.assertTrue(enrolledStudents.getItems().get(0).getFirstName().equals(FIRST_NAME));
	}

	@SuppressWarnings("unchecked")
	private void testRemoveStudentFromCourse() {

		fxer.clickOn("text:" + FIRST_NAME).waitForFxEvents();
		fxer.clickOn("text:" + "Remove").waitForFxEvents().pause(AcceptanceTestUtils.SHORT_DELAY);

		TableView<User> unenrolledStudents = (TableView<User>) fxer.getAt("#unenrolled-students-table");
		Assert.assertEquals(FIRST_NAME, unenrolledStudents.getItems().get(0).getFirstName());
		Assert.assertEquals(1, unenrolledStudents.getItems().size());
	}

	@After
	public void tearDown() {
		UserCourseRepository repo = new UserCourseRepository();
		repo.destroy();
		CourseRepository courseRepo = new CourseRepository();
		courseRepo.destroy();
	}
}
