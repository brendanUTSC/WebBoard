package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Utility class for reading files.
 * 
 * @author Brendan Zhang
 *
 */
public class FileReader {
	private static final Logger logger = Logger.getLogger(FileReader.class.getSimpleName());

	/**
	 * Reads the contents of a file into a String, using the OS default character
	 * set.
	 * 
	 * @param filePath
	 *            Path to the file
	 * @return String that contains the contents of the file.
	 */
	public static String readFile(String filePath) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(filePath));
			return new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {
			logger.severe("Failed to read file: " + filePath + ". " + e);
			return null;
		}
	}

	/**
	 * Gets the FileInputStream for the file.
	 * 
	 * @param filePath
	 *            Path to the file
	 * @return FileInputStream of the file
	 */
	public static FileInputStream getFileStream(String filePath) {
		File file = new File(filePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			logger.severe("An error occurred trying to read " + filePath + ". " + fnfe);
		}
		return fis;
	}
}
