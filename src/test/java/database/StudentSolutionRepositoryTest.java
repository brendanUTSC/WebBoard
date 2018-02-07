package database;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import core.studentsolution.StringStudentSolution;

public class StudentSolutionRepositoryTest extends DatabaseTest {
	// Constants
	private static final int ID = -1; // Used for student id, assignment id and question number
	private static final int MARK = 1;
	private static final int MAX_MARK = 3;
	private static final String ANSWER = "ABC";

	private StudentSolutionRepository submissionRepository;
	private StringStudentSolution solution;

	@Before
	public void setUp() throws Exception {
		submissionRepository = new StudentSolutionRepository();
		solution = new StringStudentSolution(ID, ID, ID, ANSWER, MARK, MAX_MARK);
	}

	@Test
	public void testInsert() {
		int result = submissionRepository.insert(solution);
		Assert.assertEquals(1, result);
	}

	@Test
	public void testGetStudentSolutions() {
		submissionRepository.insert(solution);
		List<StringStudentSolution> retrieved = submissionRepository.getStudentSolutions(ID, ID, ID);
		Assert.assertEquals(ANSWER, retrieved.get(0).getStringValue());
	}

	@Test
	public void testDeleteStudentSolution() {
		submissionRepository.insert(solution);
		List<StringStudentSolution> retrieved = submissionRepository.getStudentSolutions(ID, ID, ID);
		int studentSolutionId = retrieved.get(0).getStudentSolutionId();
		Assert.assertTrue(submissionRepository.deleteStudentSolution(studentSolutionId));
		Assert.assertFalse(submissionRepository.deleteStudentSolution(studentSolutionId));
	}

	@After
	public void tearDown() {
		// Cleanup database
		List<StringStudentSolution> retrieved = submissionRepository.getStudentSolutions(-1, -1, -1);
		for (int i = 0; i < retrieved.size(); i++) {
			submissionRepository.deleteStudentSolution(retrieved.get(0).getStudentSolutionId());
		}
	}
}
