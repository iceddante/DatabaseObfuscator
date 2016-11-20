package com.iced.dbobfuscate.input;

import lombok.Builder;
import lombok.Data;

/**
 * Created by IcedDante on 11/20/2016.
 */
@Data
@Builder
public class Cell {
	Column column;
	Object value;
}
