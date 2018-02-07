package database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.Course;

public class CourseRepositoryTest extends DatabaseTest {
	private CourseRepository courseRepository;
	private static final String COURSE_ID = "CSCC01";
	private static final String COURSE_NAME = "Software Engineering";

	@Before
	public void setUp() {
		courseRepository = new CourseRepository();
	}

	@Test
	public void testInsertCourse() {
		Assert.assertEquals(1, courseRepository.insert(COURSE_ID, COURSE_NAME));
	}

	@Test
	public void testGetCourseByCourseName() {
		courseRepository.insert(COURSE_ID, COURSE_NAME);
		Course course = courseRepository.getCourseByCourseName(COURSE_NAME);
		Assert.assertEquals(COURSE_ID, course.getCourseId());
		Assert.assertEquals(COURSE_NAME, course.getCourseName());
	}

	@After
	public void tearDown() {
		courseRepository.destroy();
	}
}
