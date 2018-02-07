package client.scenes;

import java.io.File;
import java.util.List;

import controls.ConfirmationAlert;
import controls.CustomToolBar;
import controls.ImageButton;
import core.User;
import database.UserRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import utils.ExcelUtils;
import utils.ResourceUtils;

/**
 * Scene that allows a professor to add a student. The user passed as a
 * dependency to UserInputScene is assumed to have a privilege level of
 * Professor.
 * 
 * @author Brendan Zhang
 *
 */
public class UserInputScene {
	private static String ADD_USER_ICON_FILE = "add-user-icon.png";
	private static String IMPORT_ICON_FILE = "import.png";
	private static final String HEADER = "Add a Student";
	private static final double LABEL_ALIGNMENT_WIDTH = 70;

	private Scene scene;
	private BorderPane borderPane;
	private Label userNameLabel;
	private TextField userNameTextField;
	private Label passwordLabel;
	private TextField passwordTextField;
	private Label firstNameLabel;
	private TextField firstNameTextField;
	private Label lastNameLabel;
	private TextField lastNameTextField;
	private Label headerLabel;

	public UserInputScene(Stage stage, User user) {

		initializeNodeAttributes();

		Region fillerRegion = new Region();
		HBox.setHgrow(fillerRegion, Priority.ALWAYS);

		HBox headerBox = new HBox(10);
		headerBox.getChildren().addAll(headerLabel, fillerRegion, createAddStudentButton(stage));

		HBox userNameBox = new HBox(10, userNameLabel, userNameTextField);
		userNameBox.setAlignment(Pos.CENTER_LEFT);
		HBox passwordBox = new HBox(10, passwordLabel, passwordTextField);
		passwordBox.setAlignment(Pos.CENTER_LEFT);
		HBox firstNameBox = new HBox(10, firstNameLabel, firstNameTextField);
		firstNameBox.setAlignment(Pos.CENTER_LEFT);
		HBox lastNameBox = new HBox(10, lastNameLabel, lastNameTextField);
		lastNameBox.setAlignment(Pos.CENTER_LEFT);

		VBox contentBox = new VBox(15);
		contentBox.getChildren().addAll(headerBox, userNameBox, passwordBox, firstNameBox, lastNameBox);
		contentBox.setPadding(new Insets(15, 15, 0, 15));
		borderPane.setCenter(contentBox);

		ToolBar toolBar = new CustomToolBar(user, stage).removeAddAnAssignment().removeAddCourse().removeViewMarks()
				.removeViewStudents().removeLogOut().removeAddStudent().getToolBar();
		borderPane.setTop(toolBar);

		setScene(new Scene(borderPane, 450, 300));
		getScene().getStylesheets().add(ResourceUtils.PRIMARY_STYLESHEET);
	}

	/**
	 * Initializes the JavaFX nodes that are in the Scene.
	 */
	private void initializeNodeAttributes() {
		borderPane = new BorderPane();

		headerLabel = new Label(HEADER);
		headerLabel.getStyleClass().add("header");

		userNameLabel = new Label("Username:");
		userNameLabel.setMinWidth(LABEL_ALIGNMENT_WIDTH);
		userNameTextField = new TextField();
		HBox.setHgrow(userNameTextField, Priority.ALWAYS);

		passwordLabel = new Label("Password:");
		passwordLabel.setMinWidth(LABEL_ALIGNMENT_WIDTH);
		passwordTextField = new TextField();
		HBox.setHgrow(passwordTextField, Priority.ALWAYS);

		firstNameLabel = new Label("First name:");
		firstNameLabel.setMinWidth(LABEL_ALIGNMENT_WIDTH);
		firstNameTextField = new TextField();
		HBox.setHgrow(firstNameTextField, Priority.ALWAYS);

		lastNameLabel = new Label("Last name:");
		lastNameLabel.setMinWidth(LABEL_ALIGNMENT_WIDTH);
		lastNameTextField = new TextField();
		HBox.setHgrow(lastNameTextField, Priority.ALWAYS);

		applyToTextFields(userNameTextField, passwordTextField, firstNameTextField, lastNameTextField);
	}

	/**
	 * Enables horizontal re-sizing for the TextFields.
	 * 
	 * @param fields
	 *            The TextFields to modify
	 */
	private void applyToTextFields(TextField... fields) {
		for (TextField textField : fields) {
			HBox.setHgrow(textField, Priority.ALWAYS);
		}
	}

	/**
	 * Creates the Add Student button and Import button.
	 * 
	 * @return HBox that contains the created buttons
	 */
	private Node createAddStudentButton(Stage stage) {

		ImageButton addStudentButton = new ImageButton("Add Student", ADD_USER_ICON_FILE);
		addStudentButton.setOnAction(e -> {
			UserRepository userRepository = new UserRepository();
			userRepository.insert(userNameTextField.getText(), passwordTextField.getText(), 1,
					firstNameTextField.getText(), lastNameTextField.getText());
			ConfirmationAlert.display("Success! The student has been added!");
		});

		ImageButton importUserButton = new ImageButton("Import", IMPORT_ICON_FILE);
		importUserButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Worksheets", "*.xlsx", "*.xls"));
			File selectedFile = fileChooser.showOpenDialog(stage);
			if (selectedFile != null) {
				processSpreadsheet(selectedFile);
			}
		});

		HBox hBox = new HBox(10);
		hBox.setAlignment(Pos.CENTER_RIGHT);
		hBox.getChildren().addAll(addStudentButton.getButton(), importUserButton.getButton());

		return hBox;
	}

	/**
	 * Processes the spreadsheet by reading all of the users and inserting them to
	 * the database.
	 * 
	 * @param file
	 *            The Excel file
	 */
	private void processSpreadsheet(File file) {
		if (file.getName().toLowerCase().contains(".xls")) {
			List<User> users = ExcelUtils.readUsersFromExcelFile(file);
			UserRepository userRepository = new UserRepository();
			int inserted = 0;
			for (User user : users) {
				userRepository.insert(user);
				inserted++;
			}
			ConfirmationAlert.display("Success! " + inserted + " student(s) has been imported!");
		}
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
