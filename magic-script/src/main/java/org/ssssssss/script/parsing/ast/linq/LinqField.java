package org.ssssssss.script.parsing.ast.linq;

import org.ssssssss.script.parsing.Span;
import org.ssssssss.script.parsing.VarIndex;
import org.ssssssss.script.parsing.ast.Expression;
import org.ssssssss.script.parsing.ast.VariableSetter;
import org.ssssssss.script.parsing.ast.statement.MemberAccess;

public class LinqField extends LinqExpression implements VariableSetter {

	private final String aliasName;

	private final VarIndex varIndex;

	public LinqField(Span span, Expression expression, VarIndex alias) {
		super(span, expression);
		if (expression instanceof MemberAccess){
			this.aliasName = alias != null ? alias.getName() : ((MemberAccess)expression).getName().getText();
		} else {
			this.aliasName = alias != null ? alias.getName() : expression.getSpan().getText();
		}
		this.varIndex = alias;
	}

	public VarIndex getVarIndex() {
		return varIndex;
	}

	public String getAlias() {
		return aliasName;
	}

}
