package com.iwhalecloud.bss.magic.script.parsing.ast.literal;

import com.iwhalecloud.bss.magic.script.MagicScriptError;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;

/**
 * double常量
 */
public class DoubleLiteral extends NumberLiteral {

	public DoubleLiteral(Span literal) {
		super(literal);
	}

	@Override
	public void compile(MagicScriptCompiler context) {
		if(this.value == null){
			try {
				setValue(Double.parseDouble(getText().replace("_", "")));
			} catch (NumberFormatException e) {
				MagicScriptError.error("定义double变量值不合法", getSpan(), e);
			}
		}
		context.ldc(value).invoke(INVOKESTATIC, Double.class, "valueOf", Double.class, double.class);
	}
}
