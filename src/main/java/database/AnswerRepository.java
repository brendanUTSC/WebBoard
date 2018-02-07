package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import core.AssignmentElement;
import core.AssignmentElement.Format;
import core.answer.AssignmentAnswer;
import core.answer.MultipleChoiceAnswer;
import core.answer.StringAssignmentAnswer;

public class AnswerRepository extends Repository {
	public static final String TABLE_NAME = "Answer";
	private static final String TABLE_CREATION_SQL = "CREATE TABLE Answer ("
			+ "AnswerId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " + "AnswerFile BLOB NOT NULL, "
			+ "AssignmentId INT NOT NULL, " + "AnswerNumber INT NOT NULL, " + "AnswerFormat INT NOT NULL);";
	private static Logger logger = Logger.getLogger(AnswerRepository.class.getName());

	public AnswerRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	public int insert(AssignmentElement assignmentElement) {
		return insert(assignmentElement.getFilePath(), assignmentElement.getAssignment().getAssignmentId(),
				assignmentElement.getIndex(), assignmentElement.getFormat());
	}

	public int insert(String questionFilePath, int assignmentId, int answerNumber, int answerFormat) {
		File file = new File(questionFilePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			logger.severe("An error occurred trying to read " + questionFilePath + ". " + fnfe);
		}

		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			preparedStatement = connection.prepareStatement("insert into Answer ("
					+ "AnswerFile, AssignmentId, AnswerNumber, AnswerFormat" + ") values(?,?,?,?)");

			preparedStatement.setBinaryStream(1, fis, (int) file.length());
			preparedStatement.setInt(2, assignmentId);
			preparedStatement.setInt(3, answerNumber);
			preparedStatement.setInt(4, answerFormat);
			count = preparedStatement.executeUpdate();

			logger.info("Answer was succesfully inserted into Answer table");
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to insert to Answer table. " + sqle);
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
	 * Gets the answers for the assignment with the given answer number. If
	 * answerNumber is null, this function returns all the answers for the
	 * assignment.
	 * 
	 * @param assignmentId
	 *            The AssignmentId of the Assignment
	 * @param answerNumber
	 *            The AnswerNumber of the Answer to return
	 * @return List of answers
	 */
	public List<AssignmentAnswer> getAnswersForAssignment(int assignmentId, Integer answerNumber) {
		logger.info("Getting answers for assignment with AssignmentId: " + assignmentId + " and answerNumber: "
				+ answerNumber);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<AssignmentAnswer> answers = new ArrayList<AssignmentAnswer>();

		try {
			if (answerNumber == null) {
				// Return all answers from the assignment
				preparedStatement = connection.prepareStatement("select * from Answer where AssignmentId = ?");
				preparedStatement.setInt(1, assignmentId);
			} else {
				preparedStatement = connection
						.prepareStatement("select * from Answer where AssignmentId = ? AND AnswerNumber = ?");
				preparedStatement.setInt(1, assignmentId);
				preparedStatement.setInt(2, answerNumber);
			}

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				logger.info("Answer found.");
				logger.info("AnswerId: " + resultSet.getInt(1));
				logger.info("AnswerFile: " + resultSet.getBlob(2));
				logger.info("AssignmentId: " + resultSet.getInt(3));
				logger.info("AnswerNumber: " + resultSet.getInt(4));
				logger.info("AnswerFormat: " + resultSet.getInt(5));

				int answerFormat = resultSet.getInt(5);

				Blob answerFile = resultSet.getBlob(2);
				InputStream binaryStream = answerFile.getBinaryStream(1, answerFile.length());
				StringWriter stringWriter = new StringWriter();
				IOUtils.copy(binaryStream, stringWriter, "UTF-8");
				String questionContents = stringWriter.toString();

				if (answerFormat == Format.STRING) {
					logger.info("Answer contents: " + questionContents);
					AssignmentAnswer answer = new StringAssignmentAnswer(questionContents);
					answer.setId(resultSet.getInt(1));
					answer.setIndex(resultSet.getInt(4));
					answers.add(answer);
				}
				if (answerFormat == Format.MULTIPLE_CHOICE) {
					AssignmentAnswer answer = new MultipleChoiceAnswer(Integer.parseInt(questionContents));
					answer.setId(resultSet.getInt(1));
					answer.setIndex(resultSet.getInt(4));
					answers.add(answer);
				}
			}
		} catch (SQLException e) {
			logger.severe("An error occurred trying to execute an SQL query." + e);
		} catch (IOException e) {
			logger.severe("An error occurred trying to copy the InputStream to a StringWriter." + e);
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

		return answers;
	}

	public List<AssignmentAnswer> getAnswersForAssignment(int assignmentId) {
		// Return all of the answers for the assignment
		return getAnswersForAssignment(assignmentId, null);
	}

	/**
	 * Updates an answer in the Answer table.
	 * 
	 * @param assignmentId
	 *            The AssignmentId of the answer to update
	 * @param answerNumber
	 *            The AnswerNumber (or {@link AssignmentAnswer#getIndex()} of the
	 *            question to update
	 * @param updatedAnswer
	 *            The updated answer
	 * @return {@code true} iff the update successfully updated a single row.
	 *         Otherwise, return {@code false}.
	 */
	public boolean update(int assignmentId, int answerNumber, AssignmentAnswer updatedAnswer) {
		List<AssignmentAnswer> answers = getAnswersForAssignment(assignmentId);
		if (answerNumber < answers.size()) {
			AssignmentAnswer answer = getAnswersForAssignment(assignmentId).get(answerNumber);
			logger.info("Updating answer with AnswerId: " + answer.getId());
			return update(answer.getId(), updatedAnswer.getFilePath(), updatedAnswer.getAssignment().getAssignmentId(),
					updatedAnswer.getIndex(), updatedAnswer.getFormat());
		}
		return false;
	}

	/**
	 * Updates an answer in the Answer table.
	 * 
	 * @param answerId
	 *            The AnswerId of the question to update
	 * @param questionFilePath
	 *            The updated answer file path
	 * @param totalMarks
	 *            The updated total marks for the answer
	 * @param assignmentId
	 *            The updated assignmentId
	 * @param answerNumber
	 *            The updated answer number
	 * @param answerFormat
	 *            The updated answer format
	 * @return {@code true} iff the update successfully updated a single row.
	 *         Otherwise, return {@code false}.
	 */
	private boolean update(int answerId, String questionFilePath, int assignmentId, int answerNumber,
			int answerFormat) {
		logger.info("Updating question in Question table");

		File file = new File(questionFilePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			logger.severe("An error occurred trying to read " + questionFilePath + ". " + fnfe);
		}

		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			String updateSQL = "UPDATE Answer SET " + "AnswerFile = ?, " + "AssignmentId = ?, " + "AnswerNumber = ?, "
					+ "AnswerFormat = ? " + "WHERE AnswerId = ?";
			preparedStatement = connection.prepareStatement(updateSQL);

			preparedStatement.setBinaryStream(1, fis, (int) file.length());
			preparedStatement.setInt(2, assignmentId);
			preparedStatement.setInt(3, answerNumber);
			preparedStatement.setInt(4, answerFormat);
			preparedStatement.setInt(5, answerId);
			count = preparedStatement.executeUpdate();

			logger.info("Answer was succesfully updated in Answer table");
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to update a row in the Answer table. " + sqle);
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
}
