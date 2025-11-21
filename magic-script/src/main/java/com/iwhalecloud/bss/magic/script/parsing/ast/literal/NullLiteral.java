package com.iwhalecloud.bss.magic.script.parsing.ast.literal;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.Literal;

/**
 * null 常量
 */
public class NullLiteral extends Literal {
	public NullLiteral(Span span) {
		super(span);
	}

	@Override
	public void compile(MagicScriptCompiler context) {
		context.insn(ACONST_NULL);
	}
}