package database;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import utils.ConfigurationManager;

/**
 * All tests that require a database connection must inherit from the class.
 * This will set the tests to use "testing mode" and work on the testing
 * database rather than the actual database.
 * 
 * @author Brendan Zhang
 *
 */
public class DatabaseTest {
	// Sets TEST_MODE to 1 to use the testing database
	@BeforeClass
	public static void setMode() {
		ConfigurationManager configurationManager = ConfigurationManager.getInstance();
		configurationManager.writeToConfigFile("TEST_MODE", "1");
	}

	// Sets TEST_MODE to 0 after finished tests
	@AfterClass
	public static void unSetMode() {
		ConfigurationManager configurationManager = ConfigurationManager.getInstance();
		configurationManager.writeToConfigFile("TEST_MODE", "0");
	}
}
