package com.iced;

import com.iced.dbobfuscate.input.Column;
import com.iced.dbobfuscate.input.Command;
import com.iced.dbobfuscate.input.Table;
import com.iced.dbofuscate.service.DBRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.SQLType;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DbOfuscatorApplication {

	@Autowired
	DBRunner dbRunner;

	public static void main(String[] args) {

		SpringApplication.run(DbOfuscatorApplication.class, args);

	}

	@Bean
	public CommandLineRunner run(DBRunner dbRunner) {
		return (args) -> {
			dbRunner.execute(buildCommand());
		};
	}

	private Command buildCommand() {
		Command c = Command.builder()
				.schema("doctorsorders").build();

		List<Table> tables = new ArrayList<>();
		tables.add(Table.builder()
				.name("address")
				.column(Column.builder()
						.name("address1")
						.fakerStrategy("address.streetAddress")
						.build())
				.build());

		c.setTables(tables);
		return c;
	}


}
