package com.iwhalecloud.bss.magic.script.parsing.ast.binary;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.BinaryOperation;
import com.iwhalecloud.bss.magic.script.parsing.ast.Expression;

/**
 * % 运算
 */
public class ModuloOperation extends BinaryOperation {

	public ModuloOperation(Expression leftOperand, Span span, Expression rightOperand) {
		super(leftOperand, span, rightOperand);
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.visit(getLeftOperand())
				.visit(getRightOperand())
				.lineNumber(getSpan())
				.arithmetic("divideAndRemainder");
	}
}
