package com.iwhalecloud.bss.magic.script.parsing.ast.literal;

import com.iwhalecloud.bss.magic.script.MagicScriptError;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;

/**
 * byte常量
 */
public class ByteLiteral extends NumberLiteral {

	private Byte value;

	public ByteLiteral(Span literal) {
		super(literal);
	}

	public ByteLiteral(Span span, Object value) {
		super(span, value);
		this.value = (Byte) value;
	}

	@Override
	public void compile(MagicScriptCompiler context) {
		if(this.value == null){
			try {
				String text = getText();
				this.value = Byte.parseByte(text.substring(0, text.length() - 1).replace("_",""));
			} catch (NumberFormatException e) {
				MagicScriptError.error("Invalid byte variable value definition", getSpan(), e);
			}
		}
		context.bipush(this.value).invoke(INVOKESTATIC, Byte.class, "valueOf", Byte.class, byte.class);
	}
}
