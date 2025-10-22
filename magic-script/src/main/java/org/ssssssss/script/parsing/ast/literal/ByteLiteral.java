package org.ssssssss.script.parsing.ast.literal;

import org.ssssssss.script.MagicScriptError;
import org.ssssssss.script.compile.MagicScriptCompiler;
import org.ssssssss.script.parsing.Span;
import org.ssssssss.script.parsing.ast.Literal;

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
				MagicScriptError.error("定义byte变量值不合法", getSpan(), e);
			}
		}
		context.bipush(this.value).invoke(INVOKESTATIC, Byte.class, "valueOf", Byte.class, byte.class);
	}
}
