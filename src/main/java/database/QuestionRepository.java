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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import core.AssignmentElement;
import core.AssignmentElement.Format;
import core.question.AssignmentQuestion;
import core.question.MultipleChoiceQuestion;
import core.question.PDFAssignmentQuestion;
import core.question.StringQuestion;
import core.MultipleChoiceOptions;
import utils.FileWriter;

public class QuestionRepository extends Repository {
	private static Logger logger = Logger.getLogger(QuestionRepository.class.getSimpleName());
	private static final String TABLE_NAME = "Question";
	private static final String TABLE_CREATION_SQL = "CREATE TABLE Question ("
			+ "QuestionId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " + "QuestionFile MEDIUMBLOB NOT NULL, "
			+ "QuestionTotalMarks INT NOT NULL, " + "AssignmentId INT NOT NULL, " + "QuestionNumber INT NOT NULL, "
			+ "QuestionFormat INT NOT NULL);";

	public QuestionRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	public int insert(AssignmentQuestion assignmentQuestion) {
		return insert(assignmentQuestion.getFilePath(), assignmentQuestion.getWeight(),
				assignmentQuestion.getAssignment().getAssignmentId(), assignmentQuestion.getIndex(),
				assignmentQuestion.getFormat());
	}

	public int insert(String questionFilePath, int totalMarks, int assignmentId, int questionNumber,
			int questionFormat) {
		File file = new File(questionFilePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			logger.severe("An error occurred trying to read " + questionFilePath + ". " + fnfe);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int questionId = -1;
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO Question ("
					+ "QuestionFile, QuestionTotalMarks, AssignmentId, QuestionNumber, QuestionFormat"
					+ ") values(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

			preparedStatement.setBinaryStream(1, fis, (int) file.length());
			preparedStatement.setInt(2, totalMarks);
			preparedStatement.setInt(3, assignmentId);
			preparedStatement.setInt(4, questionNumber);
			preparedStatement.setInt(5, questionFormat);
			preparedStatement.executeUpdate();

			resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				questionId = resultSet.getInt(1);
			}

			logger.info("Question was succesfully inserted into Question table");
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to insert to Question table. " + sqle);
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

		return questionId;
	}

	/**
	 * Returns all of the questions for the given Assignment.
	 * 
	 * @param assignmentId
	 * @return
	 */
	public List<AssignmentQuestion> getQuestionsForAssignment(int assignmentId) {
		return getQuestionsForAssignment(assignmentId, null);
	}

	/**
	 * Deletes all questions for a specified Assignment.
	 * 
	 * @param assignmentId
	 *            AssignmentId of the Assignment
	 * @return The number of questions deleted
	 */
	public int deleteAllQuestionsForAssignment(int assignmentId) {
		logger.info("Deleting questions for assignment with AssignmentId: " + assignmentId);
		PreparedStatement preparedStatement = null;
		String sqlQuery = "DELETE FROM " + TABLE_NAME + " where AssignmentId = ?";
		int rowsDeleted = 0;
		// Get questions to delete options later
		List<AssignmentQuestion> questions = getQuestionsForAssignment(assignmentId);

		try {
			preparedStatement = connection.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, assignmentId);
			rowsDeleted = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			logger.severe("An error occurred trying to execute an SQL query. " + e);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
			}
		}

		QuestionOptionRepository questionOptionRepository = new QuestionOptionRepository();
		for (AssignmentQuestion question : questions) {
			questionOptionRepository.deleteAllOptionsFromQuestion(question.getId());
		}

		return rowsDeleted;
	}

	/**
	 * Returns the question in the Question table based on the question number of an
	 * assignment.
	 * 
	 * @param assignmentId
	 *            AssignmentId of the assignment
	 * @param questionNumber
	 *            QuestionNumber of the question to return
	 * 
	 * @return List of all of the questions for the assignment
	 */
	public List<AssignmentQuestion> getQuestionsForAssignment(int assignmentId, Integer questionNumber) {
		logger.info("Getting questions for assignment with AssignmentId: " + assignmentId);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<AssignmentQuestion> questions = new ArrayList<AssignmentQuestion>();

		try {
			if (questionNumber == null) {
				// Return all answers from the assignment
				preparedStatement = connection
						.prepareStatement("select * from " + TABLE_NAME + " where AssignmentId = ?");
				preparedStatement.setInt(1, assignmentId);
			} else {
				preparedStatement = connection.prepareStatement(
						"select * from " + TABLE_NAME + " where AssignmentId = ? AND QuestionNumber = ?");
				preparedStatement.setInt(1, assignmentId);
				preparedStatement.setInt(2, questionNumber);
			}

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				logger.info(TABLE_NAME + " found." + "QuestionId: " + resultSet.getInt(1) + ", QuestionTotalMarks: "
						+ resultSet.getInt(3) + ", AssignmentId: " + resultSet.getInt(4) + ", QuestionNumber: "
						+ resultSet.getInt(5) + ", QuestionFormat: " + resultSet.getInt(6));

				Blob questionFile = resultSet.getBlob(2);
				InputStream binaryStream = questionFile.getBinaryStream(1, questionFile.length());

				int questionId = resultSet.getInt(1);
				int questionWeight = resultSet.getInt(3);
				int questionFormat = resultSet.getInt(6);

				AssignmentQuestion question = null;

				if (questionFormat == Format.STRING) {
					StringWriter stringWriter = new StringWriter();
					IOUtils.copy(binaryStream, stringWriter, "UTF-8");
					String questionContents = stringWriter.toString();
					question = new StringQuestion(questionContents, questionWeight);
				}

				if (questionFormat == Format.MULTIPLE_CHOICE) {
					// Initialize question to have no options
					StringWriter stringWriter = new StringWriter();
					IOUtils.copy(binaryStream, stringWriter, "UTF-8");
					String questionContents = stringWriter.toString();
					question = new MultipleChoiceQuestion(questionContents,
							new MultipleChoiceOptions(new ArrayList<String>()), questionWeight);
				}

				if (questionFormat == Format.PDF) {
					String downloadFile = FileWriter.getDownloadsFolder() + questionId + ".pdf";
					FileUtils.copyInputStreamToFile(binaryStream, new File(downloadFile));
					question = new PDFAssignmentQuestion(downloadFile, questionWeight);
				}

				if (question != null) {
					question.setId(questionId);
					questions.add(question);
				}
			}

			for (AssignmentQuestion question : questions) {
				// Get options for each of the multiple choice questions
				if (question.getFormat() == Format.MULTIPLE_CHOICE) {
					QuestionOptionRepository optionRepository = new QuestionOptionRepository();
					MultipleChoiceOptions options = optionRepository.getOptionsForQuestion(question.getId());
					((MultipleChoiceQuestion) question).setOptions(options);
				}
			}
		} catch (SQLException e) {
			logger.severe("An error occurred trying to execute an SQL query. " + e);
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
		return questions;
	}

	/**
	 * Updates a question in the Question table.
	 * 
	 * @param assignmentId
	 *            The AssignmentId of the question to update
	 * @param questionNumber
	 *            The QuestionNumber (or {@link AssignmentElement#getIndex()} of the
	 *            question to update
	 * @param updatedQuestion
	 *            The updated question
	 * @return {@code true} iff the update successfully updated a single row.
	 *         Otherwise, return {@code false}.
	 */
	public boolean update(int assignmentId, int questionNumber, AssignmentQuestion updatedQuestion) {
		List<AssignmentQuestion> questions = getQuestionsForAssignment(assignmentId);
		if (questionNumber < questions.size()) {
			AssignmentElement question = getQuestionsForAssignment(assignmentId).get(questionNumber);
			logger.info("Updating question with QuestionId: " + question.getId());
			return update(question.getId(), updatedQuestion.getFilePath(), updatedQuestion.getWeight(),
					updatedQuestion.getAssignment().getAssignmentId(), updatedQuestion.getIndex(),
					updatedQuestion.getFormat());
		}
		return false;
	}

	/**
	 * Updates a question in the Question table.
	 * 
	 * @param questionId
	 *            The QuestionId of the question to update
	 * @param questionFilePath
	 *            The updated question file path
	 * @param totalMarks
	 *            The updated total marks for the question
	 * @param assignmentId
	 *            The updated assignmentId
	 * @param questionNumber
	 *            The updated question number
	 * @param questionFormat
	 *            The updated question format
	 * @return {@code true} iff the update successfully updated a single row.
	 *         Otherwise, return {@code false}.
	 */
	private boolean update(int questionId, String questionFilePath, int totalMarks, int assignmentId,
			int questionNumber, int questionFormat) {
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
			preparedStatement = connection.prepareStatement(
					"UPDATE Question SET " + "QuestionFile = ?, " + "QuestionTotalMarks = ?, " + "AssignmentId = ?, "
							+ "QuestionNumber = ?, " + "QuestionFormat = ? " + "WHERE QuestionId = ?");

			preparedStatement.setBinaryStream(1, fis, (int) file.length());
			preparedStatement.setInt(2, totalMarks);
			preparedStatement.setInt(3, assignmentId);
			preparedStatement.setInt(4, questionNumber);
			preparedStatement.setInt(5, questionFormat);
			preparedStatement.setInt(6, questionId);
			count = preparedStatement.executeUpdate();

			logger.info("Question was succesfully updated in Question table");
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to update a row in the Question table. " + sqle);
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
