package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.exception.MagicScriptException;
import com.iwhalecloud.bss.magic.script.parsing.ast.BinaryOperation;
import com.iwhalecloud.bss.magic.script.parsing.ast.literal.BooleanLiteral;
import com.iwhalecloud.bss.magic.script.reflection.JavaReflection;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamExtension {

	/**
	 * 将对象转为List
	 */
	@Comment("Convert an object to a List")
	public static List<Object> arrayLikeToList(Object arrayLike) {
		if (arrayLike == null) {
			// 不返回EmptyList
			return new ArrayList<>();
		}
		if (arrayLike instanceof Collection) {
			return new ArrayList<>((Collection<?>) arrayLike);
		} else if (arrayLike.getClass().isArray()) {
			List<Object> list = new ArrayList<>(Array.getLength(arrayLike));
			IntStream.range(0, Array.getLength(arrayLike)).forEach(i -> list.add(Array.get(arrayLike, i)));
			return list;
		} else if (arrayLike instanceof Iterator) {
			List<Object> list = new ArrayList<>();
			Iterator<Object> it = (Iterator<Object>) arrayLike;
			it.forEachRemaining(list::add);
			return list;
		} else if (arrayLike instanceof Enumeration) {
			Enumeration<Object> en = (Enumeration<Object>) arrayLike;
			return Collections.list(en);
		}
		throw new MagicScriptException("Unsupported types:" + arrayLike.getClass());
	}

	/**
	 * 将list拼接起来
	 */
	@Comment("Concatenate collections using concatenation operators")
	public static String join(Object target,
							  @Comment(name = "separator", value = "Concatenation operators, such as `,`") String separator) {
		List<Object> objects = arrayLikeToList(target);
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = objects.size(); i < len; i++) {
			sb.append(objects.get(i));
			if (i + 1 < len) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	@Comment("Convert a collection to a JavaBean")
	public static Object asBean(Object source,
								@Comment(name = "target", value = "Target type") Class<?> target) {
		return asBean(source, target, false);
	}

	@Comment("Convert a collection to a JavaBean")
	public static Object asBean(Object source,
								@Comment(name = "target", value = "Target type") Class<?> target,
								@Comment(name = "isArray", value = "Check if it is an array") boolean isArray) {
		Class<?> sourceClass = source.getClass();
		List<Object> objects = arrayLikeToList(source);
		int size = objects.size();
		boolean isCollection = false;
		boolean isMap = false;
		boolean isOrigin = false;
		boolean targetIsArray = false;
		Class<?> innerClass = null;
		if (size > 0) {
			Object first = objects.get(0);
			innerClass = first.getClass();
			isCollection = ObjectTypeConditionExtension.isCollection(innerClass);
			isMap = ObjectTypeConditionExtension.isMap(innerClass);
			isOrigin = ObjectTypeConditionExtension.is(first, target);
			isOrigin = isOrigin || JavaReflection.isPrimitiveAssignableFrom(first.getClass(), target);
			targetIsArray = ObjectTypeConditionExtension.isArray(target);
		}
		if (isArray) {
			Object result = Array.newInstance(target, objects.size());
			for (int i = 0; i < size; i++) {
				Object value = objects.get(i);
				if (innerClass.isArray()) {
					Array.set(result, i, asBean(value, innerClass.getComponentType(), true));
				} else if (targetIsArray) {
					Array.set(result, i, asBean(value, target.getComponentType(), true));
				} else if (isCollection) {
					Array.set(result, i, asBean(value, innerClass, false));
				} else if (isMap) {
					Array.set(result, i, MapExtension.asBean((Map<String, Object>) value, innerClass));
				} else if (isOrigin) {
					Array.set(result, i, value);
				}
			}
			return result;
		}
		Collection<Object> collection;
		if (List.class.isAssignableFrom(sourceClass)) {
			collection = new ArrayList<>(size);
		} else if (Set.class.isAssignableFrom(sourceClass)) {
			collection = new HashSet<>(size);
		} else {
			throw new MagicScriptException("Unsupported types:" + sourceClass);
		}
		for (Object value : objects) {
			if (isCollection) {
				collection.add(asBean(value, target));
			} else if (isMap) {
				collection.add(MapExtension.asBean((Map<?, ?>) value, target));
			} else if (isOrigin) {
				collection.add(value);
			} else {
				break;
			}
		}
		return collection;
	}

	private Object toOriginType(Object target, Collection<Object> results) {
		if (target instanceof Collection) {
			return results;
		} else if (target.getClass().isArray()) {
			return results.toArray();
		} else if (target instanceof Iterator) {
			return results;
		} else if (target instanceof Enumeration) {
			return results;
		}
		return null;
	}

	@Comment(value = "Add elements to a collection", origin = true)
	public Object push(Object target,
					   @Comment(name = "item", value = "Elements to add") Object item) {
		if (target instanceof Collection) {
			if (item instanceof Collection) {
				((Collection) target).addAll((Collection) item);
			} else {
				((Collection) target).add(item);
			}
		} else {
			throw new MagicScriptException("Types not supported by the push method:" + target.getClass());
		}
		return target;
	}

	/**
	 * map 函数
	 *
	 * @param function 回调函数
	 */
	@Comment(value = "Transforms the collection and returns a new collection", origin = true)
	public Object map(Object target,
					  @Comment(name = "function", value = "Transformation function, such as extracting attributes `(item)=>item.xxx`") Function<Object[], Object> function) {
		List<Object> objects = arrayLikeToList(target);
		List<Object> results = new ArrayList<>(objects.size());
		for (int i = 0, len = objects.size(); i < len; i++) {
			Object object = objects.get(i);
			results.add(function.apply(new Object[]{object, i, len}));
		}
		return toOriginType(target, results);
	}

	/**
	 * 对List进行过滤
	 *
	 * @param function 回调函数
	 */
	@Comment(value = "Filters the collection and returns a new collection", origin = true)
	public Object filter(Object target,
						 @Comment(name = "function", value = "Filtering condition, such as `(item)=>item.xxx == 1`") Function<Object[], Object> function) {
		List<Object> objects = arrayLikeToList(target);
		List<Object> results = new ArrayList<>(objects.size());
		for (int i = 0, len = objects.size(); i < len; i++) {
			Object object = objects.get(i);
			if (BooleanLiteral.isTrue(function.apply(new Object[]{object, i, len}))) {
				results.add(object);
			}
		}
		return toOriginType(target, results);
	}

	@Comment("Finds an element")
	public Object find(Object target,
					   @Comment(name = "function", value = "Matching condition, such as `(item)=>item.xxx == 1`") Function<Object[], Object> function) {
		List<Object> objects = arrayLikeToList(target);
		for (int i = 0, len = objects.size(); i < len; i++) {
			Object object = objects.get(i);
			if (BooleanLiteral.isTrue(function.apply(new Object[]{object, i, len}))) {
				return object;
			}
		}
		return null;
	}

	@Comment("Finds the index of the element")
	public int findIndex(Object target,
						 @Comment(name = "function", value = "Matching condition, such as `(item)=>item.xxx == 1`") Function<Object[], Object> function) {
		List<Object> objects = arrayLikeToList(target);
		for (int i = 0, len = objects.size(); i < len; i++) {
			Object object = objects.get(i);
			if (BooleanLiteral.isTrue(function.apply(new Object[]{object, i, len}))) {
				return i;
			}
		}
		return -1;
	}

	@Comment(value = "Merge multiple collections and return a new collection", origin = true)
	public Object concat(Object src, @Comment(name = "target", value = "Set") Object... target) {
		List<Object> results = new ArrayList<>(arrayLikeToList(src));
		if (target != null) {
			for (Object o : target) {
				results.addAll(arrayLikeToList(o));
			}
		}
		return toOriginType(target, results);
	}

	@Comment("Convert a collection to a Map")
	public Map<Object, Object> toMap(Object src,
									 @Comment(name = "mappingKey", value = "Key value") Function<Object[], Object> mappingKey,
									 @Comment(name = "mappingValue", value = "Value value") Function<Object[], Object> mappingValue) {
		List<Object> target = new ArrayList<>(arrayLikeToList(src));
		int size = target.size();
		Map<Object, Object> map = new LinkedHashMap<>(size);
		for (int i = 0; i < size; i++) {
			Object item = target.get(i);
			Object[] parameters = new Object[]{item, i, size};
			map.put(mappingKey.apply(parameters), mappingValue.apply(parameters));
		}
		return map;
	}

	@Comment("Convert a collection to a Map")
	public Map<Object, Object> toMap(Object src,
									 @Comment(name = "mappingKey", value = "Key value") Function<Object[], Object> mappingKey) {
		return toMap(src, mappingKey, objects -> objects[0]);
	}

	/**
	 * 循环List
	 *
	 * @param function 回调函数
	 */
	@Comment(value = "Iterate through collections and return a new collection", origin = true)
	public Object each(Object target,
					   @Comment(name = "function", value = "Loop function, such as iterating through adding attributes `(item)=>{item.xxx = 'newVal'}`") Function<Object[], Object> function) {
		List<Object> objects = arrayLikeToList(target);
		List<Object> results = new ArrayList<>(objects.size());
		for (int i = 0, len = objects.size(); i < len; i++) {
			Object object = objects.get(i);
			function.apply(new Object[]{object, i, len});
			results.add(object);
		}
		return toOriginType(target, results);
	}

	/**
	 * 排序
	 */
	@Comment(value = "Sort collections and return a new collection", origin = true)
	public Object sort(Object target,
					   @Comment(name = "function", value = "Sorting function, such as sorting from largest to smallest `(a,b)=>a-b`") Function<Object[], Object> function) {
		List<Object> objects = arrayLikeToList(target);
		objects.sort((o1, o2) -> ObjectConvertExtension.asInt(function.apply(new Object[]{o1, o2}), 0));
		return toOriginType(target, objects);
	}

	/**
	 * 反转
	 */
	@Comment(value = "Reverse the set", origin = true)
	public Object reserve(Object target) {
		List<Object> objects = arrayLikeToList(target);
		Collections.reverse(objects);
		return toOriginType(target, objects);
	}

	/**
	 * 将list打乱
	 */
	@Comment(value = "Shuffle the order of the set", origin = true)
	public Object shuffle(Object target) {
		List<Object> objects = arrayLikeToList(target);
		Collections.shuffle(objects);
		return toOriginType(target, objects);
	}

	/**
	 * 数组去重
	 *
	 * @since 1.4.6
	 */
	@Comment(value = "Deduplicate the set", origin = true)
	public Object distinct(Object target) {
		List<Object> list = arrayLikeToList(target);
		List<Object> result = new ArrayList<>(list.size());
		Set<Object> sets = new LinkedHashSet<>(list.size());
		for (Object e : list) {
			if (!sets.contains(e)) {
				sets.add(e);
				result.add(e);
			}
		}
		return toOriginType(target, result);
	}

	/**
	 * 数组去重
	 *
	 * @since 1.6.2
	 */
	@Comment(value = "Deduplicate the set", origin = true)
	public Object distinct(Object target,
						   @Comment(name = "condition", value = "Deduplication converter, such as deduplicating by ID `item=>item.id`") Function<Object[], Object> condition) {
		List<Object> list = arrayLikeToList(target);
		List<Object> result = new ArrayList<>(list.size());
		Set<Object> sets = new LinkedHashSet<>(list.size());
		for (Object e : list) {
			Object res = condition.apply(new Object[]{e});
			if (!sets.contains(res)) {
				sets.add(res);
				result.add(e);
			}
		}
		return toOriginType(target, result);
	}

	/**
	 * 将list拼接起来
	 */
	@Comment("Concatenate the sets using commas")
	public String join(Object target) {
		return join(target, ",");
	}

	/**
	 * 取最大值
	 */
	@Comment("Retrieve the maximum value of the set, returning `null` if not found")
	public Object max(Object target) {
		return arrayLikeToList(target).stream()
				.filter(Objects::nonNull)
				.max(BinaryOperation::compare)
				.orElse(null);
	}

	/**
	 * 取最小值
	 */
	@Comment("Retrieve the minimum value of the set, returning `null` if not found")
	public Object min(Object target) {
		return arrayLikeToList(target).stream()
				.filter(Objects::nonNull)
				.min(BinaryOperation::compare)
				.orElse(null);
	}

	/**
	 * 取平均值
	 */
	@Comment("Retrieve the average value of the set, returning `null` if it cannot be calculated")
	public Double avg(Object target) {
		OptionalDouble average = arrayLikeToList(target).stream()
				.filter(v -> v instanceof Number)
				.mapToDouble(value -> ((Number) value).doubleValue())
				.average();
		return average.isPresent() ? average.getAsDouble() : null;
	}

	/**
	 * 累计求和
	 */
	@Comment("Accumulate the values in the set")
	public Number sum(Object target) {
		return arrayLikeToList(target).stream()
				.filter(value -> value instanceof Number)
				.mapToDouble(value -> ((Number) value).doubleValue())
				.sum();
	}

	/**
	 * 分组
	 *
	 * @param condition 分组条件
	 */
	@Comment("Grouping Collections")
	public Map<Object, List<Object>> group(Object target,
										   @Comment(name = "condition", value = "Grouping condition, such as `item=>item.xxx + '_' + item.yyy`") Function<Object[], Object> condition) {
		return arrayLikeToList(target).stream()
				.collect(Collectors.groupingBy(item -> condition.apply(Stream.of(item).toArray()), LinkedHashMap::new, Collectors.toList()));
	}

	/**
	 * 分组
	 *
	 * @param condition 分组条件
	 * @param mapping   结果映射
	 */
	@Comment("Grouping and Transforming Collections")
	public Map<Object, Object> group(Object target,
									 @Comment(name = "condition", value = "Grouping condition, such as `item=>item.xxx + '_' + item.yyy`") Function<Object[], Object> condition,
									 @Comment(name = "mapping", value = "Transformation function, such as grouped summation `(list)=>list.sum()`") Function<Object[], Object> mapping) {
		return arrayLikeToList(target).stream()
				.collect(Collectors.groupingBy(item -> condition.apply(Stream.of(item).toArray()),
						LinkedHashMap::new,
						Collectors.collectingAndThen(Collectors.toList(), list -> mapping.apply(Stream.of(list).toArray()))
						)
				);
	}

	/**
	 * 合并两个集合，类似sql join 操作
	 *
	 * @param source    左表
	 * @param target    右表
	 * @param condition 条件
	 */
	@Comment("Associating Two Collections")
	public List<Object> join(Object source,
							 @Comment(name = "target", value = "Another collection") Object target,
							 @Comment(name = "condition", value = "Association condition, such as: `(left,right)=>left.xxx = right.xxx`") Function<Object[], Object> condition) {
		return join(source, target, condition, (args) -> {
			Object left = args[0];
			Object right = args[1];
			Map<Object, Object> map = new LinkedHashMap<>();
			if (left instanceof Map) {
				map.putAll((Map) left);
			}
			if (right instanceof Map) {
				map.putAll((Map) right);
			}
			return map;
		});
	}

	/**
	 * 合并两个集合，类似 sql join 操作
	 *
	 * @param source    左表
	 * @param target    右表
	 * @param condition 条件
	 * @param mapping   映射
	 */
	@Comment("Associating and Transforming Two Collections")
	public List<Object> join(Object source,
							 @Comment(name = "target", value = "Another collection") Object target,
							 @Comment(name = "condition", value = "Association conditions, such as: `(left,right)=>left.xxx == right.xxx`") Function<Object[], Object> condition,
							 @Comment(name = "mapping", value = "Mapping functions, such as: `(left,right)=>{xxx : left.xxx, yyy : right.yyy}`") Function<Object[], Object> mapping) {
		if (target == null) {
			return null;
		}
		List<Object> targetList = arrayLikeToList(target);
		return arrayLikeToList(source).stream()
				// 将匹配结果进行映射
				.map(left -> mapping.apply(
						Stream.of(left,
								targetList.stream()
										// 匹配条件
										.filter(right -> Objects.equals(true, condition.apply(Stream.of(left, right).toArray())))
										// 只取第一条
										.findFirst()
										.orElse(null)
						).toArray()
				)).collect(Collectors.toList());
	}

	@Comment(value = "Slice the set", origin = true)
	public Object skip(Object source,
					   @Comment(name = "value", value = "Number of elements to skip") int value) {
		return toOriginType(source, arrayLikeToList(source).stream().skip(value).collect(Collectors.toList()));
	}

	@Comment(value = "Limit the number of elements in the set", origin = true)
	public Object limit(Object source,
						@Comment(name = "value", value = "Number of elements to skip") int value) {
		return toOriginType(source, arrayLikeToList(source).stream().limit(value).collect(Collectors.toList()));
	}

	@Comment("Check if all elements in the set satisfy the condition")
	public boolean every(Object source,
						 @Comment(name = "condition", value = "Conditions to check") Function<Object[], Object> condition) {
		List<Object> objects = arrayLikeToList(source);
		for (int i = 0, size = objects.size(); i < size; i++) {
			if (!BooleanLiteral.isTrue(condition.apply(new Object[]{objects.get(i), i}))) {
				return false;
			}
		}
		return true;
	}

	@Comment("Check if at least one element in the set satisfies the condition")
	public boolean some(Object source,
						@Comment(name = "condition", value = "Conditions to check") Function<Object[], Object> condition) {
		List<Object> objects = arrayLikeToList(source);
		for (int i = 0, size = objects.size(); i < size; i++) {
			if (BooleanLiteral.isTrue(condition.apply(new Object[]{objects.get(i), i}))) {
				return true;
			}
		}
		return false;
	}

	@Comment("Find the first non-null element in the set")
	public Object findNotNull(Object source) {
		List<Object> objects = arrayLikeToList(source);
		for (Object object : objects) {
			if (object != null) {
				return object;
			}
		}
		return null;
	}

	@Comment("Returns a new value from a collection using a given computation function")
	public Object reduce(Object source,
						 @Comment(name = "reduceFunction", value = "Processing function, such as accumulation calculation: `(val,item)=>val + item`") Function<Object[], Object> reduceFunction) {
		List<Object> objects = arrayLikeToList(source);
		if (objects.isEmpty()) {
			return null;
		}
		int size = objects.size();
		if (size == 1) {
			return objects.get(0);
		}
		Object result = objects.get(0);
		for (int i = 1; i < size; i++) {
			result = reduceFunction.apply(new Object[]{result, objects.get(i)});
		}
		return result;
	}

	@Comment("Returns the first element of a collection")
	public static Object first(Object source) {
		List<Object> objects = arrayLikeToList(source);
		return objects.size() > 0 ? objects.get(0) : null;
	}

	@Comment("Returns the last element of a collection")
	public Object last(Object source) {
		List<Object> objects = arrayLikeToList(source);
		return objects.size() > 0 ? objects.get(objects.size() - 1) : null;
	}

	@Comment("Returns the length of a collection or array")
	public int size(Object arrayLike) {
		if (arrayLike instanceof Collection) {
			return ((Collection<?>) arrayLike).size();
		} else if (arrayLike.getClass().isArray()) {
			return Array.getLength(arrayLike);
		} else if (arrayLike instanceof Iterator) {
			List<Object> list = new ArrayList<>();
			Iterator<Object> it = (Iterator<Object>) arrayLike;
			it.forEachRemaining(list::add);
			return list.size();
		} else if (arrayLike instanceof Enumeration) {
			Enumeration<Object> en = (Enumeration<Object>) arrayLike;
			return Collections.list(en).size();
		}
		throw new MagicScriptException("Unsupported types:" + arrayLike.getClass());
	}

	@Comment("Returns the length of a collection or array")
	public int getLength(Object source) {
		return size(source);
	}
}
