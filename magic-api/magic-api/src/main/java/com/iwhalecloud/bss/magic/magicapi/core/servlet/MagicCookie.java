package com.iwhalecloud.bss.magic.magicapi.core.servlet;

public interface MagicCookie {

	String getName();

	String getValue();

	<T> T getCookie();
}
