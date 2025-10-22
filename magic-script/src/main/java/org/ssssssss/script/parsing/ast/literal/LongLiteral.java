package org.ssssssss.script.parsing.ast.literal;

import org.ssssssss.script.MagicScriptError;
import org.ssssssss.script.compile.MagicScriptCompiler;
import org.ssssssss.script.parsing.Span;
import org.ssssssss.script.parsing.ast.Literal;

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
				MagicScriptError.error("定义long变量值不合法", getSpan(), e);
			}
		}
		context.ldc(value).invoke(INVOKESTATIC, Long.class, "valueOf", Long.class, long.class);
	}
}
