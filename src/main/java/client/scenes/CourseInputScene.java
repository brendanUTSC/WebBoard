package client.scenes;

import java.util.logging.Logger;

import controls.ConfirmationAlert;
import controls.CustomToolBar;
import controls.ImageButton;
import core.User;
import database.CourseRepository;
import database.UserCourseRepository;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ResourceUtils;

public class CourseInputScene {
	private static Logger logger = Logger.getLogger(CourseInputScene.class.getName());
	private static final String HEADER = "Add a Course";
	private static final String COURSE_SUBMITTED = "Success!\n" + "Your course has been added!";
	public static int LABEL_ALIGNMENT_WIDTH = 80;
	private Label courseNameLabel, courseIdLabel;
	private TextField courseNameTextField, courseIdTextField;
	private BorderPane borderPane;
	private ImageButton submitButton;
	private Scene scene;

	public CourseInputScene(Stage stage, User user) {
		Label headerLabel = new Label(HEADER);
		headerLabel.getStyleClass().add("header");

		Region fillerRegion = new Region();
		HBox.setHgrow(fillerRegion, Priority.ALWAYS);
		

		submitButton = new ImageButton("Submit", "submit.png");
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String courseId = courseIdTextField.getText();
				String courseName = courseNameTextField.getText();
				CourseRepository courseRepository = new CourseRepository();
				logger.info("Adding course with CourseId: " + courseId + ", CourseName: " + courseName);
				courseRepository.insert(courseId, courseName);
				UserCourseRepository userCourseRepository = new UserCourseRepository();
				userCourseRepository.insert(user.getId(), courseId);
				ConfirmationAlert.display(COURSE_SUBMITTED);
			}
		});
		
		HBox headerBox = new HBox(10);
		headerBox.getChildren().addAll(headerLabel, fillerRegion, submitButton.getButton());
		
		borderPane = new BorderPane();

		courseIdLabel = new Label("Course Id:");
		courseIdLabel.setMinWidth(LABEL_ALIGNMENT_WIDTH);
		courseIdTextField = new TextField();

		courseNameLabel = new Label("Course name:");
		courseNameLabel.setMinWidth(LABEL_ALIGNMENT_WIDTH);
		courseNameTextField = new TextField();

		HBox courseIdHBox = new HBox(10);
		courseIdHBox.setAlignment(Pos.CENTER_LEFT);
		courseIdHBox.getChildren().addAll(courseIdLabel, courseIdTextField);

		HBox courseNameHBox = new HBox(10);
		courseNameHBox.setAlignment(Pos.CENTER_LEFT);
		courseNameHBox.getChildren().addAll(courseNameLabel, courseNameTextField);
		
		HBox.setHgrow(courseIdTextField, Priority.ALWAYS);
		HBox.setHgrow(courseNameTextField, Priority.ALWAYS);

		VBox mainContentVBox = new VBox(10);
		mainContentVBox.getChildren().addAll(headerBox, courseIdHBox, courseNameHBox);
		mainContentVBox.setPadding(new Insets(10, 10, 0, 10));
		borderPane.setCenter(mainContentVBox);

		ToolBar toolBar = new CustomToolBar(user, stage).removeAddAnAssignment().removeAddCourse().removeViewMarks()
				.removeViewStudents().removeLogOut().removeAddStudent().getToolBar();
		borderPane.setTop(toolBar);

		initializeNodeAttributes();

		scene = new Scene(borderPane, 350, 200);
		getScene().getStylesheets().add(ResourceUtils.PRIMARY_STYLESHEET);
	}

	/**
	 * Sets the id property and style classes for the Nodes in this scene.
	 */
	private void initializeNodeAttributes() {
		courseIdTextField.setId("course-id-textfield");

		courseNameTextField.setId("course-name-textfield");

		submitButton.getButton().getStyleClass().add("submit-button");
		submitButton.getButton().setId("submit-button");
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
