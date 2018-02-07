package core.answer;

import core.AssignmentElement;
import javafx.scene.Node;
import utils.ApplicationDataDirectoryUtils;
import utils.FileWriter;

public class MultipleChoiceAnswer extends AssignmentAnswer {

	private int correctOption;

	public MultipleChoiceAnswer(int correctOption) {
		this.setCorrectOption(correctOption);
		this.setFormat(AssignmentElement.Format.MULTIPLE_CHOICE);
	}

	@Override
	public String saveToLocalMachine() {
		ApplicationDataDirectoryUtils.createAssignmentDirectory(this.getAssignment());
		String assignmentFilePath;

		assignmentFilePath = ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\"
				+ this.getAssignment().getName() + "\\answers\\" + this.getIndex() + ".txt";
		FileWriter.createTextFile(assignmentFilePath, String.valueOf(correctOption));

		this.setFilePath(assignmentFilePath);
		return assignmentFilePath;
	}

	public int getCorrectOption() {
		return correctOption;
	}

	public void setCorrectOption(int correctOption) {
		this.correctOption = correctOption;
	}

	@Override
	public Node renderAnswer() {
		// TODO Auto-generated method stub
		return null;
	}
}
