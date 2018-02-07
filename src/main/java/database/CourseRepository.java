package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import core.Course;

/**
 * This class encapsulates the functionality of the Course table in the
 * database.
 * 
 * @author Brendan Zhang
 *
 */
public class CourseRepository extends Repository {
	private static Logger logger = Logger.getLogger(CourseRepository.class.getName());
	private static final String TABLE_NAME = "Course";
	private static final String ALL_PARAMS = "CourseId, CourseName";
	private static final String TABLE_CREATION_SQL = "CREATE TABLE " + TABLE_NAME + " ("
			+ "CourseId VARCHAR(6) PRIMARY KEY NOT NULL, " + "CourseName VARCHAR(250) NOT NULL" + ");";

	public CourseRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	public int insert(String courseId, String courseName) {
		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			preparedStatement = connection
					.prepareStatement("insert into " + TABLE_NAME + " (" + ALL_PARAMS + ") values(?,?)");
			preparedStatement.setString(1, courseId);
			preparedStatement.setString(2, courseName);
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

	public Course getCourseByCourseId(String courseId) {
		return getCourse(courseId, GetCourseSearchType.COURSE_ID);
	}

	public Course getCourseByCourseName(String courseName) {
		return getCourse(courseName, GetCourseSearchType.COURSE_NAME);
	}

	/**
	 * A static class that contains constants for the search types.
	 */
	private static class GetCourseSearchType {
		public static int COURSE_ID = 0;
		public static int COURSE_NAME = 1;
	}

	private Course getCourse(String searchParameter, int searchType) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			logger.info("Getting course with searchParamater: " + searchParameter);
			if (searchType == GetCourseSearchType.COURSE_ID) {
				preparedStatement = connection
						.prepareStatement("select " + ALL_PARAMS + " from " + TABLE_NAME + " where CourseId = ?");
			} else {
				preparedStatement = connection
						.prepareStatement("select " + ALL_PARAMS + " from " + TABLE_NAME + " where CourseName = ?");
			}
			preparedStatement.setString(1, searchParameter);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				logger.info("Course found.");
				logger.info("Course Id: " + resultSet.getString(1));
				logger.info("Course Name: " + resultSet.getString(2));

				return new Course(resultSet.getString(1), resultSet.getString(2));
			}
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

		return null;
	}
}
