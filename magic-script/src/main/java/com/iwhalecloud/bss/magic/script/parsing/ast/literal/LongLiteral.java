package com.iwhalecloud.bss.magic.script.parsing.ast.literal;

import com.iwhalecloud.bss.magic.script.MagicScriptError;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;

/**
 * long 常量
 */
public class LongLiteral extends NumberLiteral {

	public LongLiteral(Span literal) {
		super(literal);
	}

	public LongLiteral(Span span, Object value) {
		super(span, value);
	}

	@Override
	public void compile(MagicScriptCompiler context) {
		if(this.value == null){
			try {
				String text = getText();
				this.value = Long.parseLong(text.substring(0, text.length() - 1).replace("_", ""));
			} catch (NumberFormatException e) {
				MagicScriptError.error("Invalid long variable value definition", getSpan(), e);
			}
		}
		context.ldc(value).invoke(INVOKESTATIC, Long.class, "valueOf", Long.class, long.class);
	}
}
