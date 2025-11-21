package com.iwhalecloud.bss.magic.script.parsing.ast.statement;

import com.iwhalecloud.bss.magic.script.MagicResourceLoader;
import com.iwhalecloud.bss.magic.script.MagicScriptContext;
import com.iwhalecloud.bss.magic.script.asm.Label;
import com.iwhalecloud.bss.magic.script.compile.MagicScriptCompiler;
import com.iwhalecloud.bss.magic.script.functions.DynamicModuleImport;
import com.iwhalecloud.bss.magic.script.parsing.Span;
import com.iwhalecloud.bss.magic.script.parsing.VarIndex;
import com.iwhalecloud.bss.magic.script.parsing.ast.Node;

public class Import extends Node {

	private final VarIndex varIndex;
	private final boolean module;
	private String packageName;
	private boolean function;

	public Import(Span span, String packageName, VarIndex varIndex, boolean module) {
		super(span);
		this.packageName = packageName;
		this.varIndex = varIndex;
		this.module = module;
		if (!module && packageName.startsWith("@")) {
			function = true;
			this.packageName = packageName.substring(1);
		}
	}

	public boolean isImportPackage() {
		return packageName.endsWith(".*");
	}

	@Override
	public void compile(MagicScriptCompiler compiler) {
		if (isImportPackage()) {
			compiler.loadContext()
					.ldc(packageName.substring(0, packageName.length() - 1))
					.invoke(INVOKEVIRTUAL, MagicScriptContext.class, "addImport", void.class, String.class);
		} else {
			String methodName = "loadClass";
			if (this.module) {
				methodName = "loadModule";
			} else if (this.function) {
				methodName = "loadFunction";
			}
			compiler.pre_store(varIndex)    // 保存变量前的准备
					.loadContext()
					.ldc(packageName)    // 包名&函数名
					.invoke(INVOKESTATIC, MagicResourceLoader.class, methodName, Object.class, MagicScriptContext.class, String.class);    // 加载资源
			if (this.module) {
				// if(module instanceof DynamicModuleImport){ module = ((DynamicModuleImport)module).getDynamicModule(context); }
				Label end = new Label();
				compiler.insn(DUP)
						.typeInsn(INSTANCEOF, DynamicModuleImport.class)
						.jump(IFEQ, end)
						.typeInsn(CHECKCAST, DynamicModuleImport.class)
						.loadContext()
						.invoke(INVOKEVIRTUAL, DynamicModuleImport.class, "getDynamicModule", Object.class, MagicScriptContext.class)
						.label(end);
			}
			compiler.store(varIndex);    // 保存变量
		}
	}
}
