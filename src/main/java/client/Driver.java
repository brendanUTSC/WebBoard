package client;

import java.sql.SQLException;
import java.util.logging.Logger;

import client.scenes.UserLoginScene;
import database.DatabaseConnection;
import database.UserRepository;
import javafx.application.Application;
import javafx.stage.Stage;
import utils.ConfigurationManager;
import utils.ResourceUtils;

public class Driver extends Application {
	private static final Logger logger = Logger.getLogger(UserRepository.class.getName());
	private static final String LOGO_FILE_NAME = "logo.png";
	private static final String APPLICATION_NAME = "WebBoard";

	public static void main(String[] args) {
		if (!ConfigurationManager.getInstance().isTestMode()) {
			// Disable logging if not in test mode
			// LogManager.getLogManager().reset();
		}

		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(APPLICATION_NAME);
		primaryStage.setScene(new UserLoginScene(primaryStage).getScene());
		primaryStage.getIcons().add(ResourceUtils.getImageFromResources(LOGO_FILE_NAME));
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	@Override
	public void stop() {
		try {
			DatabaseConnection.getDatabaseConnection().close();
		} catch (SQLException e) {
			logger.severe("A SQLException occurred while trying to close the DB connection. " + e);
		}
	}
}
