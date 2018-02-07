package acceptance;

import com.athaydes.automaton.FXApp;
import com.athaydes.automaton.FXer;

import client.scenes.UserLoginScene;
import javafx.stage.Stage;

public class AcceptanceTestUtils {
	public static final String PROFESSOR_USERNAME = "user";
	public static final String STUDENT_USERNAME = "student";
	public static final String PASSWORD = "password";
	public static final int VERY_SHORT_DELAY = 250;
	public static final int SHORT_DELAY = 1000;
	public static final int LONG_DELAY = 5000;

	public static FXer openLoginScreen() {
		Stage stage = FXApp.initialize();
		FXApp.doInFXThreadBlocking(new Runnable() {
			public void run() {
				stage.setScene(new UserLoginScene(stage).getScene());
			}
		});
		return FXer.getUserWith(FXApp.getScene().getRoot());
	}

	public static void login(FXer fxer) {
		fxer.clickOn("#username-box").waitForFxEvents().type(PROFESSOR_USERNAME).waitForFxEvents();
		fxer.pause(VERY_SHORT_DELAY);
		fxer.clickOn("#password-box").waitForFxEvents().type(PASSWORD).waitForFxEvents();
		fxer.clickOn("#login-button").waitForFxEvents();
		fxer.pause(LONG_DELAY);
	}

	public static void studentLogin(FXer fxer) {
		fxer.clickOn("#username-box").type(STUDENT_USERNAME).waitForFxEvents();
		fxer.clickOn("#password-box").type(PASSWORD).waitForFxEvents();
		fxer.clickOn("#login-button").waitForFxEvents();
		fxer.pause(LONG_DELAY);
	}

	/**
	 * Adds an assignment with the specified assignment name and deadline date.
	 * Then, returns to the View Assignments menu.
	 * 
	 * @param fxer
	 *            FXer object to use
	 * @param assignmentName
	 *            Name of assignment
	 * @param date
	 *            Deadline date of assignment
	 */
	public static void addAssignment(FXer fxer, String assignmentName, String date) {
		addAssignment(fxer, assignmentName, date, null);
	}

	public static void addAssignment(FXer fxer, String assignmentName, String date, Integer weight) {
		// Add course
		fxer.clickOn("text:Add a Course").pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("#course-id-textfield").type("CSCC01").waitForFxEvents();
		fxer.clickOn("#course-name-textfield").type("Software Engineering").waitForFxEvents();
		fxer.clickOn("#submit-button").pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("text:OK").pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:View Assignments").pause(AcceptanceTestUtils.LONG_DELAY);

		fxer.clickOn("text:Add an Assignment").pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("#assignment-name-textfield").type(assignmentName).pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("#assignment-deadline-datepicker").type(date).pause(AcceptanceTestUtils.SHORT_DELAY);

		if (weight != null) {
			fxer.doubleClickOn("#assignment-weight-textfield").pause(AcceptanceTestUtils.SHORT_DELAY)
					.type(String.valueOf(weight)).waitForFxEvents();
		}
		fxer.clickOn("#submit-button").waitForFxEvents().pause(AcceptanceTestUtils.LONG_DELAY);
		fxer.clickOn("text:OK").pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:View Assignments").pause(AcceptanceTestUtils.LONG_DELAY);
	}

	public static void deleteAssignment(FXer fxer) {
		fxer.clickOn("text:Delete").pause(AcceptanceTestUtils.SHORT_DELAY);
		fxer.clickOn("text:OK").pause(AcceptanceTestUtils.SHORT_DELAY);
	}
}
