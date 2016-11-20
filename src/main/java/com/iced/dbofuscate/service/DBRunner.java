package com.iced.dbofuscate.service;

import com.iced.dbobfuscate.input.Cell;
import com.iced.dbobfuscate.input.Column;
import com.iced.dbobfuscate.input.Command;
import com.iced.dbobfuscate.input.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static com.iced.dbofuscate.service.DBObfuscateSupport.*;

/**
 * Created by IcedDante on 11/18/2016.
 */
@Repository
public class DBRunner {
	private JdbcTemplate jdbcTemplate=null;

	private static final String SCHEMA_NAME = "doctorsorders";

	@Autowired
	public DBRunner(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	public void execute(Command obfuscateCommand) throws SQLException {
		Connection con = DriverManager
				.getConnection(datasourceUrl, username, password);
		Map<Table, List<Column>> keyColumnsByTable = obfuscateCommand
				.getTables().stream()
				.collect(toMap(Function.identity(),
						(Table t) -> getIdColumns(con, t.getName())
				));

		for (Table table : obfuscateCommand.getTables()) {
			List<List<Cell>> idsByTable =
					findIdValues(table, keyColumnsByTable.get(table));
			obfuscate(table, keyColumnsByTable.get(table), idsByTable);
		}

	}

	/**
	 * For each table, given a list of ID (prim key) columns, fetch
	 * all these values from the db so we can do updates on them later
	 * @param table
	 * @param idColumns
	 * @return List of lists. One list for each row, and the row may contain one or
	 * more keys
	 */
	public List<List<Cell>> findIdValues(Table table, List<Column> idColumns) {
		String sql =
				"SELECT "+
				idColumns.stream().map(Column::getName).collect(joining(", ")) +
				" FROM " + table.getName();
		List<List<Cell>> cells = jdbcTemplate.query(sql,
				(rs, rowNum) -> {
					List<Cell> retCells = new ArrayList<>();
					for(Column col : idColumns) {
						retCells.add(Cell.builder()
								.column(Column.builder()
										.name(col.getName())
										.build())
								.value(rs.getObject(col.getName()))
								.build());
					};
					return retCells;
				}
		);
		return cells;
	}

	public List<Column> getIdColumns(Connection con, String tableName) throws RuntimeException {
		//Get empty result set
		List<Column> retList = new ArrayList<>();

		try {
			DatabaseMetaData md = con.getMetaData();
			ResultSet keys = md.getPrimaryKeys(null, SCHEMA_NAME, tableName);

			Map<String, Integer> typeMap = jdbcTemplate.query(
					"SELECT * FROM "+tableName+" WHERE 2 > 1",
					new Object[]{}, (rs) -> {
						int numCols = rs.getMetaData().getColumnCount();
						Map<String, Integer> retMap = new HashMap<>();

						for(int i=1; i<=numCols; i++) {
							retMap.put(rs.getMetaData().getColumnName(i),
									rs.getMetaData().getColumnType(i));
							System.out.println(rs.getMetaData().getColumnType(i) + ": " +
									rs.getMetaData().getColumnName(i));
						}
						return retMap;
					});

			while(keys.next()) {
				String columnName = keys.getString("COLUMN_NAME");
				retList.add(Column.builder()
						.name(columnName)
						.dataType(typeMap.get(columnName))
						.build());
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if (retList.size() > 1) {
			throw new RuntimeException("Multiple keys not currently allowed");
		}
		return retList;
	}

	/**
	 *
	 * @param table The table containing data to be obfuscated
	 * @param keys primary key columns
	 * @param idsByTable specific ids that are used to select individual
	 *                   rows
	 */
	private void obfuscate(Table table, List<Column> keys, List<List<Cell>> idsByTable) {
		StringBuffer sql = new StringBuffer("UPDATE "+table.getName()+" SET ");
		sql.append(table.getColumns().stream()
				.map(c -> c.getName() + " = ? ")
				.collect(joining(", ")));
		sql.append(" WHERE " + keys.stream()
				.map(k -> k.getName() + " = ?")
				.collect(joining(", ")));

		System.out.println("Running qry: "+sql.toString());
		List<Object[]> objectArgs = idsByTable.stream()
				.map(idsToObjectArgs(table.getColumns()))
				.collect(toList());
		jdbcTemplate.batchUpdate(sql.toString(), objectArgs);
	}


}
