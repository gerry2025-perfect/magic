package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.reflection.JavaInvoker;
import com.iwhalecloud.bss.magic.script.reflection.JavaReflection;
import com.iwhalecloud.bss.magic.script.runtime.RuntimeContext;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class ClassExtension {

	public static Object newInstance(Class<?> clazz) throws IllegalAccessException, InstantiationException {
		return clazz.newInstance();
	}

	public static Object newInstance(Class<?> clazz, RuntimeContext runtimeContext,
									 @Comment(name = "values", value = "Constructor parameters") Object... values) throws Throwable {
		if (values == null || values.length == 0) {
			return newInstance(clazz);
		}
		Class<?>[] parametersTypes = new Class<?>[values.length];
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			parametersTypes[i] = value == null ? JavaReflection.Null.class : value.getClass();
		}
		List<Constructor<?>> constructors = Arrays.asList(clazz.getConstructors());
		JavaInvoker<Constructor> invoker = JavaReflection.findConstructorInvoker(constructors, parametersTypes);
		if (invoker == null) {
			throw new RuntimeException(String.format("can not found constructor for [%s] with types: [%s]", clazz, Arrays.toString(parametersTypes)));
		}
		return invoker.invoke0(null, runtimeContext, values);
	}

	public static Object newInstance(Object target, RuntimeContext runtimeContext,
									 @Comment(name = "values", value = "Constructor parameters") Object... values) throws Throwable {
		if (target == null) {
			throw new NullPointerException("NULL cannot be created with new");
		}
		if (target instanceof Class) {
			return newInstance((Class<?>) target, runtimeContext, values);
		}
		return newInstance(target.getClass(), runtimeContext, values);
	}

	/**
	 * @since 1.6.2
	 */
	@Comment("Get the fully qualified Java class name")
	public static String getName(Class<?> clazz) {
		return clazz.getName();
	}
	/**
	 * @since 1.6.2
	 */
	@Comment("Get Java class name")
	public static String getSimpleName(Class<?> clazz) {
		return clazz.getSimpleName();
	}
	/**
	 * @since 1.6.2
	 */
	@Comment("Get Java class fully qualified name")
	public static String getCanonicalName(Class<?> clazz) {
		return clazz.getCanonicalName();
	}

}
