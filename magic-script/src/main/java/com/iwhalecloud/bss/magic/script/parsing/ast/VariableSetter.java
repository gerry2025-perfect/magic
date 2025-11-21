package com.iwhalecloud.bss.magic.script.parsing.ast;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;

public interface VariableSetter {

	default void compile_visit_variable(MagicScriptCompiler compiler) {
		throw new UnsupportedOperationException("暂不支持编译" + this.getClass().getSimpleName());
	}
}