package com.iwhalecloud.bss.magic.script.parsing.ast;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;

public interface VariableSetter {

	default void compile_visit_variable(MagicScriptCompiler compiler) {
		throw new UnsupportedOperationException("Compilation not supported at this time" + this.getClass().getSimpleName());
	}
}