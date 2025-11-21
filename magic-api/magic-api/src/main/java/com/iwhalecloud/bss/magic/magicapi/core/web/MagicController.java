package com.iwhalecloud.bss.magic.magicapi.core.web;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.iwhalecloud.bss.magic.magicapi.backup.service.MagicBackupService;
import com.iwhalecloud.bss.magic.magicapi.core.annotation.Valid;
import com.iwhalecloud.bss.magic.magicapi.core.config.Constants;
import com.iwhalecloud.bss.magic.magicapi.core.config.JsonCodeConstants;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicConfiguration;
import com.iwhalecloud.bss.magic.magicapi.core.context.MagicUser;
import com.iwhalecloud.bss.magic.magicapi.core.exception.InvalidArgumentException;
import com.iwhalecloud.bss.magic.magicapi.core.exception.MagicLoginException;
import com.iwhalecloud.bss.magic.magicapi.core.interceptor.Authorization;
import com.iwhalecloud.bss.magic.magicapi.core.model.Group;
import com.iwhalecloud.bss.magic.magicapi.core.model.JsonBean;
import com.iwhalecloud.bss.magic.magicapi.core.model.MagicEntity;
import com.iwhalecloud.bss.magic.magicapi.core.service.MagicAPIService;
import com.iwhalecloud.bss.magic.magicapi.core.service.MagicResourceService;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller 基类
 *
 * @author mxd
 */
public class MagicController implements JsonCodeConstants {

	final MagicAPIService magicAPIService;
	final MagicBackupService magicBackupService;
	protected MagicConfiguration configuration;

	public MagicController(MagicConfiguration configuration) {
		this.configuration = configuration;
		this.magicAPIService = configuration.getMagicAPIService();
		this.magicBackupService = configuration.getMagicBackupService();
	}

	public void doValid(MagicHttpServletRequest request, Valid valid) {
		if (valid != null) {
			if (!valid.readonly() && configuration.getWorkspace().readonly()) {
				throw new InvalidArgumentException(IS_READ_ONLY);
			}
			if (valid.authorization() != Authorization.NONE && !allowVisit(request, valid.authorization())) {
				throw new InvalidArgumentException(PERMISSION_INVALID);
			}
		}
	}

	/**
	 * 判断是否有权限访问按钮
	 */
	boolean allowVisit(MagicHttpServletRequest request, Authorization authorization) {
		if (authorization == null) {
			return true;
		}
		MagicUser magicUser = (MagicUser) request.getAttribute(Constants.ATTRIBUTE_MAGIC_USER);
		return configuration.getAuthorizationInterceptor().allowVisit(magicUser, request, authorization);
	}

	boolean allowVisit(MagicHttpServletRequest request, Authorization authorization, MagicEntity entity) {
		if (authorization == null) {
			return true;
		}
		MagicUser magicUser = (MagicUser) request.getAttribute(Constants.ATTRIBUTE_MAGIC_USER);
		return configuration.getAuthorizationInterceptor().allowVisit(magicUser, request, authorization, entity);
	}

	boolean allowVisit(MagicHttpServletRequest request, Authorization authorization, Group group) {
		if (authorization == null) {
			return true;
		}
		MagicUser magicUser = (MagicUser) request.getAttribute(Constants.ATTRIBUTE_MAGIC_USER);
		return configuration.getAuthorizationInterceptor().allowVisit(magicUser, request, authorization, group);
	}

	List<MagicEntity> entities(MagicHttpServletRequest request, Authorization authorization) {
		MagicResourceService service = configuration.getMagicResourceService();
		return service.tree()
				.values()
				.stream()
				.flatMap(it -> it.flat().stream())
				.filter(it -> !Constants.ROOT_ID.equals(it.getId()))
				.filter(it -> allowVisit(request, authorization, it))
				.flatMap(it -> service.listFiles(it.getId()).stream())
				.filter(it -> allowVisit(request, authorization, it))
				.filter(it -> Objects.nonNull(it.getScript()))
				.collect(Collectors.toList());
	}

	@ExceptionHandler(MagicLoginException.class)
	@ResponseBody
	public JsonBean<Void> invalidLogin(MagicLoginException exception) {
		return new JsonBean<>(401, exception.getMessage());
	}
}
