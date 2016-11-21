package com.iced.dbobfuscate.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Types;
import java.util.function.Function;

/**
 * Created by IcedDante on 11/18/2016.
 */
@Data
@Builder
@AllArgsConstructor
public class Column {
	String name;
	Integer dataType;
	Strategy strategy;
	String fakerStrategy;
}
