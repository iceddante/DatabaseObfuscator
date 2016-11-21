package com.iced.dbobfuscate.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IcedDante on 11/20/2016.
 */
@Data
@Builder
@AllArgsConstructor
public class Cell {
	Column column;
	Object value;
}
