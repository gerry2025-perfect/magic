package com.iwhalecloud.bss.magic.magicapi.component.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.iwhalecloud.bss.magic.magicapi.component.service.ComponentInfoMagicResourceStorage;
import com.iwhalecloud.bss.magic.magicapi.component.service.ComponentMagicDynamicRegistry;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicPluginConfiguration;
import com.iwhalecloud.bss.magic.magicapi.core.model.Plugin;

@Configuration
public class MagicAPIComponentConfiguration implements MagicPluginConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ComponentInfoMagicResourceStorage componentInfoMagicResourceStorage() {
		return new ComponentInfoMagicResourceStorage();
	}

	@Bean
	@ConditionalOnMissingBean
	public ComponentMagicDynamicRegistry componentMagicDynamicRegistry(ComponentInfoMagicResourceStorage componentInfoMagicResourceStorage) {
		return new ComponentMagicDynamicRegistry(componentInfoMagicResourceStorage);
	}

	@Override
	public Plugin plugin() {
		 return new Plugin("Component", "MagicComponent", "magic-component.1.0.0.iife.js");
	}

}
