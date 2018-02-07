package acceptance;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.athaydes.automaton.FXer;

import core.Assignment;
import database.AssignmentRepository;
import database.DatabaseTest;

public class AddAssignmentWeightAcceptanceTest extends DatabaseTest {
	private FXer fxer;

	@Before
	public void setUp() {
		fxer = AcceptanceTestUtils.openLoginScreen();
		AcceptanceTestUtils.login(fxer);
	}

	@Test
	public void testAddingQuestionWithWeight() {
		String testAssignment = "TestWeightedAssignment";
		String date = "01/01/2018";
		int weight = 10;

		AcceptanceTestUtils.addAssignment(fxer, testAssignment, date, weight);

		AssignmentRepository assignmentRepository = new AssignmentRepository();
		List<Assignment> assignments = assignmentRepository.getAllAssignments(false);
		for (Assignment assignment : assignments) {
			if (assignment.getName().equals(testAssignment)) {
				Assert.assertEquals(assignment.getTotalMarks(), 10);
			}
		}

		AcceptanceTestUtils.deleteAssignment(fxer);
	}
}
