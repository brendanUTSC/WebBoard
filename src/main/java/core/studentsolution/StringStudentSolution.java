package core.studentsolution;

import core.answer.AssignmentAnswer;
import core.answer.StringAssignmentAnswer;

public class StringStudentSolution extends StudentSolution {
	public StringStudentSolution(int studentId, int assignmentId, int questionNumber, String studentAnswer) {
		super(studentId, assignmentId, questionNumber, studentAnswer);
	}

	public StringStudentSolution(int studentId, int assignmentId, int questionNumber, String studentAnswer, int mark,
			int maximumMark) {
		super(studentId, assignmentId, questionNumber, studentAnswer, mark, maximumMark);
	}

	@Override
	public boolean compareAnswer(AssignmentAnswer answer) {
		return ((StringAssignmentAnswer) (answer)).getCleanValue().equals(getStringValue());
	}
}
