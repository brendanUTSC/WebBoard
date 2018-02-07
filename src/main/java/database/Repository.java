package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a table in the database. All concrete Repository classes represent
 * a concrete table in the database and inherit from this class.
 * 
 * @author Brendan Zhang
 *
 */
public abstract class Repository {
	private static Logger logger = Logger.getLogger(Repository.class.getName());
	public Connection connection;
	private String tableName;

	public Repository(String tableName, String tableCreationSQL) {
		this.tableName = tableName;

		connection = DatabaseConnection.getDatabaseConnection().getConnection();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			DatabaseMetaData metadata = connection.getMetaData();
			resultSet = metadata.getTables(null, null, tableName, null);
			List<String> tableNames = new ArrayList<String>();
			while (resultSet.next()) {
				tableNames.add(resultSet.getString(3));
			}

			if (tableNames.size() == 0) {
				logger.info(tableName + " table does not exist. Creating " + tableName + " table.");
				// User table doesn't exist. Create it.
				statement = connection.createStatement();
				statement.execute(tableCreationSQL);
				logger.info(tableName + " table successfully created");
			}
		} catch (SQLException e) {
			logger.severe("Error while executing an SQL command. " + e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Deletes a row in the table. The PreparedStatement is closed after the
	 * deletion is completed.
	 * 
	 * @param preparedStatement
	 *            The delete PreparedStatement
	 * @return
	 */
	protected boolean delete(PreparedStatement preparedStatement) {
		int success = 0;
		try {
			success = preparedStatement.executeUpdate();
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
		return success == 1;
	}

	/**
	 * Drops the specified table.
	 * 
	 * @param databaseConnection
	 *            Repository with the connection to use
	 * @param tableName
	 *            Name of the table to drop
	 * @return success indicator of the drop command
	 */
	public boolean destroy() {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement("drop table " + tableName);
			preparedStatement.executeUpdate();
			logger.info(tableName + " table was successfully deleted.");
			return true;
		} catch (SQLException e) {
			logger.severe("Error while trying to drop " + tableName + "table. " + e);
		}
		return false;
	}
}
