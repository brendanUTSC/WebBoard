package core.answer;

import core.AssignmentElement;
import javafx.scene.Node;

/**
 * This class represents an answer to an Assignment question.
 * 
 * @author Brendan Zhang
 *
 */
public abstract class AssignmentAnswer extends AssignmentElement {

	// This will be useful if we wish to display correct answers after a student's
	// attempt

	/**
	 * Creates a Node to be displayed that represents the AssignmentAnswer.
	 * 
	 * @return the Node that can be used to display the AssignmentAnswer
	 */
	public abstract Node renderAnswer();
}
