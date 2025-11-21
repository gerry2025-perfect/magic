package com.iwhalecloud.bss.magic.magicapi.cluster;

import com.iwhalecloud.bss.magic.magicapi.core.event.FileEvent;
import com.iwhalecloud.bss.magic.magicapi.core.event.GroupEvent;
import com.iwhalecloud.bss.magic.magicapi.core.event.MagicEvent;
import org.springframework.context.event.EventListener;
import com.iwhalecloud.bss.magic.magicapi.core.config.Constants;
import com.iwhalecloud.bss.magic.magicapi.core.model.MagicNotify;
import com.iwhalecloud.bss.magic.magicapi.core.service.MagicNotifyService;

public class MagicSynchronizationService {

	private final MagicNotifyService magicNotifyService;

	/**
	 * 当前实例ID
	 */
	private final String instanceId;

	public MagicSynchronizationService(MagicNotifyService magicNotifyService, String instanceId) {
		this.magicNotifyService = magicNotifyService;
		this.instanceId = instanceId;
	}

	@EventListener(condition = "#event.source != T(com.iwhalecloud.bss.magic.magicapi.core.config.Constants).EVENT_SOURCE_NOTIFY")
	public void onFolderEvent(GroupEvent event) {
		switch (event.getAction()) {
			case CREATE:
			case SAVE:
			case MOVE:
			case DELETE:
				magicNotifyService.sendNotify(new MagicNotify(instanceId, event.getGroup().getId(), event.getAction(), event.getType()));
				break;
		}
	}

	@EventListener(condition = "#event.source != T(com.iwhalecloud.bss.magic.magicapi.core.config.Constants).EVENT_SOURCE_NOTIFY")
	public void onFileEvent(FileEvent event) {
		if (Constants.EVENT_SOURCE_NOTIFY.equals(event.getSource())) {
			return;
		}
		switch (event.getAction()) {
			case CREATE:
			case SAVE:
			case MOVE:
			case DELETE:
				magicNotifyService.sendNotify(new MagicNotify(instanceId, event.getEntity().getId(), event.getAction(), Constants.EVENT_TYPE_FILE));
				break;
		}
	}

	@EventListener(condition = "#event.action == T(com.iwhalecloud.bss.magic.magicapi.core.event.EventAction).CLEAR && #event.source != T(com.iwhalecloud.bss.magic.magicapi.core.config.Constants).EVENT_SOURCE_NOTIFY")
	public void onClearEvent(MagicEvent event){
		magicNotifyService.sendNotify(new MagicNotify(instanceId, null, event.getAction(), null));
	}
}
