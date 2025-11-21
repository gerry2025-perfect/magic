package com.iwhalecloud.bss.magic.script.parsing.ast.binary;

import com.iwhalecloud.bss.magic.script.MagicScriptError;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.BinaryOperation;
import com.iwhalecloud.bss.magic.script.parsing.ast.Expression;
import com.iwhalecloud.bss.magic.script.parsing.ast.VariableSetter;
import com.iwhalecloud.bss.magic.script.parsing.ast.statement.VariableAccess;

/**
 * = 操作
 */
public class AssigmentOperation extends BinaryOperation {

	public AssigmentOperation(Expression leftOperand, Span span, Expression rightOperand) {
		super(leftOperand, span, rightOperand);
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		if (getLeftOperand() instanceof VariableAccess) {
			compiler.pre_store(((VariableAccess) getLeftOperand()).getVarIndex())
					.compile(getRightOperand());
			if (getRightOperand() instanceof AssigmentOperation) {
				compiler.visit(((AssigmentOperation) getRightOperand()).getLeftOperand());
			}
			compiler.store(((VariableAccess) getLeftOperand()).getVarIndex());
		} else if (getLeftOperand() instanceof VariableSetter) {
			compiler.newRuntimeContext();
			((VariableSetter) getLeftOperand()).compile_visit_variable(compiler);
			compiler.compile(getRightOperand()).call("set_variable_value", 4);
		} else {
			MagicScriptError.error("赋值目标应为变量", getLeftOperand().getSpan());
		}
	}
}
