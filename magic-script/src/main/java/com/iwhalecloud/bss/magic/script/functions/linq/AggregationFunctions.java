package com.iwhalecloud.bss.magic.script.functions.linq;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.annotation.Function;
import com.iwhalecloud.bss.magic.script.functions.ObjectConvertExtension;
import com.iwhalecloud.bss.magic.script.functions.StreamExtension;
import com.iwhalecloud.bss.magic.script.parsing.ast.BinaryOperation;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.BinaryOperator;

/**
 * 聚合函数
 */
public class AggregationFunctions {

	@Function
	@Comment("Aggregate function - count")
	public int count(@Comment(name = "value", value = "Set") Object target) {
		if (target == null) {
			return 0;
		} else if (target instanceof Map) {
			return 1;
		}
		try {
			return StreamExtension.arrayLikeToList(target).size();
		} catch (Exception e) {
			return 1;
		}
	}

	@Function
	@Comment("Aggregate function - max")
	public Object max(@Comment(name = "value", value = "Set") Object target) {
		if (target == null) {
			return null;
		} else if (target instanceof Map) {
			return target;
		}
		try {
			return StreamExtension.arrayLikeToList(target).stream()
					.reduce(BinaryOperator.maxBy(BinaryOperation::compare))
					.orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	@Function
	@Comment("Aggregate function - SUM")
	public Number sum(@Comment(name = "value", value = "Set") Object target) {
		if (target == null) {
			return null;
		} else if (target instanceof Map) {
			return null;
		}
		try {
			OptionalDouble value = StreamExtension.arrayLikeToList(target).stream()
					.mapToDouble(v -> ObjectConvertExtension.asDouble(v, Double.NaN))
					.filter(v -> !Double.isNaN(v))
					.reduce(Double::sum);
			return value.isPresent() ? value.getAsDouble() : null;
		} catch (Exception e) {
			return null;
		}
	}

	@Function
	@Comment("Aggregate function - MIN")
	public Object min(@Comment(name = "value", value = "Set") Object target) {
		if (target == null) {
			return null;
		} else if (target instanceof Map) {
			return target;
		}
		try {
			return StreamExtension.arrayLikeToList(target).stream()
					.reduce(BinaryOperator.minBy(BinaryOperation::compare))
					.orElse(null);
		} catch (Exception e) {
			return null;
		}
	}

	@Function
	@Comment("Aggregate function - AVG")
	public Object avg(@Comment(name = "value", value = "Set") Object target) {
		if (target == null) {
			return null;
		} else if (target instanceof Map) {
			return target;
		}
		try {
			OptionalDouble average = StreamExtension.arrayLikeToList(target).stream()
					.mapToDouble(v -> ObjectConvertExtension.asDouble(v, Double.NaN))
					.filter(v -> !Double.isNaN(v))
					.average();
			return average.isPresent() ? average.getAsDouble() : null;
		} catch (Exception e) {
			return null;
		}
	}

	@Function
	@Comment("Grouping and concatenating by a specified string")
	public String group_concat(@Comment(name = "target", value = "Column, e.g., t.a") Object target,
							   @Comment(name = "separator", value = "Delimiter, e.g., `|`") String separator) {
		if (target == null) {
			return null;
		}
		return StreamExtension.join(target, separator);
	}

	@Function
	@Comment("Grouping and concatenating using `,`")
	public String group_concat(@Comment(name = "target", value = "Column, e.g., t.a") Object target) {
		return group_concat(target, ",");
	}
}
