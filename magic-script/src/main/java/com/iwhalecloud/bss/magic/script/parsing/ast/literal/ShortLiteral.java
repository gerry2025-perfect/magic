package com.iwhalecloud.bss.magic.script.parsing.ast.literal;

import com.iwhalecloud.bss.magic.script.MagicScriptError;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;

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
