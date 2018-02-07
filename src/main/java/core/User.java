package core;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class models a User of the application.
 * 
 * @author Brendan Zhang
 *
 */
public class User {
	private String username;
	private String password;

	private StringProperty firstName;
	private StringProperty lastName;

	private IntegerProperty id;

	/**
	 * Privilege level of the user. A level of 1 indicates student, while a level of
	 * 2 indicates professor. For setting and comparing, use {@link Privilege}
	 * instead of using hard-coded values.
	 */
	private int privilegeLevel;

	/**
	 * A static class that contains constants for the different privilege levels.
	 */
	public static class Privilege {
		public static int STUDENT = 1;
		public static int PROFESSOR = 2;
	}

	public User() {
		setUsername(null);
		setPassword(null);
		setPrivilegeLevel(Privilege.STUDENT);
		setFirstName(null);
		setLastName(null);
	}

	public User(String username, String password, int privilegeLevel, String firstName, String lastName) {
		setUsername(username);
		setPassword(password);
		setPrivilegeLevel(privilegeLevel);
		setFirstName(firstName);
		setLastName(lastName);
	}

	public void setFirstName(String value) {
		firstNameProperty().set(value);
	}

	public String getFirstName() {
		return firstNameProperty().get();
	}

	public StringProperty firstNameProperty() {
		if (firstName == null)
			firstName = new SimpleStringProperty(this, "firstName");
		return firstName;
	}

	public void setLastName(String value) {
		lastNameProperty().set(value);
	}

	public String getLastName() {
		return lastNameProperty().get();
	}

	public StringProperty lastNameProperty() {
		if (lastName == null)
			lastName = new SimpleStringProperty(this, "lastName");
		return lastName;
	}

	public IntegerProperty idProperty() {
		if (id == null) {
			id = new SimpleIntegerProperty(this, "id");
		}
		return id;
	}

	public int getId() {
		return idProperty().get();
	}

	public void setId(int id) {
		idProperty().set(id);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPrivilegeLevel() {
		return privilegeLevel;
	}

	public void setPrivilegeLevel(int privilegeLevel) {
		this.privilegeLevel = privilegeLevel;
	}
}
