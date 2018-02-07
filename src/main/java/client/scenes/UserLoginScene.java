package client.scenes;

import controls.ImageButton;
import core.User;
import database.UserRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ResourceUtils;

public class UserLoginScene {
	private static final String HEADER = "Login";
	private static String INVALID_USERNAME = "Invalid username. Please sign up first!";
	private static String INVALID_PASSWORD = "Invalid password. Please try again!";
	private static String USERNAME_PROMPT = "Enter your Username";
	private static String PASSWORD_PROMPT = "Enter your Password";

	private Scene scene;
	private Label usernameLabel, passwordLabel, invalidLoginLabel;
	private TextField usernameInput, passwordTextField;
	private ImageButton loginButton;
	private VBox vBox, butBox;
	private PasswordField passwordField;

	public UserLoginScene(Stage stage) {
		Label headerLabel = new Label(HEADER);
		headerLabel.getStyleClass().add("header");

		BorderPane borderPane = new BorderPane();
		borderPane.getStyleClass().add("login-screen");

		usernameLabel = new Label("Username");
		usernameLabel.getStyleClass().add("login-label");

		passwordLabel = new Label("Password");
		passwordLabel.getStyleClass().add("login-label");

		invalidLoginLabel = new Label();
		invalidLoginLabel.setId("invalid-login-label");
		invalidLoginLabel.getStyleClass().add("invalid-login-label");
		invalidLoginLabel.setVisible(false);

		usernameInput = new TextField();
		usernameInput.setId("username-box");
		usernameInput.setPromptText(USERNAME_PROMPT);

		loginButton = new ImageButton(" Log in", "login.png");
		loginButton.getButton().setId("login-button");

		vBox = new VBox(10);
		butBox = new VBox();
		butBox.setAlignment(Pos.BOTTOM_RIGHT);
		vBox.setPadding(new Insets(0, 20, 0, 20));

		// TextField for displaying unmasked password
		passwordTextField = new TextField();
		passwordTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				login(stage);
			}
		});

		// Set initial state
		passwordTextField.setManaged(false);
		passwordTextField.setVisible(false);
		passwordTextField.setPromptText(PASSWORD_PROMPT);
		passwordTextField.setId("password-text-box");

		// Actual password field
		passwordField = new PasswordField();
		passwordField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				login(stage);
			}
		});
		passwordField.setPromptText(PASSWORD_PROMPT);
		passwordField.setId("password-box");

		CheckBox checkBox = new CheckBox("Show password");

		// Bind properties.
		passwordTextField.managedProperty().bind(checkBox.selectedProperty());
		passwordTextField.visibleProperty().bind(checkBox.selectedProperty());

		passwordField.managedProperty().bind(checkBox.selectedProperty().not());
		passwordField.visibleProperty().bind(checkBox.selectedProperty().not());

		// Bind the textField and passwordField text values bidirectionally.
		passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());

		butBox.getChildren().addAll(loginButton.getButton());

		HBox headerBox = new HBox();
		Region fillerRegion = new Region();
		HBox.setHgrow(fillerRegion, Priority.ALWAYS);
		ImageView headerIcon = new ImageView(ResourceUtils.getImageFromResources("logo.png"));
		headerIcon.setPreserveRatio(true);
		headerIcon.setFitWidth(40);
		headerBox.setAlignment(Pos.TOP_CENTER);

		headerBox.getChildren().addAll(headerLabel, fillerRegion, headerIcon);

		vBox.getChildren().addAll(headerBox, new Separator(), usernameLabel, usernameInput, passwordLabel,
				passwordField, passwordTextField, checkBox, invalidLoginLabel, butBox);
		borderPane.setCenter(vBox);
		borderPane.setPadding(new Insets(10, 0, 0, 0));

		// Check user name/password
		loginButton.setOnAction(e -> {
			login(stage);
		});

		setScene(new Scene(borderPane, 340, 310));
		getScene().getStylesheets().add(ResourceUtils.PRIMARY_STYLESHEET);
	}

	private void login(Stage stage) {
		UserRepository userRepository = new UserRepository();
		User user = userRepository.getUserFromUsername(usernameInput.getText());
		if (user == null) {
			invalidLoginLabel.setText(INVALID_USERNAME);
			invalidLoginLabel.setVisible(true);
			usernameInput.setText("");
			passwordField.setText("");
		} else {
			if ((user.getPassword().equals(passwordField.getText()))) {
				stage.setScene(new AssignmentSelectionScene(stage, user).getScene());
			} else {
				invalidLoginLabel.setText(INVALID_PASSWORD);
				invalidLoginLabel.setVisible(true);
				passwordField.setText("");
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
