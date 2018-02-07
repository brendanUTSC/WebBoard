package core;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a Course which contains a Name and Id.
 * 
 * @author Brendan Zhang
 *
 */
public class Course {
	private StringProperty courseName;
	private StringProperty courseId;

	public Course(String courseId, String courseName) {
		setCourseId(courseId);
		setCourseName(courseName);
	}

	public void setCourseName(String value) {
		courseName().set(value);
	}

	public String getCourseName() {
		return courseName().get();
	}

	public StringProperty courseName() {
		if (courseName == null)
			courseName = new SimpleStringProperty(this, "courseName");
		return courseName;
	}

	public void setCourseId(String value) {
		courseId().set(value);
	}

	public String getCourseId() {
		return courseId().get();
	}

	public StringProperty courseId() {
		if (courseId == null)
			courseId = new SimpleStringProperty(this, "courseName");
		return courseId;
	}
}
