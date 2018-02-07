package database;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.Assignment;

public class AssignmentRepositoryTest extends DatabaseTest {
	private AssignmentRepository assignmentRepository;
	private static final String ASSIGNMENT_NAME = "Test Assignment";
	private static final String COURSE_ID = "CSCC01";

	@Before
	public void setUp() {
		assignmentRepository = new AssignmentRepository();
	}

	@Test
	public void testInsert() {
		int assignmentId = assignmentRepository.insert(ASSIGNMENT_NAME, LocalDateTime.now(), COURSE_ID);
		Assert.assertNotEquals(-1, assignmentId);
		assignmentRepository.delete(assignmentId);
	}

	@Test
	public void testGetAssignment() {
		int assignmentId = assignmentRepository.insert(ASSIGNMENT_NAME, LocalDateTime.now(), COURSE_ID);

		Assignment retrievedAssignment = assignmentRepository.getAssignment(assignmentId);
		Assert.assertEquals(ASSIGNMENT_NAME, retrievedAssignment.getName());
		Assert.assertEquals(COURSE_ID, retrievedAssignment.getCourseId());
		assignmentRepository.delete(assignmentId);
	}

	@Test
	public void testUpdate() {
		String updatedName = "Updated Name";
		String updatedCourse = "CSCD01";

		int assignmentId = assignmentRepository.insert(ASSIGNMENT_NAME, LocalDateTime.now(), COURSE_ID);
		assignmentRepository.update(assignmentId, updatedName, LocalDateTime.now(), updatedCourse);

		Assignment retrievedAssignment = assignmentRepository.getAssignment(assignmentId);
		Assert.assertEquals(updatedName, retrievedAssignment.getName());
		Assert.assertEquals(updatedCourse, retrievedAssignment.getCourseId());
		assignmentRepository.delete(assignmentId);
	}

	@Test
	public void testGetAllAssignments() {
		List<Assignment> retrievedAssignments = assignmentRepository.getAllAssignments(false);
		int sizeBeforeInsert = retrievedAssignments.size();
		int assignmentId = assignmentRepository.insert(ASSIGNMENT_NAME, LocalDateTime.now(), COURSE_ID);
		retrievedAssignments = assignmentRepository.getAllAssignments(false);
		Assert.assertEquals(sizeBeforeInsert + 1, retrievedAssignments.size());
		assignmentRepository.delete(assignmentId);
	}

	@Test
	public void testDeleteAssignment() {
		int assignmentId = assignmentRepository.insert(ASSIGNMENT_NAME, LocalDateTime.now(), COURSE_ID);
		List<Assignment> retrievedAssignments = assignmentRepository.getAllAssignments(false);
		int sizeBeforeDelete = retrievedAssignments.size();
		assignmentRepository.delete(assignmentId);
		Assert.assertEquals(sizeBeforeDelete - 1, assignmentRepository.getAllAssignments(false).size());
	}

	@Test
	public void testGetAllAssignmentsForUser() {
		// TODO:
	}
}
