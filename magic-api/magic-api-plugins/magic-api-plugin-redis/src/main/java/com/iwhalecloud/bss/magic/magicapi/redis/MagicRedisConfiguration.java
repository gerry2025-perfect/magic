package com.iwhalecloud.bss.magic.magicapi.redis;

import com.iwhalecloud.bss.magic.magicapi.core.resource.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicAPIProperties;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicPluginConfiguration;
import com.iwhalecloud.bss.magic.magicapi.core.model.Plugin;

@Configuration
public class MagicRedisConfiguration implements MagicPluginConfiguration {

	private final MagicAPIProperties properties;

	public MagicRedisConfiguration(MagicAPIProperties properties) {
		this.properties = properties;
	}

	/**
	 * 使用Redis存储
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "magic-api", name = "resource.type", havingValue = "redis")
	public Resource magicRedisResource(RedisConnectionFactory connectionFactory) {
		com.iwhalecloud.bss.magic.magicapi.core.config.Resource resource = properties.getResource();
		return new RedisResource(new StringRedisTemplate(connectionFactory), resource.getPrefix(), resource.isReadonly());
	}

	/**
	 * 注入redis模块
	 */
	@Bean
	public RedisModule redisFunctions(RedisConnectionFactory connectionFactory) {
		return new RedisModule(connectionFactory);
	}

	@Override
	public Plugin plugin() {
		return new Plugin("Redis");
	}
}
