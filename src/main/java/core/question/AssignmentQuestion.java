package core.question;

import java.util.List;

import core.AssignmentElement;
import core.studentsolution.StudentSolution;
import javafx.scene.Node;

/**
 * This class represents a question of an Assignment.
 * 
 * @author Brendan Zhang
 *
 */
public abstract class AssignmentQuestion extends AssignmentElement {
	private int weight;

	/**
	 * Creates a Node that allows for the student to input their solution to the
	 * assignment question. On detecting a value change, this Node must set the
	 * studentSolution with the given index to the value that the Node detects.
	 * 
	 * @param currentIndex
	 * @param userId
	 * @param assignmentId
	 * @param studentSolutions
	 * @return
	 */
	public abstract Node renderStudentInput(int currentIndex, int userId, int assignmentId,
			List<StudentSolution> studentSolutions);

	public abstract Node renderQuestion();

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
