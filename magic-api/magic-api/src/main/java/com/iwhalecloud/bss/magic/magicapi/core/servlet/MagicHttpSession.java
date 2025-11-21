package com.iwhalecloud.bss.magic.magicapi.core.servlet;

public interface MagicHttpSession {

	Object getAttribute(String key);

	void setAttribute(String key, Object value);

	<T> T getSession();
}
