package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import core.User;

public class UserRepository extends Repository {
	private static Logger logger = Logger.getLogger(UserRepository.class.getName());
	private static final String TABLE_NAME = "User";
	private static final String EXCLUDE_ID = "Username, Password, UserPrivilegeLevel, FirstName, LastName";
	private static final String ALL_PARAMS = "UserId, " + EXCLUDE_ID;
	private static final String TABLE_CREATION_SQL = "CREATE TABLE " + TABLE_NAME + " ("
			+ "UserId INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " + "Username VARCHAR(250) NOT NULL, "
			+ "Password VARCHAR(100) NOT NULL, " + "UserPrivilegeLevel INT NOT NULL, " + "FirstName TEXT NOT NULL, "
			+ "LastName TEXT NOT NULL" + ");";

	public UserRepository() {
		super(TABLE_NAME, TABLE_CREATION_SQL);
	}

	public List<User> getAllEnrolledUsers(String courseId, boolean removeProfessors) {
		List<User> users = new ArrayList<User>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			if (removeProfessors) {
				preparedStatement = connection.prepareStatement(
						"SELECT a." + ALL_PARAMS + " FROM " + TABLE_NAME + " a, " + UserCourseRepository.TABLE_NAME
								+ " b WHERE a.UserId = b.UserId AND b.CourseId = ? AND a.UserPrivilegeLevel = 1");
			} else {
				preparedStatement = connection.prepareStatement("SELECT a." + ALL_PARAMS + " FROM " + TABLE_NAME
						+ " a, " + UserCourseRepository.TABLE_NAME + " b WHERE a.UserId = b.UserId AND b.CourseId = ?");
			}
			preparedStatement.setString(1, courseId);

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				logger.info("User found.");
				logger.info("User Id: " + resultSet.getInt(1));
				logger.info("User Name: " + resultSet.getString(2));
				logger.info("User password: " + resultSet.getString(3));
				logger.info("UserPrevilegeLevel: " + resultSet.getInt(4));
				User user = createUser(resultSet);
				users.add(user);
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

		return users;
	}

	public List<User> getAllUnenrolledUsers(String courseId, boolean removeProfessors) {
		List<User> users = new ArrayList<User>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			if (removeProfessors) {
				preparedStatement = connection.prepareStatement("SELECT a." + ALL_PARAMS + " from " + TABLE_NAME
						+ " a WHERE a.UserId not in (SELECT UserId FROM " + UserCourseRepository.TABLE_NAME
						+ " c WHERE c.CourseId = ?) AND a.UserPrivilegeLevel = 1");
			} else {
				preparedStatement = connection.prepareStatement("SELECT a." + ALL_PARAMS + " from " + TABLE_NAME
						+ " a WHERE a.UserId not in (SELECT UserId FROM " + UserCourseRepository.TABLE_NAME
						+ " c WHERE c.CourseId = ?)");
			}

			preparedStatement.setString(1, courseId);

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				logger.info("User found.");
				logger.info("User Id: " + resultSet.getInt(1));
				logger.info("User Name: " + resultSet.getString(2));
				logger.info("User password: " + resultSet.getString(3));
				logger.info("UserPrevilegeLevel: " + resultSet.getInt(4));
				User user = createUser(resultSet);
				users.add(user);
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

		return users;
	}

	public User getUserFromUsername(String username) {
		logger.info("Getting user " + username);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement("select " + ALL_PARAMS + " from User where Username = ?");
			preparedStatement.setString(1, username);

			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				logger.info("User found.");
				logger.info("User Id: " + resultSet.getInt(1));
				logger.info("User Name: " + resultSet.getString(2));
				logger.info("User password: " + resultSet.getString(3));
				logger.info("UserPrevilegeLevel: " + resultSet.getInt(4));
				User user = createUser(resultSet);
				return user;
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

	public User getUserFromId(int userId) {
		logger.info("Getting user " + userId);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement("select " + ALL_PARAMS + " from User where UserId = ?");
			preparedStatement.setInt(1, userId);

			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				logger.info("User found.");
				logger.info("User Id: " + resultSet.getInt(1));
				logger.info("User Name: " + resultSet.getString(2));
				logger.info("User password: " + resultSet.getString(3));
				logger.info("UserPrevilegeLevel: " + resultSet.getInt(4));
				User user = createUser(resultSet);
				return user;
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

	public int insert(User user) {
		if (user.getId() != 0) {
			return insert(user.getId(), user.getUsername(), user.getPassword(), user.getPrivilegeLevel(),
					user.getFirstName(), user.getLastName());
		}

		return insert(user.getUsername(), user.getPassword(), user.getPrivilegeLevel(), user.getFirstName(),
				user.getLastName());
	}

	public int insert(String username, String password, int privilegeLevel, String firstName, String lastName) {

		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			preparedStatement = connection
					.prepareStatement("insert into " + TABLE_NAME + " (" + EXCLUDE_ID + ") values(?,?,?,?,?)");

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setInt(3, privilegeLevel);
			preparedStatement.setString(4, firstName);
			preparedStatement.setString(5, lastName);

			count = preparedStatement.executeUpdate();
			logger.info("User was succesfully inserted into User table");
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

	public boolean delete(int userId) {
		logger.info("Deleting User with UserId: " + userId);
		PreparedStatement preparedStatement = null;
		Integer success = null;

		try {
			preparedStatement = connection.prepareStatement("delete from " + TABLE_NAME + " where AssignmentId = ?");
			preparedStatement.setInt(1, userId);

			success = preparedStatement.executeUpdate();

		} catch (SQLException e) {
			logger.severe("An error occurred trying to execute an SQL query." + e);
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
			}
		}

		return success == 1;
	}

	public int insert(int userId, String username, String password, int privilegeLevel, String firstName,
			String lastName) {
		PreparedStatement preparedStatement = null;
		int count = 0;
		try {
			preparedStatement = connection.prepareStatement("insert into " + TABLE_NAME + " values(?,?,?,?,?,?)");

			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, username);
			preparedStatement.setString(3, password);
			preparedStatement.setInt(4, privilegeLevel);
			preparedStatement.setString(5, firstName);
			preparedStatement.setString(6, lastName);

			count = preparedStatement.executeUpdate();
			logger.info("User was succesfully inserted into User table");
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
	 * Creates a User from a ResultSet that contains Users.
	 * 
	 * @param resultSet
	 *            The ResultSet that contains the user information
	 * @return the User
	 * @throws SQLException
	 *             If an error occurred trying to read from the ResultSet
	 */
	private User createUser(ResultSet resultSet) throws SQLException {
		User user = new User();
		user.setId(resultSet.getInt(1));
		user.setUsername(resultSet.getString(2));
		user.setPassword(resultSet.getString(3));
		user.setPrivilegeLevel(resultSet.getInt(4));
		user.setFirstName(resultSet.getString(5));
		user.setLastName(resultSet.getString(6));
		return user;
	}
}
