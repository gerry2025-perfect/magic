package com.iwhalecloud.bss.magic.magicapi.servlet.javaee;

import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpSession;

import javax.servlet.http.HttpSession;

public class MagicJavaEEHttpSession implements MagicHttpSession {

	private final HttpSession session;

	public MagicJavaEEHttpSession(HttpSession session) {
		this.session = session;
	}

	@Override
	public Object getAttribute(String key) {
		return session.getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		session.setAttribute(key, value);
	}

	@Override
	public <T> T getSession() {
		return (T) session;
	}
}
