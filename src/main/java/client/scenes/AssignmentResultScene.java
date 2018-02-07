package client.scenes;

import java.awt.Desktop;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import core.Assignment;
import core.AssignmentElement.Format;
import core.question.AssignmentQuestion;
import core.question.MultipleChoiceQuestion;
import core.question.PDFAssignmentQuestion;
import core.question.StringQuestion;
import core.studentsolution.StringStudentSolution;
import core.User;
import database.AssignmentRepository;
import database.StudentSolutionRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ResourceUtils;

public class AssignmentResultScene {
	private static final String HEADER = "Assignment Results";
	private static Logger logger = Logger.getLogger(AssignmentInputScene.class.getName());

	private Scene scene;
	private Button viewAssignmentsButton;
	private Label scoreLabel;
	private ScrollPane scrollPane;
	private VBox scrollableContentVBox;
	private Assignment assignment;

	public AssignmentResultScene(Stage stage, User user, int assignmentId, int studentMark, int maximumMark) {
		Label headerLabel = new Label(HEADER);
		headerLabel.setId("header-label");
		headerLabel.getStyleClass().add("header");

		scoreLabel = createScoreLabel(studentMark, maximumMark);

		BorderPane borderPane = new BorderPane();
		scrollableContentVBox = new VBox(20);
		scrollableContentVBox.setPadding(new Insets(10, 0, 0, 10));
		scrollableContentVBox.getChildren().addAll(headerLabel, scoreLabel);

		ToolBar toolbar = new ToolBar();
		borderPane.setTop(toolbar);

		viewAssignmentsButton = new Button("View Assignments");
		toolbar.getItems().addAll(viewAssignmentsButton);

		AssignmentRepository assignmentRepository = new AssignmentRepository();
		assignment = assignmentRepository.getAssignment(assignmentId);

		StudentSolutionRepository studentSolutionRepository = new StudentSolutionRepository();
		for (int i = 0; i < assignment.getQuestions().size(); i++) {
			List<StringStudentSolution> solutions = studentSolutionRepository.getStudentSolutions(user.getId(),
					assignment.getAssignmentId(), i);
			StringStudentSolution lastSolution = solutions.get(solutions.size() - 1);
			AssignmentQuestion question = assignment.getQuestions().get(i);

			VBox resultNode = new VBox();
			if (question.getFormat() == Format.MULTIPLE_CHOICE) {
				MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
				int selectedIndex = Integer.parseInt(lastSolution.getStringValue());
				if (selectedIndex >= 0) {
					resultNode = createResultNode(multipleChoiceQuestion.getQuestion(),
							multipleChoiceQuestion.getMultipleChoiceOptions().getOptions().get(selectedIndex),
							lastSolution.getMark() > 0, false);
				} else {
					resultNode = createResultNode(multipleChoiceQuestion.getQuestion(), "None of the Above",
							lastSolution.getMark() > 0, false);
				}
			}
			if (question.getFormat() == Format.STRING) {
				StringQuestion stringQuestion = (StringQuestion) question;
				resultNode = createResultNode(stringQuestion.getValue(), lastSolution.getStringValue(),
						lastSolution.getMark() > 0, false);
			}
			if (question.getFormat() == Format.PDF) {
				PDFAssignmentQuestion pdfQuestion = (PDFAssignmentQuestion) question;
				resultNode = createResultNode(String.valueOf(pdfQuestion.getIndex() + 1), lastSolution.getStringValue(),
						lastSolution.getMark() > 0, true);
			}
			scrollableContentVBox.getChildren().add(resultNode);
		}

		scrollPane = new ScrollPane();
		scrollPane.setContent(scrollableContentVBox);
		scrollPane.setFitToWidth(true);

		viewAssignmentsButton.setOnAction(e -> {
			stage.setScene(new AssignmentSelectionScene(stage, user).getScene());
		});

		borderPane.setCenter(scrollPane);
		setScene(new Scene(borderPane, 675, 400));
		getScene().getStylesheets().add("css/style.css");
		getScene().getStylesheets().add("css/assignment-result.css");
	}

	private Label createScoreLabel(int score, int maxScore) {
		String scorePrefix = "Your score on this assignment: ";

		Label scoreLabel = new Label(scorePrefix + score + "/" + maxScore);
		scoreLabel.setId("score-label");
		scoreLabel.getStyleClass().add("score-label");
		scoreLabel.setMaxWidth(Double.MAX_VALUE);
		scoreLabel.setAlignment(Pos.CENTER_RIGHT);

		return scoreLabel;
	}

	private VBox createResultNode(String question, String studentAnswer, boolean correct, boolean hyperLink) {
		String solutionPrefix = "Your solution: ";

		VBox resultVBox = new VBox();
		HBox questionAndResultBox = new HBox(5);

		Label questionLabel = new Label();
		questionLabel.setWrapText(true);
		questionLabel.setMinWidth(500);
		questionLabel.setMaxWidth(Double.MAX_VALUE);
		// Enable wrapping long questions over multiple lines
		questionLabel.getStyleClass().addAll("result-label");

		if (hyperLink) {
			questionLabel.setText("PDF Question " + question);
			questionLabel.getStyleClass().addAll("clickable-label", "hyper-link");
			questionLabel.setOnMouseClicked(e -> {
				String assignmentFilePath = assignment.getQuestions().get(Integer.parseInt(question) - 1).getFilePath();
				try {
					Desktop.getDesktop().open(new File(assignmentFilePath));
				} catch (Exception ex) {
					logger.severe(
							"An exception occurred trying to open the assignment: " + assignmentFilePath + ". " + ex);
				}
			});
		} else {
			questionLabel.setText(question);
		}

		Label resultLabel;
		if (correct) {
			resultLabel = createCorrectLabel();
		} else {
			resultLabel = createIncorrectLabel();
		}

		Region fillerRegion = new Region();
		HBox.setHgrow(fillerRegion, Priority.ALWAYS);

		resultLabel.setMinWidth(100);
		resultLabel.setMaxWidth(Double.MAX_VALUE);

		Label answerLabel = new Label();
		answerLabel.setWrapText(true);
		answerLabel.setMaxWidth(500);
		answerLabel.setPadding(new Insets(3, 0, 0, 0));
		answerLabel.setText(solutionPrefix + studentAnswer);
		answerLabel.getStyleClass().addAll("result-label");

		questionAndResultBox.getChildren().addAll(questionLabel, fillerRegion, resultLabel);
		resultVBox.getChildren().addAll(questionAndResultBox, answerLabel);

		return resultVBox;
	}

	private Label createCorrectLabel() {
		Label correct = new Label("Correct!  ");
		correct.getStyleClass().addAll("result-label", "correct-label");

		ImageView correctImageView = new ImageView(ResourceUtils.getImageFromResources("check-mark.png"));
		correctImageView.setPreserveRatio(true);
		correctImageView.setFitHeight(15);
		correctImageView.setFitWidth(15);
		correct.setGraphic(correctImageView);

		return correct;
	}

	private Label createIncorrectLabel() {
		Label incorrect = new Label("Incorrect!  ");
		incorrect.getStyleClass().addAll("result-label", "incorrect-label");

		ImageView incorrectImageView = new ImageView(ResourceUtils.getImageFromResources("incorrect-icon.png"));
		incorrectImageView.setPreserveRatio(true);
		incorrectImageView.setFitHeight(15);
		incorrectImageView.setFitWidth(15);
		incorrect.setGraphic(incorrectImageView);

		return incorrect;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
}
