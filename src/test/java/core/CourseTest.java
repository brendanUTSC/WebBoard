package core;

import org.junit.Assert;
import org.junit.Test;

public class CourseTest {
	@Test
	public void testCourseInitializesCorrectly() {
		String courseName = "Software Engineering";
		String courseId = "CSCC01";

		Course course = new Course(courseId, courseName);
		Assert.assertEquals(courseId, course.getCourseId());
		Assert.assertEquals(courseName, course.getCourseName());
	}
}
