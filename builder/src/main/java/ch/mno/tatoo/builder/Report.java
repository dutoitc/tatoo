package ch.mno.tatoo.builder;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dutoitc on 18/08/15.
 */
public class Report {

	private List<String> servicesBuilt = new ArrayList<>();
	private List<String> routesBuilt = new ArrayList<>();
	private List<String> jobsBuilt = new ArrayList<>();
	private List<String> errors = new ArrayList<>();

	public void addServiceBuilt(String name) {
		servicesBuilt.add(name);
	}

	public void addRouteBuilt(String name) {
		routesBuilt.add(name);
	}

	public void addJobBuilt(String label) {
		jobsBuilt.add(label);
	}

	public void addError(String error) {
		errors.add(error);
	}

	public void report(OutputStream os) {
		PrintWriter pw = new PrintWriter(os);

		pw.write("Services built: " + servicesBuilt.size()+"\n");
		servicesBuilt.forEach(s->pw.write("   - " + s+"\n"));
		pw.write("\n");

		pw.write("Routes built: " + routesBuilt.size()+"\n");
		routesBuilt.forEach(s->pw.write("   - " + s+"\n"));
		pw.write("\n");

		pw.write("Jobs built: " + jobsBuilt.size()+"\n");
		jobsBuilt.forEach(s->pw.write("   - " + s+"\n"));
		pw.write("\n");

		pw.write("Errors: " + errors.size()+"\n");
		errors.forEach(s->pw.write("   - " + s+"\n"));
		pw.write("\n");

		pw.flush();
	}

	public boolean hasError() {
		return !errors.isEmpty();
	}
}
