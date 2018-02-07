package utils;

import javafx.scene.image.Image;

public class ResourceUtils {
	public static final String PRIMARY_STYLESHEET = "css/style.css";
	public static final String IMAGES_DIRECTORY = "/images/";

	public static Image getImageFromResources(String imageFileName) throws NullPointerException {
		return new Image(ResourceUtils.class.getResourceAsStream(IMAGES_DIRECTORY + imageFileName));
	}
}
