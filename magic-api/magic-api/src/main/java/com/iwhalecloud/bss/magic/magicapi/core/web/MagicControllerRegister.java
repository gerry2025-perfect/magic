package com.iwhalecloud.bss.magic.magicapi.core.web;

import com.iwhalecloud.bss.magic.magicapi.core.config.MagicConfiguration;
import com.iwhalecloud.bss.magic.magicapi.utils.Mapping;

public interface MagicControllerRegister {

	void register(Mapping mapping, MagicConfiguration configuration);
}
