package com.iwhalecloud.bss.magic.script.parsing.ast.statement;

import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.VarIndex;
import com.iwhalecloud.bss.magic.script.parsing.ast.Expression;
import com.iwhalecloud.bss.magic.script.parsing.ast.VariableSetter;

public class VariableAccess extends Expression implements VariableSetter {

	private final VarIndex varIndex;

	public VariableAccess(Span name, VarIndex varIndex) {
		super(name);
		this.varIndex = varIndex;
	}

	public VarIndex getVarIndex() {
		return varIndex;
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		compiler.load(varIndex);
	}
}