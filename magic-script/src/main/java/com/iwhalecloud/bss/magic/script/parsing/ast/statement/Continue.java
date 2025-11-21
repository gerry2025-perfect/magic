package com.iwhalecloud.bss.magic.script.parsing.ast.statement;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.Node;

/**
 * continue语句
 */
public class Continue extends Node {

	public Continue(Span span) {
		super(span);
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.start();
	}
}