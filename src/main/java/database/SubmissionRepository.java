package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import core.Submission;

public class SubmissionRepository extends Repository {
	public static final String TABLE_NAME = "Submission";
	private static final String TABLE_CREATION_SQL = "CREATE TABLE Submission ("
			+ "SubmissionId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " + "AssignmentId INT NOT NULL, "
			+ "UserId INT NOT NULL, " + "Mark INT NOT NULL);";
	private static Logger logger = Logger.getLogger(SubmissionRepository.class.getName());

	public SubmissionRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	public int insert(Submission submission) {
		logger.info("Inserting submission with user id: " + submission.getUserId() + " assignment id: "
				+ submission.getAssignmentId() + " mark: " + submission.getMark());
		return insert(submission.getUserId(), submission.getAssignmentId(), submission.getMark());
	}

	public int insert(int userId, int assignmentId, int mark) {
		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			preparedStatement = connection
					.prepareStatement("insert into Submission (" + "UserId, AssignmentId, Mark" + ") values(?,?,?)");

			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, assignmentId);
			preparedStatement.setInt(3, mark);
			count = preparedStatement.executeUpdate();

			logger.info("Submission was succesfully inserted into Submission table");
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to insert to Submission table. " + sqle);
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

	/**
	 * Returns a list of submissions for the specified user and assignment. If
	 * assignmentId is set to null, this returns all submissions for the user.
	 * 
	 * @param userId
	 *            The UserId of the User
	 * @param assignmentId
	 *            The AssignmentId of the Assignment
	 * @return List of Submissions
	 */
	public List<Submission> getSubmission(Integer userId, Integer assignmentId) {
		logger.info("Getting questions for assignment with AssignmentId: " + assignmentId + " UserId: " + userId);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Submission> submissions = new ArrayList<Submission>();

		try {
			if (assignmentId == null) {
				// Return all submissions by user
				preparedStatement = connection.prepareStatement("select * from Submission where UserId = ?");
				preparedStatement.setInt(1, userId);
			} else {
				preparedStatement = connection
						.prepareStatement("select * from Submission where UserId = ? AND AssignmentId = ?");
				preparedStatement.setInt(1, userId);
				preparedStatement.setInt(2, assignmentId);
			}
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				logger.info("Submission found. " + "SubmissionId: " + resultSet.getInt(1) + ", AssignmentId: "
						+ resultSet.getInt(2) + ", UserId: " + resultSet.getInt(3) + ", Mark: " + resultSet.getInt(4));

				Submission submission = new Submission(resultSet.getInt(3), resultSet.getInt(2), resultSet.getInt(1),
						resultSet.getInt(4));
				submissions.add(submission);
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
		return submissions;
	}
}
