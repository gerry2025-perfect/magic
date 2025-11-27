package com.iwhalecloud.bss.magic.magicapi.modules.db;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import com.iwhalecloud.bss.magic.magicapi.core.annotation.MagicModule;
import com.iwhalecloud.bss.magic.magicapi.core.context.RequestContext;
import com.iwhalecloud.bss.magic.magicapi.core.context.RequestEntity;
import com.iwhalecloud.bss.magic.magicapi.core.interceptor.ResultProvider;
import com.iwhalecloud.bss.magic.magicapi.core.model.Options;
import com.iwhalecloud.bss.magic.magicapi.datasource.model.MagicDynamicDataSource;
import com.iwhalecloud.bss.magic.magicapi.datasource.model.MagicDynamicDataSource.DataSourceNode;
import com.iwhalecloud.bss.magic.magicapi.modules.DynamicModule;
import com.iwhalecloud.bss.magic.magicapi.modules.db.cache.SqlCache;
import com.iwhalecloud.bss.magic.magicapi.modules.db.dialect.Dialect;
import com.iwhalecloud.bss.magic.magicapi.modules.db.dialect.DialectAdapter;
import com.iwhalecloud.bss.magic.magicapi.modules.db.inteceptor.NamedTableInterceptor;
import com.iwhalecloud.bss.magic.magicapi.modules.db.inteceptor.SQLInterceptor;
import com.iwhalecloud.bss.magic.magicapi.modules.db.model.Page;
import com.iwhalecloud.bss.magic.magicapi.modules.db.model.SqlTypes;
import com.iwhalecloud.bss.magic.magicapi.modules.db.provider.PageProvider;
import com.iwhalecloud.bss.magic.magicapi.modules.db.table.NamedTable;
import com.iwhalecloud.bss.magic.magicapi.utils.ScriptManager;
import com.iwhalecloud.bss.magic.script.MagicScriptContext;
import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.functions.DynamicAttribute;
import com.iwhalecloud.bss.magic.script.parsing.ast.statement.ClassConverter;
import com.iwhalecloud.bss.magic.script.runtime.RuntimeContext;

import java.beans.Transient;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * 数据库查询模块
 *
 * @author mxd
 */
@MagicModule("db")
public class SQLModule implements DynamicAttribute<SQLModule, SQLModule>, DynamicModule<SQLModule> {
	static {
		try {
			ClassConverter.register("sql", (value, params) -> {
				if (params == null || params.length == 0) {
					return value;
				}
				if (params[0] instanceof Number) {
					return new SqlParameterValue(((Number) params[0]).intValue(), value);
				}
				String target = Objects.toString(params[0], null);
				if (StringUtils.isBlank(target)) {
					return value;
				}
				Integer sqlType = SqlTypes.getSqlType(target);
				return sqlType == null ? value : new SqlParameterValue(sqlType, target, value);
			});
		} catch (Exception ignored) {

		}
	}

	private MagicDynamicDataSource dynamicDataSource;
	private DataSourceNode dataSourceNode;
	private PageProvider pageProvider;
	private ResultProvider resultProvider;
	private ColumnMapperAdapter columnMapperAdapter;
	private DialectAdapter dialectAdapter;
	private RowMapper<Map<String, Object>> columnMapRowMapper;
	private Function<String, String> rowMapColumnMapper;
	private SqlCache sqlCache;
	private String cacheName;
	private List<SQLInterceptor> sqlInterceptors;
	private List<NamedTableInterceptor> namedTableInterceptors;
	private long ttl;
	private String logicDeleteColumn;
	private String logicDeleteValue;
    public static List<SqlParameter> params;

	public SQLModule() {

	}

	public SQLModule(MagicDynamicDataSource dynamicDataSource) {
		this.dynamicDataSource = dynamicDataSource;
	}

	@Transient
	public void setPageProvider(PageProvider pageProvider) {
		this.pageProvider = pageProvider;
	}

	@Transient
	public void setResultProvider(ResultProvider resultProvider) {
		this.resultProvider = resultProvider;
	}

	@Transient
	public void setColumnMapperProvider(ColumnMapperAdapter columnMapperAdapter) {
		this.columnMapperAdapter = columnMapperAdapter;
	}

	@Transient
	public void setDialectAdapter(DialectAdapter dialectAdapter) {
		this.dialectAdapter = dialectAdapter;
	}

	@Transient
	public void setColumnMapRowMapper(RowMapper<Map<String, Object>> columnMapRowMapper) {
		this.columnMapRowMapper = columnMapRowMapper;
	}

	@Transient
	public void setRowMapColumnMapper(Function<String, String> rowMapColumnMapper) {
		this.rowMapColumnMapper = rowMapColumnMapper;
	}

	@Transient
	public void setDynamicDataSource(MagicDynamicDataSource dynamicDataSource) {
		this.dynamicDataSource = dynamicDataSource;
	}

	@Transient
	public void setSqlInterceptors(List<SQLInterceptor> sqlInterceptors) {
		this.sqlInterceptors = sqlInterceptors;
	}

	@Transient
	public void setNamedTableInterceptors(List<NamedTableInterceptor> namedTableInterceptors) {
		this.namedTableInterceptors = namedTableInterceptors;
	}

	@Transient
	public void setDataSourceNode(DataSourceNode dataSourceNode) {
		this.dataSourceNode = dataSourceNode;
	}

	@Transient
	public String getCacheName() {
		return cacheName;
	}

	@Transient
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	@Transient
	public long getTtl() {
		return ttl;
	}

	@Transient
	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	@Transient
	public String getLogicDeleteColumn() {
		return logicDeleteColumn;
	}

	@Transient
	public void setLogicDeleteColumn(String logicDeleteColumn) {
		this.logicDeleteColumn = logicDeleteColumn;
	}

	@Transient
	public String getLogicDeleteValue() {
		return logicDeleteValue;
	}

	@Transient
	public void setLogicDeleteValue(String logicDeleteValue) {
		this.logicDeleteValue = logicDeleteValue;
	}

	@Transient
	public SqlCache getSqlCache() {
		return sqlCache;
	}

	@Transient
	public void setSqlCache(SqlCache sqlCache) {
		this.sqlCache = sqlCache;
	}

	@Transient
	public SQLModule cloneSQLModule() {
		SQLModule sqlModule = new SQLModule();
		sqlModule.setDynamicDataSource(this.dynamicDataSource);
		sqlModule.setDataSourceNode(this.dataSourceNode);
		sqlModule.setPageProvider(this.pageProvider);
		sqlModule.setColumnMapperProvider(this.columnMapperAdapter);
		sqlModule.setColumnMapRowMapper(this.columnMapRowMapper);
		sqlModule.setRowMapColumnMapper(this.rowMapColumnMapper);
		sqlModule.setSqlCache(this.sqlCache);
		sqlModule.setTtl(this.ttl);
		sqlModule.setCacheName(this.cacheName);
		sqlModule.setResultProvider(this.resultProvider);
		sqlModule.setDialectAdapter(this.dialectAdapter);
		sqlModule.setSqlInterceptors(this.sqlInterceptors);
		sqlModule.setLogicDeleteValue(this.logicDeleteValue);
		sqlModule.setLogicDeleteColumn(this.logicDeleteColumn);
		sqlModule.setNamedTableInterceptors(this.namedTableInterceptors);
		return sqlModule;
	}

	/**
	 * 开启事务，在一个回调中进行操作
	 *
	 * @param function 回调函数
	 */
	@Comment("Starts a transaction and handles it in the callback")
	public Object transaction(@Comment(name = "function", value = "Callback function, such as: ()=>{....}") Function<?, ?> function) {
		// 创建事务
		Transaction transaction = transaction();
		try {
			Object val = function.apply(null);
			transaction.commit();    //提交事务
			return val;
		} catch (Throwable throwable) {
			transaction.rollback();    //回滚事务
			throw throwable;
		}
	}

	/**
	 * 开启事务，手动提交和回滚
	 */
	@Comment("Starts a transaction and returns the transaction object")
	public Transaction transaction() {
		return new Transaction(this.dataSourceNode.getDataSourceTransactionManager());
	}

	/**
	 * 使用缓存
	 *
	 * @param cacheName 缓存名
	 * @param ttl       过期时间
	 */
	@Comment("Uses a cache")
	public SQLModule cache(@Comment(name = "cacheName", value = "Cache name") String cacheName,
						   @Comment(name = "ttl", value = "Expiration time") long ttl) {
		if (cacheName == null) {
			return this;
		}
		SQLModule sqlModule = cloneSQLModule();
		sqlModule.setCacheName(cacheName);
		sqlModule.setTtl(ttl);
		return sqlModule;
	}

	/**
	 * 使用缓存（采用默认缓存时间）
	 *
	 * @param cacheName 缓冲名
	 */
	@Comment("Uses a cache, with the expiration time using the default configuration")
	public SQLModule cache(@Comment(name = "cacheName", value = "Cache name") String cacheName) {
		return cache(cacheName, 0);
	}

	@Comment("Uses camelCase column names")
	public SQLModule camel() {
		return columnCase("camel");
	}

	@Comment("Uses PascalCase column names")
	public SQLModule pascal() {
		return columnCase("pascal");
	}

	@Comment("Uses all lowercase column names")
	public SQLModule lower() {
		return columnCase("lower");
	}

	@Comment("Use all uppercase column names")
	public SQLModule upper() {
		return columnCase("upper");
	}

	@Comment("Keep column names as they are")
	public SQLModule normal() {
		return columnCase("default");
	}

	@Comment("Specify column name conversion")
	public SQLModule columnCase(String name) {
		SQLModule sqlModule = cloneSQLModule();
		sqlModule.setColumnMapRowMapper(this.columnMapperAdapter.getColumnMapRowMapper(name));
		sqlModule.setRowMapColumnMapper(this.columnMapperAdapter.getRowMapColumnMapper(name));
		return sqlModule;
	}


	/**
	 * 数据源切换
	 */
	@Override
	@Transient
	public SQLModule getDynamicAttribute(String key) {
		SQLModule sqlModule = cloneSQLModule();
		if (key == null) {
			sqlModule.setDataSourceNode(dynamicDataSource.getDataSource());
		} else {
			sqlModule.setDataSourceNode(dynamicDataSource.getDataSource(key));
		}
		return sqlModule;
	}

	/**
	 * 查询List
	 */
	@Comment("Query SQL, return List type results")
	public List<Map<String, Object>> select(RuntimeContext runtimeContext,
											@Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		return select(runtimeContext, sqlOrXml, null);
	}

	/**
	 * 查询List，并传入变量信息
	 */
	@Comment("Query SQL, pass variable information, return List type results")
	public List<Map<String, Object>> select(RuntimeContext runtimeContext,
											@Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
											@Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		return select(new BoundSql(runtimeContext, sqlOrXml, params, this));
	}

	@Transient
	public List<Map<String, Object>> select(BoundSql boundSql) {
		assertDatasourceNotNull();
		return boundSql.execute(this.sqlInterceptors, () -> queryForList(boundSql));
	}

	private List<Map<String, Object>> queryForList(BoundSql boundSql) {
		List<Map<String, Object>> list = dataSourceNode.getJdbcTemplate().query(boundSql.getSql(), this.columnMapRowMapper, boundSql.getParameters());
		if (boundSql.getExcludeColumns() != null) {
			list.forEach(row -> boundSql.getExcludeColumns().forEach(row::remove));
		}
		return list;
	}

	private void assertDatasourceNotNull() {
		if (dataSourceNode == null) {
			throw new NullPointerException("Current data source not set");
		}
	}

	/**
	 * 执行update
	 */
	@Comment("Execute update operation, return number of affected rows")
	public int update(RuntimeContext runtimeContext,
					  @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		return update(runtimeContext, sqlOrXml, null);
	}

	/**
	 * 执行update，并传入变量信息
	 */
	@Comment("Execute update operation, pass variable information, return number of affected rows")
	public int update(RuntimeContext runtimeContext,
					  @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
					  @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		return update(new BoundSql(runtimeContext, sqlOrXml, params, this));
	}

	@Transient
	public int update(BoundSql boundSql) {
		assertDatasourceNotNull();
		return (int)boundSql.execute(sqlInterceptors, () -> {
			Object value = dataSourceNode.getJdbcTemplate().update(boundSql.getSql(), boundSql.getParameters());
			deleteCache(this.cacheName);
			return value;
		});
	}

	/**
	 * 插入并返回主键
	 */
	@Comment("Executes an insert operation and returns the inserted primary key")
	public Object insert(RuntimeContext runtimeContext,
						 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		return insert(runtimeContext, sqlOrXml, null, null);
	}

	/**
	 * 插入并返回主键，并传入变量信息
	 */
	@Comment("Executes an insert operation, passes in variable information, and returns the inserted primary key")
	public Object insert(RuntimeContext runtimeContext,
						 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
						 @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		return insert(runtimeContext, sqlOrXml, null, params);
	}

	/**
	 * 插入并返回主键
	 */
	@Comment("Executes an insert operation and returns the inserted primary key")
	public Object insert(RuntimeContext runtimeContext,
						 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
						 @Comment(name = "primary", value = "Primary key column") String primary) {
		return insert(runtimeContext, sqlOrXml, primary, null);
	}

	/**
	 * 插入并返回主键
	 */
	@Comment("Executes an insert operation, passes in primary key and variable information")
	public Object insert(RuntimeContext runtimeContext,
						 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
						 @Comment(name = "primary", value = "Primary key column") String primary,
						 @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		return insert(new BoundSql(runtimeContext, sqlOrXml, params, this), primary);
	}

	void insert(BoundSql boundSql, MagicKeyHolder keyHolder) {
		assertDatasourceNotNull();
		dataSourceNode.getJdbcTemplate().update(con -> {
			PreparedStatement ps = keyHolder.createPrepareStatement(con, boundSql.getSql());
			new ArgumentPreparedStatementSetter(boundSql.getParameters()).setValues(ps);
			return ps;
		}, keyHolder);
		deleteCache(this.cacheName);
	}

	/**
	 * 插入并返回主键
	 */
	@Comment("Batch execution operation, returns the number of rows affected")
	public int batchUpdate(RuntimeContext runtimeContext, String sql, List<Object[]> args) {
		assertDatasourceNotNull();
		BoundSql boundSql = new BoundSql(runtimeContext, sql, new ArrayList<>(args), this);
		return boundSql.execute(sqlInterceptors, () -> {
			int[] values = dataSourceNode.getJdbcTemplate().batchUpdate(boundSql.getSql(), boundSql.getBatchParameters());
			deleteCache(this.cacheName);
			return Arrays.stream(values).sum();
		});
	}

	@Transient
	public JdbcTemplate getJdbcTemplate() {
		assertDatasourceNotNull();
		return dataSourceNode.getJdbcTemplate();
	}

	@Comment("Delete `SQL` cache")
	public SQLModule deleteCache(@Comment("Cache name") String name) {
		if (StringUtils.isNotBlank(name)) {
			sqlCache.delete(name);
		}
		return this;
	}


	/**
	 * 插入并返回主键
	 */
	@Comment("Batch execution operation, returns the number of rows affected")
	public int batchUpdate(RuntimeContext runtimeContext, String sql, int batchSize, List<Object[]> args) {
		assertDatasourceNotNull();
		BoundSql boundSql = new BoundSql(runtimeContext, sql, new ArrayList<>(args), this);
		return boundSql.execute(sqlInterceptors, () -> {
			int[][] values = dataSourceNode.getJdbcTemplate().batchUpdate(boundSql.getSql(), boundSql.getBatchParameters(), batchSize, (ps, arguments) -> {
				int colIndex = 1;
				for (Object value : arguments) {
					if (value instanceof SqlParameterValue) {
						SqlParameterValue paramValue = (SqlParameterValue) value;
						StatementCreatorUtils.setParameterValue(ps, colIndex++, paramValue, paramValue.getValue());
					} else {
						StatementCreatorUtils.setParameterValue(ps, colIndex++, StatementCreatorUtils.javaTypeToSqlParameterType(value == null ? null : value.getClass()), value);
					}
				}
			});
			deleteCache(this.cacheName);
			int count = 0;
			for (int[] value : values) {
				count += Arrays.stream(value).sum();
			}
			return count;
		}, false);

	}

	/**
	 * 插入并返回主键
	 */
	@Comment("Batch execution operation, returns the number of rows affected")
	public int batchUpdate(@Comment(name = "sqls", value = "`SQL` statement") List<String> sqls) {
		assertDatasourceNotNull();
		int[] values = dataSourceNode.getJdbcTemplate().batchUpdate(sqls.toArray(new String[0]));
		deleteCache(this.cacheName);
		return Arrays.stream(values).sum();
	}

	@Transient
	public Object insert(BoundSql boundSql, String primary) {
		return boundSql.execute(sqlInterceptors, () -> {
			MagicKeyHolder keyHolder = new MagicKeyHolder(primary);
			insert(boundSql, keyHolder);
			deleteCache(this.cacheName);
			return keyHolder.getObjectKey();
		}, false);
	}

	/**
	 * 分页查询
	 */
	@Comment("Execute paginated query, pagination conditions are automatically obtained")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
					   @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		return page(new BoundSql(runtimeContext, sqlOrXml, params, this));
	}

	/**
	 * 分页查询,并传入变量信息
	 */
	@Comment("Execute paginated query, passing in variable information, pagination conditions are automatically obtained")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		return page(runtimeContext, sqlOrXml, (Map<String, Object>) null);
	}

	/**
	 * 分页查询（手动传入limit和offset参数）
	 */
	@Comment("Execute paginated query, pagination conditions are manually passed")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
					   @Comment(name = "limit", value = "Limit the number of rows") long limit,
					   @Comment(name = "offset", value = "Skip the number of rows") long offset) {
		return page(runtimeContext, sqlOrXml, limit, offset, null);
	}

	/**
	 * 分页查询（手动传入limit和offset参数）
	 */
	@Comment("Execute paginated query, passing in variable information, pagination conditions are manually passed")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
					   @Comment(name = "limit", value = "Limit the number of rows") long limit,
					   @Comment(name = "offset", value = "Skip the number of rows") long offset,
					   @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		BoundSql boundSql = new BoundSql(runtimeContext, sqlOrXml, params, this);
		return page(boundSql, new Page(limit, offset));
	}

	@Transient
	public Object page(BoundSql boundSql) {
		Page page = pageProvider.getPage(boundSql.getRuntimeContext());
		return page(boundSql, page);
	}

	@Transient
	public String getDataSourceName() {
		return this.dataSourceNode == null ? "unknown" : dataSourceNode.getName();
	}

	/**
	 * 分页查询（手动传入分页SQL语句）
	 */
	@Comment("Executes a paginated query, manually entering the pagination `SQL` statement")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "countSqlOrXml", value = "Count statement") String countSqlOrXml,
					   @Comment(name = "sqlOrXml", value = "Query statement") String sqlOrXml) {
		return page(runtimeContext, countSqlOrXml, sqlOrXml, null);
	}

	/**
	 * 分页查询（手动传入分页SQL语句）
	 */
	@Comment("Executes a paginated query, passing in variable information, pagination `SQL`countSqlOrXml")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "countSqlOrXml", value = "Count statement") String countSqlOrXml,
					   @Comment(name = "sqlOrXml", value = "Query statement") String sqlOrXml,
					   @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		int count = selectInt(new BoundSql(runtimeContext, countSqlOrXml, params, this));
		Page page = pageProvider.getPage(runtimeContext);
		BoundSql boundSql = new BoundSql(runtimeContext, sqlOrXml, params, this);
		return page(count, boundSql, page, null);
	}

	/**
	 * 分页查询（手动传入count）
	 */
	@Comment("Executes a paginated query, passing in variable information, pagination `SQL`count")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "count", value = "Total number of rows") int count,
					   @Comment(name = "sqlOrXml", value = "Query statement") String sqlOrXml,
					   @Comment(name = "limit", value = "Limit the number of rows") long limit,
					   @Comment(name = "offset", value = "Skip the number of rows") long offset,
					   @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		BoundSql boundSql = new BoundSql(runtimeContext, sqlOrXml, params, this);
		return page(count, boundSql, new Page(limit, offset), null);
	}

	private Object page(int count, BoundSql boundSql, Page page, Dialect dialect) {
		List<Map<String, Object>> list = null;
		if (count > 0) {
			if (dialect == null) {
				dialect = dataSourceNode.getDialect(dialectAdapter);
			}
			BoundSql pageBoundSql = buildPageBoundSql(dialect, boundSql, page.getOffset(), page.getLimit());
			list = pageBoundSql.execute(this.sqlInterceptors, () -> queryForList(pageBoundSql));
		}
		RequestEntity requestEntity = RequestContext.getRequestEntity();
		return resultProvider.buildPageResult(requestEntity, page, count, list);
	}

	@Transient
	public Object page(BoundSql boundSql, Page page) {
		assertDatasourceNotNull();
		Dialect dialect = dataSourceNode.getDialect(dialectAdapter);
		BoundSql countBoundSql = boundSql.copy(dialect.getCountSql(boundSql.getSql()));
		int count = selectInt(countBoundSql);
		return page(count, boundSql, page, dialect);
	}

	/**
	 * 查询总条目数
	 */
	@Comment("Query the total number of entries")
	public Integer count(RuntimeContext runtimeContext,
							 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		return count(runtimeContext, sqlOrXml, null);
	}

	/**
	 * 查询总条目数
	 */
	@Comment("Query the total number of entries, passing in variable information")
	public Integer count(RuntimeContext runtimeContext,
							 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
							 @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		BoundSql boundSql = new BoundSql(runtimeContext, sqlOrXml, params, this);
		Dialect dialect = dataSourceNode.getDialect(dialectAdapter);
		BoundSql countBoundSql = boundSql.copy(dialect.getCountSql(boundSql.getSql()));
		return selectInt(countBoundSql);
	}

	/**
	 * 查询int值
	 */
	@Comment("Query for int values, suitable for single-row, single-column int results")
	public Integer selectInt(RuntimeContext runtimeContext,
							 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		return selectInt(runtimeContext, sqlOrXml, null);
	}

	/**
	 * 查询int值
	 */
	@Comment("Query for an integer value, passing in variable information; suitable for single-row, single-column integer results")
	public Integer selectInt(RuntimeContext runtimeContext,
							 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
							 @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		return selectInt(new BoundSql(runtimeContext, sqlOrXml, params, this));
	}

	@Transient
	public Integer selectInt(BoundSql boundSql) {
		assertDatasourceNotNull();
		return boundSql.execute(this.sqlInterceptors, () -> dataSourceNode.getJdbcTemplate().query(boundSql.getSql(), new SingleRowResultSetExtractor<>(Integer.class), boundSql.getParameters()));
	}

	/**
	 * 查询Map
	 */
	@Comment("Query for a single result; returns null if not found")
	public Map<String, Object> selectOne(RuntimeContext runtimeContext,
										 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		return selectOne(runtimeContext, sqlOrXml, null);
	}

	/**
	 * 查询Map,并传入变量信息
	 */
	@Comment("Query for a single result, passing in variable information; returns null if not found")
	public Map<String, Object> selectOne(RuntimeContext runtimeContext,
										 @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
										 @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		return selectOne(new BoundSql(runtimeContext, sqlOrXml, params, this));
	}

	@Transient
	public Map<String, Object> selectOne(BoundSql boundSql) {
		assertDatasourceNotNull();
		return boundSql.execute(this.sqlInterceptors, () -> {
			Map<String, Object> row = dataSourceNode.getJdbcTemplate().query(boundSql.getSql(), new SingleRowResultSetExtractor<>(this.columnMapRowMapper), boundSql.getParameters());
			if (row != null && boundSql.getExcludeColumns() != null) {
				boundSql.getExcludeColumns().forEach(row::remove);
			}
			return row;
		});
	}

	/**
	 * 查询单行单列的值
	 */
	@Comment("Query for a single row, single column value")
	public Object selectValue(RuntimeContext runtimeContext,
							  @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		return selectValue(runtimeContext, sqlOrXml, null);
	}

	/**
	 * 查询单行单列的值，并传入变量信息
	 */
	@Comment("Query for a single row, single column value, passing in variable information")
	public Object selectValue(RuntimeContext runtimeContext,
							  @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml,
							  @Comment(name = "params", value = "Variable information") Map<String, Object> params) {
		assertDatasourceNotNull();
		BoundSql boundSql = new BoundSql(runtimeContext, sqlOrXml, params, this);
		return boundSql.execute(this.sqlInterceptors, () -> dataSourceNode.getJdbcTemplate().query(boundSql.getSql(), new SingleRowResultSetExtractor<>(Object.class), boundSql.getParameters()));
	}

	@Comment("Specify a table for single-table operations")
	public NamedTable table(@Comment(name = "tableName", value = "Table name") String tableName) {
		return new NamedTable(tableName, this, rowMapColumnMapper, namedTableInterceptors);
	}

	private BoundSql buildPageBoundSql(Dialect dialect, BoundSql boundSql, long offset, long limit) {
		String pageSql = dialect.getPageSql(boundSql.getSql(), boundSql, offset, limit);
		return boundSql.copy(pageSql);
	}

	@Transient
	@Override
	public SQLModule getDynamicModule(MagicScriptContext context) {
		String dataSourceKey = context.getString(Options.DEFAULT_DATA_SOURCE.getValue());
		if (StringUtils.isEmpty(dataSourceKey)) return this;
		SQLModule newSqlModule = cloneSQLModule();
		newSqlModule.setDataSourceNode(dynamicDataSource.getDataSource(dataSourceKey));
		return newSqlModule;
	}

	static class MagicKeyHolder extends GeneratedKeyHolder {

		private final boolean useGeneratedKeys;

		private final String primary;

		public MagicKeyHolder() {
			this(null);
		}

		public MagicKeyHolder(String primary) {
			this.primary = primary;
			this.useGeneratedKeys = StringUtils.isBlank(primary);
		}

		PreparedStatement createPrepareStatement(Connection connection, String sql) throws SQLException {
			if (useGeneratedKeys) {
				return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			}
			return connection.prepareStatement(sql, new String[]{primary});
		}

		public Object getObjectKey() {
			List<Map<String, Object>> keyList = getKeyList();
			if (keyList.isEmpty()) {
				return null;
			}
			Iterator<Object> keyIterator = keyList.get(0).values().iterator();
			Object key = keyIterator.hasNext() ? keyIterator.next() : null;
			if (key != null && "oracle.sql.ROWID".equals(key.getClass().getName())) {
				return ScriptManager.executeExpression("row.stringValue()", Collections.singletonMap("row", key));
			}
			return key;
		}
	}

    @Comment("Call a stored procedure")
    public Object call(RuntimeContext runtimeContext,
                                            @Comment(name = "sqlOrXml", value = "`SQL` statement or `xml`") String sqlOrXml) {
		assertDatasourceNotNull();
		BoundSql boundSql = new BoundSql(runtimeContext, sqlOrXml, Collections.emptyMap(), this);
		return boundSql.execute(this.sqlInterceptors, () -> this.dataSourceNode.getJdbcTemplate().call(
				con -> {
					CallableStatement statement = con.prepareCall(boundSql.getSql());
					Object[] parameters = boundSql.getParameters();
					for (int i = 0, size = parameters.length; i < size; i++) {
						Object parameter = parameters[i];
						if (parameter instanceof SqlOutParameter) {
							SqlOutParameter sop = (SqlOutParameter)parameter;
							statement.registerOutParameter(i + 1, sop.getSqlType());
						} else if(parameter instanceof SqlParameterValue){
							SqlParameterValue spv = (SqlParameterValue)parameter;
							if(spv.getName() != null){
								statement.registerOutParameter(i + 1, spv.getSqlType());
							}
							statement.setObject(i + 1, spv.getValue());
						} else {
							statement.setObject(i + 1, parameter);
						}
					}
					return statement;
				}, boundSql.getDeclareParameters()));
    }

}
