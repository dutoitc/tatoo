package ch.mno.tatoo.facade.database;

import org.apache.commons.collections4.ListUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Facade to ease database operations.
 * Created by dutoitc on 14/10/15.
 */
public class OracleDBFacade {

	private final DatabaseProperties databaseProperties;

	public OracleDBFacade(DatabaseProperties databaseProperties)  throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException{
		String driver = "oracle.jdbc.driver.OracleDriver";
		Class.forName(driver).newInstance();
		this.databaseProperties = databaseProperties;
	}

	public List<String> getTables() throws SQLException {
		try (
				Connection conn = DriverManager.getConnection(databaseProperties.getJdbcUrl(), databaseProperties.getUsername(), databaseProperties.getPassword());
				PreparedStatement ps = conn.prepareStatement("SELECT TABLE_NAME FROM ALL_TABLES");
				ResultSet rs = ps.executeQuery();
		) {
			List<String> lines = new ArrayList<>();
			while (rs.next()) {
				lines.add(rs.getString(1));
			}
			return lines;
		}
	}




	public List<String> getFields(String table) throws SQLException {
		try (
				Connection conn = DriverManager.getConnection(databaseProperties.getJdbcUrl(), databaseProperties.getUsername(), databaseProperties.getPassword());
		        ResultSet rs = conn.getMetaData().getColumns(null, null, table, null);
		) {
			List<String> lines = new ArrayList<>();
			while (rs.next()) {
				lines.add(rs.getString("COLUMN_NAME") + " " + rs.getString("DATA_TYPE") + " " + rs.getInt("COLUMN_SIZE"));
			}
			return lines;
		}
	}


	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
		String prefix="STG_";

		DatabaseProperties db1= new DatabaseProperties("jdbc:oracle:thin:@dbhostname:1527:instance1", "myUser", "myPass");
		DatabaseProperties db2= new DatabaseProperties("jdbc:oracle:thin:@dbhostname:1527:instance1", "myUser", "myPass");

//		DatabaseProperties db1= new DatabaseProperties("jdbc:oracle:thin:@hostname:1527:instance", "user", "pass");
//		DatabaseProperties db2= new DatabaseProperties("jdbc:oracle:thin:@hostname:1527:instance", "user", "pass");

		// Get tables data
		OracleDBFacade dbFacade1 = new OracleDBFacade(db1);
		OracleDBFacade dbFacade2 = new OracleDBFacade(db2);
		List<String> tables1 = dbFacade1.getTables();
		List<String> tables2 = dbFacade2.getTables();

		// Filter
		Predicate<String> filter = name->name.startsWith("MST_") || name.startsWith("STG_")  || name.startsWith("X_");
		List<String> subTables1 = tables1.stream().filter(filter).sorted().distinct().collect(Collectors.toList());
		List<String> subTables2 = tables2.stream().filter(filter).sorted().distinct().collect(Collectors.toList());

		ListUtils.subtract(subTables1, subTables2).forEach(name->System.out.println("Error, table only in 1: " + name));
		ListUtils.subtract(subTables2, subTables1).forEach(name->System.out.println("Error, table only in 2: " + name));
		ListUtils.union(subTables1, subTables2).forEach(table -> {
			try {
				List<String> fields1 = dbFacade1.getFields(table).stream().sorted().distinct().collect(Collectors.toList());
				List<String> fields2 = dbFacade2.getFields(table).stream().sorted().distinct().collect(Collectors.toList());
				ListUtils.subtract(fields1, fields2).forEach(name2->System.out.println("Error, field only in 1: " + table + "." + name2));
				ListUtils.subtract(fields2, fields1).forEach(name2->System.out.println("Error, field only in 2: " + table + "." + name2));
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		});

	}

}
