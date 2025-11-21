package com.iwhalecloud.bss.magic.magicapi.core.interceptor;

import org.springframework.web.method.HandlerMethod;
import com.iwhalecloud.bss.magic.magicapi.core.annotation.Valid;
import com.iwhalecloud.bss.magic.magicapi.core.config.Constants;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicCorsFilter;
import com.iwhalecloud.bss.magic.magicapi.core.exception.MagicLoginException;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletRequest;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletResponse;
import com.iwhalecloud.bss.magic.magicapi.core.web.MagicController;


public abstract class MagicWebRequestInterceptor {

	private final MagicCorsFilter magicCorsFilter;

	private final AuthorizationInterceptor authorizationInterceptor;

	public MagicWebRequestInterceptor(MagicCorsFilter magicCorsFilter, AuthorizationInterceptor authorizationInterceptor) {
		this.magicCorsFilter = magicCorsFilter;
		this.authorizationInterceptor = authorizationInterceptor;
	}

	public void handle(Object handler, MagicHttpServletRequest request, MagicHttpServletResponse response) throws MagicLoginException {
		HandlerMethod handlerMethod;
		if (handler instanceof HandlerMethod) {
			handlerMethod = (HandlerMethod) handler;
			handler = handlerMethod.getBean();
			if (handler instanceof MagicController) {
				if (magicCorsFilter != null) {
					magicCorsFilter.process(request, response);
				}
				Valid valid = handlerMethod.getMethodAnnotation(Valid.class);
				boolean requiredLogin = authorizationInterceptor.requireLogin();
				boolean validRequiredLogin = (valid == null || valid.requireLogin());
				if (validRequiredLogin && requiredLogin) {
					request.setAttribute(Constants.ATTRIBUTE_MAGIC_USER, authorizationInterceptor.getUserByToken(request.getHeader(Constants.MAGIC_TOKEN_HEADER)));
				}
				((MagicController) handler).doValid(request, valid);
			}
		}
	}
}
