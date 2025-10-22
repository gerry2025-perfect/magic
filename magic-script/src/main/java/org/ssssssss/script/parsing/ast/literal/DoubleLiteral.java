package org.ssssssss.script.parsing.ast.literal;

import org.ssssssss.script.MagicScriptError;
import org.ssssssss.script.compile.MagicScriptCompiler;
import org.ssssssss.script.parsing.Span;
import org.ssssssss.script.parsing.ast.Literal;

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
