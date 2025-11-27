package com.iwhalecloud.bss.magic.magicapi.modules.db.table;

import org.apache.commons.lang3.StringUtils;
import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.functions.StreamExtension;
import com.iwhalecloud.bss.magic.script.runtime.RuntimeContext;

import java.beans.Transient;
import java.util.*;
import java.util.function.Function;

/**
 * 单表API的Where
 *
 * @author mxd
 */
public class Where {

	private final List<String> tokens = new ArrayList<>();

	private final List<Object> params = new ArrayList<>();

	private final NamedTable namedTable;

	private final boolean needWhere;

	private boolean notNull = false;

	private boolean notBlank = false;

	public Where(NamedTable namedTable) {
		this(namedTable, true);
	}

	public Where(NamedTable namedTable, boolean needWhere) {
		this.namedTable = namedTable;
		this.needWhere = needWhere;
	}

	@Override
	@Comment("Clone")
	public Where clone() {
		Where where = new Where(this.namedTable, this.needWhere);
		where.tokens.addAll(this.tokens);
		where.params.addAll(this.params);
		where.notNull = this.notNull;
		where.notBlank = this.notBlank;
		return where;
	}

	@Comment("Filter null parameters")
	public Where notNull() {
		return notNull(true);
	}

	@Comment("Filter blank parameters")
	public Where notBlank() {
		return notBlank(true);
	}

	@Comment("Whether to filter null parameters")
	public Where notNull(boolean flag) {
		this.notNull = flag;
		return this;
	}

	@Comment("Whether to filter blank parameters")
	public Where notBlank(boolean flag) {
		this.notBlank = flag;
		return this;
	}

	@Comment("Equal to =, e.g., `eq('name', 'LaoWang') ---> name = 'LaoWang'`")
	public Where eq(@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		return eq(true, column, value);
	}

	@Comment("Equal to =, e.g., `eq('name', 'LaoWang') ---> name = 'LaoWang'`")
	public Where eq(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		if (condition && filterNullAndBlank(value)) {
			tokens.add(namedTable.rowMapColumnMapper.apply(column));
			if (value == null) {
				append(" is null");
			} else {
				params.add(value);
				append(" = ?");
			}
			appendAnd();
		}
		return this;
	}

	@Comment("Not equal to `<>`, e.g., `ne('name', 'LaoWang') ---> name <> 'LaoWang'`")
	public Where ne(@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		return ne(true, column, value);
	}

	@Comment("Not equal to `<>`, e.g., `ne('name', 'LaoWang') ---> name <> 'LaoWang'`")
	public Where ne(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		if (condition && filterNullAndBlank(value)) {
			append(namedTable.rowMapColumnMapper.apply(column));
			if (value == null) {
				append("is not null");
			} else {
				params.add(value);
				append("<> ?");
			}
			appendAnd();
		}
		return this;
	}

	@Comment("Less than `<`, e.g., `lt('age', 18) ---> age < 18")
	public Where lt(@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		return lt(true, column, value);
	}

	@Comment("Less than `<`, e.g., `lt('age', 18) ---> age < 18")
	public Where lt(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		return append(condition, column, " < ?", value);
	}

	@Comment("Less than or equal to `<=`, e.g., `lte('age', 18) ---> age <= 18")
	public Where lte(@Comment(name = "column", value = "Column name in the database") String column,
					 @Comment(name = "value", value = "Value") Object value) {
		return lte(true, column, value);
	}

	@Comment("Less than or equal to `<=`, e.g., `lte('age', 18) ---> age <= 18")
	public Where lte(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					 @Comment(name = "column", value = "Column name in the database") String column,
					 @Comment(name = "value", value = "Value") Object value) {
		return append(condition, column, " <= ?", value);
	}

	@Comment("Greater than `>`, e.g., `get('age', 18) ---> age > 18")
	public Where gt(@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		return gt(true, column, value);
	}

	@Comment("Greater than `>`, e.g., `get('age', 18) ---> age > 18")
	public Where gt(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		return append(condition, column, " > ?", value);
	}

	@Comment("Greater than or equal to `>=`, e.g., `get('age', 18) ---> age >= 18")
	public Where gte(@Comment(name = "column", value = "Column name in the database") String column,
					 @Comment(name = "value", value = "Value") Object value) {
		return gte(true, column, value);
	}

	@Comment("Greater than or equal to `>=`, e.g., `get('age', 18) ---> age >= 18")
	public Where gte(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					 @Comment(name = "column", value = "Column name in the database") String column,
					 @Comment(name = "value", value = "Value") Object value) {
		return append(condition, column, " >= ?", value);
	}

	@Comment("`in`, e.g., `in('age', [1,2,3]) ---> age in (1,2,3)")
	public Where in(@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		return in(true, column, value);
	}

	@Comment("`in`, e.g., `in('age', [1,2,3]) ---> age in (1,2,3)")
	public Where in(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					@Comment(name = "column", value = "Column name in the database") String column,
					@Comment(name = "value", value = "Value") Object value) {
		if (condition && value != null) {
			List<Object> objects = StreamExtension.arrayLikeToList(value);
			if (objects.size() > 0) {
				append(namedTable.rowMapColumnMapper.apply(column));
				append(" in (");
				append(String.join(",", Collections.nCopies(objects.size(), "?")));
				append(")");
				appendAnd();
				params.addAll(objects);
			}
		}
		return this;
	}

	@Comment("`not in`, e.g., `notIn('age', [1,2,3]) ---> age not in (1,2,3)")
	public Where notIn(@Comment(name = "column", value = "Column name in the database") String column,
					   @Comment(name = "value", value = "Value") Object value) {
		return notIn(true, column, value);
	}

	@Comment("`not in`, e.g., `notIn('age', [1,2,3]) ---> age not in (1,2,3)")
	public Where notIn(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					   @Comment(name = "column", value = "Column name in the database") String column,
					   @Comment(name = "value", value = "Value") Object value) {
		if (condition && value != null) {
			List<Object> objects = StreamExtension.arrayLikeToList(value);
			if (objects.size() > 0) {
				append(namedTable.rowMapColumnMapper.apply(column));
				append("not in (");
				append(String.join(",", Collections.nCopies(objects.size(), "?")));
				append(")");
				appendAnd();
				params.addAll(objects);
			}
		}
		return this;
	}

	@Comment("`like`, e.g., `like('name', '%王%') ---> name like '%王%'")
	public Where like(@Comment(name = "column", value = "Column name in the database") String column,
					  @Comment(name = "value", value = "Value") Object value) {
		return like(true, column, value);
	}

	@Comment("`like`, e.g., `like('name', '%王%') ---> name like '%王%'")
	public Where like(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					  @Comment(name = "column", value = "Column name in the database") String column,
					  @Comment(name = "value", value = "Value") Object value) {
		return append(condition, column, "like ?", value);
	}

	@Comment("`not like`, e.g., `notLike('name', '%王%') ---> name not like '%王%'")
	public Where notLike(@Comment(name = "column", value = "Column name in the database") String column,
						 @Comment(name = "value", value = "Value") Object value) {
		return notLike(true, column, value);
	}

	@Comment("`not like` , e.g., `notLike('name', '%王%') ---> name not like '%王%'")
	public Where notLike(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
						 @Comment(name = "column", value = "Column name in the database") String column,
						 @Comment(name = "value", value = "Value") Object value) {
		return append(condition, column, "not like ?", value);
	}

	@Comment("`is null`, e.g., `isNull('name') ---> name is null")
	public Where isNull(@Comment(name = "column", value = "Column name in the database") String column) {
		return isNull(true, column);
	}

	@Comment("`is null`, e.g., `isNull('name') ---> name is null")
	public Where isNull(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
						@Comment(name = "column", value = "Column name in the database") String column) {
		if (condition) {
			append(namedTable.rowMapColumnMapper.apply(column));
			append("is null");
			appendAnd();
		}
		return this;
	}

	@Comment("`is not null`, e.g., `isNotNull('name') ---> name is not null")
	public Where isNotNull(@Comment(name = "column", value = "Column name in the database") String column) {
		return isNotNull(true, column);
	}

	@Comment("`is not null`, e.g., `isNotNull('name') ---> name is not null")
	public Where isNotNull(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
						   @Comment(name = "column", value = "Column name in the database") String column) {
		if (condition) {
			append(namedTable.rowMapColumnMapper.apply(column));
			append("is not null");
			appendAnd();
		}
		return this;
	}

	@Comment("Concatenation of `or`")
	public Where or() {
		appendOr();
		return this;
	}

	@Comment("Concatenation of `and`")
	public Where and() {
		appendAnd();
		return this;
	}

	@Comment("Nested `and`, e.g., and(it => it.eq('name','李白').ne('status','正常') --> and (name = '李白' and status <> '正常')")
	public Where and(@Comment(name = "function", value = "Callback function") Function<Object[], Where> function) {
		return and(true, function);
	}

	@Comment("Nested `and`, e.g., and(it => it.eq('name','李白').ne('status','正常') --> and (name = '李白' and status <> '正常')")
	public Where and(@Comment(name = "condition", value = "Evaluation expression; appends the condition if it is true") boolean condition,
					 @Comment(name = "function", value = "Callback function") Function<Object[], Where> function) {
		if (condition) {
			Where expr = function.apply(new Object[]{new Where(this.namedTable, false)});
			this.params.addAll(expr.params);
			append("(");
			append(expr.getSql());
			append(")");
			appendAnd();
		}
		return this;
	}

	@Comment("Concatenate `order by xxx asc/desc`")
	public Where orderBy(@Comment(name = "column", value = "Columns to sort") String column,
						 @Comment(name = "sort", value = "`asc` or `desc`") String sort) {
		this.namedTable.orderBy(column, sort);
		return this;
	}

	@Comment("Concatenate `order by xxx asc`")
	public Where orderBy(@Comment(name = "column", value = "Columns to sort") String column) {
		return orderBy(column, "asc");
	}

	@Comment("Concatenate `order by xxx desc`")
	public Where orderByDesc(@Comment(name = "column", value = "Columns to sort") String column) {
		return orderBy(column, "desc");
	}

	@Comment("Concatenate `group by`")
	public Where groupBy(@Comment("Columns to group") String... columns) {
		this.namedTable.groupBy(columns);
		return this;
	}

	@Comment("Save to table, modify if primary key has a value, otherwise insert")
	public Object save(RuntimeContext runtimeContext) {
		return namedTable.save(runtimeContext);
	}

	@Comment("Save to table, modify if primary key has a value, otherwise insert")
	public Object save(RuntimeContext runtimeContext,
					   @Comment(name = "beforeQuery", value = "Whether to query for data by id") boolean beforeQuery) {
		return namedTable.save(runtimeContext, beforeQuery);
	}

	@Comment("Save to table, modify if primary key has a value, otherwise insert")
	public Object save(RuntimeContext runtimeContext,
					   @Comment(name = "data", value = "Sum of each column") Map<String, Object> data) {
		return namedTable.save(runtimeContext, data);
	}

	@Comment("Save to table, modify if primary key has a value, otherwise insert")
	public Object save(RuntimeContext runtimeContext,
					   @Comment(name = "data", value = "Sum of each column") Map<String, Object> data,
					   @Comment(name = "beforeQuery", value = "Whether to query for data by id") boolean beforeQuery) {
		return namedTable.save(runtimeContext, data, beforeQuery);
	}

	@Comment("Execute INSERT statement, return primary key")
	public Object insert(RuntimeContext runtimeContext) {
		return namedTable.insert(runtimeContext);
	}

	@Comment("Execute INSERT statement, return primary key")
	public Object insert(RuntimeContext runtimeContext,
						 @Comment(name = "data", value = "Sum of each column") Map<String, Object> data) {
		return namedTable.insert(runtimeContext, data);
	}

	@Comment("Execute an UPDATE statement")
	public int update(RuntimeContext runtimeContext) {
		return namedTable.update(runtimeContext);
	}

	@Comment("Execute DELETE statement")
	public int delete(RuntimeContext runtimeContext) {
		return namedTable.delete(runtimeContext);
	}

	@Comment("Execute an UPDATE statement")
	public int update(RuntimeContext runtimeContext,
					  @Comment(name = "data", value = "Sum of each column") Map<String, Object> data) {
		return namedTable.update(runtimeContext, data);
	}

	@Comment("Execute an UPDATE statement")
	public int update(RuntimeContext runtimeContext,
					  @Comment(name = "data", value = "Sum of each column") Map<String, Object> data,
					  @Comment(name = "isUpdateBlank", value = "Update null fields?") boolean isUpdateBlank) {
		return namedTable.update(runtimeContext, data, isUpdateBlank);
	}

	@Comment("Execute a paginated query")
	public Object page(RuntimeContext runtimeContext) {
		return namedTable.page(runtimeContext);
	}

	@Comment("Execute paginated query, pagination conditions are manually passed")
	public Object page(RuntimeContext runtimeContext,
					   @Comment(name = "limit", value = "Limit the number of rows") long limit,
					   @Comment(name = "offset", value = "Skip the number of rows") long offset) {
		return namedTable.page(runtimeContext, limit, offset);
	}

	@Comment("Execute SELECT query")
	public List<Map<String, Object>> select(RuntimeContext runtimeContext) {
		return namedTable.select(runtimeContext);
	}

	@Comment("Execute SELECT One query")
	public Map<String, Object> selectOne(RuntimeContext runtimeContext) {
		return namedTable.selectOne(runtimeContext);
	}

	@Comment("Number of rows to query")
	public int count(RuntimeContext runtimeContext) {
		return namedTable.count(runtimeContext);
	}

	@Comment("Check if exists")
	public boolean exists(RuntimeContext runtimeContext) {
		return namedTable.exists(runtimeContext);
	}

	@Transient
	public void appendAnd() {
		remove();
		tokens.add("and");
	}

	@Transient
	public void appendOr() {
		remove();
		tokens.add("or");
	}

	List<Object> getParams() {
		return params;
	}

	void remove() {
		int size = tokens.size();
		while (size > 0) {
			String token = tokens.get(size - 1);
			if ("and".equalsIgnoreCase(token) || "or".equalsIgnoreCase(token)) {
				tokens.remove(size - 1);
				size--;
			} else {
				break;
			}
		}
		while (size > 0) {
			String token = tokens.get(0);
			if ("and".equalsIgnoreCase(token) || "or".equalsIgnoreCase(token)) {
				tokens.remove(0);
				size--;
			} else {
				break;
			}
		}
	}

	boolean isEmpty() {
		return tokens.isEmpty();
	}

	@Transient
	public void append(String value) {
		tokens.add(value);
	}

	@Transient
	public void append(String sql, Object value) {
		tokens.add(sql);
		params.add(value);
	}

	String getSql() {
		remove();
		if (isEmpty()) {
			return "";
		}
		return (needWhere ? " where " : "") + String.join(" ", tokens);
	}

	boolean filterNullAndBlank(Object value) {
		if (notNull && value == null) {
			return false;
		}
		return !notBlank || !StringUtils.isEmpty(Objects.toString(value, ""));
	}

	private Where append(boolean append, String column, String condition, Object value) {
		if (append && filterNullAndBlank(value)) {
			append(namedTable.rowMapColumnMapper.apply(column));
			append(condition);
			appendAnd();
			params.add(value);
		}
		return this;
	}
}
