package core;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceOptions {
	private List<String> options;
	private List<String> optionsFilePaths;

	public MultipleChoiceOptions(List<String> options) {
		setOptions(options);
		setOptionsFilePaths(new ArrayList<String>());
	}

	public MultipleChoiceOptions(List<String> options, List<String> optionsFilePaths) {
		setOptions(options);
		setOptionsFilePaths(optionsFilePaths);
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public List<String> getOptionsFilePaths() {
		return optionsFilePaths;
	}

	public void setOptionsFilePaths(List<String> optionsFilePaths) {
		this.optionsFilePaths = optionsFilePaths;
	}
}
