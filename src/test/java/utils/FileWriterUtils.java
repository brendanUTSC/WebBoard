package utils;

import org.junit.Assert;
import org.junit.Test;

public class FileWriterUtils {
	
	@Test
	public void testGetDownloadsFolder() {
		Assert.assertTrue(FileWriter.getDownloadsFolder().contains("Download"));
	}
}
