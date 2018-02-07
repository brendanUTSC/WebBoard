package core;

public class Submission {
	private int userId;
	private int assignmentId;
	private int submissionId;
	private int mark;

	public Submission(int userId, int assignmentId, int mark) {
		setUserId(userId);
		setAssignmentId(assignmentId);
		setMark(mark);
	}

	public Submission(int userId, int assignmentId, int submissionId, int mark) {
		setUserId(userId);
		setAssignmentId(assignmentId);
		setSubmissionId(submissionId);
		setMark(mark);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(int assignmentId) {
		this.assignmentId = assignmentId;
	}

	public int getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(int submissionId) {
		this.submissionId = submissionId;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}
}
