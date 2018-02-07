package utils;

import java.nio.file.Files;
import java.nio.file.Paths;

import core.Assignment;
import core.AssignmentElement;

/**
 * Class for interacting with the user's Local application data directory.
 * 
 * Contains method for creating an assignment in the user's local application
 * data directory.
 * 
 * @author Brendan Zhang
 *
 */
public class ApplicationDataDirectoryUtils {
	private static String DIRECTORY_NAME = "WebWorks";

	/**
	 * Gets the path to the user's local application data directory. For windows,
	 * this defaults to 'C:\Users\{User}\AppData\Local'.
	 * 
	 * @return path to the user's local application data directory
	 */
	public static String getLocalAppDataPath() {
		return System.getenv("LOCALAPPDATA");
	}

	/**
	 * Gets the path to the directory used for storing application data for this
	 * application. If the directory does not exist, it creates it and returns the
	 * path to the directory. If the directory was not able to be created, it
	 * returns null.
	 * 
	 * @return path to the application data directory
	 */
	public static String getAppDataDirectoryPath() {
		String expectedPath = getLocalAppDataPath() + "\\" + DIRECTORY_NAME;
		if (!Files.exists(Paths.get(expectedPath))) {
			FileWriter.createDirectory(expectedPath);
		}
		return expectedPath;
	}

	/**
	 * Creates a new directory to store the assignment and saves all of the
	 * questions and answers into the assignment directory.
	 * 
	 * @param assignment
	 *            Assignment to create. The Assignment must have a valid name, list
	 *            of questions, and list of answers.
	 * 
	 * @return path to the new assignment directory
	 */
	public static String createAssignment(Assignment assignment) {
		int index = 0;

		String assignmentDirectoryPath = createAssignmentDirectory(assignment);

		while (index < assignment.getQuestions().size()) {
			AssignmentElement question = assignment.getQuestions().get(index);
			AssignmentElement answer = assignment.getAnswers().get(index);
			question.setFilePath(question.saveToLocalMachine());
			answer.setFilePath(answer.saveToLocalMachine());
			index++;
		}

		return assignmentDirectoryPath;
	}

	public static String createAssignmentDirectory(Assignment assignment) {
		String assignmentDirectoryPath = getAppDataDirectoryPath() + "\\" + assignment.getName();
		String questionsDirectoryPath = assignmentDirectoryPath + "\\questions";
		String answersDirectoryPath = assignmentDirectoryPath + "\\answers";
		String optionsDirectoryPath = assignmentDirectoryPath + "\\options\\";
		String pdfImagesPath = assignmentDirectoryPath + "\\pdf-images\\";

		FileWriter.createDirectory(assignmentDirectoryPath);
		FileWriter.createDirectory(questionsDirectoryPath);
		FileWriter.createDirectory(answersDirectoryPath);
		FileWriter.createDirectory(optionsDirectoryPath);
		FileWriter.createDirectory(pdfImagesPath);
		return assignmentDirectoryPath;
	}
}
