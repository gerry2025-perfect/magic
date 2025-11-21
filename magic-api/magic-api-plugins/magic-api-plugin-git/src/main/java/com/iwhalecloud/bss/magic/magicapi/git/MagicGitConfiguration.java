package com.iwhalecloud.bss.magic.magicapi.git;

import com.iwhalecloud.bss.magic.magicapi.core.resource.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicAPIProperties;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicPluginConfiguration;
import com.iwhalecloud.bss.magic.magicapi.core.model.Plugin;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(MagicGitProperties.class)
public class MagicGitConfiguration implements MagicPluginConfiguration {

	private final MagicAPIProperties properties;
	private final MagicGitProperties gitProperties;

	public MagicGitConfiguration(MagicAPIProperties properties, MagicGitProperties gitProperties) {
		this.properties = properties;
		this.gitProperties = gitProperties;
	}

	/**
	 * git存储
	 * @author soriee
	 * @date 2022/2/28 19:50
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "magic-api", name = "resource.type", havingValue = "git")
	public Resource magicGitResource() throws IOException, GitAPIException {
		com.iwhalecloud.bss.magic.magicapi.core.config.Resource resourceConfig = properties.getResource();
		return GitResource.of(resourceConfig, this.gitProperties);
	}


	@Override
	public Plugin plugin() {
		return new Plugin("Git");
	}
}
