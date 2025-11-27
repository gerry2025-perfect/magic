package com.iwhalecloud.bss.magic.magicapi.servlet.jakarta;

import jakarta.servlet.http.Cookie;
import org.apache.commons.lang3.StringUtils;
import com.iwhalecloud.bss.magic.magicapi.core.context.RequestContext;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletResponse;
import com.iwhalecloud.bss.magic.magicapi.modules.servlet.ResponseModule;
import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.functions.ExtensionMethod;
import com.iwhalecloud.bss.magic.script.functions.ObjectConvertExtension;

import java.util.Map;

public class MagicJakartaResponseExtension implements ExtensionMethod {

	/**
	 * 添加cookie
	 */
	@Comment("Add Cookie")
	public ResponseModule addCookie(ResponseModule module, @Comment(name = "cookie", value = "Cookie Object") Cookie cookie) {
		if (cookie != null) {
			MagicHttpServletResponse response = RequestContext.getHttpServletResponse();
			if (response != null) {
				response.addCookie(new MagicJakartaCookie(cookie));
			}
		}
		return module;
	}

	/**
	 * 批量添加cookie
	 */
	@Comment("Batch Add Cookies")
	public ResponseModule addCookies(ResponseModule module, @Comment(name = "cookies", value = "Cookies") Map<String, String> cookies) {
		return addCookies(module, cookies, null);

	}

	/**
	 * 添加cookie
	 */
	@Comment("Add Cookie")
	public ResponseModule addCookie(ResponseModule module, @Comment(name = "name", value = "Cookie Name") String name,
									@Comment(name = "value", value = "Cookie Value") String value,
									@Comment(name = "options", value = "Cookie Options, such as `path`, `httpOnly`, `domain`, `maxAge`") Map<String, Object> options) {
		if (StringUtils.isNotBlank(name)) {
			Cookie cookie = new Cookie(name, value);
			if (options != null) {
				Object path = options.get("path");
				if (path != null) {
					cookie.setPath(path.toString());
				}
				Object httpOnly = options.get("httpOnly");
				if (httpOnly != null) {
					cookie.setHttpOnly("true".equalsIgnoreCase(httpOnly.toString()));
				}
				Object domain = options.get("domain");
				if (domain != null) {
					cookie.setDomain(domain.toString());
				}
				Object maxAge = options.get("maxAge");
				int age;
				if (maxAge != null && (age = ObjectConvertExtension.asInt(maxAge, Integer.MIN_VALUE)) != Integer.MIN_VALUE) {
					cookie.setMaxAge(age);
				}
			}
			addCookie(module, cookie);
		}
		return module;
	}


	/**
	 * 添加cookie
	 */
	@Comment("Add Cookie")
	public ResponseModule addCookie(ResponseModule module, @Comment(name = "name", value = "Cookie Name") String name,
									@Comment(name = "value", value = "Cookie Value") String value) {
		if (StringUtils.isNotBlank(name)) {
			addCookie(module, new Cookie(name, value));
		}
		return module;
	}

	/**
	 * 批量添加cookie
	 */
	@Comment("Batch Add Cookies")
	public ResponseModule addCookies(ResponseModule module,
									 @Comment(name = "cookies", value = "Cookies") Map<String, String> cookies,
									 @Comment(name = "options", value = "Cookie Options, such as `path`, `httpOnly`, `domain`, `maxAge`") Map<String, Object> options) {
		if (cookies != null) {
			for (Map.Entry<String, String> entry : cookies.entrySet()) {
				addCookie(module, entry.getKey(), entry.getValue(), options);
			}
		}
		return module;
	}

	@Override
	public Class<?> support() {
		return ResponseModule.class;
	}
}
