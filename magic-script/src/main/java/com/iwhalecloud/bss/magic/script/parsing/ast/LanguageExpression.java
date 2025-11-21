package com.iwhalecloud.bss.magic.script.parsing.ast;

import com.iwhalecloud.bss.magic.script.MagicScriptContext;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.runtime.function.MagicScriptLanguageFunction;


public class LanguageExpression extends Expression {

	private final String language;

	private final String content;

	public LanguageExpression(Span language, Span content) {
		super(new Span(language, content));
		this.language = language.getText();
		this.content = content.getText();
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		// new MagicScriptLanguageFunction(language, content)
		compiler.typeInsn(NEW, MagicScriptLanguageFunction.class)
				.insn(DUP)
				.loadContext()
				.ldc(this.language)
				.ldc(this.content)
				.invoke(INVOKESPECIAL, MagicScriptLanguageFunction.class, "<init>", void.class, MagicScriptContext.class, String.class, String.class);
	}
}
