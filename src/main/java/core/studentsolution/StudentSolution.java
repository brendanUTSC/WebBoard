package core.studentsolution;

import java.util.logging.Logger;

import core.AssignmentElement;
import core.answer.AssignmentAnswer;
import core.question.AssignmentQuestion;
import database.AnswerRepository;
import database.QuestionRepository;

public abstract class StudentSolution {
	private int studentSolutionId;
	private int studentId;
	private int assignmentId;
	private int questionNumber;
	private int mark;
	private int maximumMark;
	private String stringValue;

	public StudentSolution(int studentId, int assignmentId, int questionNumber, String stringValue) {
		setStringValue(stringValue);
		setStudentId(studentId);
		setAssignmentId(assignmentId);
		setQuestionNumber(questionNumber);
	}

	public StudentSolution(int studentId, int assignmentId, int questionNumber, String stringValue, int mark,
			int maximumMark) {
		setStringValue(stringValue);
		setStudentId(studentId);
		setAssignmentId(assignmentId);
		setQuestionNumber(questionNumber);
		setMark(mark);
		this.maximumMark = maximumMark;
	}

	public abstract boolean compareAnswer(AssignmentAnswer answer);

	public void calculateMark() {
		AnswerRepository answerRepository = new AnswerRepository();
		// Assume that every answer has a distinct AnswerNumber, per assignment. Note,
		// this may not hold in the future and could potentially need re-working.
		Logger.getLogger(this.getClass().getSimpleName())
				.info("Getting answers for assignment: " + assignmentId + " question: " + questionNumber);
		AssignmentElement expectedAnswer = answerRepository.getAnswersForAssignment(assignmentId, questionNumber)
				.get(0);
		QuestionRepository questionRepository = new QuestionRepository();
		maximumMark = ((AssignmentQuestion) questionRepository.getQuestionsForAssignment(assignmentId, questionNumber)
				.get(0)).getWeight();

		if (this.compareAnswer((AssignmentAnswer) expectedAnswer)) {
			setMark(maximumMark);
		} else {
			setMark(0);
		}
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public int getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(int assignmentId) {
		this.assignmentId = assignmentId;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public int getMaximumMark() {
		return maximumMark;
	}

	public int getStudentSolutionId() {
		return studentSolutionId;
	}

	public void setStudentSolutionId(int studentSolutionId) {
		this.studentSolutionId = studentSolutionId;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int userId) {
		this.studentId = userId;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
}
