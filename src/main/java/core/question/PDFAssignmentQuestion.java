package core.question;

import java.io.File;
import java.util.logging.Logger;

import core.AssignmentElement;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utils.ApplicationDataDirectoryUtils;
import utils.FileWriter;
import utils.PDFUtils;

public class PDFAssignmentQuestion extends StringStudentInputQuestion {
	private static Logger logger = Logger.getLogger(PDFAssignmentQuestion.class.getSimpleName());

	public PDFAssignmentQuestion(String filePath, int weight) {
		setFilePath(filePath);
		this.setWeight(weight);
		this.setFormat(AssignmentElement.Format.PDF);
	}

	@Override
	public Node renderQuestion() {
		String questionImagePath = ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\"
				+ this.getAssignment().getName() + "\\pdf-images\\q" + this.getIndex();
		String firstImage = questionImagePath + "-0.png";

		if (!new File(firstImage).exists()) {
			logger.info("Printing PDF to image files");
			PDFUtils.printPDFToPNGFile(getFilePath(), ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\"
					+ this.getAssignment().getName() + "\\pdf-images\\", "q" + this.getIndex());
		}

		Image pdfImage = new Image("file:" + firstImage);
		ImageView previewBox = new ImageView(pdfImage);
		previewBox.setUserData(new Integer(0));
		previewBox.setPreserveRatio(true);

		HBox buttonBox = new HBox(10);

		Rectangle previousRectangle = createRectangle(pdfImage);
		previousRectangle.setOnMouseClicked(e -> {
			if ((Integer) previewBox.getUserData() > 0) {
				previewBox.setUserData(((Integer) (previewBox.getUserData())) - 1);
				previewBox.setImage(new Image("file:" + questionImagePath + "-" + previewBox.getUserData() + ".png"));
			}
		});

		Rectangle nextRectangle = createRectangle(pdfImage);
		nextRectangle.setOnMouseClicked(e -> {
			if (new File(questionImagePath + "-" + ((Integer) (previewBox.getUserData()) + 1) + ".png").exists()) {
				previewBox.setUserData(((Integer) (previewBox.getUserData())) + 1);
				previewBox.setImage(new Image("file:" + questionImagePath + "-" + previewBox.getUserData() + ".png"));
			}

		});
		buttonBox.getChildren().addAll(previousRectangle, previewBox, nextRectangle);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(buttonBox);
		scrollPane.getStyleClass().add("content-pane");

		GridPane outerPane = new GridPane();
		ColumnConstraints col = new ColumnConstraints();
		col.setHalignment(HPos.CENTER);
		outerPane.getColumnConstraints().add(col);
		outerPane.add(scrollPane, 0, 0);

		buttonBox.setAlignment(Pos.CENTER);
		outerPane.setAlignment(Pos.CENTER);

		return outerPane;
	}

	private Rectangle createRectangle(Image pdfImage) {
		Rectangle nextRectangle = new Rectangle(0, 0, 40, pdfImage.getHeight());
		nextRectangle.setFill(Color.rgb(74, 75, 78, 0.5));
		nextRectangle.setOnMouseEntered(e -> {
			nextRectangle.setFill(Color.rgb(39, 40, 40, 1));
		});
		nextRectangle.setOnMouseExited(e -> {
			nextRectangle.setFill(Color.rgb(74, 75, 78, 0.1));
		});
		return nextRectangle;
	}

	@Override
	public String saveToLocalMachine() {
		// TODO Auto-generated method stub
		ApplicationDataDirectoryUtils.createAssignmentDirectory(this.getAssignment());
		String questionFilePath;

		questionFilePath = ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\"
				+ this.getAssignment().getName() + "\\questions\\" + this.getIndex() + ".pdf";
		FileWriter.copyFile(getFilePath(), questionFilePath);

		setFilePath(questionFilePath);
		return questionFilePath;
	}
}
