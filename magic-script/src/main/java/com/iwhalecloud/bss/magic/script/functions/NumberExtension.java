package com.iwhalecloud.bss.magic.script.functions;


import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Number类型扩展
 */
public class NumberExtension {

	@Comment("Round to N decimal places")
	public static double round(Number number,
							   @Comment(name = "num", value = "Specify the number of decimal places") int num) {
		return new BigDecimal("" + number.doubleValue()).setScale(num, RoundingMode.HALF_UP).doubleValue();
	}

	@Comment("Round down")
	public static Number floor(Number number) {
		if (number instanceof Double || number instanceof Float) {
			return fixed(Math.floor(number.floatValue()));
		} else if (number instanceof BigDecimal) {
			return ((BigDecimal) number).setScale(0, RoundingMode.FLOOR);
		}
		return number;
	}

	@Comment("Round up")
	public static Number ceil(Number number) {
		if (number instanceof Double || number instanceof Float) {
			return fixed(Math.ceil(number.doubleValue()));
		} else if (number instanceof BigDecimal) {
			return ((BigDecimal) number).setScale(0, RoundingMode.UP);
		}
		return number;
	}

	@Comment("Convert to percentage")
	public static String asPercent(Number number,
								   @Comment(name = "num", value = "Specify the number of decimal places") int num) {
		return new BigDecimal(number.doubleValue() * 100).setScale(num, RoundingMode.HALF_UP).toString() + "%";
	}

	private static Number fixed(double value) {
		if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
			return (long) value;
		}
		return value;
	}

	@Comment("Rounds to N decimal places, similar to JavaScript's toFixed")
	public String toFixed(Number number,
						  @Comment(name = "num", value = "Specify the number of decimal places") int num) {
		return new BigDecimal("" + number.doubleValue()).setScale(num, RoundingMode.HALF_UP).toString();
	}
}
