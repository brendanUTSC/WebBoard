package client.scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.mysql.jdbc.StringUtils;

import controls.CustomToolBar;
import controls.ImageButton;
import core.Assignment;
import core.AssignmentMark;
import core.Submission;
import core.User;
import database.AssignmentRepository;
import database.SubmissionRepository;
import database.UserCourseRepository;
import database.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ResourceUtils;

public class ViewMarksScene {
	private static Logger logger = Logger.getLogger(ViewMarksScene.class.getName());

	private Scene scene;
	private ImageButton searchButton;
	private TextField inputStudent;
	private TableView<AssignmentMark> tableView;

	private List<AssignmentMark> assignmentMarks;
	private ObservableList<AssignmentMark> tableAssignmentMarks;

	@SuppressWarnings("unchecked")
	public ViewMarksScene(Stage stage, User user) {
		Label headerLabel = new Label("Student Marks");
		headerLabel.getStyleClass().add("header");
		assignmentMarks = new ArrayList<AssignmentMark>();
		tableAssignmentMarks = FXCollections.observableArrayList();
		initializeAssignmentMarks(user.getId());

		tableView = new TableView<>();
		tableView.setPrefWidth(600);
		BorderPane borderPane = new BorderPane();

		searchButton = new ImageButton("Search", "search.png");
		searchButton.setOnAction(e -> {
			String searchQuery = inputStudent.getText();
			if (StringUtils.isStrictlyNumeric(searchQuery)) {
				logger.info("Search query is a number. Searching for student Id: " + Integer.parseInt(searchQuery));
				searchForStudentId(Integer.parseInt(searchQuery));
			} else {
				logger.info("Search query is a String. Searching for student name: " + searchQuery);
				searchForStudentName(searchQuery);
			}
		});

		inputStudent = new TextField();
		inputStudent.getStyleClass().add("search-bar");
		inputStudent.setPrefWidth(200);
		inputStudent.setPromptText("Enter the Student's Name or ID");

		Region toolBarFillerRegion = new Region();
		HBox.setHgrow(toolBarFillerRegion, Priority.ALWAYS);

		ToolBar toolBar = new CustomToolBar(user, stage).removeViewMarks().removeAddAnAssignment().removeAddCourse()
				.removeAddStudent().removeLogOut().getToolBar();
		toolBar.getItems().addAll(toolBarFillerRegion, inputStudent, searchButton.getButton());
		borderPane.setTop(toolBar);

		// Create Table Columns
		TableColumn<AssignmentMark, Integer> idCol = new TableColumn<AssignmentMark, Integer>("Student ID");
		idCol.setCellValueFactory(new PropertyValueFactory<AssignmentMark, Integer>("id"));

		TableColumn<AssignmentMark, String> nameCol = new TableColumn<AssignmentMark, String>("Student Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<AssignmentMark, String>("name"));

		TableColumn<AssignmentMark, String> assignmentIdCol = new TableColumn<AssignmentMark, String>("Assignment");
		assignmentIdCol.setCellValueFactory(new PropertyValueFactory<AssignmentMark, String>("assignmentName"));

		TableColumn<AssignmentMark, String> markCol = new TableColumn<AssignmentMark, String>("Grade");
		markCol.setCellValueFactory(new PropertyValueFactory<AssignmentMark, String>("grade"));

		tableView.getColumns().addAll(idCol, nameCol, assignmentIdCol, markCol);
		tableView.setItems(tableAssignmentMarks);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10, 20, 0, 20));
		vbox.getChildren().addAll(headerLabel, tableView);

		ScrollPane scrollPane = new ScrollPane();

		scrollPane.setContent(vbox);
		borderPane.setCenter(scrollPane);
		setScene(new Scene(borderPane, 660, 300));
		getScene().getStylesheets().add(ResourceUtils.PRIMARY_STYLESHEET);
	}

	private void searchForStudentId(Integer id) {
		tableAssignmentMarks.clear();
		for (AssignmentMark assignmentMark : assignmentMarks) {
			if (assignmentMark.getId() == id) {
				tableAssignmentMarks.add(assignmentMark);
			}
		}
	}

	private void searchForStudentName(String name) {
		tableAssignmentMarks.clear();
		for (AssignmentMark assignmentMark : assignmentMarks) {
			if (assignmentMark.getName().contains(name)) {
				tableAssignmentMarks.add(assignmentMark);
			}
		}
	}

	private void initializeAssignmentMarks(Integer professorId) {
		UserCourseRepository userCourseRepo = new UserCourseRepository();
		List<Integer> allStudentIds = userCourseRepo.getAllStudentsFlatMap(professorId);
		List<String> allCourseIds = userCourseRepo.getCoursesForUser(professorId);

		SubmissionRepository submissionRepo = new SubmissionRepository();
		AssignmentRepository assignmentRepo = new AssignmentRepository();
		UserRepository userRepo = new UserRepository();

		for (Integer studentId : allStudentIds) {
			User user = userRepo.getUserFromId(studentId);
			List<Submission> submissionsForUser = submissionRepo.getSubmission(studentId, null);
			for (Submission submission : submissionsForUser) {
				Assignment assignment = assignmentRepo.getAssignment(submission.getAssignmentId());
				if (allCourseIds.contains(assignment.getCourseId())) {
					AssignmentMark tableEntry = new AssignmentMark(studentId,
							user.getFirstName() + " " + user.getLastName(), assignment.getName(),
							submission.getMark() + "/" + assignment.getTotalMarks());
					assignmentMarks.add(tableEntry);
					tableAssignmentMarks.add(tableEntry);
				}
			}
		}
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
}