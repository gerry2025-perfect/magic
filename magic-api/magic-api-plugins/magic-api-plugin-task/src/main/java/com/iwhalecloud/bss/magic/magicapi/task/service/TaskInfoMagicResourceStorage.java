package com.iwhalecloud.bss.magic.magicapi.task.service;

import com.iwhalecloud.bss.magic.magicapi.core.service.AbstractPathMagicResourceStorage;
import com.iwhalecloud.bss.magic.magicapi.task.model.TaskInfo;

public class TaskInfoMagicResourceStorage extends AbstractPathMagicResourceStorage<TaskInfo> {

	@Override
	public String folder() {
		return "task";
	}

	@Override
	public Class<TaskInfo> magicClass() {
		return TaskInfo.class;
	}

	@Override
	public void validate(TaskInfo entity) {
		notBlank(entity.getCron(), CRON_ID_REQUIRED);
	}

	@Override
	public String buildMappingKey(TaskInfo info) {
		return buildMappingKey(info, magicResourceService.getGroupPath(info.getGroupId()));
	}
}
