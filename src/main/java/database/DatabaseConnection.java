package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import utils.ConfigurationManager;

/**
 * This class represents a MYSQL database connection. This class implements a
 * Singleton design pattern and thus, only a single DatabaseConnection can ever
 * exist (or be in use) by the application.
 * 
 * @author Brendan Zhang
 *
 */
public class DatabaseConnection {
	public static DatabaseConnection databaseConnection;
	private static Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
	private Connection connection;

	private DatabaseConnection() {
		ConfigurationManager configurationManager = ConfigurationManager.getInstance();
		String url;
		String username;
		String password;
		if (configurationManager.isTestMode()) {
			logger.info("Using testing configuration");
			// Use testing url
			url = configurationManager.readFromConfigFile("TEST_JDBC_URL");
			username = configurationManager.readFromConfigFile("TEST_JDBC_USERNAME");
			password = configurationManager.readFromConfigFile("TEST_JDBC_PASSWORD");
		} else {
			logger.info("Using real configuration");
			url = configurationManager.readFromConfigFile("JDBC_URL");
			username = configurationManager.readFromConfigFile("JDBC_USERNAME");
			password = configurationManager.readFromConfigFile("JDBC_PASSWORD");
		}
		logger.info("Connecting to database.");
		try {
			this.connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			logger.severe("Error trying to connect to database. " + e);
		}
	}

	public static DatabaseConnection getDatabaseConnection() {
		if (databaseConnection == null) {
			databaseConnection = new DatabaseConnection();
		}
		return databaseConnection;
	}

	public void close() throws SQLException {
		if (databaseConnection != null) {
			databaseConnection.connection.close();
			databaseConnection = null;
		}
	}

	public Connection getConnection() {
		return connection;
	}
}
