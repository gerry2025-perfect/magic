package com.iwhalecloud.bss.magic.magicapi.function.service;

import com.iwhalecloud.bss.magic.magicapi.function.model.FunctionInfo;
import com.iwhalecloud.bss.magic.magicapi.core.service.AbstractPathMagicResourceStorage;

public class FunctionInfoMagicResourceStorage extends AbstractPathMagicResourceStorage<FunctionInfo> {

	@Override
	public String folder() {
		return "function";
	}

	@Override
	public Class<FunctionInfo> magicClass() {
		return FunctionInfo.class;
	}

	@Override
	public String buildMappingKey(FunctionInfo info) {
		return buildMappingKey(info, magicResourceService.getGroupPath(info.getGroupId()));
	}

	@Override
	public void validate(FunctionInfo entity) {
		notBlank(entity.getPath(), FUNCTION_PATH_REQUIRED);
	}
}
