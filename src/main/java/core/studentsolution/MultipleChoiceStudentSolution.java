package core.studentsolution;

import core.answer.AssignmentAnswer;
import core.answer.MultipleChoiceAnswer;

public class MultipleChoiceStudentSolution extends StudentSolution {

	private int selectedOption;

	public MultipleChoiceStudentSolution(int studentId, int assignmentId, int questionNumber, int selectedOption) {
		super(studentId, assignmentId, questionNumber, String.valueOf(selectedOption));
		this.selectedOption = selectedOption;
	}

	@Override
	public boolean compareAnswer(AssignmentAnswer answer) {
		return ((MultipleChoiceAnswer) answer).getCorrectOption() == selectedOption;
	}
}
