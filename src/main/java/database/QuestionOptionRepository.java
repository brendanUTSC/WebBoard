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

import core.MultipleChoiceOptions;

public class QuestionOptionRepository extends Repository {
	private static final String TABLE_NAME = "QuestionOption";
	private static final String EXCLUDE_ID = "QuestionId, OptionFile";
	private static final String ALL_PARAMS = "QuestionOptionId, " + EXCLUDE_ID;
	private static final String TABLE_CREATION_SQL = "CREATE TABLE " + TABLE_NAME + " ("
			+ "QuestionOptionId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " + "QuestionId INT NOT NULL, "
			+ "OptionFile BLOB NOT NULL" + ");";
	private static Logger logger = Logger.getLogger(QuestionRepository.class.getSimpleName());

	public QuestionOptionRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	public int insert(int questionId, List<String> optionFilePaths) {
		int sum = 0;
		for (String filePath : optionFilePaths) {
			sum += insert(questionId, filePath);
		}
		return sum;
	}

	public int insert(int questionId, String optionFilePath) {
		File file = new File(optionFilePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
			logger.severe("An error occurred trying to read " + optionFilePath + ". " + fnfe);
		}

		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			preparedStatement = connection
					.prepareStatement("insert into " + TABLE_NAME + " (" + EXCLUDE_ID + ") values(?,?)");

			preparedStatement.setInt(1, questionId);
			preparedStatement.setBinaryStream(2, fis, (int) file.length());
			count = preparedStatement.executeUpdate();

			logger.info(TABLE_NAME + " was succesfully inserted into " + TABLE_NAME + " table");
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to insert to Question table. " + sqle);
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
	 * Returns all of the options for the given Question.
	 * 
	 * @param questionId
	 *            The question's QuestionId
	 * @return The options for the Question
	 */
	public MultipleChoiceOptions getOptionsForQuestion(int questionId) {
		logger.info("Getting options for question with QuestionId: " + questionId);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		MultipleChoiceOptions options = null;

		try {
			preparedStatement = connection
					.prepareStatement("select " + ALL_PARAMS + " from " + TABLE_NAME + " where QuestionId = ?");
			preparedStatement.setInt(1, questionId);

			resultSet = preparedStatement.executeQuery();

			options = new MultipleChoiceOptions(new ArrayList<String>(), new ArrayList<String>());
			while (resultSet.next()) {
				logger.info("Option found.");

				Blob questionFile = resultSet.getBlob(3);
				InputStream binaryStream = questionFile.getBinaryStream(1, questionFile.length());
				StringWriter stringWriter = new StringWriter();
				IOUtils.copy(binaryStream, stringWriter, "UTF-8");
				String optionContents = stringWriter.toString();
				logger.info("Option contents: " + optionContents);
				options.getOptions().add(optionContents);
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
		return options;
	}

	/**
	 * Updates a question in the Question table.
	 * 
	 * @param questionOptionId
	 *            The QuestionOptionId of the QuestionOption to update
	 * @param questionId
	 *            The QuestionId of the question the QuestionOption is associated
	 *            with.
	 * @param questionOptionFile
	 *            The path to the file of the QuestionOption.
	 * 
	 * @return The number of options added/updated.
	 */
	public int update(int questionId, List<String> questionOptionFiles) {
		deleteAllOptionsFromQuestion(questionId);
		return insert(questionId, questionOptionFiles);
	}

	/**
	 * Updates a question in the Question table.
	 * 
	 * @param questionOptionId
	 *            The QuestionOptionId of the QuestionOption to update
	 * @param questionId
	 *            The QuestionId of the question the QuestionOption is associated
	 *            with.
	 * @param questionOptionFile
	 *            The path to the file of the QuestionOption.
	 * 
	 * @return The number of options added/updated.
	 */
	public int update(int questionId, String questionOptionFile) {
		deleteAllOptionsFromQuestion(questionId);
		return insert(questionId, questionOptionFile);
	}

	public boolean deleteAllOptionsFromQuestion(int questionId) {
		logger.info("Deleting options for question with QuestionId: " + questionId);
		PreparedStatement preparedStatement = null;
		int count = 0;

		try {
			preparedStatement = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE QuestionId = ?");
			preparedStatement.setInt(1, questionId);

			count = preparedStatement.executeUpdate();
			logger.info(count + " options were deleted.");
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

		// Only one row should be updated
		return count > 0;
	}
}
