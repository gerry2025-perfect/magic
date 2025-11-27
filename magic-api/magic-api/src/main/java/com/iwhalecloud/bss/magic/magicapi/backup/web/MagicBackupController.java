package com.iwhalecloud.bss.magic.magicapi.backup.web;

import com.iwhalecloud.bss.magic.magicapi.core.model.Group;
import com.iwhalecloud.bss.magic.magicapi.core.model.JsonBean;
import com.iwhalecloud.bss.magic.magicapi.core.model.MagicEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.iwhalecloud.bss.magic.magicapi.backup.model.Backup;
import com.iwhalecloud.bss.magic.magicapi.core.config.Constants;
import com.iwhalecloud.bss.magic.magicapi.core.web.MagicController;
import com.iwhalecloud.bss.magic.magicapi.core.web.MagicExceptionHandler;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicConfiguration;
import com.iwhalecloud.bss.magic.magicapi.backup.service.MagicBackupService;
import com.iwhalecloud.bss.magic.magicapi.core.service.MagicDynamicRegistry;
import com.iwhalecloud.bss.magic.magicapi.utils.JsonUtils;
import com.iwhalecloud.bss.magic.magicapi.utils.WebUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MagicBackupController extends MagicController implements MagicExceptionHandler {

	private final MagicBackupService service;

	public MagicBackupController(MagicConfiguration configuration) {
		super(configuration);
		this.service = configuration.getMagicBackupService();
	}

	@GetMapping("/backups")
	@ResponseBody
	public JsonBean<List<Backup>> backups(Long timestamp) {
		if(service == null){
			return new JsonBean<>(Collections.emptyList());
		}
		return new JsonBean<>(service.backupList(timestamp == null ? System.currentTimeMillis() : timestamp));
	}

	@GetMapping("/backup/{id}")
	@ResponseBody
	public JsonBean<List<Backup>> backups(@PathVariable("id") String id) {
		if(service == null || StringUtils.isBlank(id)){
			return new JsonBean<>(Collections.emptyList());
		}
		return new JsonBean<>(service.backupById(id));
	}

	@GetMapping("/backup/rollback")
	@ResponseBody
	public JsonBean<Boolean> rollback(String id, Long timestamp) throws IOException {
		notNull(service, BACKUP_NOT_ENABLED);
		Backup backup = service.backupInfo(id, timestamp);
		if("full".equals(id)){
			service.doBackupAll("The system automatically performed a full backup before restoring the full backup", WebUtils.currentUserName());
			configuration.getMagicAPIService().upload(new ByteArrayInputStream(backup.getContent()), Constants.UPLOAD_MODE_FULL);
			return new JsonBean<>(true);
		}
		if(backup.getType().endsWith("-group")){
			Group group = JsonUtils.readValue(backup.getContent(), Group.class);
			return new JsonBean<>(MagicConfiguration.getMagicResourceService().saveGroup(group));
		}
		MagicEntity entity = configuration.getMagicDynamicRegistries().stream()
				.map(MagicDynamicRegistry::getMagicResourceStorage)
				.filter(it -> it.folder().equals(backup.getType()))
				.map(it -> it.read(backup.getContent()))
				.findFirst()
				.orElse(null);
		if(entity != null){
			return new JsonBean<>(MagicConfiguration.getMagicResourceService().saveFile(entity));
		}
		return new JsonBean<>(false);
	}

	@GetMapping("/backup")
	@ResponseBody
	public JsonBean<String> backup(Long timestamp, String id) {
		notNull(service, BACKUP_NOT_ENABLED);
		notBlank(id, PARAMETER_INVALID);
		notNull(timestamp, PARAMETER_INVALID);
		Backup backup = service.backupInfo(id, timestamp);
		MagicEntity entity = JsonUtils.readValue(backup.getContent(), MagicEntity.class);
		return new JsonBean<>(entity == null ? null : entity.getScript());
	}

	@PostMapping("/backup/full")
	@ResponseBody
	public JsonBean<Boolean> doBackup() throws IOException {
		notNull(service, BACKUP_NOT_ENABLED);
		service.doBackupAll("Active full backup", WebUtils.currentUserName());
		return new JsonBean<>(true);
	}
}
