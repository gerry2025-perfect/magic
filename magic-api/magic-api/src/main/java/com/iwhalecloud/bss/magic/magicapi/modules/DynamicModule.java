package com.iwhalecloud.bss.magic.magicapi.modules;

import com.iwhalecloud.bss.magic.script.MagicScriptContext;

import java.beans.Transient;

public interface DynamicModule<T> {

	@Transient
	T getDynamicModule(MagicScriptContext context);
}
