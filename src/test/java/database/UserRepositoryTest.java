package database;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.User;
import core.User.Privilege;

public class UserRepositoryTest extends DatabaseTest {
	// Constants for User
	private static final String TEST_USER_NAME = "testUser";
	private static final String TEST_PASSWORD = "testPassword";
	private static final String TEST_FIRST_NAME = "Bob";
	private static final String TEST_LAST_NAME = "Smith";

	private static final String TEST_USER_NAME_TWO = "testUser";
	private static final String TEST_PASSWORD_TWO = "testPassword";
	private static final String TEST_FIRST_NAME_TWO = "Bob";
	private static final String TEST_LAST_NAME_TWO = "Smith";

	private UserRepository userRepository;
	private User testUser;
	private User testUserTwo;

	// Dependency for testGetAllEnrolledUsers and testGetAllUnenrolledUsers
	private CourseRepository courseRepository;
	private UserCourseRepository userCourseRepository;
	private static final int USER_ID = 1;
	private static final String COURSE_ID = "CSCC01";
	private static final String COURSE_NAME = "Software Engineering";

	@Before
	public void setUp() {
		userRepository = new UserRepository();
		courseRepository = new CourseRepository();
		userCourseRepository = new UserCourseRepository();
		testUser = new User(TEST_USER_NAME, TEST_PASSWORD, Privilege.STUDENT, TEST_FIRST_NAME, TEST_LAST_NAME);
		testUserTwo = new User(TEST_USER_NAME_TWO, TEST_PASSWORD_TWO, Privilege.STUDENT, TEST_FIRST_NAME_TWO,
				TEST_LAST_NAME_TWO);
	}

	@Test
	public void testInsert() {
		int result = userRepository.insert(testUser);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testInsertSucessfully() {
		int result = userRepository.insert(testUser);
		Assert.assertEquals(1, result);

		User retrievedUser = userRepository.getUserFromUsername(testUser.getUsername());
		Assert.assertEquals(testUser.getPassword(), retrievedUser.getPassword());
		Assert.assertEquals(testUser.getPrivilegeLevel(), retrievedUser.getPrivilegeLevel());
		Assert.assertNotNull(retrievedUser.getId());
	}

	@Test
	public void testGetUserFromUsername() {
		userRepository.insert(testUser);

		User retrievedUser = userRepository.getUserFromUsername(TEST_USER_NAME);
		Assert.assertEquals(TEST_USER_NAME, retrievedUser.getUsername());
		Assert.assertEquals(TEST_PASSWORD, retrievedUser.getPassword());
	}

	@Test
	public void testGetAllEnrolledUsersSimpleCase() {
		userRepository.insert(testUser);
		courseRepository.insert(COURSE_ID, COURSE_NAME);
		userCourseRepository.insert(USER_ID, COURSE_ID);
		List<User> enrolledUsers = userRepository.getAllEnrolledUsers(COURSE_ID, false);
		Assert.assertEquals(1, enrolledUsers.size());
		Assert.assertEquals(USER_ID, enrolledUsers.get(0).getId());
	}

	@Test
	public void testGetAllEnrolledUsersOneEnrolledOneUnenrolled() {
		userRepository.insert(testUser);
		userRepository.insert(testUserTwo);
		courseRepository.insert(COURSE_ID, COURSE_NAME);
		userCourseRepository.insert(USER_ID, COURSE_ID);
		List<User> enrolledUsers = userRepository.getAllEnrolledUsers(COURSE_ID, false);
		Assert.assertEquals(1, enrolledUsers.size());
		Assert.assertEquals(1, enrolledUsers.get(0).getId());
	}

	@Test
	public void testGetAllUnerolledUsersSimpleCase() {
		userRepository.insert(testUser);
		courseRepository.insert(COURSE_ID, COURSE_NAME);
		List<User> unEnrolledUsers = userRepository.getAllUnenrolledUsers(COURSE_ID, false);
		Assert.assertEquals(3, unEnrolledUsers.size());
		Assert.assertEquals(USER_ID, unEnrolledUsers.get(0).getId());
	}

	@Test
	public void testGetAllUnerolledUsersOneEnrolledOneUnenrolled() {
		userRepository.insert(testUser);
		userRepository.insert(testUserTwo);
		courseRepository.insert(COURSE_ID, COURSE_NAME);
		userCourseRepository.insert(USER_ID, COURSE_ID);
		List<User> unEnrolledUsers = userRepository.getAllUnenrolledUsers(COURSE_ID, false);
		Assert.assertEquals(3, unEnrolledUsers.size());
		Assert.assertEquals(2, unEnrolledUsers.get(0).getId());
	}

	@After
	public void tearDown() {
		userRepository.destroy();
		userRepository = new UserRepository();
		userRepository.insert("user", "password", 2, "Admin", "Admin");
		userRepository.insert("student", "password", 1, "Student", "Student");
		courseRepository.destroy();
		userCourseRepository.destroy();
	}

}
