package ch.mno.tatoo.facade.commandline.commands;

import ch.mno.tatoo.facade.commandline.data.Route;

import java.util.regex.Pattern;

/**
 * Command object for "listRoute".
 * Will list all routes in the project, matching the given pattern
 *
 * Created by dutoitc on 11/08/15.
 */
public class ListRouteCommand extends AbstractListCommand<Route> {

	public ListRouteCommand(Pattern pattern) {
		super(pattern);
	}

	@Override
	protected String getFullPath(Route obj) {
		return obj.getFullName();
	}


	@Override
	protected String getShortPath(Route obj) {
		return obj.getName();
	}

	public Route buildElement(String name, String path) {
		return new Route(name, path);
	}

	@Override
	public String build() {
		return "listRoute";
	}


	@Override
	public String toString() {
		return "ListRouteCommand[]";
	}

}
