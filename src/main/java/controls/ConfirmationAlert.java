package controls;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;
import utils.ResourceUtils;

public class ConfirmationAlert {
	/**
	 * Displays a Success Alert with default text.
	 */
	public static void display() {
		display("Success!");
	}
	
	public static void display(String successText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("");
		alert.setHeaderText("");
		alert.setContentText(successText);

		ImageView checkImageView = new ImageView(ResourceUtils.getImageFromResources("checked.png"));
		checkImageView.setPreserveRatio(true);

		alert.initStyle(StageStyle.UTILITY);
		alert.setGraphic(checkImageView);
		alert.getDialogPane().getStylesheets().add(ResourceUtils.PRIMARY_STYLESHEET);
		alert.getDialogPane().getStyleClass().add("myDialog");
		alert.showAndWait();
	}
}
