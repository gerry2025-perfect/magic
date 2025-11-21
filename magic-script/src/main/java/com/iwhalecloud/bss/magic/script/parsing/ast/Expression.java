package com.iwhalecloud.bss.magic.script.parsing.ast;

import com.iwhalecloud.bss.magic.script.parsing.Span;

/**
 * 表达式
 */
public abstract class Expression extends Node {
	public Expression(Span span) {
		super(span);
	}

}