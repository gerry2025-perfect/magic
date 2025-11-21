package com.iwhalecloud.bss.magic.script.parsing.ast.binary;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.functions.ObjectTypeConditionExtension;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.BinaryOperation;
import com.iwhalecloud.bss.magic.script.parsing.ast.Expression;

/**
 * instanceof
 */
public class InstanceofOperation extends BinaryOperation {

    public InstanceofOperation(Expression leftOperand, Span span, Expression rightOperand) {
        super(leftOperand, span, rightOperand);
    }

    @Override
    public void compile(MagicScriptCompiler compiler) {
        compiler.visit(getLeftOperand())
                .visit(getRightOperand())
                .typeInsn(CHECKCAST, Class.class)
                .lineNumber(getSpan())
                .invoke(INVOKESTATIC, ObjectTypeConditionExtension.class, "is", boolean.class, Object.class, Class.class)
                .asBoolean();
    }

}
