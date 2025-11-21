package com.iwhalecloud.bss.magic.magicapi.component.service;

import com.iwhalecloud.bss.magic.magicapi.component.model.ComponentInfo;
import com.iwhalecloud.bss.magic.magicapi.core.service.AbstractPathMagicResourceStorage;

public class ComponentInfoMagicResourceStorage extends AbstractPathMagicResourceStorage<ComponentInfo> {

	@Override
	public String folder() {
		return "component";
	}

	@Override
	public Class<ComponentInfo> magicClass() {
		return ComponentInfo.class;
	}

	@Override
	public void validate(ComponentInfo entity) {
	}

	@Override
	public String buildMappingKey(ComponentInfo info) {
		return buildMappingKey(info, magicResourceService.getGroupPath(info.getGroupId()));
	}

}
