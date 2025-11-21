package com.iwhalecloud.bss.magic.script.parsing.ast;

import com.iwhalecloud.bss.magic.script.asm.Opcodes;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.parsing.Span;

/**
 * 节点
 */
public abstract class Node implements Opcodes {
	/**
	 * 对应的文本
	 */
	private final Span span;

	/**
	 * 在Linq中
	 */
	private boolean inLinq;

	public Node(Span span) {
		this.span = span;
	}

	public Span getSpan() {
		return span;
	}

	public boolean isInLinq() {
		return inLinq;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + span.getText();
	}

	public void visitMethod(MagicScriptCompiler compiler) {

	}

	public void compile(MagicScriptCompiler compiler) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + "不支持编译");
	}

}