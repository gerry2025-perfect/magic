package com.iwhalecloud.bss.magic.script.parsing.ast.statement;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.exception.MagicExitException;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.Expression;
import com.iwhalecloud.bss.magic.script.parsing.ast.Node;
import com.iwhalecloud.bss.magic.script.runtime.ExitValue;

import java.util.List;

public class Exit extends Node {

	private final List<Expression> expressions;

	public Exit(Span span, List<Expression> expressions) {
		super(span);
		this.expressions = expressions;
	}

	@Override
	public void visitMethod(MagicScriptCompiler compiler) {
		expressions.forEach(it -> it.visitMethod(compiler));
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.typeInsn(NEW, MagicExitException.class)
				.insn(DUP)
				.typeInsn(NEW, ExitValue.class)
				.insn(DUP);
		if (expressions == null) {
			compiler.invoke(INVOKESPECIAL, ExitValue.class, "<init>", void.class);
		} else {
			compiler.newArray(expressions)
					.invoke(INVOKESPECIAL, ExitValue.class, "<init>", void.class, Object[].class);
		}
		compiler.invoke(INVOKESPECIAL, MagicExitException.class, "<init>", void.class, ExitValue.class)
				.insn(ATHROW);
	}
}
