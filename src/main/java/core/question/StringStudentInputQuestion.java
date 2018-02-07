package core.question;

import java.util.List;

import core.studentsolution.StringStudentSolution;
import core.studentsolution.StudentSolution;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * An abstract class that represents an AssignmentQuestion that takes the
 * student's input through a TextField.
 * 
 * @author Brendan Zhang
 *
 */
public abstract class StringStudentInputQuestion extends AssignmentQuestion {
	@Override
	public Node renderStudentInput(int currentIndex, int userId, int assignmentId,
			List<StudentSolution> studentSolutions) {
		if (currentIndex >= studentSolutions.size()) {
			studentSolutions.add(new StringStudentSolution(userId, assignmentId, currentIndex, ""));
		}

		TextField studentSolutionTextField = new TextField();
		studentSolutionTextField.setPromptText("Enter your answer here");

		studentSolutionTextField.textProperty().set(studentSolutions.get(currentIndex).getStringValue());

		studentSolutionTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (currentIndex < studentSolutions.size()) {
					studentSolutions.set(currentIndex,
							new StringStudentSolution(userId, assignmentId, currentIndex, newValue));
				} else {
					studentSolutions.add(new StringStudentSolution(userId, assignmentId, currentIndex, newValue));
				}
			}
		});

		return studentSolutionTextField;
	}
}
