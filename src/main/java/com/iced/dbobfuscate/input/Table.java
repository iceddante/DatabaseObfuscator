package com.iced.dbobfuscate.input;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Created by IcedDante on 11/18/2016.
 */
@Data
@Builder
public class Table {
	String name;

	@Singular List<Column> columns;
}
