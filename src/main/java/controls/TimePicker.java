package controls;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import core.Assignment;
import database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * Class that contains a JavaFX control for selecting a time.
 * 
 * @author Brendan Zhang
 *
 */
public class TimePicker {
	private static Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
	private static int PREFERRED_INPUT_BOX_WIDTH = 70;

	// Default settings
	private static final int DEFAULT_HOURS = 11;
	private static final int DEFAULT_MINUTES = 0;
	private static final String DEFAULT_MODE = "PM";

	private int hours;
	private int minutes;
	private String mode; // Either AM or PM
	private Node node;

	private Spinner<Integer> hourSpinner;
	private Spinner<Integer> minuteSpinner;
	private Spinner<String> modeSpinner;

	public TimePicker(Assignment assignment) {
		this(DEFAULT_HOURS, DEFAULT_MINUTES, DEFAULT_MODE, assignment);
	}

	/**
	 * Sets the values of the TimePicker based on the time component of
	 * localDateTime. The Date component of localDateTime is never used.
	 * 
	 * @param localDateTime
	 *            The LocalDateTime to set the TimePicker's values to
	 */
	public void setValues(LocalDateTime localDateTime) {
		int displayHour = localDateTime.getHour();
		if (displayHour >= 12) {
			mode = "PM";
			if (displayHour != 12) {
				displayHour -= 12;
			}
		} else {
			if (displayHour == 0) {
				displayHour = 12;
			}
			mode = "AM";
		}
		hours = displayHour;
		minutes = localDateTime.getMinute();
		// Update values in spinners to reflect values
		drawValuesToNode();
	}

	public TimePicker(int hours, int minutes, String mode, Assignment assignment) {
		HBox timePickerHBox = new HBox(10);
		timePickerHBox.setAlignment(Pos.CENTER_LEFT);

		this.hours = hours;
		this.minutes = minutes;
		this.mode = mode;

		Label timeLabel = new Label("Time:");
		Label colonLabel = new Label(":");
		// Invisible label for spacing purposes
		Label invisibleLabel = new Label();

		// Hour spinner
		hourSpinner = new Spinner<>(1, 12, 1, 1);
		hourSpinner.getValueFactory().setValue(hours);
		hourSpinner.setEditable(true);

		hourSpinner.getValueFactory().setConverter(new StringConverter<Integer>() {
			@Override
			public String toString(Integer value) {
				if (value < 10) {
					return "0" + value;
				}
				return value.toString();
			}

			@Override
			public Integer fromString(String string) {
				return Integer.parseInt(string);
			}
		});

		hourSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
			logger.info("Setting hours to: " + newValue);
			this.hours = newValue;
			assignment.setDeadline(initializeTime(assignment.getDeadline()));
		});
		hourSpinner.setPrefWidth(PREFERRED_INPUT_BOX_WIDTH);

		// Minute spinner
		minuteSpinner = new Spinner<>(0, 59, 1, 1);

		// Display as 2 digit number always
		minuteSpinner.getValueFactory().setConverter(new StringConverter<Integer>() {
			@Override
			public String toString(Integer value) {
				if (value < 10) {
					return "0" + value;
				}
				return value.toString();
			}

			@Override
			public Integer fromString(String string) {
				return Integer.parseInt(string);
			}
		});

		minuteSpinner.setEditable(true);
		minuteSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
			logger.info("Setting minutes to: " + newValue);
			this.minutes = newValue;
			assignment.setDeadline(initializeTime(assignment.getDeadline()));
		});
		minuteSpinner.setPrefWidth(PREFERRED_INPUT_BOX_WIDTH);

		// AM/PM spinner
		ObservableList<String> modeValues = FXCollections.observableArrayList("AM", "PM");
		SpinnerValueFactory<String> spinnerValueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<String>(
				modeValues);

		modeSpinner = new Spinner<>();
		modeSpinner.setValueFactory(spinnerValueFactory);

		modeSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
			logger.info("Setting mode to: " + newValue);
			setMode(newValue);
			assignment.setDeadline(initializeTime(assignment.getDeadline()));
		});
		modeSpinner.setPrefWidth(PREFERRED_INPUT_BOX_WIDTH);

		drawValuesToNode();
		timePickerHBox.getChildren().addAll(timeLabel, hourSpinner, colonLabel, minuteSpinner, invisibleLabel,
				modeSpinner);
		node = timePickerHBox;
	}

	/**
	 * Updates the values of the Spinners manually and force a UI redraw.
	 */
	private void drawValuesToNode() {
		hourSpinner.getValueFactory().setValue(hours);
		minuteSpinner.getValueFactory().setValue(minutes);
		modeSpinner.getValueFactory().setValue(mode);
	}

	/**
	 * Adds the time in the current TimePicker to the given LocalDateTime that only
	 * has a date.
	 * 
	 * @param dateOnly
	 *            LocalDateTime with only a date
	 * @return LocalDateTime with a date and time
	 */
	public LocalDateTime initializeTime(LocalDateTime dateOnly) {
		if (mode.equals("AM")) {
			return dateOnly.withHour(hours % 12).withMinute(minutes);
		} else {
			return dateOnly.withHour((hours % 12) + 12).withMinute(minutes);
		}
	}

	public Node getNode() {
		return node;
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public String getMode() {
		return mode;
	}

	private void setMode(String mode) {
		if (mode.equals("AM") || mode.equals("PM")) {
			this.mode = mode;
		} else {
			throw new RuntimeException("Invalid mode was passed to setMode: " + mode);
		}
	}
}
