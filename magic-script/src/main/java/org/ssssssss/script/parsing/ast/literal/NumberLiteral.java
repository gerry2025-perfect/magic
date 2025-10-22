package org.ssssssss.script.parsing.ast.literal;

import org.ssssssss.script.parsing.Span;
import org.ssssssss.script.parsing.ast.Literal;

public class NumberLiteral extends Literal {

	protected boolean neg;

	public NumberLiteral(Span span) {
		super(span);
	}

	public NumberLiteral(Span span, Object value) {
		super(span, value);
	}

	public String getText() {
		return (neg ? "-" : "") + getSpan().getText();
	}

	public void useNeg() {
		this.neg = !this.neg;
	}
}
