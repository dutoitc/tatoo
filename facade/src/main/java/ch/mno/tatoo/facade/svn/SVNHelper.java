package ch.mno.tatoo.facade.svn;

import ch.mno.tatoo.facade.commandline.data.AbstractDeployableComponent;
import ch.mno.tatoo.facade.commandline.data.Job;
import ch.mno.tatoo.facade.commandline.data.Route;
import ch.mno.tatoo.facade.commandline.data.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 11/08/15.
 */
public class SVNHelper {


	public static final int CONNECT_TIMEOUT = 10000;

	/**
	 *
	 * @param svnUrl SVN server URL base, e.g.: http://server/project/PROJECT/trunk
	 * @param job
	 * @param username SVN Username
	 * @param password SVN Password
	 * @return Greatest version, or null if none found
	 * @throws Exception
	 */
	public static Float getLatestVersion(Job job, String svnUrl, String username, String password) throws Exception {
		return getLatestVersion(svnUrl, job, username, password, "process");
	}
	public static Float getLatestVersion(Service service, String svnUrl, String username, String password) throws Exception {
		return getLatestVersion(svnUrl, service, username, password, "services");
	}
	public static Float getLatestVersion(Route route, String svnUrl, String username, String password) throws Exception {
		return getLatestVersion(svnUrl, route, username, password, "routes");
	}



	private static Float getLatestVersion(String svnUrl, AbstractDeployableComponent component, String username, String password, String path) throws Exception {
		// http://server/project/PROJECT/trunk/process/F_Publication/A_commun/
		try {
			if (component.getPath()==null || component.getPath().isEmpty()) {
				//throw new RuntimeException("Missing component path");
				System.err.println("Skipping component with invalid path: " + component.getFullName());
				return Float.MIN_VALUE;
			}
			String urlBase = svnUrl + "/"+path+"/" + component.getPath().replace(".", "/") + '/';
			urlBase = urlBase.replace("/Standard Jobs", ""); // Note dutoitc: Talend 6 semble ajouter un dossier qui n'existe pas dans l'URL... on la vire.
			String page = getHttpResponse(urlBase, username, password);
			Matcher matcher = Pattern.compile(">"+component.getName() + "_(\\d+\\.\\d+)\\.item").matcher(page); // '>' avoids False-Positive like SubJobXX.item instead of XX.item
			Float bestVersion = null;
			while (matcher.find()) {
				String versionStr = matcher.group(1);
				float version = Float.parseFloat(versionStr);
				if (bestVersion == null || bestVersion < version) {
					bestVersion = version;
				}
			}
			return bestVersion;
		} catch (FileNotFoundException e) {
			throw e;
		}
	}

	public List<AbstractDeployableComponent> getLatestVersions(String username, String password, String blacklistIn, String basepath) throws Exception {
		List<String> blacklist = Arrays.asList(blacklistIn.split(","));
		List<AbstractDeployableComponent> ret = new ArrayList<>();
		ret.addAll(getLatestVersions(username, password, blacklist, basepath, "/process"));
		ret.addAll(getLatestVersions(username, password, blacklist, basepath, "/services"));
		ret.addAll(getLatestVersions(username, password, blacklist, basepath, "/routes"));
		return ret;
	}

	public List<AbstractDeployableComponent> getLatestVersions(String username, String password, List<String> blacklist, String basepath, String path) throws Exception {
		List<AbstractDeployableComponent> ret = new ArrayList<>();

		String page = getHttpResponse(basepath + path, username, password);
		Pattern patref = Pattern.compile("href=.(.*?).>");
		Matcher match = patref.matcher(page);
		while (match.find()) {
			String el = match.group(1);
			if (el.charAt(0)=='.') continue;
			for (String el2: blacklist) {
				if (el.indexOf(el2)>=0) continue;
			}
			System.out.println("Found " + match.group(1));
		}

//		System.out.println(page);
		return ret;
	}


	/**
	 * Get HTTP page with basic-auth
	 * @param address
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static String getHttpResponse(String address, final String username, final String password) throws Exception {
		System.out.println("Downloading " + address);
		URL url = new URL(address);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(CONNECT_TIMEOUT);

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password.toCharArray());
			}
		});

		/*if (username != null && password != null){
			String user_pass = username + ":" + password;
			String encoded = Base64.encodeBase64String(user_pass.getBytes());
			conn.setRequestProperty("Authorization", "Basic " + encoded);
		}*/

		String line = "";
		StringBuffer sb = new StringBuffer();
		BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()) );
		while((line = input.readLine()) != null)
			sb.append(line);
		input.close();
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		Service service = new Service("RechercheCryptomonnaie", "E_Bank/F_Common");
		Float v = SVNHelper.getLatestVersion(service, "http://server/app/project/trunk", "username", "password");
		System.out.println("Version is " + v);
	}

}
