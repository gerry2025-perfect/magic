package com.iwhalecloud.bss.magic.magicapi.modules.db.table;

import org.apache.commons.lang3.StringUtils;
import com.iwhalecloud.bss.magic.magicapi.core.context.RequestContext;
import com.iwhalecloud.bss.magic.magicapi.core.exception.MagicAPIException;
import com.iwhalecloud.bss.magic.magicapi.core.model.Attributes;
import com.iwhalecloud.bss.magic.magicapi.core.context.RequestEntity;
import com.iwhalecloud.bss.magic.magicapi.modules.db.BoundSql;
import com.iwhalecloud.bss.magic.magicapi.modules.db.inteceptor.NamedTableInterceptor;
import com.iwhalecloud.bss.magic.magicapi.modules.db.SQLModule;
import com.iwhalecloud.bss.magic.magicapi.modules.db.model.Page;
import com.iwhalecloud.bss.magic.magicapi.modules.db.model.SqlMode;
import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.runtime.RuntimeContext;

import java.beans.Transient;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 单表操作API
 *
 * @author mxd
 */
public class NamedTable extends Attributes<Object> {

	String tableName;

	SQLModule sqlModule;

	String primary;

	String logicDeleteColumn;

	Object logicDeleteValue;

	Map<String, Object> columns = new HashMap<>();

	List<String> fields = new ArrayList<>();

	List<String> groups = new ArrayList<>();

	List<String> orders = new ArrayList<>();

	Set<String> excludeColumns = new HashSet<>();

	Function<String, String> rowMapColumnMapper;

	Object defaultPrimaryValue;

	boolean useLogic = false;

	boolean withBlank = false;

	List<NamedTableInterceptor> namedTableInterceptors;

	Where where = new Where(this);

	public NamedTable(String tableName, SQLModule sqlModule, Function<String, String> rowMapColumnMapper, List<NamedTableInterceptor> namedTableInterceptors) {
		this.tableName = tableName;
		this.sqlModule = sqlModule;
		this.rowMapColumnMapper = rowMapColumnMapper;
		this.namedTableInterceptors = namedTableInterceptors;
		this.logicDeleteColumn = sqlModule.getLogicDeleteColumn();
		String deleteValue = sqlModule.getLogicDeleteValue();
		this.logicDeleteValue = deleteValue;
		if (deleteValue != null) {
			boolean isString = deleteValue.startsWith("'") || deleteValue.startsWith("\"");
			if (isString && deleteValue.length() > 2) {
				this.logicDeleteValue = deleteValue.substring(1, deleteValue.length() - 1);
			} else {
				try {
					this.logicDeleteValue = Integer.parseInt(deleteValue);
				} catch (NumberFormatException e) {
					this.logicDeleteValue = deleteValue;
				}
			}
		}
	}

	private NamedTable() {
	}

	@Override
	@Comment("Clone")
	public NamedTable clone() {
		NamedTable namedTable = new NamedTable();
		namedTable.tableName = this.tableName;
		namedTable.sqlModule = this.sqlModule;
		namedTable.primary = this.primary;
		namedTable.logicDeleteValue = this.logicDeleteValue;
		namedTable.logicDeleteColumn = this.logicDeleteColumn;
		namedTable.columns = new HashMap<>(this.columns);
		namedTable.fields = new ArrayList<>(fields);
		namedTable.groups = new ArrayList<>(groups);
		namedTable.orders = new ArrayList<>(orders);
		namedTable.excludeColumns = new HashSet<>(excludeColumns);
		namedTable.rowMapColumnMapper = this.rowMapColumnMapper;
		namedTable.defaultPrimaryValue = this.defaultPrimaryValue;
		namedTable.useLogic = this.useLogic;
		namedTable.withBlank = this.withBlank;
		namedTable.where = this.where == null ? null : this.where.clone();
		namedTable.namedTableInterceptors = this.namedTableInterceptors;
		namedTable.properties = this.properties;
		return namedTable;
	}

	@Comment("Logical deletion")
	public NamedTable logic() {
		this.useLogic = true;
		return this;
	}

	@Comment("Update null values")
	public NamedTable withBlank() {
		this.withBlank = true;
		return this;
	}

	@Comment("Set primary key name, used in update")
	public NamedTable primary(@Comment(name = "primary", value = "Primary key column") String primary) {
		this.primary = rowMapColumnMapper.apply(primary);
		return this;
	}

	@Comment("Set primary key name and default primary key value (mainly used for insert)")
	public NamedTable primary(@Comment(name = "primary", value = "Primary key column") String primary,
							  @Comment(name = "defaultPrimaryValue", value = "Default value") Serializable defaultPrimaryValue) {
		this.primary = rowMapColumnMapper.apply(primary);
		this.defaultPrimaryValue = defaultPrimaryValue;
		return this;
	}

	@Comment("Set primary key name and default primary key value (mainly used for insert)")
	public NamedTable primary(@Comment(name = "primary", value = "Primary key column") String primary,
							  @Comment(name = "defaultPrimaryValue", value = "Default value") Supplier<Object> defaultPrimaryValue) {
		this.primary = rowMapColumnMapper.apply(primary);
		this.defaultPrimaryValue = defaultPrimaryValue;
		return this;
	}

	@Comment("Concatenate WHERE clause")
	public Where where() {
		return where;
	}

	@Comment("Set value of a single column")
	public NamedTable column(@Comment(name = "property", value = "Column name") String property,
							 @Comment(name = "value", value = "Value") Object value) {
		this.columns.put(rowMapColumnMapper.apply(property), value);
		return this;
	}

	@Comment("Set columns for query, such as `columns('a','b','c')`")
	public NamedTable columns(@Comment(name = "properties", value = "Columns") String... properties) {
		if (properties != null) {
			for (String property : properties) {
				column(property);
			}
		}
		return this;
	}

	@Comment("Set the columns to exclude")
	public NamedTable exclude(@Comment(name = "property", value = "Excluded columns") String property) {
		if (property != null) {
			excludeColumns.add(property);
		}
		return this;
	}

	@Comment("Set the columns to exclude")
	public NamedTable excludes(@Comment(name = "properties", value = "Excluded columns") String... properties) {
		if (columns != null) {
			excludeColumns.addAll(Arrays.asList(properties));
		}
		return this;
	}

	@Comment("Set the columns to exclude")
	public NamedTable excludes(@Comment(name = "properties", value = "Excluded columns") List<String> properties) {
		if (columns != null) {
			excludeColumns.addAll(properties);
		}
		return this;
	}

	@Comment("Set the columns to query, such as `columns(['a','b','c'])`")
	public NamedTable columns(@Comment(name = "properties", value = "Query columns") Collection<String> properties) {
		if (properties != null) {
			properties.stream().filter(StringUtils::isNotBlank).map(rowMapColumnMapper).forEach(this.fields::add);
		}
		return this;
	}

	@Comment("Set the query columns, such as `column('a')`")
	public NamedTable column(@Comment(name = "property", value = "Query columns") String property) {
		if (StringUtils.isNotBlank(property)) {
			this.fields.add(this.rowMapColumnMapper.apply(property));
		}
		return this;
	}

	@Comment("Concatenate `order by xxx asc/desc`")
	public NamedTable orderBy(@Comment(name = "property", value = "Columns to sort") String property,
							  @Comment(name = "sort", value = "`asc` or `desc`") String sort) {
		this.orders.add(rowMapColumnMapper.apply(property) + " " + sort);
		return this;
	}

	@Comment("Concatenate `order by xxx asc`")
	public NamedTable orderBy(@Comment(name = "property", value = "Columns to sort") String property) {
		return orderBy(property, "asc");
	}

	@Comment("Concatenate `order by xxx desc`")
	public NamedTable orderByDesc(@Comment(name = "property", value = "Columns to sort") String property) {
		return orderBy(property, "desc");
	}

	@Comment("Concatenate `group by`")
	public NamedTable groupBy(@Comment(name = "properties", value = "Columns to group") String... properties) {
		this.groups.addAll(Arrays.stream(properties).map(rowMapColumnMapper).collect(Collectors.toList()));
		return this;
	}

	@Comment("Execute INSERT, return primary key")
	public Object insert(RuntimeContext runtimeContext) {
		return insert(runtimeContext, null);
	}

	@Comment("Execute INSERT, return primary key")
	public Object insert(RuntimeContext runtimeContext,
						 @Comment(name = "data", value = "Sum of each column") Map<String, Object> data) {
		if (data != null) {
			data.forEach((key, value) -> this.columns.put(rowMapColumnMapper.apply(key), value));
		}
		if (this.defaultPrimaryValue != null && StringUtils.isBlank(Objects.toString(this.columns.getOrDefault(this.primary, "")))) {
			if (this.defaultPrimaryValue instanceof Supplier) {
				this.columns.put(this.primary, ((Supplier<?>) this.defaultPrimaryValue).get());
			} else {
				this.columns.put(this.primary, this.defaultPrimaryValue);
			}
		}
		preHandle(SqlMode.INSERT);
		Collection<Map.Entry<String, Object>> entries = filterNotBlanks();
		if (entries.isEmpty()) {
			throw new MagicAPIException("Parameter cannot be empty");
		}
		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(tableName);
		builder.append("(");
		builder.append(StringUtils.join(entries.stream().map(Map.Entry::getKey).toArray(), ","));
		builder.append(") values (");
		builder.append(StringUtils.join(Collections.nCopies(entries.size(), "?"), ","));
		builder.append(")");
		Object value = sqlModule.insert(new BoundSql(runtimeContext, builder.toString(), entries.stream().map(Map.Entry::getValue).collect(Collectors.toList()), sqlModule), this.primary);
		if(value == null && StringUtils.isNotBlank(this.primary)){
			return this.columns.get(this.primary);
		}
		return value;
	}

	@Comment("Execute DELETE statement")
	public int delete(RuntimeContext runtimeContext) {
		preHandle(SqlMode.DELETE);
		if (useLogic) {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put(logicDeleteColumn, logicDeleteValue);
			return update(runtimeContext, dataMap);
		}
		if (where.isEmpty()) {
			throw new MagicAPIException("DELETE statement cannot be without a condition");
		}
		StringBuilder builder = new StringBuilder();
		builder.append("delete from ");
		builder.append(tableName);
		builder.append(where.getSql());
		return sqlModule.update(new BoundSql(runtimeContext, builder.toString(), where.getParams(), sqlModule));
	}

	@Comment("Save to table, modify if primary key has a value, otherwise insert")
	public Object save(RuntimeContext runtimeContext) {
		return this.save(runtimeContext, null, false);
	}

	@Comment("Save to table, modify if primary key has a value, otherwise insert")
	public Object save(RuntimeContext runtimeContext,
					   @Comment(name = "data", value = "Sum of each column") Map<String, Object> data,
					   @Comment(name = "beforeQuery", value = "Whether to query for data by id") boolean beforeQuery) {
		if (StringUtils.isBlank(this.primary)) {
			throw new MagicAPIException("Please set the primary key");
		}
		if (data != null) {
			data.forEach((key, value) -> this.columns.put(rowMapColumnMapper.apply(key), value));
		}
		String primaryValue = Objects.toString(this.columns.get(this.primary), "");
		if (StringUtils.isBlank(primaryValue) && data != null) {
			primaryValue = Objects.toString(data.get(this.primary), "");
		}
		if (beforeQuery) {
			if (StringUtils.isNotBlank(primaryValue)) {
				List<Object> params = new ArrayList<>();
				params.add(primaryValue);
				Integer count = sqlModule.selectInt(new BoundSql(runtimeContext, "select count(*) count from " + this.tableName + " where " + this.primary + " = ?", params, sqlModule));
				if (count == 0) {
					return insert(runtimeContext, data);
				}
				return update(runtimeContext, data);
			} else {
				return insert(runtimeContext, data);
			}
		}

		if (StringUtils.isNotBlank(primaryValue)) {
			return update(runtimeContext, data);
		}
		return insert(runtimeContext, data);
	}

	@Comment("Save to table, modify if primary key has a value, otherwise insert")
	public Object save(RuntimeContext runtimeContext,
					   @Comment(name = "beforeQuery", value = "Whether to query for data by id") boolean beforeQuery) {
		return this.save(runtimeContext, null, beforeQuery);
	}

	@Comment("Save to table, modify if primary key has a value, otherwise insert")
	public Object save(RuntimeContext runtimeContext,
					   @Comment(name = "data", value = "Sum of each column") Map<String, Object> data) {
		return this.save(runtimeContext, data, false);
	}

	@Comment("Batch insert")
	public int batchInsert(RuntimeContext runtimeContext,
						   @Comment(name = "collection", value = "Sum of each column") Collection<Map<String, Object>> collection, @Comment("batchSize") int batchSize) {
		Set<String> keys = collection.stream().flatMap(it -> it.keySet().stream()).collect(Collectors.toSet());
		if (keys.isEmpty()) {
			throw new MagicAPIException("Columns to be inserted cannot be empty");
		}
		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(tableName);
		builder.append("(");
		builder.append(StringUtils.join(keys.stream().map(rowMapColumnMapper).collect(Collectors.toList()), ","));
		builder.append(") values (");
		builder.append(StringUtils.join(Collections.nCopies(keys.size(), "?"), ","));
		builder.append(")");
		return this.sqlModule.batchUpdate(runtimeContext, builder.toString(), batchSize, collection.stream()
				.map(it -> keys.stream().map(it::get).toArray())
				.collect(Collectors.toList()));
	}

	@Comment("Batch insert")
	public int batchInsert(RuntimeContext runtimeContext, @Comment(name = "collection", value = "Sum of each column") Collection<Map<String, Object>> collection) {
		return batchInsert(runtimeContext, collection, 100);
	}

	@Comment("Execute a `SELECT` query")
	public List<Map<String, Object>> select(RuntimeContext runtimeContext) {
		preHandle(SqlMode.SELECT);
		return sqlModule.select(buildSelect(runtimeContext));
	}

	@Comment("Execute a `SELECTOne` query")
	public Map<String, Object> selectOne(RuntimeContext runtimeContext) {
		preHandle(SqlMode.SELECT_ONE);
		return sqlModule.selectOne(buildSelect(runtimeContext));
	}

	@Comment("Execute a paginated query")
	public Object page(RuntimeContext runtimeContext) {
		preHandle(SqlMode.PAGE);
		return sqlModule.page(buildSelect(runtimeContext));
	}

	@Comment("Execute paginated query, pagination conditions are manually passed")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "limit", value = "Limit the number of rows") long limit,
					   @Comment(name = "offset", value = "Skip the number of rows") long offset) {
		preHandle(SqlMode.PAGE);
		return sqlModule.page(buildSelect(runtimeContext), new Page(limit, offset));
	}

	@Comment("Execute an UPDATE statement")
	public int update(RuntimeContext runtimeContext) {
		return update(runtimeContext, null);
	}

	@Comment("Execute an UPDATE statement")
	public int update(RuntimeContext runtimeContext,
					  @Comment(name = "data", value = "Sum of each column") Map<String, Object> data,
					  @Comment(name = "isUpdateBlank", value = "Update null fields?") boolean isUpdateBlank) {
		if (null != data) {
			data.forEach((key, value) -> this.columns.put(rowMapColumnMapper.apply(key), value));
		}
		preHandle(SqlMode.UPDATE);
		Object primaryValue = null;
		if (StringUtils.isNotBlank(this.primary)) {
			primaryValue = this.columns.remove(this.primary);
		}
		this.withBlank = isUpdateBlank;
		List<Map.Entry<String, Object>> entries = new ArrayList<>(filterNotBlanks());
		if (entries.isEmpty()) {
			throw new MagicAPIException("Columns to be modified cannot be empty");
		}
		StringBuilder builder = new StringBuilder();
		builder.append("update ");
		builder.append(tableName);
		builder.append(" set ");
		List<Object> params = new ArrayList<>();
		for (int i = 0, size = entries.size(); i < size; i++) {
			Map.Entry<String, Object> entry = entries.get(i);
			builder.append(entry.getKey()).append(" = ?");
			params.add(entry.getValue());
			if (i + 1 < size) {
				builder.append(",");
			}
		}
		if (!where.isEmpty()) {
			builder.append(where.getSql());
			params.addAll(where.getParams());
		} else if (primaryValue != null) {
			builder.append(" where ").append(this.primary).append(" = ?");
			params.add(String.valueOf(primaryValue));
		} else {
			throw new MagicAPIException("Primary key values cannot be empty");
		}
		return sqlModule.update(new BoundSql(runtimeContext, builder.toString(), params, sqlModule));
	}

	@Comment("Execute an UPDATE statement")
	public int update(RuntimeContext runtimeContext,
					  @Comment(name = "data", value = "Sum of each column") Map<String, Object> data) {
		return update(runtimeContext, data, this.withBlank);
	}

	@Comment("Number of rows to query")
	public int count(RuntimeContext runtimeContext) {
		preHandle(SqlMode.COUNT);
		StringBuilder builder = new StringBuilder();
		builder.append("select count(1) from ").append(tableName);
		List<Object> params = buildWhere(builder);
		return sqlModule.selectInt(new BoundSql(runtimeContext, builder.toString(), params, sqlModule));
	}

	@Comment("Check if it exists")
	public boolean exists(RuntimeContext runtimeContext) {
		return count(runtimeContext) > 0;
	}

	private Collection<Map.Entry<String, Object>> filterNotBlanks() {
		if (this.withBlank) {
			return this.columns.entrySet()
					.stream()
					.filter(it -> !excludeColumns.contains(it.getKey()))
					.collect(Collectors.toList());
		}
		return this.columns.entrySet()
				.stream()
				.filter(it -> StringUtils.isNotBlank(Objects.toString(it.getValue(), "")))
				.filter(it -> !excludeColumns.contains(it.getKey()))
				.collect(Collectors.toList());
	}

	private void preHandle(SqlMode sqlMode) {
		if (this.namedTableInterceptors != null) {
			this.namedTableInterceptors.forEach(interceptor -> interceptor.preHandle(sqlMode, this));
		}
	}

	private BoundSql buildSelect(RuntimeContext runtimeContext) {
		StringBuilder builder = new StringBuilder();
		builder.append("select ");
		List<String> fields = this.fields.stream()
				.filter(it -> !excludeColumns.contains(it))
				.collect(Collectors.toList());
		if (fields.isEmpty()) {
			builder.append("*");
		} else {
			builder.append(StringUtils.join(fields, ","));
		}
		builder.append(" from ").append(tableName);
		List<Object> params = buildWhere(builder);
        if (!groups.isEmpty()) {
			builder.append(" group by ");
			builder.append(String.join(",", groups));
		}
		if (!orders.isEmpty()) {
			builder.append(" order by ");
			builder.append(String.join(",", orders));
		}
		BoundSql boundSql = new BoundSql(runtimeContext, builder.toString(), params, sqlModule);
		boundSql.setExcludeColumns(excludeColumns);
		return boundSql;
	}


	private List<Object> buildWhere(StringBuilder builder) {
		List<Object> params = new ArrayList<>();
		if (!where.isEmpty()) {
			where.and();
			where.ne(useLogic, logicDeleteColumn, logicDeleteValue);
			builder.append(where.getSql());
			params.addAll(where.getParams());
		} else if (useLogic) {
			where.ne(logicDeleteColumn, logicDeleteValue);
			builder.append(where.getSql());
			params.addAll(where.getParams());
		}
		return params;
	}


	/**
	 * 获取查询的表名
	 *
	 * @return 表名
	 */
	@Transient
	public String getTableName() {
		return tableName;
	}

	/**
	 * 设置表名
	 *
	 * @param tableName 表名
	 */
	@Transient
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 获取SQL模块
	 */
	@Transient
	public SQLModule getSqlModule() {
		return sqlModule;
	}

	/**
	 * 获取主键列
	 */
	@Transient
	public String getPrimary() {
		return primary;
	}

	/**
	 * 获取逻辑删除列
	 */
	@Transient
	public String getLogicDeleteColumn() {
		return logicDeleteColumn;
	}

	/**
	 * 获取逻辑删除值
	 */
	@Transient
	public Object getLogicDeleteValue() {
		return logicDeleteValue;
	}

	/**
	 * 获取设置的columns
	 */
	@Transient
	public Map<String, Object> getColumns() {
		return columns;
	}

	/**
	 * 设置columns
	 */
	@Transient
	public void setColumns(Map<String, Object> columns) {
		this.columns = columns;
	}

	/**
	 * 获取设置的fields
	 */
	@Transient
	public List<String> getFields() {
		return fields;
	}

	/**
	 * 设置 fields
	 */
	@Transient
	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	/**
	 * 获取设置的group
	 */
	@Transient
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * 设置 group
	 */
	@Transient
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * 获取设置的order
	 */
	@Transient
	public List<String> getOrders() {
		return orders;
	}

	/**
	 * 设置 order
	 */
	@Transient
	public void setOrders(List<String> orders) {
		this.orders = orders;
	}

	/**
	 * 获取设置的排除的列
	 */
	@Transient
	public Set<String> getExcludeColumns() {
		return excludeColumns;
	}

	/**
	 * 设置排除的列
	 */
	@Transient
	public void setExcludeColumns(Set<String> excludeColumns) {
		this.excludeColumns = excludeColumns;
	}

	/**
	 * 主键默认值
	 *
	 * @return
	 */
	@Transient
	public Object getDefaultPrimaryValue() {
		return defaultPrimaryValue;
	}

	/**
	 * 是否设逻辑了逻辑删除
	 */
	@Transient
	public boolean isUseLogic() {
		return useLogic;
	}

	/**
	 * 设置是否使用逻辑删除
	 */
	@Transient
	public void setUseLogic(boolean useLogic) {
		this.useLogic = useLogic;
	}

	/**
	 * 获取是否不过滤空参数
	 */
	@Transient
	public boolean isWithBlank() {
		return withBlank;
	}

	/**
	 * 设置是否不过滤空参数
	 */
	@Transient
	public void setWithBlank(boolean withBlank) {
		this.withBlank = withBlank;
	}

	/**
	 * 获取where
	 */
	@Transient
	public Where getWhere() {
		return where;
	}

	/**
	 * 获取RequestEntity
	 */
	@Transient
	public RequestEntity getRequestEntity() {
		return RequestContext.getRequestEntity();
	}
}
