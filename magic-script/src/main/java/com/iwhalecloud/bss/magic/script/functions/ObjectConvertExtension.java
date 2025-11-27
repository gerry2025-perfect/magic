package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 类型转换
 */
public class ObjectConvertExtension {

	/**
	 * 转int
	 *
	 * @param defaultValue 默认值
	 */
	@Comment("Converts a value to int type")
	public static int asInt(Object val,
							@Comment(name = "defaultValue", value = "Default value when conversion fails") int defaultValue) {
		try {
			return asDecimal(val).intValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转double
	 *
	 * @param defaultValue 默认值
	 */
	@Comment("Converts an object to double type")
	public static double asDouble(Object val,
								  @Comment(name = "defaultValue", value = "Default value when conversion fails") double defaultValue) {
		try {
			return asDecimal(val).doubleValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转long
	 *
	 * @param defaultValue 默认值
	 */
	@Comment("Converts an object to long type")
	public static long asLong(Object val,
							  @Comment(name = "defaultValue", value = "Default value when conversion fails") long defaultValue) {
		try {
			return asDecimal(val).longValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转String
	 */
	@Comment("Converts an object to String type")
	public static String asString(Object val) {
		return asString(val, null);
	}

	/**
	 * 转Date
	 */
	@Comment("Converts an object to Date type, default string format is yyyy-MM-dd HH:mm:ss")
	public static Date asDate(Object val) {
		return asDate(val, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 转BigDecimal
	 */
	@Comment("Converts an object to BigDecimal type")
	public static BigDecimal asDecimal(Object val) {
		if (val instanceof BigDecimal) {
			return (BigDecimal) val;
		}
		return new BigDecimal(asString(val));
	}

	/**
	 * 转BigDecimal
	 */
	@Comment("Converts an object to BigDecimal type")
	public static BigDecimal asDecimal(Object val,
									   @Comment(name = "defaultVal", value = "Default value when conversion fails") BigDecimal defaultVal) {
		if (val instanceof BigDecimal) {
			return (BigDecimal) val;
		}
		try {
			return new BigDecimal(asString(val));
		} catch (Exception e) {
			return defaultVal;
		}
	}

	/**
	 * 转Date
	 */
	@Comment("Converts an object to Date type, supports String, 10-digit, and 13-digit timestamps")
	public static Date asDate(Object val,
							  @Comment(name = "format", value = "Date format, e.g., yyyy-MM-dd HH:mm:ss") String... formats) {
		if (val == null) {
			return null;
		}
		if (val instanceof String) {
			for (String format : formats) {
				try {
					return new SimpleDateFormat(format).parse(val.toString());
				} catch (ParseException e) {
					long longVal = asLong(val, -1);
					if (longVal > 0) {
						return asDate(longVal, format);
					}
				}
			}
		} else if (val instanceof Date) {
			return (Date) val;
		} else if (val instanceof Number) {
			Number number = (Number) val;
			if (val.toString().length() == 10) { //10位时间戳
				return new Date(number.longValue() * 1000L);
			} else if (val.toString().length() == 13) {    //13位时间戳
				return new Date(number.longValue());
			}
		} else if (val instanceof LocalDateTime) { //LocalDateTime类型
			return Date.from(((LocalDateTime) val).atZone(ZoneId.systemDefault()).toInstant());
		}
		return null;
	}

	/**
	 * 转String
	 *
	 * @param defaultValue 默认值
	 */
	@Comment("Converts an object to String type")
	public static String asString(Object val,
								  @Comment(name = "defaultValue", value = "Default value when conversion fails") String defaultValue) {
		return val == null ? defaultValue : val.toString();
	}

	/**
	 * 转int
	 */
	@Comment("Converts a value to int type, returning 0 if conversion fails")
	public int asInt(Object val) {
		return asInt(val, 0);
	}

	/**
	 * 转double
	 */
	@Comment("Converts an object to double type, returning 0.0 if conversion fails")
	public double asDouble(Object val) {
		return asDouble(val, 0.0);
	}

	/**
	 * 转long
	 */
	@Comment("Converts an object to long type, returning 0L if conversion fails")
	public long asLong(Object val) {
		return asLong(val, 0L);
	}

	/**
	 * 转byte
	 */
	@Comment("Converts an object to byte type, returning 0 if conversion fails")
	public byte asByte(Object val) {
		return asByte(val, (byte) 0);
	}

	/**
	 * 转byte
	 *
	 * @param defaultValue 默认值
	 */
	@Comment("Converts an object to byte type")
	public byte asByte(Object val,
					   @Comment(name = "defaultValue", value = "Default value when conversion fails") byte defaultValue) {
		try {
			return asDecimal(val).byteValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转short
	 */
	@Comment("Converts an object to short type, returning 0 if conversion fails")
	public short asShort(Object val) {
		return asShort(val, (short) 0);
	}

	/**
	 * 转short
	 *
	 * @param defaultValue 默认值
	 */
	@Comment("Converts an object to short type")
	public short asShort(Object val,
						 @Comment(name = "defaultValue", value = "Default value when conversion fails") short defaultValue) {
		try {
			return asDecimal(val).shortValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转float
	 */
	@Comment("Converts an object to float type; defaults to 0.0f if conversion fails")
	public float asFloat(Object val) {
		return asFloat(val, 0.0f);
	}

	/**
	 * 转float
	 *
	 * @param defaultValue 默认值
	 */
	@Comment("Converts an object to float type")
	public float asFloat(Object val,
						 @Comment(name = "defaultValue", value = "Default value when conversion fails") float defaultValue) {
		try {
			return asDecimal(val).floatValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
