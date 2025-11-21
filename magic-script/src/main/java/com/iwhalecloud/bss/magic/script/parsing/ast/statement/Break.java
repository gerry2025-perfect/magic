package com.iwhalecloud.bss.magic.script.parsing.ast.statement;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.ast.Node;

/**
 * break 语句
 */
public class Break extends Node {

	public Break(Span span) {
		super(span);
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.end();
	}
}