package com.iwhalecloud.bss.magic.script.parsing.ast.literal;

import com.iwhalecloud.bss.magic.script.MagicScriptError;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;

/**
 * int常量
 */
public class IntegerLiteral extends NumberLiteral {

	public IntegerLiteral(Span literal) {
		super(literal);
	}

	public IntegerLiteral(Span span, Object value) {
		super(span, value);
	}

	@Override
	public void compile(MagicScriptCompiler context) {
		if(this.value == null){
			try {
				this.value = Integer.parseInt(getText().replace("_",""));
			} catch (NumberFormatException e) {
				MagicScriptError.error("定义int变量值不合法", getSpan(), e);
			}
		}
		context.visitInt((Integer) value)
				.invoke(INVOKESTATIC, Integer.class, "valueOf", Integer.class, int.class);
	}
}
