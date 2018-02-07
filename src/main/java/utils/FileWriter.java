package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * Class that contains static methods for creating directories and files.
 * 
 * @author Brendan Zhang
 *
 */
public class FileWriter {
	private static Logger logger = Logger.getLogger(FileWriter.class.getName());

	/**
	 * Creates a directory at the specified path
	 * 
	 * @param path
	 *            The path of the directory to create
	 */
	public static void createDirectory(String path) {
		if (!Files.exists(Paths.get(path))) {
			try {
				logger.info("Creating directory: " + path);
				Files.createDirectories(Paths.get(path));
				logger.info("Directory was successfully created.");
			} catch (IOException e) {
				logger.severe("Unable to create directory. " + e);
			}
		}
	}

	/**
	 * Deletes the directory at the specified path.
	 * 
	 * @param path
	 *            The path of the directory to delete.
	 * @return True if the directory was successfully deleted. Otherwise, return
	 *         false.
	 */
	public static boolean deleteDirectory(String path) {
		try {
			FileUtils.deleteDirectory(new File(path));
			return true;
		} catch (IOException e) {
			logger.severe("Unable to delete directory. " + e);
			return false;
		}
	}

	/**
	 * Creates text file at the given file path with the given data. If the text
	 * file already exists, it will overwrite it.
	 * 
	 * @param path
	 *            The path of the file to create
	 * @param data
	 *            The data to write to the file
	 */
	public static void createTextFile(String path, List<String> data) {
		try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
			logger.info("Creating file: " + path);
			for (String s : data) {
				writer.print(s);
			}
			logger.info("File was successfully created.");
		} catch (IOException e) {
			logger.severe("Unable to create file. " + e);
		}
	}

	/**
	 * Copies file from srcPath to destPath.
	 * 
	 * @param srcPath
	 *            Source path
	 * @param destPath
	 *            Destination path
	 */
	public static void copyFile(String srcPath, String destPath) {
		try {
			FileInputStream fileInputStream = new FileInputStream(srcPath);

			FileOutputStream fileOutputStream = new FileOutputStream(destPath);
			byte[] buffer = new byte[4096];
			int bytes;

			while ((bytes = fileInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, bytes);
			}

			fileOutputStream.close();
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates text file at the given file path with the given data. If the text
	 * file already exists, it will overwrite it.
	 * 
	 * @param path
	 *            The path of the file to create
	 * @param data
	 *            The data to write to the file
	 */
	public static void createTextFile(String path, String data) {
		try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
			logger.info("Creating file: " + path);
			writer.print(data);
			logger.info("File was successfully created.");
		} catch (IOException e) {
			logger.severe("Unable to create file. " + e);
		}
	}

	/**
	 * Returns the path to the current user's Downloads folder.
	 * 
	 * @return the path to the current user's Downloads folder
	 */
	public static String getDownloadsFolder() {
		String homePath = System.getProperty("user.home");
		return homePath + "/Downloads/";
	}
}
