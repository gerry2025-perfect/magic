package com.iwhalecloud.bss.magic.script.functions.linq;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.annotation.Function;
import com.iwhalecloud.bss.magic.script.functions.DateExtension;
import com.iwhalecloud.bss.magic.script.functions.TemporalAccessorExtension;

import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * Linq中的函数
 */
public class LinqFunctions {

	@Function
	@Comment("Checking if a value is empty")
	public Object ifnull(@Comment(name = "target", value = "Target value") Object target,
						 @Comment(name = "trueValue", value = "Empty values") Object trueValue) {
		return target == null ? trueValue : target;
	}

	@Function
	@Comment("Date formatting")
	public String date_format(@Comment(name = "target", value = "Target date") Date target,
							  @Comment(name = "pattern", value = "Format") String pattern) {
		return target == null ? null : DateExtension.format(target, pattern);
	}

	@Function
	@Comment("Date formatting")
	public String date_format(@Comment(name = "target", value = "Target date") Date target) {
		return target == null ? null : DateExtension.format(target, "yyyy-MM-dd HH:mm:ss");
	}


	@Function
	@Comment("Date formatting")
	public String date_format(@Comment(name = "target", value = "Target date") TemporalAccessor target,
							  @Comment(name = "pattern", value = "Format") String pattern) {
		return target == null ? null : TemporalAccessorExtension.format(target, pattern);
	}

	@Function
	@Comment("Get current time")
	public Date now() {
		return new Date();
	}

	@Function
	@Comment("Get current timestamp (seconds)")
	public long current_timestamp() {
		return System.currentTimeMillis() / 1000;
	}

	@Function
	@Comment("Get current timestamp (milliseconds)")
	public long current_timestamp_millis() {
		return System.currentTimeMillis();
	}

}
