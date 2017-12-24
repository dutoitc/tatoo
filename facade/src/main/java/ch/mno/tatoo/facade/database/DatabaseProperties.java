package ch.mno.tatoo.facade.database;

/**
 * Object holding JDBC url, username and password.
 */
public class DatabaseProperties {
	private final String jdbcUrl;
	private final String username;
	private final String password;

	public DatabaseProperties(String jdbcUrl, String username, String password) {
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}