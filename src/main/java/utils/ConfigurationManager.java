package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Class for writing to and reading from a configuration file. For using this
 * class, use {@link getInstance()} to get an instance of the
 * ConfigurationManager. Afterwards, the instance can be used to read/write to
 * the configuration file.
 * 
 * @author Brendan Zhang
 *
 */
public class ConfigurationManager {
	/**
	 * File name of the configuration file. The configuration file must have a
	 * .properties extension.
	 */
	private static final String FILE_NAME = "config.properties";
	private static final String TEST_MODE = "TEST_MODE";
	private static Logger logger = Logger.getLogger(ApplicationDataDirectoryUtils.class.getName());
	private static ConfigurationManager configurationManager = null;
	/**
	 * The properties in the configuration file
	 */
	private Properties properties;

	/**
	 * Default private constructor for ConfigurationManager. Use
	 * {@link #getInstance()} to get an instance of the ConfigurationManager.
	 */
	private ConfigurationManager() {
		properties = new Properties();
		try (InputStream input = new FileInputStream(FILE_NAME)) {
			properties.load(input);
		} catch (IOException e) {
			logger.severe("An error ocurred trying to open the configuration file." + e);
		}
	}

	/**
	 * Return the instance of ConfigurationManager.
	 * 
	 * @return the ConfigurationManager
	 */
	public static ConfigurationManager getInstance() {
		if (configurationManager == null) {
			configurationManager = new ConfigurationManager();
		}
		return configurationManager;
	}

	/**
	 * Writes the property name/value key-pair to the configuration file. The
	 * property is saved as 'name'='value' and appended into the configuration file.
	 * 
	 * @param propertyName
	 *            The name of the property
	 * @param propertyValue
	 *            The value of the property
	 */
	public void writeToConfigFile(String propertyName, String propertyValue) {
		try (OutputStream output = new FileOutputStream(FILE_NAME)) {
			// set the property value
			properties.setProperty(propertyName, propertyValue);
			// save property to configuration file in project root folder
			properties.store(output, null);
		} catch (IOException io) {
			logger.severe("An error ocurred trying to open the configuration file." + io);
		}
	}

	/**
	 * Returns the property value for the specified property name.
	 * 
	 * @param propertyName
	 *            Name of the property
	 * @return the value of the Property
	 */
	public String readFromConfigFile(String propertyName) {
		return properties.getProperty(propertyName);
	}

	public void setTestMode() {
		writeToConfigFile(TEST_MODE, "1");
	}

	public void unsetTestMode() {
		writeToConfigFile(TEST_MODE, "0");
	}

	/**
	 * Reads the {@code TEST_MODE} property in the configuration file and returns
	 * {@code true} iff {@code TEST_MODE} is set to 1.
	 * 
	 * @return {@code true} if {@code TEST_MODE} is set to 1. Otherwise, return
	 *         {@code false}.
	 */
	public boolean isTestMode() {
		int mode = Integer.parseInt(configurationManager.readFromConfigFile(TEST_MODE));
		return mode == Mode.TESTING;
	}

	public static class Mode {
		public static int REAL = 0;
		public static int TESTING = 1;
	}
}
