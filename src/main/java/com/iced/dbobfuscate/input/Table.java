package com.iced.dbobfuscate.input;

import lombok.*;

import java.util.List;

/**
 * Created by IcedDante on 11/18/2016.
 */
@Data
@Builder
@AllArgsConstructor
public class Table {
	String name;

	@Singular List<Column> columns;
}
