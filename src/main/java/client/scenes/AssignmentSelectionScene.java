package client.scenes;

import java.util.List;
import java.util.logging.Logger;

import controls.AssignmentSelector;
import controls.ConfirmationAlert;
import controls.CustomToolBar;
import core.Assignment;
import core.User;
import database.AssignmentRepository;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AssignmentSelectionScene {
	public static final String HEADER = "Assignments";
	private static final String DELETE_TEXT = "Success!\n" + "Your assignment has been deleted!";
	private static Logger logger = Logger.getLogger(AssignmentSelectionScene.class.getName());
	private static final int PAGE_SIZE = 5;
	private Scene scene;
	private Button nextButton, prevButton;
	private List<Assignment> assignments;
	private int currentPage;
	private VBox mainContentBox;
	private Label paginationLabel;
	private AssignmentRepository assignmentRepository;

	public AssignmentSelectionScene(Stage stage, User user) {

		BorderPane borderPane = new BorderPane();

		// Initialize current page to 0
		currentPage = 0;

		assignmentRepository = new AssignmentRepository();
		assignments = assignmentRepository.getAllAssignmentForUser(user);

		mainContentBox = new VBox();
		mainContentBox.getChildren().addAll(createHeader());
		mainContentBox.setAlignment(Pos.TOP_LEFT);

		borderPane.setCenter(mainContentBox);

		nextButton = new Button("Next Page");
		nextButton.setOnAction(e -> {
			if ((currentPage + 1) * PAGE_SIZE < assignments.size()) {
				currentPage++;
			}
			displayPage(currentPage, user, stage);
		});

		prevButton = new Button("Previous Page");
		prevButton.setOnAction(e -> {
			if (currentPage > 0) {
				currentPage--;
			}
			displayPage(currentPage, user, stage);
		});

		borderPane.setTop(new CustomToolBar(user, stage).removeViewAssignments().getToolBar());
		ToolBar bottomBar = new ToolBar();
		paginationLabel = new Label();
		setPaginationLabel();
		bottomBar.getItems().addAll(prevButton, nextButton, new Separator(Orientation.VERTICAL), paginationLabel);

		borderPane.setBottom(bottomBar);

		// Initialize on first page
		displayPage(currentPage, user, stage);

		setScene(new Scene(borderPane, 640, 400));
		getScene().getStylesheets().add("css/style.css");
	}

	public Node createHeader() {
		Label headerLabel = new Label(HEADER);
		headerLabel.getStyleClass().add("header");
		headerLabel.setId("header-label");
		headerLabel.setPadding(new Insets(10, 0, 0, 10));
		return headerLabel;
	}

	private void displayPage(int page, User user, Stage stage) {
		logger.info("Displaying page: " + page);
		mainContentBox.getChildren().clear();
		mainContentBox.getChildren().add(createHeader());
		for (int i = page * PAGE_SIZE; i < (page + 1) * PAGE_SIZE && i < assignments.size(); i++) {
			Assignment assignment = assignments.get(i);
			int index = i;
			AssignmentSelector assignmentSelector = new AssignmentSelector(stage, user, assignment);
			mainContentBox.getChildren().add(assignmentSelector.getNode());
			Button deleteButton = assignmentSelector.getDeleteButton();
			if (deleteButton != null) {
				deleteButton.setOnAction(e -> {
					assignmentRepository.delete(assignment.getAssignmentId());
					assignments.remove(index);
					mainContentBox.getChildren().remove(assignmentSelector.getNode());
					ConfirmationAlert.display(DELETE_TEXT);
					displayPage(currentPage, user, stage);
				});
			}
		}
		setPaginationLabel();
	}

	private void setPaginationLabel() {
		logger.info("Updating pagination label");
		String paginationText;
		if (assignments.size() == 0) {
			// Special case when no assignments
			paginationText = "0-0 of 0";
		} else if (assignments.size() < (currentPage + 1) * PAGE_SIZE) {
			paginationText = (currentPage * PAGE_SIZE + 1) + "-" + assignments.size() + " of " + assignments.size();
		} else {
			paginationText = (currentPage * PAGE_SIZE + 1) + "-" + (currentPage + 1) * PAGE_SIZE + " of "
					+ assignments.size();
		}
		paginationLabel.setText("Page " + paginationText);
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
