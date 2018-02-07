package database;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserCourseRepositoryTest extends DatabaseTest {
	UserCourseRepository repository;
	private static final String COURSE_ID = "CSCC01";
	private static final int USER_ID = 1;

	@Before
	public void setUp() {
		repository = new UserCourseRepository();
	}

	@Test
	public void testInsert() {
		Assert.assertEquals(1, repository.insert(USER_ID, COURSE_ID));
	}

	@Test
	public void testGetCoursesForStudentWithSingleCourse() {
		repository.insert(USER_ID, COURSE_ID);
		List<String> courses = repository.getCoursesForUser(USER_ID);
		Assert.assertEquals(1, courses.size());
		Assert.assertEquals(COURSE_ID, courses.get(0));
	}

	@Test
	public void testGetCoursesForStudentWithNoCourse() {
		List<String> courses = repository.getCoursesForUser(USER_ID);
		Assert.assertEquals(0, courses.size());
	}

	@Test
	public void testGetCoursesForStudentWithMultipleCourses() {
		String secondCourseId = "CSCD01";
		String thirdCourseId = "CSCD27";

		repository.insert(USER_ID, COURSE_ID);
		repository.insert(USER_ID, secondCourseId);
		repository.insert(USER_ID, thirdCourseId);

		List<String> courses = repository.getCoursesForUser(USER_ID);
		Assert.assertEquals(3, courses.size());

		Assert.assertTrue(courses.contains(COURSE_ID));
		Assert.assertTrue(courses.contains(secondCourseId));
		Assert.assertTrue(courses.contains(thirdCourseId));
	}

	@Test
	public void testGetStudentsForCourseWithSingleStudent() {
		repository.insert(USER_ID, COURSE_ID);

		List<Integer> students = repository.getStudentsForCourse(COURSE_ID);
		Assert.assertEquals(1, students.size());
		Assert.assertEquals(USER_ID, students.get(0).intValue());
	}

	@Test
	public void testGetStudentsForCourseWithNoStudent() {
		List<Integer> students = repository.getStudentsForCourse(COURSE_ID);
		Assert.assertEquals(0, students.size());
	}

	@Test
	public void testGetStudentsForCourseWithMultipleStudents() {
		int secondStudentId = 1336;
		int thirdStudentId = 1337;
		int fourthStudentId = 8999;

		repository.insert(USER_ID, COURSE_ID);
		repository.insert(secondStudentId, COURSE_ID);
		repository.insert(thirdStudentId, COURSE_ID);
		repository.insert(fourthStudentId, COURSE_ID);

		List<Integer> students = repository.getStudentsForCourse(COURSE_ID);
		Assert.assertEquals(4, students.size());

		Assert.assertTrue(students.contains(USER_ID));
		Assert.assertTrue(students.contains(secondStudentId));
		Assert.assertTrue(students.contains(thirdStudentId));
		Assert.assertTrue(students.contains(fourthStudentId));
	}

	@Test
	public void testGetAllStudentsFlatMap() {
		int secondStudentId = 1336;
		int thirdStudentId = 1337;
		int fourthStudentId = 8999;

		repository.insert(USER_ID, COURSE_ID);
		repository.insert(secondStudentId, COURSE_ID);
		repository.insert(thirdStudentId, COURSE_ID);
		repository.insert(fourthStudentId, "NotCSCC01");

		List<Integer> students = repository.getAllStudentsFlatMap(USER_ID);
		Assert.assertEquals(3, students.size());
	}

	@After
	public void tearDown() {
		repository.destroy();
	}
}
