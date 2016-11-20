package com.iced.dbofuscate.service;

import com.github.javafaker.Faker;
import com.iced.dbobfuscate.input.Cell;
import com.iced.dbobfuscate.input.Column;
import com.iced.dbobfuscate.input.Strategy;

import java.lang.reflect.Method;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Created by IcedDante on 11/20/2016.
 */
public class DBObfuscateSupport {
	private static final Faker faker = new Faker();

	public static Function<List<Cell>, Object[]> idsToObjectArgs(List<Column> columnsToObfuscate) {
		return (cells -> {
			List<Object> retList = new ArrayList<>();
			retList.addAll(columnsToObfuscate.stream()
					.map(obfuscateData).collect(toList()));
			retList.addAll(cells.stream()
					.map(c -> c.getValue()).collect(toList()));
			return retList.toArray();
		});
	}

	public static Function<Column, Object> obfuscateData = (column) -> {
		if (column.getFakerStrategy() != null) {
			return obfuscateByFaker(column.getFakerStrategy());
		} else if (column.getStrategy() != null) {
			return obfuscateByStrategy(column.getStrategy());
		} else {
			return obfuscateByType(column.getDataType());
		}
	};

	private static Object obfuscateByType(Integer dataType) {
		switch (dataType) {
			case Types.BIGINT:
				return Long.valueOf(ThreadLocalRandom.current().nextLong());
			case Types.DATE:
			case Types.TIMESTAMP:
				return new Date(new Random().nextLong());
			case Types.VARCHAR:
				return UUID.randomUUID().toString();
		};
		throw new RuntimeException("Unsupported data type: " + dataType);
	}

	private static Object obfuscateByStrategy(Strategy strategy) {
		switch (strategy) {

			case FINANCIAL_ACCOUNT:
				return UUID.randomUUID().toString();
		}
		throw new RuntimeException("Unsupported/Invalid strategy: "+strategy);
	}

	public static Object obfuscateByFaker(String fakerMethod) {
		String[] chain = fakerMethod.split("\\.");
		Object fakerInstance = faker;
		Method method = null;
		try {
			for (int i = 0; i < chain.length; i++) {
				method = fakerInstance.getClass().getDeclaredMethod(chain[i]);
				fakerInstance = method.invoke(fakerInstance);
			}
		}catch (Exception e) {
			throw new RuntimeException("Invalid faker: " + fakerMethod, e);
		}
		return fakerInstance;
	}
}
