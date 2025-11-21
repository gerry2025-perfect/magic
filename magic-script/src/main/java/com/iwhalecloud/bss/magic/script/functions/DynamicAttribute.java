package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.exception.MagicScriptRuntimeException;

import java.beans.Transient;

public interface DynamicAttribute<T, R> {

	@Transient
	T getDynamicAttribute(String key);

	@Transient
	default R setDynamicAttribute(String key, T value) {
		throw new MagicScriptRuntimeException("不支持此赋值操作");
	}
}
