package controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.ResourceUtils;

/**
 * Creates a Button with an Image icon.
 * 
 * @author Brendan Zhang
 *
 */
public class ImageButton {
	private Button button;

	/**
	 * Creates the ImageButton. The fileName corresponds to image file that will be
	 * used by the ImageButton. The image file must be located in
	 * {@code src/main/resources/images}.
	 * 
	 * @param buttonText
	 *            Text of the Button
	 * @param fileName
	 *            File name of the Image to be placed in the Buttons
	 */
	public ImageButton(String buttonText, String fileName) {
		Image iconImage = ResourceUtils.getImageFromResources(fileName);
		ImageView iconImageView = new ImageView(iconImage);
		iconImageView.setPreserveRatio(true);
		iconImageView.setFitHeight(15);

		button = new Button(buttonText, iconImageView);
	}

	public void setOnAction(EventHandler<ActionEvent> e) {
		button.setOnAction(e);
	}

	public Button getButton() {
		return button;
	}
}
