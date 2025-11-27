package com.iwhalecloud.bss.magic.script.parsing.ast.statement;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.exception.MagicScriptException;
import com.iwhalecloud.bss.magic.script.functions.ObjectConvertExtension;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.Expression;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClassConverter extends Expression {

	private final static Map<String, BiFunction<Object, Object[], Object>> CONVERTERS = new HashMap<>();

	static {
		register("int", BigDecimal::intValue);
		register("double", BigDecimal::doubleValue);
		register("long", BigDecimal::longValue);
		register("byte", BigDecimal::byteValue);
		register("float", BigDecimal::floatValue);
		register("short", BigDecimal::shortValue);
		register("string", (target, params) -> process(target::toString, params));
		register("date", (target, params) -> {
			if (params.length == 0) {
				throw new IllegalArgumentException("::date requires date format, such as ::date('yyyy-mm-dd')");
			}
			try {
				return ObjectConvertExtension.asDate(target, params[0].toString());
			} catch (Exception e) {
				return null;
			}
		});
	}

	private final Expression target;
	private final String convert;
	private final List<Expression> arguments;


	public ClassConverter(Span span, String convert, Expression target, List<Expression> arguments) {
		super(span);
		this.convert = convert;
		this.target = target;
		this.arguments = arguments;
	}

	private static void register(String target, Function<BigDecimal, Object> converter) {
		register(target, (value, params) -> {
			try {
				BigDecimal decimal;
				if (value instanceof BigDecimal) {
					decimal =  (BigDecimal) value;
				}else{
					decimal = new BigDecimal(value.toString().trim());
				}
				return converter.apply(decimal);
			} catch (Exception e) {
				return params != null && params.length > 0 ? params[0] : null;
			}
		});
	}

	private static Object process(Supplier<Object> callback, Object[] params) {
		try {
			return callback.get();
		} catch (Exception e) {
			return params != null && params.length > 0 ? params[0] : null;
		}
	}

	public static Object process(Object object, String target, Object[] params) {
		try {
			BiFunction<Object, Object[], Object> function = CONVERTERS.get(target);
			if (function == null) {
				throw new MagicScriptException(String.format("Transformer [%s] not found", target));
			}
			return function.apply(object, params);
		} catch (Exception e) {
			return params != null && params.length > 0 ? params[0] : null;
		}
	}

	public static void register(String target, BiFunction<Object, Object[], Object> converter) {
		CONVERTERS.put(target, converter);
	}

	@Override
	public void visitMethod(MagicScriptCompiler compiler) {
		target.visitMethod(compiler);
		arguments.forEach(it -> it.visitMethod(compiler));
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.visit(target)
				.ldc(convert)
				.visit(arguments)
				.call("type_cast", arguments.size() + 2);
	}
}
