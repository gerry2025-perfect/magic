package com.iwhalecloud.bss.magic.magicapi.servlet.jakarta;

import org.springframework.web.multipart.MultipartResolver;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletRequest;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletResponse;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicRequestContextHolder;

public class MagicJakartaRequestContextHolder implements MagicRequestContextHolder {

	private final MultipartResolver multipartResolver;

	public MagicJakartaRequestContextHolder(MultipartResolver multipartResolver) {
		this.multipartResolver = multipartResolver;
	}

	@Override
	public MagicHttpServletRequest getRequest() {
		return convert(servletRequestAttributes -> new MagicJakartaHttpServletRequest(servletRequestAttributes.getRequest(), multipartResolver));
	}

	@Override
	public MagicHttpServletResponse getResponse() {
		return convert(servletRequestAttributes -> new MagicJakartaHttpServletResponse(servletRequestAttributes.getResponse()));
	}
}
