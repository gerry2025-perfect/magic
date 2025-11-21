package com.iwhalecloud.bss.magic.magicapi.core.config;

import com.iwhalecloud.bss.magic.magicapi.core.model.Plugin;
import com.iwhalecloud.bss.magic.magicapi.core.web.MagicControllerRegister;

public interface MagicPluginConfiguration {

	Plugin plugin();


	/**
	 * 注册Controller
	 */
	default MagicControllerRegister controllerRegister(){
		return (mapping, configuration) -> { };
	}
}
