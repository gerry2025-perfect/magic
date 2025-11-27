package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Date扩展
 */
public class DateExtension {

	private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

	@Comment("Date formatting")
	public static String format(Date source,
								@Comment(name = "pattern", value = "Format, e.g., yyyy-MM-dd") String pattern) {
		return Instant.ofEpochMilli(source.getTime()).atZone(SYSTEM_ZONE_ID).format(DateTimeFormatter.ofPattern(pattern));
	}
}
