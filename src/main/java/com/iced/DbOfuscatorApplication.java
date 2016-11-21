package com.iced;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iced.dbobfuscate.input.Column;
import com.iced.dbobfuscate.input.Command;
import com.iced.dbobfuscate.input.Table;
import com.iced.dbofuscate.service.DBRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.sql.SQLType;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DbOfuscatorApplication {

	@Autowired
	DBRunner dbRunner;

	@Value("${dbobfuscate.command-source}") String commandLocation;

	public static void main(String[] args) {

		SpringApplication.run(DbOfuscatorApplication.class, args);

	}

	@Bean
	public CommandLineRunner run(DBRunner dbRunner) {
		return (args) -> {
			Command jsonCommand = loadCommand();
			dbRunner.execute(jsonCommand==null ? buildCommand() : jsonCommand);
		};
	}

	private Command loadCommand() {
		File jsonFile = new File(commandLocation);
		if (jsonFile.exists()) {
			ObjectMapper om = new ObjectMapper();
			om.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			Command cmd = null;
			Table t = null;
			try {
				cmd = om.readValue(jsonFile, Command.class);
			} catch (IOException e) {
				throw new RuntimeException("Unable to load json command: ", e);
			}
			return cmd;
		}
		return null;
	}

	private Command buildCommand() {
		return null;
			/*Command c = Command.builder()
				.schema("").build();

		List<Table> tables = new ArrayList<>();
		tables.add(Table.builder()
				.name("address")
				.column(Column.builder()
						.name("address1")
						.fakerStrategy("address.streetAddress")
						.build())
				.build());

		c.setTables(tables);
		return c;*/
	}


}
