package com.iwhalecloud.bss.magic.magicapi.utils;

import com.iwhalecloud.bss.magic.magicapi.core.config.Constants;
import com.iwhalecloud.bss.magic.magicapi.core.context.MagicUser;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletRequest;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicRequestContextHolder;

import java.security.Principal;
import java.util.Optional;

/**
 * Web相关工具类
 *
 * @author mxd
 */
public class WebUtils {

	public static MagicRequestContextHolder magicRequestContextHolder;

	public static String currentUserName() {
		Optional<MagicHttpServletRequest> request = Optional.ofNullable(magicRequestContextHolder.getRequest());
		return request.map(r -> (MagicUser) r.getAttribute(Constants.ATTRIBUTE_MAGIC_USER))
				.map(MagicUser::getUsername)
				.orElseGet(() -> request.map(MagicHttpServletRequest::getUserPrincipal)
						.map(Principal::getName)
						.orElse(null)
				);
	}
}
