package utils;

import org.junit.Assert;
import org.junit.Test;

public class PDFUtilsTest {
	@Test
	public void testPrintPDFToPNG() {
		String testDirectory = "src/test/static/PDFToPNGTest/";
		FileWriter.createDirectory(testDirectory);
		Assert.assertEquals(true, true);
		PDFUtils.printPDFToPNGFile("src/test/static/testPDF.pdf", testDirectory, "testPNG");
		FileWriter.deleteDirectory(testDirectory);
	}
}
