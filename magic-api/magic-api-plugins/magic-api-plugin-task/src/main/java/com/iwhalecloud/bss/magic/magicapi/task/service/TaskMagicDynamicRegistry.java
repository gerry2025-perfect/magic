package com.iwhalecloud.bss.magic.magicapi.task.service;

import com.iwhalecloud.bss.magic.magicapi.redis.RedisModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronTrigger;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicConfiguration;
import com.iwhalecloud.bss.magic.magicapi.core.event.FileEvent;
import com.iwhalecloud.bss.magic.magicapi.core.event.GroupEvent;
import com.iwhalecloud.bss.magic.magicapi.core.service.AbstractMagicDynamicRegistry;
import com.iwhalecloud.bss.magic.magicapi.core.service.MagicResourceStorage;
import com.iwhalecloud.bss.magic.magicapi.task.model.TaskInfo;
import com.iwhalecloud.bss.magic.magicapi.utils.ScriptManager;
import com.iwhalecloud.bss.magic.script.MagicScriptContext;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

public class TaskMagicDynamicRegistry extends AbstractMagicDynamicRegistry<TaskInfo> {

	private final TaskScheduler taskScheduler;

	private static final Logger logger = LoggerFactory.getLogger(TaskMagicDynamicRegistry.class);

	private final boolean showLog;

	private final RedisModule redisModule;

	public TaskMagicDynamicRegistry(MagicResourceStorage<TaskInfo> magicResourceStorage, TaskScheduler taskScheduler, boolean showLog, RedisModule redisModule) {
		super(magicResourceStorage);
		this.taskScheduler = taskScheduler;
		this.showLog = showLog;
		this.redisModule = redisModule;
	}

	@EventListener(condition = "#event.type == 'task'")
	public void onFileEvent(FileEvent event) {
		processEvent(event);
	}

	@EventListener(condition = "#event.type == 'task'")
	public void onGroupEvent(GroupEvent event) {
		processEvent(event);
	}

	@Override
	public boolean register(TaskInfo entity) {
		unregister(entity);
		return super.register(entity);
	}

	@Override
	protected boolean register(MappingNode<TaskInfo> mappingNode) {
		TaskInfo entity = mappingNode.getEntity();
		if (taskScheduler != null) {
			//通过redis加锁，确保只有一个应用会启动
			String lockValue = UUID.randomUUID().toString();
			if(redisModule.tryLock(entity.getId(), lockValue, 10*60)) {
				try {
					String scriptName = MagicConfiguration.getMagicResourceService().getScriptName(entity);
					try {
						CronTrigger trigger = new CronTrigger(entity.getCron());
						CronTask cronTask = new CronTask(() -> {
							if (entity.isEnabled()) {
								try {
									if (showLog) {
										logger.info("Scheduled Task: [{}] Started Execution", scriptName);
									}
									MagicScriptContext magicScriptContext = new MagicScriptContext();
									magicScriptContext.setScriptName(scriptName);
									ScriptManager.executeScript(entity.getScript(), magicScriptContext);
								} catch (Exception e) {
									logger.error("Scheduled Task Execution Error", e);
								} finally {
									if (showLog) {
										logger.info("Scheduled Task: [{}] Completed Execution", scriptName);
									}
								}
							}
						}, trigger);
						mappingNode.setMappingData(taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger()));
					} catch (Exception e) {
						logger.error("Scheduled Task: [{}] Registration Failed", scriptName, e);
					}
					logger.debug("Scheduled Task Registration: [{}, {}]", MagicConfiguration.getMagicResourceService().getScriptName(entity), entity.getCron());
				} finally {
					redisModule.unlock(entity.getId(), lockValue);
				}
			}
		}

		return true;
	}

	@Override
	protected void unregister(MappingNode<TaskInfo> mappingNode) {
		if (taskScheduler == null) {
			return;
		}
		TaskInfo info = mappingNode.getEntity();
		logger.debug("Scheduled Task Unregistered: [{}, {}, {}]", info.getName(), info.getPath(), info.getCron());
		ScheduledFuture<?> scheduledFuture = (ScheduledFuture<?>) mappingNode.getMappingData();
		if (scheduledFuture != null) {
			try {
				scheduledFuture.cancel(true);
			} catch (Exception e) {
				String scriptName = MagicConfiguration.getMagicResourceService().getScriptName(info);
				logger.warn("Scheduled Task: [{}] Cancellation Failed", scriptName, e);
			}
		}
	}
}
