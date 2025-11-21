package com.iwhalecloud.bss.magic.magicapi.core.event;

import com.iwhalecloud.bss.magic.magicapi.core.config.Constants;
import com.iwhalecloud.bss.magic.magicapi.core.model.MagicNotify;

public class NotifyEvent extends MagicEvent {

	private String id;

	public NotifyEvent(MagicNotify notify) {
		super(notify.getType(), notify.getAction(), Constants.EVENT_SOURCE_NOTIFY);
		this.id = notify.getId();
	}

	public String getId() {
		return id;
	}
}
