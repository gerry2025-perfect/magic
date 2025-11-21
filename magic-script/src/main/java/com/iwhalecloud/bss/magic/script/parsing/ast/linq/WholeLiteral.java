package com.iwhalecloud.bss.magic.script.parsing.ast.linq;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.Literal;

public class WholeLiteral extends Literal {

	public WholeLiteral(Span span) {
		super(span);
	}

	public WholeLiteral(Span span, Object value) {
		super(span, value);
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.load2();
	}
}
