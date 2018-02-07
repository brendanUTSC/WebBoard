package core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import core.AssignmentElement.Format;
import core.answer.AssignmentAnswer;
import core.question.AssignmentQuestion;
import core.question.MultipleChoiceQuestion;
import database.AnswerRepository;
import database.AssignmentRepository;
import database.QuestionOptionRepository;
import database.QuestionRepository;
import utils.ApplicationDataDirectoryUtils;

/**
 * An implementation of IAssignment for a MySQL database. An Assignment contains
 * a list of questions and answers, along with a deadline. This class implements
 * saving and retrieving from a MySQL database and uses
 * {@link QuestionRepository} and {@link AnswerRepository} to create, read, and
 * update the questions and answers respectively.
 *
 * @author Brendan Zhang
 * 
 * @see QuestionRepository
 * @see AnswerRepository
 */
public class Assignment implements IAssignment {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private List<AssignmentQuestion> questions;

	public List<AssignmentQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AssignmentQuestion> questions) {
		for (AssignmentQuestion question : questions) {
			addQuestion(question);
		}
	}

	private List<AssignmentAnswer> answers;

	public List<AssignmentAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<AssignmentAnswer> answers) {
		for (AssignmentAnswer answer : answers) {
			addAnswer(answer);
		}
	}

	private int assignmentId;

	public int getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(int id) {
		this.assignmentId = id;
	}

	private LocalDateTime deadline;

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

	private String courseId;

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public Assignment() {
		setName("");
		setCourseId("");
		questions = new ArrayList<AssignmentQuestion>();
		answers = new ArrayList<AssignmentAnswer>();
		setDeadline(LocalDateTime.now());
	}

	public static Assignment retrieveFromDatabase(int assignmentId) {
		AssignmentRepository assignmentRepository = new AssignmentRepository();
		return assignmentRepository.getAssignment(assignmentId);
	}

	public void saveToDatabase() {
		AssignmentRepository assignmentRepository = new AssignmentRepository();
		int assignmentId = assignmentRepository.insert(this);
		this.setAssignmentId(assignmentId);

		QuestionRepository questionRepository = new QuestionRepository();
		for (AssignmentQuestion question : questions) {
			int questionId = questionRepository.insert(question);
			question.setId(questionId);
			if (question.getFormat() == Format.MULTIPLE_CHOICE) {
				QuestionOptionRepository questionOptionRepository = new QuestionOptionRepository();
				questionOptionRepository.insert(questionId,
						((MultipleChoiceQuestion) question).getMultipleChoiceOptions().getOptionsFilePaths());
			}
		}

		AnswerRepository answerRepository = new AnswerRepository();
		for (AssignmentAnswer answer : answers) {
			answerRepository.insert(answer);
		}
	}

	public void updateToDatabase() {
		AssignmentRepository assignmentRepository = new AssignmentRepository();
		assignmentRepository.update(this.getAssignmentId(), getName(), deadline, courseId);

		QuestionRepository questionRepository = new QuestionRepository();
		for (AssignmentQuestion question : questions) {
			boolean success = questionRepository.update(this.getAssignmentId(), question.getIndex(), question);
			if (!success) {
				// Question doesn't exist, so insert it.
				int questionId = questionRepository.insert(question);
				question.setId(questionId);
			}

			if (question.getFormat() == Format.MULTIPLE_CHOICE) {
				QuestionOptionRepository questionOptionRepository = new QuestionOptionRepository();
				questionOptionRepository.update(question.getId(),
						((MultipleChoiceQuestion) question).getMultipleChoiceOptions().getOptionsFilePaths());
			}
		}

		AnswerRepository answerRepository = new AnswerRepository();
		for (AssignmentAnswer answer : answers) {
			boolean success = answerRepository.update(this.getAssignmentId(), answer.getIndex(), answer);
			if (!success) {
				// Answer doesn't exist, so insert it.
				answerRepository.insert(answer);
			}
		}
	}

	public String saveToLocalMachine() {
		for (AssignmentElement question : questions) {
			question.saveToLocalMachine();
		}
		for (AssignmentElement answer : answers) {
			answer.saveToLocalMachine();
		}

		String assignmentFilePath = ApplicationDataDirectoryUtils.getAppDataDirectoryPath() + "\\" + getName() + ".txt";
		return assignmentFilePath;
	}

	public void addQuestion(AssignmentQuestion question) {
		question.setAssignment(this);
		question.setIndex(questions.size());
		questions.add(question);
	}

	/**
	 * Inserts the question to the specified index of the questions list. If the
	 * index exceeds the size of the questions list, it adds it to the end of the
	 * list.
	 * 
	 * @param index
	 *            The index to insert the question to
	 * @param question
	 *            The question to add
	 */
	public void addQuestion(int index, AssignmentQuestion question) {
		// Outside current answer list size
		question.setAssignment(this);
		if (index >= questions.size()) {
			question.setIndex(questions.size());
			questions.add(question);
		} else {
			question.setIndex(index);
			questions.set(index, question);
		}
	}

	public void addAnswer(AssignmentAnswer answer) {
		answer.setAssignment(this);
		answer.setIndex(answers.size());
		answers.add(answer);
	}

	/**
	 * Inserts the answer to the specified index of the answers list. If the index
	 * exceeds the size of the answers list, it adds it to the end of the list.
	 * 
	 * @param index
	 *            The index to insert the answer to
	 * @param answer
	 *            The answer to add
	 */
	public void addAnswer(int index, AssignmentAnswer answer) {
		// Outside current answer list size
		answer.setAssignment(this);
		if (index >= answers.size()) {
			answer.setIndex(answers.size());
			answers.add(answer);
		} else {
			answer.setIndex(index);
			answers.set(index, answer);
		}
	}

	/**
	 * Returns true if and only if the deadline for the assignment has already
	 * passed. This is determined by checking the system local date time versus the
	 * assignment deadline.
	 * 
	 * @return whether the deadline of the assignment has passed
	 */
	public boolean isDeadlinePassed() {
		return LocalDateTime.now().isAfter(deadline);
	}

	/**
	 * Gets the total number of marks for the Assignment by adding up the weights of
	 * all the questions.
	 * 
	 * @return the number of marks for the Assignment
	 */
	public int getTotalMarks() {
		int total = 0;
		for (AssignmentQuestion question : questions) {
			total += question.getWeight();
		}
		return total;
	}
}
