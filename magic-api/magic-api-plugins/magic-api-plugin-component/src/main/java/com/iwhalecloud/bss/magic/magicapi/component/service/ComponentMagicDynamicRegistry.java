package com.iwhalecloud.bss.magic.magicapi.component.service;

import org.springframework.context.event.EventListener;
import com.iwhalecloud.bss.magic.magicapi.component.model.ComponentInfo;
import com.iwhalecloud.bss.magic.magicapi.core.event.FileEvent;
import com.iwhalecloud.bss.magic.magicapi.core.event.GroupEvent;
import com.iwhalecloud.bss.magic.magicapi.core.service.AbstractMagicDynamicRegistry;
import com.iwhalecloud.bss.magic.magicapi.core.service.MagicResourceStorage;

public class ComponentMagicDynamicRegistry extends AbstractMagicDynamicRegistry<ComponentInfo> {

	public ComponentMagicDynamicRegistry(MagicResourceStorage<ComponentInfo> magicResourceStorage) {
		super(magicResourceStorage);
	}

	@EventListener(condition = "#event.type == 'component'")
	public void onFileEvent(FileEvent event) {
		processEvent(event);
	}

	@EventListener(condition = "#event.type == 'component'")
	public void onGroupEvent(GroupEvent event) {
		processEvent(event);
	}

}
