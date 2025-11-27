package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.parsing.ast.binary.LessOperation;
import com.iwhalecloud.bss.magic.script.reflection.JavaReflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class MapExtension {


	@Comment("Convert a Map object to a JavaBean")
	public static Object asBean(Map<?, ?> source,
								@Comment(name = "target", value = "Target Class") Class<?> target) {
		Object result = null;
		try {
			result = target.newInstance();
			Set<? extends Map.Entry<?, ?>> entries = source.entrySet();
			for (Map.Entry<?, ?> entry : entries) {
				Object value = entry.getValue();
				String member = Objects.toString(entry.getKey(), null);
				if (value != null && member != null) {
					Field field = JavaReflection.getField(target, member);
					setFieldValue(result, field, value);
				}
			}
		} catch (InstantiationException | IllegalAccessException ignored) {
		}
		return result;
	}

	@Comment("Map to List")
	public static List<?> asList(Map<?, ?> source,
								 @Comment(name = "mapping", value = "Mapping function, such as: (key, value, source) => {'k' : key, 'v' : value}") Function<Object[], Object> mapping) {
		List<Object> result = new ArrayList<>();
		source.forEach((key, value) -> result.add(mapping.apply(new Object[]{key, value, source})));
		return result;
	}

	private static void setFieldValue(Object object, Field field, Object value) {
		if (field != null) {
			try {
				if (ObjectTypeConditionExtension.isCollection(field.getType())) {
					Type genericType = field.getGenericType();
					if (genericType instanceof ParameterizedType) {
						Class<?> type = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
						JavaReflection.setFieldValue(object, field, StreamExtension.asBean(value, type));
					}
				} else if (field.getType().isArray()) {
					JavaReflection.setFieldValue(object, field, StreamExtension.asBean(value, field.getType().getComponentType(), true));
				} else if (JavaReflection.isPrimitiveAssignableFrom(value.getClass(), field.getType()) || field.getType().isAssignableFrom(value.getClass())) {
					JavaReflection.setFieldValue(object, field, value);
				}
			} catch (Exception ignored) {
			}
		}
	}

	@Comment(value = "Looping through a Map", origin = true)
	public Map<?, ?> each(Map<?, ?> source,
						  @Comment(name = "function", value = "Loop function, such as: (key, value, source)=>map['xx'] = key;") Function<Object[], Object> function) {
		source.forEach((key, value) -> function.apply(new Object[]{key, value, source}));
		return source;
	}

	@Comment("Merge Maps")
	public Map<?, ?> merge(Map<Object, Object> source,
						   @Comment(name = "key", value = "key") Object key,
						   @Comment(name = "value", value = "value") Object value) {
		source.put(key, value);
		return source;
	}

	@Comment("Merge Maps")
	public Map<?, ?> merge(Map<Object, Object> source,
						   @Comment("Another Map or multiple Maps") Map<Object, Object>... targets) {
		if (targets != null) {
			for (int i = 0, len = targets.length; i < len; i++) {
				source.putAll(targets[i]);
			}
		}
		return source;
	}

	@Comment("Convert Map to String")
	public String asString(Map<?, ?> source,
						   @Comment(name = "separator", value = "Concatenation operator between keys, such as &") String separator,
						   @Comment(name = "join", value = "Concatenation operator between keys and values, such as =") String join) {
		Set<? extends Map.Entry<?, ?>> entries = source.entrySet();
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<?, ?> entry : entries) {
			builder.append(entry.getKey());
			builder.append(join);
			builder.append(Objects.toString(entry.getValue(), ""));
			builder.append(separator);
		}
		if (entries.size() > 0) {
			return builder.substring(0, builder.length() - separator.length());
		}
		return builder.toString();
	}

	@Comment("Convert Map to String")
	public String asString(Map<?, ?> source,
						   @Comment(name = "separator", value = "Concatenation operator between keys and values, such as =") String separator,
						   @Comment(name = "mapping", value = "Conversion method, such as: (key, value)=>key + '=' + value || ''") Function<Object[], Object> mapping) {
		Set<? extends Map.Entry<?, ?>> entries = source.entrySet();
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<?, ?> entry : entries) {
			builder.append(Objects.toString(mapping.apply(new Object[]{entry.getKey(), entry.getValue()}), ""));
			builder.append(separator);
		}
		if (entries.size() > 0) {
			return builder.substring(0, builder.length() - separator.length());
		}
		return builder.toString();
	}

	@Comment("Sorting Maps")
	public Map<?, ?> sort(Map<?, ?> source) {
		Set<?> keys = source.keySet();
		Map<Object, Object> sortedMap = new LinkedHashMap<>();
		keys.stream().sorted((Comparator<Object>) (o1, o2) -> {
			if (Objects.equals(o1, o2)) {
				return 0;
			}
			int value = LessOperation.compare(o1, o2);
			return value == -2 ? 0 : value;
		}).forEach(key -> {
			sortedMap.put(key, source.get(key));
		});
		return sortedMap;
	}

	@Comment("Sorting Maps")
	public Map<?, ?> sort(Map<?, ?> source,
						  @Comment(name = "comparator", value = "Comparator, e.g., (k1,k2,v1,v2)=>k1.compareTo(k2);") Function<Object[], Object> comparator) {
		Set<?> keys = source.keySet();
		Map<Object, Object> sortedMap = new LinkedHashMap<>();
		keys.stream().sorted((Comparator<Object>) (o1, o2) -> ObjectConvertExtension.asInt(comparator.apply(new Object[]{o1, o2, source.get(o1), source.get(o2)}), 0)).forEach(key -> {
			sortedMap.put(key, source.get(key));
		});
		return sortedMap;
	}

	@Comment("Replace keys in a Map")
	public Map<String, Object> replaceKey(Map<String, Object> source,
										  @Comment(name = "target", value = "Search for strings") String target,
										  @Comment(name = "replacement", value = "Replace strings") String replacement) {
		return replaceKey(source, src -> src[0].toString().replace(target, replacement));
	}

	@Comment("Perform regular expression replacement on keys in a Map")
	public Map<String, Object> replaceAllKey(Map<String, Object> source,
											 @Comment(name = "pattern", value = "Regular expression") String regx,
											 @Comment(name = "replacement", value = "Replace strings") String replacement) {
		return replaceKey(source, src -> src[0].toString().replaceAll(regx, replacement));
	}

	@Comment("Replace keys in a Map")
	public Map<String, Object> replaceKey(Map<String, Object> source,
										  @Comment(name = "function", value = "Callback function") Function<Object[], String> functional) {
		Map<String, Object> result = new LinkedHashMap<>();
		Set<Map.Entry<String, Object>> entries = source.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			result.put(functional.apply(new Object[]{entry.getKey()}), entry.getValue());
		}
		return result;
	}
}
