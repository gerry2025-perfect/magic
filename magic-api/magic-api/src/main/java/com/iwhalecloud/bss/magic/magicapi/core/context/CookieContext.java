package com.iwhalecloud.bss.magic.magicapi.core.context;

import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicCookie;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletRequest;

import java.util.HashMap;

/**
 * Cookie Context 用于脚本中获取cookie信息
 *
 * @author mxd
 */
public class CookieContext extends HashMap<String, String> {

	private final MagicCookie[] cookies;

	public CookieContext(MagicHttpServletRequest request) {
		 this.cookies = request.getCookies();
	}

	@Override
	public String get(Object key) {
		if (cookies != null) {
			for (MagicCookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase("" + key)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
