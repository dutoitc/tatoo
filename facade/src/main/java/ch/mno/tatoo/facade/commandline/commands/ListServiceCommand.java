package ch.mno.tatoo.facade.commandline.commands;

import ch.mno.tatoo.facade.commandline.data.Service;

import java.util.regex.Pattern;

/**
 * Command object for "listService".
 * Will list all services in the project, matching the given pattern
 *
 * Created by dutoitc on 11/08/15.
 */
public class ListServiceCommand extends AbstractListCommand<Service> {

	public ListServiceCommand(Pattern pattern) {
		super(pattern);
	}

	@Override
	protected String getFullPath(Service obj) {
		return obj.getFullName();
	}

	@Override
	protected String getShortPath(Service obj) {
		return obj.getName();
	}

	public Service buildElement(String name, String path) {
		return new Service(name, path);
	}

	@Override
	public String build() {
		return "listService";
	}


	@Override
	public String toString() {
		return "ListServiceCommand[]";
	}

}
