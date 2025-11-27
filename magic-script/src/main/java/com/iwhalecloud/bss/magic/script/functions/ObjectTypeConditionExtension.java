package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类型判断
 */
public class ObjectTypeConditionExtension {

	/**
	 * 判断是否是目标类型
	 */
	@Comment("Determines if an object is of a specified type")
	public static boolean is(Object target,
							 @Comment(name = "clazz", value = "Target type") Class<?> clazz) {
		if (target == null || clazz == null) {
			return false;
		}
		return clazz.isAssignableFrom(target.getClass());
	}

	/**
	 * 判断是否是数组
	 */
	@Comment("Determines if an object is an array")
	public static boolean isArray(Object target) {
		if (target instanceof Class) {
			return ((Class<?>) target).isArray();
		}
		return target.getClass().isArray();
	}

	/**
	 * 判断是否是集合
	 */
	@Comment("Determines if an object is a collection")
	public static boolean isCollection(Object target) {
		if (target instanceof Class) {
			return Collection.class.isAssignableFrom((Class<?>) target);
		}
		return Collection.class.isAssignableFrom(target.getClass());
	}

	/**
	 * 判断是否是Map
	 */
	@Comment("Determines if an object is a Map")
	public static boolean isMap(Object target) {
		if (target instanceof Class) {
			return Map.class.isAssignableFrom((Class<?>) target);
		}
		return Map.class.isAssignableFrom(target.getClass());
	}

	/**
	 * 判断是否是目标类型
	 */
	@Comment("Determines if an object is of a specified type; returns false if type is null; supports class name abbreviations")
	public boolean is(Object target,
					  @Comment(name = "type", value = "Class name or fully qualified class name or string, int, double, float, long, byte, short, bigdecimal, boolean") String type) {
		if (type == null) {
			return false;
		}
		Class<?> clazz;
		if (target instanceof Class) {
			clazz = (Class<?>) target;
		} else {
			clazz = target.getClass();
		}
		if (clazz.getName().equals(type) || clazz.getSimpleName().equals(type)) {
			return true;
		}
		if ("string".equalsIgnoreCase(type) && clazz == String.class) {
			return true;
		}
		if ("int".equalsIgnoreCase(type) && clazz == Integer.class) {
			return true;
		}
		if ("double".equalsIgnoreCase(type) && clazz == Double.class) {
			return true;
		}
		if ("float".equalsIgnoreCase(type) && clazz == Float.class) {
			return true;
		}
		if ("long".equalsIgnoreCase(type) && clazz == Long.class) {
			return true;
		}
		if ("byte".equalsIgnoreCase(type) && clazz == Byte.class) {
			return true;
		}
		if ("short".equalsIgnoreCase(type) && clazz == Short.class) {
			return true;
		}
		if ("boolean".equalsIgnoreCase(type) && clazz == Boolean.class) {
			return true;
		}
		if ("decimal".equalsIgnoreCase(type) && clazz == BigDecimal.class) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是String
	 */
	@Comment("Checks if the object is of type String")
	public boolean isString(Object target) {
		return is(target, String.class);
	}

	/**
	 * 判断是否是int
	 */
	@Comment("Checks if the object is of type int")
	public boolean isInt(Object target) {
		return is(target, Integer.class);
	}

	/**
	 * 判断是否是double
	 */
	@Comment("Checks if the object is of type double")
	public boolean isDouble(Object target) {
		return is(target, Double.class);
	}

	/**
	 * 判断是否是long
	 */
	@Comment("Checks if the object is of type long")
	public boolean isLong(Object target) {
		return is(target, Long.class);
	}

	/**
	 * 判断是否是byte
	 */
	@Comment("Checks if the object is of type byte")
	public boolean isByte(Object target) {
		return is(target, Byte.class);
	}

	/**
	 * 判断是否是short
	 */
	@Comment("Checks if the object is of type short")
	public boolean isShort(Object target) {
		return is(target, Short.class);
	}

	/**
	 * 判断是否是boolean
	 */
	@Comment("Checks if the object is of type boolean")
	public boolean isBoolean(Object target) {
		return is(target, Boolean.class);
	}

	/**
	 * 判断是否是BigDecimal
	 */
	@Comment("Checks if the object is of type BigDecimal")
	public boolean isDecimal(Object target) {
		return is(target, BigDecimal.class);
	}

	/**
	 * 判断是否是Date
	 */
	@Comment("Determines if the object is of type Date")
	public boolean isDate(Object target) {
		return is(target, Date.class);
	}

	/**
	 * 判断是否是List
	 */
	@Comment("Check if an object is a List")
	public boolean isList(Object target) {
		if (target instanceof Class) {
			return List.class.isAssignableFrom((Class<?>) target);
		}
		return List.class.isAssignableFrom(target.getClass());
	}
}
