package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import core.Assignment;
import core.User;
import core.answer.AssignmentAnswer;
import core.question.AssignmentQuestion;

/**
 * Class used for interacting with the Assignment table in the database.
 * Contains methods for creating, editing and retrieving a list of all of the
 * assignments from the database.
 * 
 * @author Brendan Zhang
 *
 */
public class AssignmentRepository extends Repository {
	public static final String TABLE_NAME = "Assignment";
	private static final String EXCLUDE_ID = "AssignmentName, AssignmentDeadline, CourseId";
	private static final String TABLE_CREATION_SQL = "CREATE TABLE " + TABLE_NAME + " ("
			+ "AssignmentId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " + "AssignmentName VARCHAR(250) NOT NULL, "
			+ "AssignmentDeadline DATETIME NOT NULL, " + "CourseId VARCHAR(6) NOT NULL" + ");";
	private static final Logger logger = Logger.getLogger(AssignmentRepository.class.getName());

	public AssignmentRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	/**
	 * Returns a list of all the AssignmentId in the database.
	 * 
	 * @return the Assignment Id of all the assignments in the database
	 */
	private List<Integer> getAllAssignmentIds() {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Integer> assignmentIds = new ArrayList<Integer>();

		try {
			preparedStatement = connection.prepareStatement("select AssignmentId from Assignment");
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int assignmentId = resultSet.getInt(1);
				assignmentIds.add(assignmentId);
			}
		} catch (SQLException e) {
			logger.severe("An error occurred trying to get all the assignment Id's." + e);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
			}
		}

		return assignmentIds;
	}

	/**
	 * Gets all the assignments from the Assignment table.
	 * 
	 * @param removePassedDeadlines
	 *            If true, returns only assignments that have not had their deadline
	 *            passed.
	 * @return List that contains all the assignments
	 */
	public List<Assignment> getAllAssignments(boolean removePassedDeadlines) {
		List<Assignment> assignments = new ArrayList<Assignment>();

		List<Integer> assignmentIds = getAllAssignmentIds();
		for (Integer id : assignmentIds) {
			Assignment assignment = getAssignment(id);
			if (removePassedDeadlines) {
				if (!assignment.isDeadlinePassed()) {
					assignments.add(assignment);
				}
			} else {
				assignments.add(assignment);
			}
		}

		return assignments;
	}

	/**
	 * Gets all the assignments for a user.
	 * 
	 * @param user
	 *            The User
	 * @return List of Assignments that the User is allowed to view
	 */
	public List<Assignment> getAllAssignmentForUser(User user) {
		List<Assignment> assignments = getAllAssignments(false);
		UserCourseRepository userCourseRepository = new UserCourseRepository();
		List<String> courseIds = userCourseRepository.getCoursesForUser(user.getId());

		int i = 0;
		while (i < assignments.size()) {
			if (!courseIds.contains(assignments.get(i).getCourseId())) {
				assignments.remove(i);
			} else {
				i++;
			}
		}
		return assignments;
	}

	/**
	 * Returns the Assignment with the given assignment Id.
	 * 
	 * @param assignmentId
	 *            The AssignmentId of the assignment
	 * @return the Assignment
	 */
	public Assignment getAssignment(int assignmentId) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection
					.prepareStatement("select " + EXCLUDE_ID + " from " + TABLE_NAME + " where AssignmentId = ?");
			preparedStatement.setInt(1, assignmentId);

			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				Assignment assignment = new Assignment();
				assignment.setAssignmentId(assignmentId);
				assignment.setName(resultSet.getString(1));
				assignment.setDeadline(resultSet.getTimestamp(2).toLocalDateTime());
				assignment.setCourseId(resultSet.getString(3));

				QuestionRepository questionRepository = new QuestionRepository();
				// Add questions to Assignment object
				assignment.setQuestions(questionRepository.getQuestionsForAssignment(assignmentId));
				// Save a local copy of the questions onto user's machine
				for (AssignmentQuestion question : assignment.getQuestions()) {
					question.saveToLocalMachine();
				}

				AnswerRepository answerRepository = new AnswerRepository();
				// Add answers to Assignment object
				assignment.setAnswers(answerRepository.getAnswersForAssignment(assignmentId));
				// Save a local copy of the answers onto user's machine
				for (AssignmentAnswer answer : assignment.getAnswers()) {
					answer.saveToLocalMachine();
				}

				return assignment;
			}
		} catch (SQLException e) {
			logger.severe("An error occurred trying to retrieve an assignment." + e);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
			}
		}

		return null;
	}

	public boolean delete(int assignmentId) {
		logger.info("Deleting Assignment with AssignmentId: " + assignmentId);
		PreparedStatement preparedStatement = null;
		Integer success = null;

		try {
			preparedStatement = connection.prepareStatement("delete from " + TABLE_NAME + " where AssignmentId = ?");
			preparedStatement.setInt(1, assignmentId);

			success = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			logger.severe("An error occurred trying to execute an SQL query." + e);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
			}
		}

		return success == 1;
	}

	/**
	 * Updates an assignment in the Assignment table.
	 * 
	 * @param assignmentId
	 *            The AssignmentId of the Assignment to update
	 * @param assignmentName
	 *            The updated name of the Assignment
	 * @param deadline
	 *            The updated deadline of the Assignment
	 * @param courseId
	 *            The updated course Id of the Assignment
	 * @return {@code true} if the update succeeded. Otherwise, return
	 *         {@code false}.
	 */
	public boolean update(int assignmentId, String assignmentName, LocalDateTime deadline, String courseId) {
		PreparedStatement preparedStatement = null;
		int count = 0;

		try {
			preparedStatement = connection.prepareStatement("UPDATE Assignment SET "
					+ "AssignmentName = ?, AssignmentDeadline = ?, CourseId = ? WHERE AssignmentId = ?");

			preparedStatement.setString(1, assignmentName);
			preparedStatement.setTimestamp(2, Timestamp.valueOf(deadline));
			preparedStatement.setString(3, courseId);
			preparedStatement.setInt(4, assignmentId);
			count = preparedStatement.executeUpdate();
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to update a row in the " + TABLE_NAME + " table. " + sqle);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
			}
		}

		// Only one row should be updated
		return count == 1;
	}

	/**
	 * Inserts a new assignment.
	 * 
	 * @param assignment
	 *            The Assignment to insert
	 * @return the AssignmentId of the inserted Assignment. If an error occurred,
	 *         the function returns -1.
	 */
	public int insert(Assignment assignment) {
		return insert(assignment.getName(), assignment.getDeadline(), assignment.getCourseId());
	}

	/**
	 * Inserts a new assignment.
	 * 
	 * @param assignmentName
	 *            Name of the assignment
	 * @param deadline
	 *            The deadline for the assignment
	 * @return the AssignmentId of the inserted assignment. If an error occurred,
	 *         the function returns -1.
	 */
	public int insert(String assignmentName, LocalDateTime deadline, String courseId) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int assignmentId = -1;

		try {
			preparedStatement = connection.prepareStatement(
					"INSERT INTO " + TABLE_NAME + " (" + EXCLUDE_ID + ") values(?,?,?)",
					Statement.RETURN_GENERATED_KEYS);

			preparedStatement.setString(1, assignmentName);
			preparedStatement.setTimestamp(2, Timestamp.valueOf(deadline));
			preparedStatement.setString(3, courseId);
			preparedStatement.executeUpdate();

			resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				assignmentId = resultSet.getInt(1);
			}
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to insert to Assignment table. " + sqle);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
			}
		}

		return assignmentId;
	}
}
