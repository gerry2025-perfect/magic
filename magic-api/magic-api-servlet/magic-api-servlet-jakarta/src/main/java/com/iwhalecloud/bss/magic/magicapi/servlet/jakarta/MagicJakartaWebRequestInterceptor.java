package com.iwhalecloud.bss.magic.magicapi.servlet.jakarta;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicCorsFilter;
import com.iwhalecloud.bss.magic.magicapi.core.interceptor.AuthorizationInterceptor;
import com.iwhalecloud.bss.magic.magicapi.core.interceptor.MagicWebRequestInterceptor;


public class MagicJakartaWebRequestInterceptor extends MagicWebRequestInterceptor implements HandlerInterceptor {


	public MagicJakartaWebRequestInterceptor(MagicCorsFilter magicCorsFilter, AuthorizationInterceptor authorizationInterceptor) {
		super(magicCorsFilter, authorizationInterceptor);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		super.handle(handler, new MagicJakartaHttpServletRequest(request, null), new MagicJakartaHttpServletResponse(response));
		return true;
	}
}
