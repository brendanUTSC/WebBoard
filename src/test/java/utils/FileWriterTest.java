package utils;

public class FileWriterTest {
	/**
	 * This test relies on the file 'C:\devel\apdf.pdf' existing.
	 */
	// @Test
	public void testCopyFile() {
		FileWriter.createDirectory("C:\\devel\\NewDir");
		FileWriter.copyFile("C:\\devel\\apdf.pdf", "C:\\devel\\NewDir\\Copied.pdf");
	}
}
