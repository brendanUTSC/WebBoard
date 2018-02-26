package core;

import java.util.List;

import database.AssignmentRepository;
import database.StudentSolutionRepository;
import database.SubmissionRepository;

public class UserGrade {
	private User user;
	private int grade;

	public UserGrade(User user, Course course) {
		AssignmentRepository assignmentRepository = new AssignmentRepository();
		List<Assignment> assignments = assignmentRepository.getAllAssignmentsForCourse(course.getCourseId());
		StudentSolutionRepository studentSolutionRepository = new StudentSolutionRepository();
		SubmissionRepository submissionRepository = new SubmissionRepository();
		int counter = 0;
		for (Assignment assignment : assignments) {
			counter += submissionRepository.getSubmission(user.getId(), assignment.getAssignmentId()).get(0).getMark();
		}
	}

	public UserGrade(User user, int grade) {
		this.user = user;
		this.grade = grade;
	}

	public int getUserId() {
		return user.getId();
	}

	public int getGrade() {
		return grade;
	}
}
