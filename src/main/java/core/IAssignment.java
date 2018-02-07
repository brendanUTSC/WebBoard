package core;

public interface IAssignment {
	/**
	 * Saves the Assignment (and consequently each of the Assignment's questions and
	 * answers) onto the user's machine. The questions and answers are each saved by
	 * calling {@link AssignmentElement#saveToLocalMachine()}.
	 * 
	 * @return path to the Assignment directory on the user's machine
	 */
	public String saveToLocalMachine();

	/**
	 * Saves the Assignment (and consequently each of the Assignment's questions and
	 * answers) to the database. This function also sets the Assignment's
	 * AssignmentId appropriately.
	 */
	public void saveToDatabase();

	/**
	 * Updates a previous Assignment (and consequently each of the Assignment's
	 * questions and answers) and saves the updated Assignment (and each of the
	 * Assignment's updated questions and answers) to the database.
	 */
	public void updateToDatabase();
}
