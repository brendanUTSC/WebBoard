package core.question;

import core.AssignmentElement;
import javafx.scene.Node;
import javafx.scene.control.Label;
import utils.ApplicationDataDirectoryUtils;
import utils.FileWriter;

/**
 * A Question that has its contents saved as a String.
 * 
 * @author Brendan Zhang
 *
 */
public class StringQuestion extends StringStudentInputQuestion {
	private String value;

	public StringQuestion(String value, int weight) {
		this.value = value;
		this.setWeight(weight);
		this.setFormat(AssignmentElement.Format.STRING);
	}

	public StringQuestion(String value, int weight, int index) {
		this(value, weight);
		this.setIndex(index);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String saveToLocalMachine() {
		ApplicationDataDirectoryUtils.createAssignmentDirectory(this.getAssignment());
		String questionFilePath;

		questionFilePath = ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\"
				+ this.getAssignment().getName() + "\\questions\\" + this.getIndex() + ".txt";
		FileWriter.createTextFile(questionFilePath, value);

		this.setFilePath(questionFilePath);
		return questionFilePath;
	}

	@Override
	public Node renderQuestion() {
		Label label = new Label();
		label.setText(value);
		return label;
	}
}
