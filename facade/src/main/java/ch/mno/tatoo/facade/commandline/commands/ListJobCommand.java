package ch.mno.tatoo.facade.commandline.commands;

import ch.mno.tatoo.facade.commandline.data.Job;

import java.util.regex.Pattern;

/**
 * Command object for "listJob".
 * Will list all jobs in the project, matching the given pattern
 *
 * Created by dutoitc on 11/08/15.
 */
public class ListJobCommand extends AbstractListCommand<Job> {

	public ListJobCommand(Pattern pattern) {
		super(pattern);
	}

	@Override
	protected String getFullPath(Job obj) {
		return obj.getFullName();
	}

	@Override
	protected String getShortPath(Job obj) {
		return obj.getName();
	}

	public Job buildElement(String name, String path) {
		return new Job(name, path);
	}

	@Override
	public String build() {
		return "listJob";
	}


	@Override
	public String toString() {
		return "ListJobCommand[]";
	}

}
