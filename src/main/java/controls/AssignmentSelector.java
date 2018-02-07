package controls;

import java.util.List;

import client.scenes.AssignmentInputScene;
import client.scenes.AssignmentOutputScene;
import core.Assignment;
import core.Submission;
import core.User;
import core.User.Privilege;
import database.SubmissionRepository;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AssignmentSelector {
	private HBox hBox;
	private static final String EDIT_ICON_FILE_NAME = "pencil-icon.png";
	private static final String DELETE_ICON_FILE_NAME = "cancel.png";
	private static final double MIN_LABEL_TEXT_LENGTH = 300;
	private Button deleteButton;

	public AssignmentSelector(Stage stage, User user, Assignment assignment) {
		HBox assignmentHBox = new HBox(10);
		assignmentHBox.setAlignment(Pos.CENTER_LEFT);

		String assignmentLabelText = "\t" + assignment.getName();
		Label newAssignmentLabel = new Label(assignmentLabelText);
		newAssignmentLabel.getStyleClass().add("#" + assignment.getAssignmentId());
		newAssignmentLabel.setMinWidth(MIN_LABEL_TEXT_LENGTH);
		newAssignmentLabel.setDisable(user.getPrivilegeLevel() == Privilege.STUDENT && assignment.isDeadlinePassed());
		newAssignmentLabel.setOnMouseClicked(e -> {
			stage.setScene(new AssignmentOutputScene(stage, assignment.getAssignmentId(), user).getScene());
		});
		newAssignmentLabel.getStyleClass().add("clickable-label");

		Label deadlinePassedLabel = new Label("Deadline has passed!");
		deadlinePassedLabel.getStyleClass().add("warning-label");

		Button editButton = null;
		deleteButton = null;
		Label markLabel = null;

		// edit button for professors
		if ((user.getPrivilegeLevel() == Privilege.PROFESSOR)) {
			editButton = new ImageButton("Edit", EDIT_ICON_FILE_NAME).getButton();
			editButton.setOnAction(e -> {
				stage.setScene(new AssignmentInputScene(stage, user, assignment).getScene());
			});
			deleteButton = new ImageButton("Delete", DELETE_ICON_FILE_NAME).getButton();
		} else { // student

			// get mark from assignment submission for this student
			SubmissionRepository submissionRepo = new SubmissionRepository();
			List<Submission> submissionList = submissionRepo.getSubmission(user.getId(), assignment.getAssignmentId());

			// get the highest mark
			int mark = 0;
			for (Submission sub : submissionList) {
				if (sub.getMark() > mark) {
					mark = sub.getMark();
				}
			}

			String markText;
			if (submissionList.size() > 0) {
				markText = mark + " out of " + assignment.getTotalMarks();

			} else {
				markText = "Not Attempted";
			}

			markLabel = new Label(markText);
			markLabel.setMinWidth(100);
			markLabel.getStyleClass().add("mark-label");
		}

		assignmentHBox.getChildren().add(newAssignmentLabel);
		if (editButton != null) {
			assignmentHBox.getChildren().add(editButton);
		}
		if (deleteButton != null) {
			assignmentHBox.getChildren().add(deleteButton);
		}
		if (markLabel != null) {
			assignmentHBox.getChildren().add(markLabel);
		}
		if (assignment.isDeadlinePassed()) {
			assignmentHBox.getChildren().add(deadlinePassedLabel);
		}

		hBox = assignmentHBox;
		hBox.getStyleClass().add("assignment-box");
		hBox.setOnMouseEntered(e -> {
			hBox.getStyleClass().clear();
			hBox.getStyleClass().add("assignment-box-hover");
		});
		hBox.setOnMouseExited(e -> {
			hBox.getStyleClass().clear();
			hBox.getStyleClass().add("assignment-box");
		});
	}

	/**
	 * Returns the Button that corresponds to this AssignmentSelector's delete
	 * button. It is necessary to expose this button, as we need to make sure the
	 * AssignmentSelectionScene updates the pagination and assignment list when an
	 * assignment is deleted.
	 * 
	 * @return the Delete Button
	 */
	public Button getDeleteButton() {
		return deleteButton;
	}

	public HBox getNode() {
		return hBox;
	}
}
