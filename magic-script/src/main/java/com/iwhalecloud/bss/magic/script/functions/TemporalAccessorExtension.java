package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TemporalAccessorExtension {

	@Comment("Date formatting")
	public static String format(TemporalAccessor source,
								@Comment(name = "pattern", value = "Format, e.g., yyyy-MM-dd") String pattern) {
		return DateTimeFormatter.ofPattern(pattern).format(source);
	}
}
