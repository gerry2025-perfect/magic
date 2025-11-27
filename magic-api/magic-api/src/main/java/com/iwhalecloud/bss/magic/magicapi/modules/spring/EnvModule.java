package com.iwhalecloud.bss.magic.magicapi.modules.spring;

import org.springframework.core.env.Environment;
import com.iwhalecloud.bss.magic.magicapi.core.annotation.MagicModule;
import com.iwhalecloud.bss.magic.script.annotation.Comment;

/**
 * env模块
 *
 * @author mxd
 */
@MagicModule("env")
public class EnvModule {

	private final Environment environment;

	@Comment("Get Configuration")
	public String get(@Comment(name = "key", value = "Configuration Items") String key) {
		return environment.getProperty(key);
	}

	public EnvModule(Environment environment) {
		this.environment = environment;
	}

	@Comment("Get Configuration")
	public String get(@Comment(name = "key", value = "Configuration Items") String key,
					  @Comment(name = "defaultValue", value = "Default Values When Not Configured") String defaultValue) {
		return environment.getProperty(key, defaultValue);
	}
}
