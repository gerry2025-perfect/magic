package com.iwhalecloud.bss.magic.magicapi.servlet.jakarta;

import jakarta.servlet.http.HttpSession;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpSession;


public class MagicJakartaHttpSession implements MagicHttpSession {

	private final HttpSession session;

	public MagicJakartaHttpSession(HttpSession session) {
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
