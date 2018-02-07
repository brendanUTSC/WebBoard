package controls;

import client.scenes.AssignmentInputScene;
import client.scenes.AssignmentSelectionScene;
import client.scenes.CourseInputScene;
import client.scenes.StudentHighScoresScene;
import client.scenes.UserCourseScene;
import client.scenes.UserInputScene;
import client.scenes.UserLoginScene;
import client.scenes.ViewMarksScene;
import core.User;
import core.User.Privilege;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;

/**
 * A customized ToolBar to be used throughout the application. The default
 * constructor initializes with a complete ToolBar. This class features numerous
 * methods to remove the buttons that the client does not wish to use and
 * exposes a method to retrieve the underlying ToolBar associated with the
 * CustomToolBar.
 * 
 * @author Brendan Zhang
 * @see ToolBar
 */
public class CustomToolBar {
	/**
	 * Note: For extending this class (adding a button to the CustomToolBar), it is
	 * necessary to define a new Button, initialize it (and it's behavior when
	 * clicked) in the default constructor, add the new Button into
	 * {@link #addItems(User)}, and then supplement the class with a remove function
	 * for removing the Button from the ToolBar.
	 */
	private ToolBar toolBar;
	private Button viewStudentsButton, viewMarksButton, addCourseButton, viewAssignmentsButton, addAnAssignmentButton,
			logOutButton, addStudentButton, viewStudentHighScoresButton;

	public CustomToolBar(User user, Stage stage) {
		viewStudentsButton = new Button("View Students");
		viewStudentsButton.setVisible(user.getPrivilegeLevel() == Privilege.PROFESSOR);
		viewStudentsButton.setOnAction(e -> {
			stage.setScene(new UserCourseScene(stage, user).getScene());
		});

		viewMarksButton = new Button("View Marks");
		viewMarksButton.setOnAction(e -> {
			stage.setScene(new ViewMarksScene(stage, user).getScene());
		});

		addAnAssignmentButton = new Button("Add an Assignment");
		addAnAssignmentButton.setVisible(user.getPrivilegeLevel() == Privilege.PROFESSOR);
		addAnAssignmentButton.setOnAction(e -> {
			stage.setScene(new AssignmentInputScene(stage, user, true).getScene());
		});

		addCourseButton = new Button("Add a Course");
		addCourseButton.setVisible(user.getPrivilegeLevel() == Privilege.PROFESSOR);
		addCourseButton.setOnAction(e -> {
			stage.setScene(new CourseInputScene(stage, user).getScene());
		});

		viewAssignmentsButton = new Button("View Assignments");
		viewAssignmentsButton.setMaxWidth(Double.MAX_VALUE);

		viewAssignmentsButton.setOnAction(e -> {
			stage.setScene(new AssignmentSelectionScene(stage, user).getScene());
		});

		logOutButton = new Button("Log Out");
		logOutButton.setOnAction(e -> {
			stage.setScene(new UserLoginScene(stage).getScene());
		});

		addStudentButton = new Button("Add a Student");
		addStudentButton.setOnAction(e -> {
			stage.setScene(new UserInputScene(stage, user).getScene());
		});

		viewStudentHighScoresButton = new Button("View High Score");
		viewStudentHighScoresButton.setOnAction(e -> {
			stage.setScene(new StudentHighScoresScene(stage, user).getScene());
		});

		toolBar = new ToolBar();
		addItems(user);
	}

	private void addItems(User user) {
		if (user.getPrivilegeLevel() == Privilege.PROFESSOR) {
			// Complete ToolBar for Professors
			toolBar.getItems().addAll(viewAssignmentsButton, viewMarksButton, viewStudentsButton, addAnAssignmentButton,
					addCourseButton, addStudentButton, logOutButton);
		} else {
			// Complete ToolBar for Students
			toolBar.getItems().addAll(viewAssignmentsButton, viewStudentHighScoresButton, logOutButton);
		}
	}

	public CustomToolBar viewMarksButton() {
		toolBar.getItems().remove(viewMarksButton);
		return this;
	}

	public CustomToolBar removeAddStudent() {
		toolBar.getItems().remove(addStudentButton);
		return this;
	}

	public CustomToolBar removeViewAssignments() {
		toolBar.getItems().remove(viewAssignmentsButton);
		return this;
	}

	public CustomToolBar removeViewStudents() {
		toolBar.getItems().remove(viewStudentsButton);
		return this;
	}

	public CustomToolBar removeViewMarks() {
		toolBar.getItems().remove(viewMarksButton);
		return this;
	}

	public CustomToolBar removeAddCourse() {
		toolBar.getItems().remove(addCourseButton);
		return this;
	}

	public CustomToolBar removeAddAnAssignment() {
		toolBar.getItems().remove(addAnAssignmentButton);
		return this;
	}

	public CustomToolBar removeLogOut() {
		toolBar.getItems().remove(logOutButton);
		return this;
	}

	public ToolBar getToolBar() {
		return toolBar;
	}
}
