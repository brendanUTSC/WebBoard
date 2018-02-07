package utils;

import org.junit.Assert;
import org.junit.Test;

import javafx.scene.image.Image;

public class ResourceUtilsTest {
	private static String existingImageFile = "pencil-icon.png";
	private static String nonExistantImageFile = "!!!abcDoesntExist.png";

	@Test
	public void testGetImageFromResources() {
		Image image = null;
		image = ResourceUtils.getImageFromResources(existingImageFile);
		Assert.assertNotNull(image);
	}

	@Test(expected = NullPointerException.class)
	public void testGetImageFromResourcesWhenImageDoesntExist() {
		ResourceUtils.getImageFromResources(nonExistantImageFile);
	}
}
