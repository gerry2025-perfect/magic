package org.ssssssss.script.parsing.ast.literal;

import org.ssssssss.script.MagicScriptError;
import org.ssssssss.script.compile.MagicScriptCompiler;
import org.ssssssss.script.parsing.Span;
import org.ssssssss.script.parsing.ast.Literal;

/**
 * short 常量
 */
public class ShortLiteral extends NumberLiteral {

	public ShortLiteral(Span literal) {
		super(literal);

	}

	@Override
	public void compile(MagicScriptCompiler context) {
		if(this.value == null){
			try {
				String text = getText();
				setValue(Short.parseShort(text.substring(0, text.length() - 1).replace("_","")));
			} catch (NumberFormatException e) {
				MagicScriptError.error("定义short变量值不合法", getSpan(), e);
			}
		}
		context.ldc(value).invoke(INVOKESTATIC, Short.class, "valueOf", Short.class, short.class);
	}
}
