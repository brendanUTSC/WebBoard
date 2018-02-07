package client.scenes;

import java.util.ArrayList;
import java.util.List;

import controls.ConfirmationAlert;
import controls.CustomToolBar;
import core.Assignment;
import core.Submission;
import core.User;
import core.question.AssignmentQuestion;
import core.studentsolution.StudentSolution;
import database.StudentSolutionRepository;
import database.SubmissionRepository;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ResourceUtils;

/**
 * A scene for displaying an assignment for a user.
 * 
 * @author Brendan Zhang
 *
 */
public class AssignmentOutputScene {
	private static final String ASSIGNMENT_SUBMITTED_TEXT = "Success!\n" + "Your assignment has been submitted!";
	// Button width for buttons on left sidebar
	private static int PREFERRED_BUTTON_WIDTH = 150;
	private Scene scene;
	private int questionIndex;
	private Assignment assignment;
	private Node questionNode;
	private BorderPane borderPane;
	private List<StudentSolution> studentSolutions;
	private Label assignmentNameLabel;

	public AssignmentOutputScene(Stage stage, int assignmentId, User user) {

		borderPane = new BorderPane();
		studentSolutions = new ArrayList<StudentSolution>();

		Button previousQuestionButton = new Button("Previous Question");
		Button nextQuestionButton = new Button("Next Question");

		previousQuestionButton.setOnAction(e -> {
			if (questionIndex > 0) {
				questionIndex--;
				displayQuestion(user.getId(), assignment.getQuestions().get(questionIndex));
				nextQuestionButton.setDisable(false);
			}
		});

		assignmentNameLabel = new Label();
		assignmentNameLabel.setId("assignment-name-label");

		Button submitButton = new Button("Submit Assignment");
		submitButton.setId("submit-button");
		submitButton.setOnAction(e -> {
			int studentMark = 0;
			int maxMark = 0;
			StudentSolutionRepository studentSolutionRepository = new StudentSolutionRepository();
			for (StudentSolution solution : studentSolutions) {
				solution.calculateMark();
				studentSolutionRepository.insert(solution);
				studentMark += solution.getMark();
				maxMark += solution.getMaximumMark();
			}
			Submission submission = new Submission(user.getId(), assignmentId, studentMark);
			SubmissionRepository submissionRepository = new SubmissionRepository();
			submissionRepository.insert(submission);
			ConfirmationAlert.display(ASSIGNMENT_SUBMITTED_TEXT);
			stage.setScene(new AssignmentResultScene(stage, user, assignmentId, studentMark, maxMark).getScene());
		});

		nextQuestionButton.setOnAction(e -> {
			// Add empty StudentSolution if necessary
			if (questionIndex < assignment.getQuestions().size() - 1) {
				questionIndex++;
				displayQuestion(user.getId(), assignment.getQuestions().get(questionIndex));
				submitButton.setVisible(questionIndex == assignment.getQuestions().size() - 1);
				nextQuestionButton.setDisable(questionIndex == assignment.getQuestions().size() - 1);
			}
		});

		VBox vBox = new VBox(10);
		vBox.getChildren().addAll(assignmentNameLabel, previousQuestionButton, nextQuestionButton);
		previousQuestionButton.setPrefWidth(PREFERRED_BUTTON_WIDTH);
		previousQuestionButton.setMaxWidth(Double.MAX_VALUE);
		nextQuestionButton.setPrefWidth(PREFERRED_BUTTON_WIDTH);
		nextQuestionButton.setMaxWidth(Double.MAX_VALUE);
		nextQuestionButton.setId("next-question-button");
		vBox.getStyleClass().add("left-bar");
		borderPane.setLeft(vBox);

		ToolBar toolbar = new CustomToolBar(user, stage).removeAddAnAssignment().removeAddCourse().removeViewMarks()
				.removeViewStudents().removeLogOut().removeAddStudent().getToolBar();
		borderPane.setTop(toolbar);

		// Need to make this async so that event thread doesn't set text before
		// assignment is retrieved
		Task<Assignment> assignmentTask = new Task<Assignment>() {
			@Override
			protected Assignment call() throws Exception {
				return Assignment.retrieveFromDatabase(assignmentId);
			}
		};

		assignmentTask.setOnSucceeded(e -> {
			assignment = assignmentTask.getValue();
			questionIndex = 0;
			if (assignment.getQuestions().size() > 0) {
				displayQuestion(user.getId(), assignment.getQuestions().get(questionIndex));
			}
			assignmentNameLabel.setText(assignment.getName());
			assignmentNameLabel.getStyleClass().add("header2-bold");

			Region fillerRegion = new Region();
			HBox.setHgrow(fillerRegion, Priority.ALWAYS);
			submitButton.setVisible(questionIndex == assignment.getQuestions().size() - 1);
			toolbar.getItems().addAll(fillerRegion, submitButton);
			nextQuestionButton.setDisable(questionIndex == assignment.getQuestions().size() - 1);
		});

		new Thread(assignmentTask).start();

		setScene(new Scene(borderPane, 700, 700));
		getScene().getStylesheets().addAll(ResourceUtils.PRIMARY_STYLESHEET, "css/assignment-output.css");
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	private void displayQuestion(int userId, AssignmentQuestion question) {
		questionNode = question.renderQuestion();
		HBox.setHgrow(questionNode, Priority.ALWAYS);
		VBox vBox = new VBox(20);
		// vBox.setAlignment(Pos.TOP_LEFT);
		vBox.getStyleClass().add("question-box");

		vBox.getChildren().addAll(questionNode, question.renderStudentInput(questionIndex, userId,
				question.getAssignment().getAssignmentId(), studentSolutions));
		borderPane.setCenter(vBox);
	}
}
