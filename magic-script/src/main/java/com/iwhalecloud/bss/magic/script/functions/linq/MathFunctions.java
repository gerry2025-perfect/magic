package com.iwhalecloud.bss.magic.script.functions.linq;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.annotation.Function;
import com.iwhalecloud.bss.magic.script.functions.NumberExtension;

public class MathFunctions {

	@Comment("Round to N decimal places")
	@Function
	public double round(@Comment(name = "target", value = "Target value") Number target,
						@Comment(name = "len", value = "Number of decimal places to retain") int len) {
		return NumberExtension.round(target, len);
	}

	@Comment("Round to N decimal places")
	@Function
	public double round(@Comment(name = "target", value = "Target value") Number target) {
		return NumberExtension.round(target, 0);
	}

	@Comment("Round up")
	@Function
	public Number ceil(@Comment(name = "target", value = "Target value") Number target) {
		return NumberExtension.ceil(target);
	}

	@Comment("Round down")
	@Function
	public Number floor(@Comment(name = "target", value = "Target value") Number target) {
		return NumberExtension.floor(target);
	}

	@Comment("Calculate percentage")
	@Function
	public String percent(@Comment(name = "target", value = "Target value") Number target,
						  @Comment(name = "len", value = "Number of decimal places to retain") int len) {
		return NumberExtension.asPercent(target, len);
	}

	@Comment("Calculate percentage")
	@Function
	public String percent(@Comment(name = "target", value = "Target value") Number target) {
		return NumberExtension.asPercent(target, 0);
	}
}
