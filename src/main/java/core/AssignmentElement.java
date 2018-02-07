package core;

/**
 * An element (either question or answer) that is part of an
 * {@link IAssignment}. The user of this abstract class has complete control
 * over how the question is internally stored, displayed and saved locally.
 * 
 * @author Brendan Zhang
 *
 */
public abstract class AssignmentElement {
	// The Assignment that the AssignmentElement belongs to
	private Assignment assignment;
	// This refers to the QuestionId or AnswerId (which is used as the primary key
	// for their respective table) for the AssignmentElement
	private int id;
	private String filePath;
	/**
	 * The format of the AssignmentElement. This is used to determine whether the
	 * question is a multiple choice question or text question and will affect how
	 * the AssignmentElement is displayed, saved and retrieved.
	 */
	private int format;

	/**
	 * This class contains the constants that correspond to the different types of
	 * AssignmentElement formats.
	 */
	public static class Format {
		public static int STRING = 1;
		public static int MULTIPLE_CHOICE = 2;
		public static int PDF = 3;
	}

	/**
	 * Question/answer number of the AssignmentElement in the current assignment.
	 * This is implicitly set when the AssignmentElement is added to an Assignment.
	 *
	 * @see Assignment#addQuestion(AssignmentElement)
	 * @see Assignment#addAnswer(AssignmentElement)
	 */
	private int index;

	/**
	 * Saves the AssignmentElement onto the user's machine and changes the filePath
	 * of the AssignmentElement to the newly-created file.
	 * 
	 * @return the path to the AssignmentElement file
	 */
	public abstract String saveToLocalMachine();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}
}
