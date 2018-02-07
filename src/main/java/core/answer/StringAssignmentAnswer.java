package core.answer;

import core.AssignmentElement;
import javafx.scene.Node;
import utils.ApplicationDataDirectoryUtils;
import utils.FileWriter;

/**
 * A Question/Answer that has its contents saved as a String.
 * 
 * @author Brendan Zhang
 *
 */
public class StringAssignmentAnswer extends AssignmentAnswer {
	private String value;

	public StringAssignmentAnswer(String value) {
		this.value = value;
		this.setFormat(AssignmentElement.Format.STRING);
	}

	public StringAssignmentAnswer(String value, int index) {
		this(value);
		this.setIndex(index);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * This returns the value of the StringAssignmentElement with the "Answer: "
	 * prefix removed. This is ideal when doing comparisons between a
	 * StringAssignmentElement and a String.
	 * 
	 * @return value of the StringAssignmentElement with prefix removed
	 */
	public String getCleanValue() {
		String newValue = value;
		String ANSWER_PREFIX = "Answer: ";
		if (newValue.startsWith("Answer: ")) {
			newValue = newValue.substring(ANSWER_PREFIX.length());
		}
		return newValue;
	}

	public String saveToLocalMachine() {
		ApplicationDataDirectoryUtils.createAssignmentDirectory(this.getAssignment());
		String assignmentFilePath;

		assignmentFilePath = ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\"
				+ this.getAssignment().getName() + "\\answers\\" + this.getIndex() + ".txt";
		FileWriter.createTextFile(assignmentFilePath, value);

		this.setFilePath(assignmentFilePath);
		return assignmentFilePath;
	}

	@Override
	public Node renderAnswer() {
		// TODO Auto-generated method stub
		return null;
	}
}
