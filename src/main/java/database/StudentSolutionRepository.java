package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import core.studentsolution.StringStudentSolution;
import core.studentsolution.StudentSolution;

public class StudentSolutionRepository extends Repository {
	private static Logger logger = Logger.getLogger(StudentSolutionRepository.class.getName());
	public static final String TABLE_NAME = "StudentSolution";
	private static final String TABLE_CREATION_SQL = "CREATE TABLE StudentSolution ("
			+ "StudentSolutionId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " + "UserId INT NOT NULL, "
			+ "AssignmentId INT NOT NULL, " + "QuestionNumber INT NOT NULL, " + "StudentAnswer TEXT NOT NULL, "
			+ "Mark INT NOT NULL, " + "MaximumMark INT NOT NULL);";

	public StudentSolutionRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	public int insert(StudentSolution solution) {
		return insert(solution.getStudentId(), solution.getAssignmentId(), solution.getQuestionNumber(),
				solution.getStringValue(), solution.getMark(), solution.getMaximumMark());
	}

	public int insert(int userId, int assignmentId, int questionNumber, String studentAnswer, int mark,
			int maximumMark) {
		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			logger.info("Inserting StudentSolution with userId: " + userId + ", AssignmentId: " + assignmentId
					+ ", QuestionNumber: " + questionNumber + ", studentAnswer: " + studentAnswer + ", mark: " + mark);
			preparedStatement = connection.prepareStatement("insert into StudentSolution ("
					+ "UserId, AssignmentId, QuestionNumber, StudentAnswer, Mark, MaximumMark"
					+ ") values(?,?,?,?,?,?)");

			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, assignmentId);
			preparedStatement.setInt(3, questionNumber);
			preparedStatement.setString(4, studentAnswer);
			preparedStatement.setInt(5, mark);
			preparedStatement.setInt(6, mark);

			count = preparedStatement.executeUpdate();

			logger.info("StudentSolution was succesfully inserted into StudentSolution table");
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to insert to StudentSolution table. " + sqle);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
			}
		}
		return count;
	}

	public List<StringStudentSolution> getStudentSolutions(Integer userId, Integer assignmentId, int questionNumber) {
		logger.info("Getting StudentSolution for student with user Id: " + userId
				+ " and assignment with AssignmentId: " + assignmentId + ", QuestionNumber: " + questionNumber);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<StringStudentSolution> studentSolutions = new ArrayList<StringStudentSolution>();

		try {
			preparedStatement = connection.prepareStatement(
					"select * from StudentSolution where UserId = ? AND AssignmentId = ? AND QuestionNumber = ?");
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, assignmentId);
			preparedStatement.setInt(3, questionNumber);

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				logger.info("StudentSolution found.");
				logger.info("StudentSolutionId: " + resultSet.getInt(1));
				logger.info("UserId: " + resultSet.getInt(2));
				logger.info("AssignmentId: " + resultSet.getInt(3));
				logger.info("QuestionNumber: " + resultSet.getInt(4));
				logger.info("StudentAnswer: " + resultSet.getString(5));
				logger.info("Mark: " + resultSet.getInt(6));
				logger.info("MaximumMark: " + resultSet.getInt(7));

				StringStudentSolution studentSolution = new StringStudentSolution(resultSet.getInt(2),
						resultSet.getInt(3), resultSet.getInt(4), resultSet.getString(5), resultSet.getInt(6),
						resultSet.getInt(7));
				studentSolution.setStudentSolutionId(resultSet.getInt(1));
				studentSolutions.add(studentSolution);
			}
		} catch (SQLException e) {
			logger.severe("An error occurred trying to execute an SQL query." + e);
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

		return studentSolutions;
	}

	public boolean deleteStudentSolution(Integer studentSolutionId) {
		logger.info("Deleting StudentSolution with StudentSolutionId: " + studentSolutionId);
		PreparedStatement preparedStatement = null;
		Integer success = null;

		try {
			preparedStatement = connection.prepareStatement("delete from StudentSolution where StudentSolutionId = ?");
			preparedStatement.setInt(1, studentSolutionId);

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
}
