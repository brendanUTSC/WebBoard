package core.question;

import java.util.List;
import java.util.logging.Logger;

import core.AssignmentElement;
import core.MultipleChoiceOptions;
import core.studentsolution.MultipleChoiceStudentSolution;
import core.studentsolution.StudentSolution;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import utils.ApplicationDataDirectoryUtils;
import utils.FileWriter;

public class MultipleChoiceQuestion extends AssignmentQuestion {
	private static final Logger logger = Logger.getLogger(MultipleChoiceQuestion.class.getSimpleName());

	private String question;
	private MultipleChoiceOptions options;

	public MultipleChoiceQuestion(String question, List<String> options, int weight) {
		this(question, new MultipleChoiceOptions(options), weight);
	}

	public MultipleChoiceQuestion(String question, MultipleChoiceOptions options, int weight) {
		this.setQuestion(question);
		this.options = options;
		setWeight(weight);
		this.setFormat(AssignmentElement.Format.MULTIPLE_CHOICE);
	}

	@Override
	public String saveToLocalMachine() {
		ApplicationDataDirectoryUtils.createAssignmentDirectory(this.getAssignment());

		String questionFilePath = ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\"
				+ this.getAssignment().getName() + "\\questions\\" + this.getIndex() + ".txt";
		FileWriter.createTextFile(questionFilePath, question);

		String optionDirectoryPath = ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\"
				+ this.getAssignment().getName() + "\\options\\" + this.getIndex() + "\\";
		FileWriter.createDirectory(optionDirectoryPath);

		// Clear the option file paths since it is possible that they were initialized
		// in a previous save.
		options.getOptionsFilePaths().clear();

		for (int i = 0; i < options.getOptions().size(); i++) {
			String optionFilePath = optionDirectoryPath + i + ".txt";
			FileWriter.createTextFile(optionFilePath, options.getOptions().get(i));
			options.getOptionsFilePaths().add(optionFilePath);
		}

		this.setFilePath(questionFilePath);
		return questionFilePath;
	}

	@Override
	public Node renderQuestion() {
		Label label = new Label();
		label.setText(question);
		return label;
	}

	@Override
	public Node renderStudentInput(int currentIndex, int userId, int assignmentId,
			List<StudentSolution> studentSolutions) {
		if (currentIndex >= studentSolutions.size()) {
			studentSolutions.add(new MultipleChoiceStudentSolution(userId, assignmentId, currentIndex, -1));
		}

		int selectedOption = Integer.parseInt(studentSolutions.get(currentIndex).getStringValue());
		ToggleGroup group = new ToggleGroup();
		VBox toggleBox = new VBox(10);
		for (int i = 0; i < options.getOptions().size(); i++) {
			logger.info("Adding radio button with: " + options.getOptions().get(i));
			RadioButton button = new RadioButton(options.getOptions().get(i));
			button.setToggleGroup(group);
			button.setUserData(new Integer(i));
			toggleBox.getChildren().add(button);
			if (selectedOption == i) {
				group.selectToggle(button);
			}

		}

		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle arg1, Toggle arg2) {
				if (currentIndex < studentSolutions.size()) {
					studentSolutions.set(currentIndex, new MultipleChoiceStudentSolution(userId, assignmentId,
							currentIndex, (Integer) group.getSelectedToggle().getUserData()));
				} else {
					studentSolutions.add(new MultipleChoiceStudentSolution(userId, assignmentId, currentIndex,
							(Integer) group.getSelectedToggle().getUserData()));
				}
				logger.info("Solution " + currentIndex + ": " + studentSolutions.get(currentIndex).getStringValue());
			}

		});

		return toggleBox;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public MultipleChoiceOptions getMultipleChoiceOptions() {
		return options;
	}

	public void setOptions(MultipleChoiceOptions options) {
		this.options = options;
	}
}
