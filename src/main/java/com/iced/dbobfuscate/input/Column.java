package com.iced.dbobfuscate.input;

import lombok.Builder;
import lombok.Data;

import java.sql.Types;
import java.util.function.Function;

/**
 * Created by IcedDante on 11/18/2016.
 */
@Data
@Builder
public class Column {
	String name;
	Integer dataType;
	Strategy strategy;
	String fakerStrategy;
}
