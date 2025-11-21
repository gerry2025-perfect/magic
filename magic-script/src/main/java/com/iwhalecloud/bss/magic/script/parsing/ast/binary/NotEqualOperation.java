package com.iwhalecloud.bss.magic.script.parsing.ast.binary;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.Expression;

/**
 * !=、!==操作
 */
public class NotEqualOperation extends EqualOperation {

	public NotEqualOperation(Expression leftOperand, Span span, Expression rightOperand, boolean accurate) {
		super(leftOperand, span, rightOperand, accurate);
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.visit(getLeftOperand())
				.visit(getRightOperand())
				.lineNumber(getSpan())
				.operator(accurate ? "not_accurate_equals" : "not_equals");
	}
}
