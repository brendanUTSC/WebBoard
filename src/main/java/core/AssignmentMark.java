package core;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AssignmentMark {
	private SimpleStringProperty name, grade, assignmentName;
	private SimpleIntegerProperty id, assignmentId;

	public AssignmentMark(int id, String name, int assignmentId, String grade) {
		setId(id);
		setName(name);
		setAssignmentId(assignmentId);
		setGrade(grade);
	}

	public AssignmentMark(int id, String name, String assignmentName, String grade) {
		setId(id);
		setName(name);
		setAssignmentName(assignmentName);
		setGrade(grade);
	}

	public StringProperty nameProperty() {
		if (name == null)
			name = new SimpleStringProperty(this, "name");
		return name;
	}

	public void setName(String name) {
		nameProperty().set(name);
	}

	public String getName() {
		return nameProperty().get();
	}

	public StringProperty assignmentNameProperty() {
		if (assignmentName == null)
			assignmentName = new SimpleStringProperty(this, "assignmentName");
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		assignmentNameProperty().set(assignmentName);
	}

	public String getAssignmentName() {
		return assignmentNameProperty().get();
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

	public SimpleStringProperty gradeProperty() {
		if (grade == null) {
			grade = new SimpleStringProperty(this, "grade");
		}
		return grade;
	}

	public String getGrade() {
		return gradeProperty().get();
	}

	public void setGrade(String grade) {
		gradeProperty().set(grade);
	}

	public IntegerProperty assignmentIdProperty() {
		if (assignmentId == null) {
			assignmentId = new SimpleIntegerProperty(this, "assignmentId");
		}
		return assignmentId;
	}

	public int getAssignmentId() {
		return assignmentIdProperty().get();
	}

	public void setAssignmentId(int assignmentId) {
		assignmentIdProperty().set(assignmentId);
	}
}