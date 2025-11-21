package com.iwhalecloud.bss.magic.script.parsing.ast.binary;

import com.iwhalecloud.bss.magic.script.asm.Label;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.BinaryOperation;
import com.iwhalecloud.bss.magic.script.parsing.ast.Expression;
import com.iwhalecloud.bss.magic.script.runtime.handle.OperatorHandle;

/**
 * || 操作
 */
public class OrOperation extends BinaryOperation {

	public OrOperation(Expression leftOperand, Span span, Expression rightOperand) {
		super(leftOperand, span, rightOperand);
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		Label end = new Label();
		compiler.visit(getLeftOperand())
				.insn(DUP)
				.invoke(INVOKESTATIC, OperatorHandle.class, "isTrue", boolean.class, Object.class)
				.jump(IFNE, end)
				.insn(POP)
				.visit(getRightOperand())
				.label(end);
	}
}
