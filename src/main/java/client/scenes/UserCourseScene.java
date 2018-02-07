package client.scenes;

import java.util.List;

import controls.CustomToolBar;
import core.User;
import database.UserCourseRepository;
import database.UserRepository;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ResourceUtils;

public class UserCourseScene {
	private static String HEADER = "Students";
	private Scene scene;
	private HBox tableBox;

	@SuppressWarnings("unchecked")
	public UserCourseScene(Stage stage, User professor) {
		ObservableList<User> unenrolledUsers = FXCollections.observableArrayList();
		ObservableList<User> enrolledUsers = FXCollections.observableArrayList();

		ComboBox<String> comboBox = new ComboBox<String>();

		// Get values for comboBox
		UserCourseRepository userCourseRepository = new UserCourseRepository();
		List<String> courses = userCourseRepository.getCoursesForUser(professor.getId());
		comboBox.getItems().addAll(courses);
		if (courses.size() > 0) {
			comboBox.setValue(courses.get(0));
			UserRepository userRepository = new UserRepository();
			List<User> users = userRepository.getAllEnrolledUsers(courses.get(0), true);
			enrolledUsers.addAll(users);

			users = userRepository.getAllUnenrolledUsers(courses.get(0), true);
			unenrolledUsers.addAll(users);
		}

		comboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				enrolledUsers.clear();
				unenrolledUsers.clear();

				UserRepository userRepository = new UserRepository();

				List<User> users = userRepository.getAllEnrolledUsers(newValue, true);
				enrolledUsers.addAll(users);

				users = userRepository.getAllUnenrolledUsers(newValue, true);
				unenrolledUsers.addAll(users);
			}
		});

		// Initialize table
		TableView<User> unEnrolledStudentsTable = new TableView<User>();
		unEnrolledStudentsTable.setItems(unenrolledUsers);

		TableColumn<User, Integer> idColumn = new TableColumn<User, Integer>("Student ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));

		TableColumn<User, String> firstNameColumn = new TableColumn<User, String>("First Name");
		firstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));

		TableColumn<User, String> lastNameColumn = new TableColumn<User, String>("Last Name");
		lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

		unEnrolledStudentsTable.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn);
		unEnrolledStudentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		unEnrolledStudentsTable.setId("unenrolled-students-table");

		TableView<User> enrolledStudentsTable = new TableView<User>();
		enrolledStudentsTable.setItems(enrolledUsers);

		TableColumn<User, Integer> enrolledIdColumn = new TableColumn<User, Integer>("Student ID");
		enrolledIdColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));

		TableColumn<User, String> enrolledFirstNameColumn = new TableColumn<User, String>("First Name");
		enrolledFirstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));

		TableColumn<User, String> enrolledLastNameColumn = new TableColumn<User, String>("Last Name");
		enrolledLastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

		enrolledStudentsTable.getColumns().addAll(enrolledIdColumn, enrolledFirstNameColumn, enrolledLastNameColumn);
		enrolledStudentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		enrolledStudentsTable.setId("enrolled-students-table");

		unEnrolledStudentsTable.setRowFactory(tv -> {
			TableRow<User> row = new TableRow<>();

			row.setOnDragDetected(event -> {
				if (!row.isEmpty()) {
					Integer index = row.getIndex();
					Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
					db.setDragView(row.snapshot(null, null));
					ClipboardContent cc = new ClipboardContent();
					cc.put(DataFormat.PLAIN_TEXT, index);
					db.setContent(cc);
					event.consume();
				}
			});

			return row;
		});

		enrolledStudentsTable.setRowFactory(tv -> {
			TableRow<User> row = new TableRow<>();
			return row;
		});

		enrolledStudentsTable.setOnDragOver(event -> {
			Dragboard db = event.getDragboard();
			if (db.hasContent(DataFormat.PLAIN_TEXT)) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				event.consume();

			}
		});

		enrolledStudentsTable.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Dragboard db = event.getDragboard();
				if (db.hasContent(DataFormat.PLAIN_TEXT)) {
					int draggedIndex = (Integer) db.getContent(DataFormat.PLAIN_TEXT);

					// Remove from table
					User draggedPerson = unEnrolledStudentsTable.getItems().remove(draggedIndex);

					// Move user into course by inserting record into UserCourse
					userCourseRepository.insert(draggedPerson.getId(), comboBox.getValue());

					int dropIndex = unEnrolledStudentsTable.getItems().size();

					enrolledStudentsTable.getItems().add(draggedPerson);

					event.setDropCompleted(true);
					enrolledStudentsTable.getSelectionModel().select(dropIndex);
					event.consume();
				}
			}
		});

		VBox leftBox = new VBox(10);
		VBox rightBox = new VBox(10);

		Label allStudentHeader = new Label("Un-enrolled Students");
		allStudentHeader.getStyleClass().add("header2");
		Label enrolledHeader = new Label("Enrolled Students");
		enrolledHeader.getStyleClass().add("header2");

		Button removeFromCourseButton = new Button("Remove");
		removeFromCourseButton.setAlignment(Pos.CENTER_RIGHT);
		removeFromCourseButton.setMaxWidth(Double.MAX_VALUE);
		removeFromCourseButton.setOnAction(e -> {
			User selectedUser = enrolledStudentsTable.getSelectionModel().getSelectedItem();
			userCourseRepository.removeUserFromCourse(selectedUser.getId(), comboBox.getValue());
			enrolledStudentsTable.getItems().remove(selectedUser);
			unEnrolledStudentsTable.getItems().add(selectedUser);
		});
		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);

		HBox enrolled = new HBox(10);
		enrolled.getChildren().addAll(enrolledHeader, region, removeFromCourseButton);

		leftBox.getChildren().addAll(allStudentHeader, unEnrolledStudentsTable);
		rightBox.getChildren().addAll(enrolled, enrolledStudentsTable);

		tableBox = new HBox(25);
		tableBox.getChildren().addAll(leftBox, rightBox);

		Label courseSelectionLabel = new Label("Course Selection:");
		courseSelectionLabel.getStyleClass().add("header2");

		Label headerLabel = new Label(HEADER);
		headerLabel.getStyleClass().add("header");

		VBox headerBox = new VBox(10);
		headerBox.getChildren().addAll(headerLabel, courseSelectionLabel, comboBox);

		VBox contentBox = new VBox(10);
		contentBox.getChildren().addAll(headerBox, new Separator(), tableBox);
		contentBox.setPadding(new Insets(10, 10, 0, 20));

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(contentBox);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(scrollPane);

		Button viewAssignmentsButton = new Button("View Assigments");
		viewAssignmentsButton.setMaxWidth(Double.MAX_VALUE);

		viewAssignmentsButton.setOnAction(e -> {
			stage.setScene(new AssignmentSelectionScene(stage, professor).getScene());
		});

		borderPane.setTop(new CustomToolBar(professor, stage).removeViewStudents().removeLogOut()
				.removeAddAnAssignment().removeAddCourse().removeAddStudent().getToolBar());

		setScene(new Scene(borderPane, 580, 400));
		getScene().getStylesheets().add(ResourceUtils.PRIMARY_STYLESHEET);
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
