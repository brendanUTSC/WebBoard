package client.scenes;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import controls.ConfirmationAlert;
import controls.CustomToolBar;
import controls.ImageButton;
import controls.TimePicker;
import core.Assignment;
import core.AssignmentElement.Format;
import core.MultipleChoiceOptions;
import core.User;
import core.answer.AssignmentAnswer;
import core.answer.MultipleChoiceAnswer;
import core.answer.StringAssignmentAnswer;
import core.question.AssignmentQuestion;
import core.question.MultipleChoiceQuestion;
import core.question.PDFAssignmentQuestion;
import core.question.StringQuestion;
import database.UserCourseRepository;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import utils.ResourceUtils;

/**
 * Scene that allows a User to create an Assignment. The Scene features a form
 * for filling out Assignment data (and questions/answers) and a Submit button
 * for saving to the database.
 * 
 * @author Brendan Zhang
 *
 */
public class AssignmentInputScene {
	private static final String ADD_HEADER = "Create an Assignment";
	private static final String EDIT_HEADER = "Edit an Assignment";
	private static final String ASSIGNMENT_ADDED_TEXT = "Success!\n" + "Your assignment has been added!";
	private static final String ASSIGNMENT_EDITED_TEXT = "Success!\n" + "Your assignment has been updated!";
	private static Logger logger = Logger.getLogger(AssignmentInputScene.class.getName());
	private static int LABEL_ALIGNMENT_WIDTH = 130;

	private Scene scene;
	private Assignment currentAssignment;
	private ImageButton submitButton, addQuestionButton;
	private Label assignmentLabel;
	private TextField assignmentNameTextField;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private BorderPane borderPane;
	private TabPane questionTabPane;
	private Label headerLabel;
	private Stage stage;

	/**
	 * Creates a new AssignmentInputScene.
	 *
	 * @param stage
	 *            The Stage
	 * @param user
	 *            The authenticated User
	 * @param addQuestion
	 *            If this is set to true, it adds a question/answer text area on
	 *            initialization. This should be set to false when loading an
	 *            Assignment for editing.
	 */
	public AssignmentInputScene(Stage stage, User user, boolean addQuestion) {
		this.stage = stage;
		borderPane = new BorderPane();
		questionTabPane = new TabPane();
		currentAssignment = new Assignment();
		timePicker = new TimePicker(currentAssignment);

		headerLabel = new Label(ADD_HEADER);
		headerLabel.setId("header-label");
		headerLabel.getStyleClass().add("header");

		assignmentLabel = new Label("Assignment name:");
		assignmentLabel.setMinWidth(LABEL_ALIGNMENT_WIDTH);

		assignmentNameTextField = new TextField();
		assignmentNameTextField.setId("assignment-name-textfield");
		assignmentNameTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentAssignment.setName(newValue);
				logger.info("currentAssignment.name: " + currentAssignment.getName());
			}
		});

		Label courseLabel = new Label("Course:");
		courseLabel.setPadding(new Insets(0, 0, 0, 175));

		// Get values for comboBox
		ComboBox<String> courseComboBox = new ComboBox<>();
		courseComboBox.setId("course-combo-box");
		UserCourseRepository userCourseRepository = new UserCourseRepository();
		List<String> courses = userCourseRepository.getCoursesForUser(user.getId());
		courseComboBox.getItems().addAll(courses);

		courseComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentAssignment.setCourseId(newValue);
			}
		});
		if (courseComboBox.getItems().size() > 0) {
			courseComboBox.setValue(courses.get(0));
		}

		submitButton = new ImageButton("Submit", "submit.png");
		submitButton.getButton().setId("submit-button");
		submitButton.setOnAction(e -> {
			currentAssignment.saveToLocalMachine();
			currentAssignment.saveToDatabase();
			ConfirmationAlert.display(ASSIGNMENT_ADDED_TEXT);
		});

		addQuestionButton = new ImageButton("Add Question", "add-file.png");
		addQuestionButton.setOnAction(e -> {
			addQuestionAnswerTextArea();
		});

		Region fillerRegion = new Region();
		HBox.setHgrow(fillerRegion, Priority.ALWAYS);

		HBox headerBox = new HBox(10);
		headerBox.getChildren().addAll(headerLabel, fillerRegion, addQuestionButton.getButton(),
				submitButton.getButton());
		addQuestionButton.getButton().setAlignment(Pos.BASELINE_CENTER);

		ToolBar toolbar = new CustomToolBar(user, stage).removeAddAnAssignment().removeAddCourse().removeViewMarks()
				.removeViewStudents().removeLogOut().removeAddStudent().getToolBar();

		HBox hBox = new HBox(10);
		hBox.setAlignment(Pos.CENTER_LEFT);
		hBox.getChildren().addAll(assignmentLabel, assignmentNameTextField, courseLabel, courseComboBox);

		datePicker = new DatePicker();
		// Add some action (in Java 8 lambda syntax style).
		datePicker.setOnAction(e -> {
			LocalDateTime deadline = datePicker.getValue().atStartOfDay();
			logger.info("Selected date: " + deadline);
			currentAssignment.setDeadline(deadline);
		});
		datePicker.setId("assignment-deadline-datepicker");

		Label assignmentDeadlineLabel = new Label("Assignment deadline:");
		assignmentDeadlineLabel.setMinWidth(LABEL_ALIGNMENT_WIDTH);

		HBox assignmentDeadlineHBox = new HBox(10);
		assignmentDeadlineHBox.setAlignment(Pos.CENTER_LEFT);
		assignmentDeadlineHBox.getChildren().addAll(assignmentDeadlineLabel, datePicker, timePicker.getNode());

		VBox topContentVBox = new VBox(10);
		topContentVBox.getChildren().addAll(hBox, assignmentDeadlineHBox);

		borderPane.setTop(toolbar);

		VBox contentBox = new VBox(10);
		contentBox.getChildren().addAll(headerBox, topContentVBox, questionTabPane);
		contentBox.setPadding(new Insets(10));
		borderPane.setCenter(contentBox);

		if (addQuestion) {
			addQuestionAnswerTextArea();
		}

		scene = new Scene(borderPane, 660, 500);
		getScene().getStylesheets().add(ResourceUtils.PRIMARY_STYLESHEET);
	}

	public AssignmentInputScene(Stage stage, User user, Assignment assignment) {
		this(stage, user, false);

		headerLabel.setText(EDIT_HEADER);

		currentAssignment.setDeadline(assignment.getDeadline());
		assignmentNameTextField.setText(assignment.getName());
		datePicker.setValue(currentAssignment.getDeadline().toLocalDate());
		timePicker.setValues(currentAssignment.getDeadline());
		// Initialize assignment
		currentAssignment.setAssignmentId(assignment.getAssignmentId());

		// Initialize questions/answers
		int numberOfQuestions = assignment.getQuestions().size();
		for (int i = 0; i < numberOfQuestions; i++) {
			// this is where it loads assignment questions and answers
			addQuestionInput(assignment.getQuestions().get(i), assignment.getAnswers().get(i));
		}

		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				currentAssignment.saveToLocalMachine();
				currentAssignment.updateToDatabase();
				ConfirmationAlert.display(ASSIGNMENT_EDITED_TEXT);
			}
		});
	}

	private void addQuestionInput(AssignmentQuestion question, AssignmentAnswer answer) {
		int currentIndex = currentAssignment.getQuestions().size();
		currentAssignment.addQuestion(question);
		currentAssignment.addAnswer(answer);

		Tab questionTab = new Tab("Question " + (currentIndex + 1));
		questionTab.setUserData(currentIndex);
		questionTab.setText("Question " + ((Integer) questionTab.getUserData() + 1));
		questionTab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				int indexToRemove = getIndex(questionTab);
				currentAssignment.getQuestions().remove(indexToRemove);
				for (Tab tab : questionTabPane.getTabs()) {
					if ((Integer) tab.getUserData() >= indexToRemove) {
						tab.setUserData((Integer) tab.getUserData() - 1);
						tab.setText("Question " + ((Integer) tab.getUserData() + 1));
					}
				}
			}
		});

		VBox contentBox = new VBox(10);
		ToggleGroup group = new ToggleGroup();
		VBox toggleBox = new VBox(10);
		Label questionType = new Label("Question Type:");
		RadioButton text = new RadioButton("Text");
		text.setToggleGroup(group);
		RadioButton multipleChoice = new RadioButton("Multiple Choice");
		multipleChoice.setToggleGroup(group);
		RadioButton pdf = new RadioButton("PDF");
		pdf.setToggleGroup(group);

		// Weight text box setup
		TextField weightTextField = new TextField();
		weightTextField.setId("assignment-weight-textfield");
		weightTextField.setMaxWidth(100);
		weightTextField.setText(Integer.toString(question.getWeight()));
		weightTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				try {
					int newWeight = Integer.parseInt(newValue);
					currentAssignment.getQuestions().get(getIndex(questionTab)).setWeight(newWeight);
				} catch (NumberFormatException e) {
					currentAssignment.getQuestions().get(getIndex(questionTab)).setWeight(0);
				}
			}
		});

		Label weightLabel = new Label("Question Weight:");
		weightLabel.setMaxWidth(Double.MAX_VALUE);

		VBox weightVBox = new VBox(10);
		weightVBox.getChildren().addAll(weightLabel, weightTextField);
		HBox.setHgrow(weightVBox, Priority.ALWAYS);

		VBox questionBox = new VBox(10);
		HBox questionTypeAndWeightBox = new HBox(20);
		questionTypeAndWeightBox.getChildren().addAll(toggleBox, weightVBox);
		contentBox.getChildren().addAll(questionTypeAndWeightBox, questionBox);

		// Initialize
		if (question.getFormat() == Format.STRING) {
			group.selectToggle(text);
			StringQuestion stringQuestion = (StringQuestion) question;
			StringAssignmentAnswer stringAnswer = (StringAssignmentAnswer) answer;
			// Question text box setup
			setContentForStringQuestion(questionTab, questionBox, weightTextField, stringQuestion.getValue(),
					stringAnswer.getValue());
		} else if (question.getFormat() == Format.MULTIPLE_CHOICE) {
			group.selectToggle(multipleChoice);
			MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
			MultipleChoiceAnswer multipleChoiceAnswer = (MultipleChoiceAnswer) answer;
			setContentForMultipleChoiceQuestion(questionTab, questionBox, weightTextField,
					multipleChoiceQuestion.getQuestion(), multipleChoiceAnswer.getCorrectOption(),
					multipleChoiceQuestion.getMultipleChoiceOptions(), multipleChoiceQuestion.getId());
		} else {
			group.selectToggle(pdf);
			PDFAssignmentQuestion pdfQuestion = (PDFAssignmentQuestion) question;
			StringAssignmentAnswer stringAnswer = (StringAssignmentAnswer) answer;
			setContentForPDFQuestion(questionTab, questionBox, weightTextField, pdfQuestion.getFilePath(),
					stringAnswer.getValue());
		}

		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (!oldValue.equals(text) && newValue.equals(text)) {
					currentAssignment.addQuestion(getIndex(questionTab), new StringQuestion("Question: ", 1));
					currentAssignment.addAnswer(getIndex(questionTab), new StringAssignmentAnswer("Answer: "));

					StringQuestion stringQuestion = (StringQuestion) currentAssignment.getQuestions()
							.get(getIndex(questionTab));
					StringAssignmentAnswer stringAnswer = (StringAssignmentAnswer) currentAssignment.getAnswers()
							.get(getIndex(questionTab));
					// Question text box setup
					setContentForStringQuestion(questionTab, questionBox, weightTextField, stringQuestion.getValue(),
							stringAnswer.getValue());
				}
				if (!oldValue.equals(multipleChoice) && newValue.equals(multipleChoice)) {
					currentAssignment.addQuestion(getIndex(questionTab), new MultipleChoiceQuestion("Question: ",
							new MultipleChoiceOptions(new ArrayList<String>()), 1));
					currentAssignment.addAnswer(getIndex(questionTab), new MultipleChoiceAnswer(-1));

					MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) currentAssignment
							.getQuestions().get(getIndex(questionTab));
					MultipleChoiceAnswer multipleChoiceAnswer = (MultipleChoiceAnswer) currentAssignment.getAnswers()
							.get(getIndex(questionTab));
					setContentForMultipleChoiceQuestion(questionTab, questionBox, weightTextField,
							multipleChoiceQuestion.getQuestion(), multipleChoiceAnswer.getCorrectOption(),
							new MultipleChoiceOptions(new ArrayList<String>()), null);
				}
				if (!oldValue.equals(pdf) && newValue.equals(pdf)) {
					currentAssignment.addQuestion(getIndex(questionTab), new PDFAssignmentQuestion("", 1));
					currentAssignment.addAnswer(getIndex(questionTab), new StringAssignmentAnswer("Answer: "));

					PDFAssignmentQuestion pdfQuestion = (PDFAssignmentQuestion) currentAssignment.getQuestions()
							.get(getIndex(questionTab));
					StringAssignmentAnswer stringAnswer = (StringAssignmentAnswer) currentAssignment.getAnswers()
							.get(getIndex(questionTab));
					setContentForPDFQuestion(questionTab, questionBox, weightTextField, pdfQuestion.getFilePath(),
							stringAnswer.getValue());
				}
			}
		});

		toggleBox.getChildren().addAll(questionType, text, multipleChoice, pdf);

		contentBox.setPadding(new Insets(10, 0, 0, 0));
		questionTab.setContent(contentBox);
		questionTabPane.getTabs().add(questionTab);
	}

	private void addQuestionAnswerTextArea() {
		addQuestionInput(new StringQuestion("Question: ", 1), new StringAssignmentAnswer("Answer: ", 1));
	}

	private void setContentForStringQuestion(Tab questionTab, VBox questionBox, TextField weightTextField,
			String question, String answer) {
		questionBox.getChildren().clear();
		// Question text box setup
		TextArea newQuestionTextArea = new TextArea();
		newQuestionTextArea.setPrefWidth(560);
		newQuestionTextArea.setMaxWidth(Double.MAX_VALUE);
		newQuestionTextArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int newWeight = Integer.parseInt(weightTextField.getText());
				currentAssignment.addQuestion(getIndex(questionTab),
						new StringQuestion(newValue, newWeight, getIndex(questionTab)));
			}
		});
		newQuestionTextArea.setText(question);

		TextArea newAnswerTextArea = new TextArea();
		newAnswerTextArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentAssignment.addAnswer(getIndex(questionTab),
						new StringAssignmentAnswer(newValue, getIndex(questionTab)));
			}
		});

		newAnswerTextArea.setText(answer);

		questionBox.getChildren().addAll(newQuestionTextArea, newAnswerTextArea);
	}

	private void setContentForPDFQuestion(Tab questionTab, VBox questionBox, TextField weightTextField, String filePath,
			String answer) {
		questionBox.getChildren().clear();

		Label label = new Label("File selected: " + filePath);
		if (filePath.equals("")) {
			label.setText("No file selected");
		}

		ImageButton openButton = new ImageButton("Open", "add-file.png");
		openButton.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("PDF", "*.pdf"));
			File selectedFile = fileChooser.showOpenDialog(stage);

			if (selectedFile != null) {
				PDFAssignmentQuestion question = (PDFAssignmentQuestion) currentAssignment.getQuestions()
						.get(getIndex(questionTab));
				question.setFilePath(selectedFile.getAbsolutePath());
				label.setText(selectedFile.getName());
			}
		});

		TextArea newAnswerTextArea = new TextArea();
		newAnswerTextArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentAssignment.addAnswer(getIndex(questionTab),
						new StringAssignmentAnswer(newValue, getIndex(questionTab)));
			}
		});

		newAnswerTextArea.setText(answer);

		questionBox.getChildren().addAll(new Separator(), openButton.getButton(), label, newAnswerTextArea);
	}

	private void setContentForMultipleChoiceQuestion(Tab questionTab, VBox questionBox, TextField weightTextField,
			String question, int answer, MultipleChoiceOptions multipleChoiceOptions, Integer questionId) {
		questionBox.getChildren().clear();

		List<String> options = new ArrayList<String>();
		List<CheckBox> allCheckBoxes = new ArrayList<CheckBox>();
		multipleChoiceOptions.getOptionsFilePaths().clear();

		TextArea newQuestionTextArea = new TextArea();
		newQuestionTextArea.setPrefWidth(560);
		newQuestionTextArea.setMaxWidth(Double.MAX_VALUE);
		newQuestionTextArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				int newWeight = Integer.parseInt(weightTextField.getText());
				MultipleChoiceQuestion question = ((MultipleChoiceQuestion) currentAssignment.getQuestions()
						.get(getIndex(questionTab)));
				question.setQuestion(newValue);
				question.setWeight(newWeight);
			}
		});
		newQuestionTextArea.setText(question);

		Button addOptionButton = new Button("Add Option");
		addOptionButton.setMinWidth(200);
		addOptionButton.setMaxWidth(Double.MAX_VALUE);

		VBox allOptions = new VBox(10);
		allOptions.getChildren().add(addOptionButton);
		allOptions.setPadding(new Insets(0, 10, 0, 0));

		for (String s : multipleChoiceOptions.getOptions()) {
			allOptions.getChildren().add(addOption(s, answer, questionId, options.size(), options, allCheckBoxes,
					questionTab, weightTextField));
		}

		HBox.setHgrow(allOptions, Priority.ALWAYS);
		addOptionButton.setOnAction(e -> {
			allOptions.getChildren().add(addOption("", answer, questionId, options.size(), options, allCheckBoxes,
					questionTab, weightTextField));
		});

		HBox fullContent = new HBox(20);
		fullContent.getChildren().addAll(newQuestionTextArea, allOptions);

		questionBox.getChildren().addAll(fullContent);
	}

	private Node addOption(String optionToAdd, int answer, Integer questionId, int newIndex, List<String> options,
			List<CheckBox> allCheckBoxes, Tab questionTab, TextField weightTextField) {
		options.add(optionToAdd);

		CheckBox checkBox = new CheckBox();
		allCheckBoxes.add(checkBox);

		if (answer >= 0 && answer < allCheckBoxes.size()) {
			for (int i = 0; i < allCheckBoxes.size(); i++) {
				if (i == answer) {
					allCheckBoxes.get(answer).setSelected(true);
				} else {
					allCheckBoxes.get(i).setSelected(false);
				}
			}
		}

		HBox optionBox = new HBox(10);
		optionBox.setAlignment(Pos.CENTER_LEFT);

		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					currentAssignment.addAnswer(getIndex(questionTab), new MultipleChoiceAnswer(newIndex));
					for (CheckBox c : allCheckBoxes) {
						if (checkBox != c) {
							c.setSelected(false);
						}
					}
				}
			}
		});

		TextField optionTextField = new TextField(optionToAdd);
		optionTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				options.set(newIndex, newValue);
				MultipleChoiceQuestion question = ((MultipleChoiceQuestion) currentAssignment.getQuestions()
						.get(getIndex(questionTab)));
				question.getMultipleChoiceOptions().setOptions(options);
			}
		});
		optionBox.getChildren().addAll(checkBox, optionTextField);
		HBox.setHgrow(optionTextField, Priority.ALWAYS);
		return optionBox;
	}

	private int getIndex(Tab tab) {
		return (Integer) tab.getUserData();
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
