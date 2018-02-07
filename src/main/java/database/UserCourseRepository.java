package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This repository encapsulates the functionality of the UserCourse table in the
 * database. A record in this table indicates that the user is either taking the
 * course (if the user is a student) or instructing the course (if the user is a
 * professor).
 * 
 * @author Brendan Zhang
 *
 */
public class UserCourseRepository extends Repository {
	public static final String TABLE_NAME = "UserCourse";
	private static final String TABLE_CREATION_SQL = "CREATE TABLE " + TABLE_NAME + " (" + "UserId INT NOT NULL, "
			+ "CourseId VARCHAR(6) NOT NULL" + ");";
	private static Logger logger = Logger.getLogger(UserCourseRepository.class.getSimpleName());
	private static final String ALL_PARAMS = "UserId, CourseId";

	public UserCourseRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	/**
	 * Inserts a new row into the UserCourse table.
	 * 
	 * @param userId
	 *            The User Id
	 * @param courseId
	 *            The Course Id
	 * @return the number of rows that were inserted. This should always return 1.
	 */
	public int insert(Integer userId, String courseId) {

		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			preparedStatement = connection
					.prepareStatement("insert into " + TABLE_NAME + " (" + ALL_PARAMS + ") values(?,?)");
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, courseId);
			count = preparedStatement.executeUpdate();
			logger.info(TABLE_NAME + " was succesfully inserted into " + TABLE_NAME + " table");
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to insert to User table. " + sqle);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
			}
		}

		return count;
	}

	/**
	 * Returns the list of the Course Id's for all the courses that the user is
	 * taking or instructing in.
	 * 
	 * @param userId
	 *            The User Id
	 * @return list of Course Id's
	 */
	public List<String> getCoursesForUser(Integer userId) {
		List<Object> list = search(userId, SearchType.USER_ID);
		List<String> courses = new ArrayList<String>();
		for (Object object : list) {
			courses.add((String) object);
		}
		return courses;
	}

	/**
	 * Returns the list of the User Id's for all the users that are taking the
	 * course. This includes professors.
	 * 
	 * @param courseId
	 *            The Course Id
	 * @return list of User Id's
	 */
	public List<Integer> getStudentsForCourse(String courseId) {
		List<Object> list = search(courseId, SearchType.COURSE_ID);
		List<Integer> students = new ArrayList<Integer>();
		for (Object object : list) {
			students.add((Integer) object);
		}
		return students;
	}

	/**
	 * Returns a Map<K, V>. K are the courses the professor instructs. V is a list
	 * of student Id's enrolled in that course.
	 * 
	 * @param professorId
	 *            The userId of the professor we want to get the students of.
	 * @return map of <course name, list of User Id's>
	 */
	public Map<String, List<Integer>> getAllStudents(Integer professorId) {
		Map<String, List<Integer>> hmap = new HashMap<String, List<Integer>>();
		List<String> courses = getCoursesForUser(professorId);

		for (String course : courses) {
			List<Integer> students = getStudentsForCourse(course);
			List<Integer> hmap_student_list = new ArrayList<Integer>();

			for (Integer studentId : students) {
				hmap_student_list.add(studentId);
			}
			hmap.put(course, hmap_student_list);
		}
		return hmap;
	}

	/**
	 * Returns a list of students that the professor is teaching. This function
	 * takes the results returned in {@link #getAllStudents(Integer)} and flattens
	 * it into a single List.
	 * 
	 * @param professorId
	 *            The UserId of the professor
	 * @return List of students that are taking a course the professor instructs.
	 */
	public List<Integer> getAllStudentsFlatMap(Integer professorId) {
		Map<String, List<Integer>> map = getAllStudents(professorId);
		List<Integer> flatMap = new ArrayList<Integer>();
		for (List<Integer> l : map.values()) {
			for (Integer i : l) {
				if (!flatMap.contains(i)) {
					flatMap.add(i);
				}
			}
		}
		return flatMap;
	}

	/**
	 * A static class that contains constants for the search types.
	 */
	private static class SearchType {
		/**
		 * Search by User Id
		 */
		public static int USER_ID = 0;

		/**
		 * Search by Course Id
		 */
		public static int COURSE_ID = 1;
	}

	/**
	 * Searches using the specified search parameter and search type.
	 * 
	 * @param searchParameter
	 *            The parameter to search on
	 * @param searchType
	 *            The search type
	 * @return List of Objects found using the search parameter and search type.
	 *         This will either be a list of User Id's or Course Id's.
	 * 
	 * @see SearchType
	 */
	private List<Object> search(Object searchParameter, int searchType) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Object> list = new ArrayList<Object>();

		try {
			if (searchType == SearchType.USER_ID) {
				preparedStatement = connection
						.prepareStatement("select " + ALL_PARAMS + " from " + TABLE_NAME + " where UserId = ?");
				preparedStatement.setInt(1, (Integer) searchParameter);
			} else {
				preparedStatement = connection
						.prepareStatement("select " + ALL_PARAMS + " from " + TABLE_NAME + " where CourseId = ?");
				preparedStatement.setString(1, (String) searchParameter);
			}

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				logger.info("StudentCourse found.");
				logger.info("User Id: " + resultSet.getInt(1));
				logger.info("Course Id: " + resultSet.getString(2));

				if (searchType == SearchType.USER_ID) {
					list.add(resultSet.getString(2));
				} else {
					list.add(new Integer(resultSet.getInt(1)));
				}
			}

			return list;
		} catch (SQLException e) {
			logger.severe("An error occurred trying to execute an SQL query." + e);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e) {
			}
		}

		return list;
	}

	public boolean removeUserFromCourse(Integer userId, String courseId) {

		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			preparedStatement = connection
					.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE UserId = ? AND CourseId = ?");
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, courseId);
			count = preparedStatement.executeUpdate();
		} catch (SQLException sqle) {
			logger.severe("An error occurred trying to delete from " + TABLE_NAME + " table. " + sqle);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
			}
		}

		return count == 1;
	}

}
