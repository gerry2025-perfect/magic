package com.iwhalecloud.bss.magic.magicapi.spring.boot.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicAPIProperties;
import com.iwhalecloud.bss.magic.magicapi.core.service.impl.ApiInfoMagicResourceStorage;
import com.iwhalecloud.bss.magic.magicapi.core.service.impl.RequestMagicDynamicRegistry;
import com.iwhalecloud.bss.magic.magicapi.datasource.model.MagicDynamicDataSource;
import com.iwhalecloud.bss.magic.magicapi.datasource.service.DataSourceInfoMagicResourceStorage;
import com.iwhalecloud.bss.magic.magicapi.datasource.service.DataSourceMagicDynamicRegistry;
import com.iwhalecloud.bss.magic.magicapi.function.service.FunctionInfoMagicResourceStorage;
import com.iwhalecloud.bss.magic.magicapi.function.service.FunctionMagicDynamicRegistry;
import com.iwhalecloud.bss.magic.magicapi.utils.Mapping;

@Configuration
@AutoConfigureAfter(MagicModuleConfiguration.class)
public class MagicDynamicRegistryConfiguration {


	private final MagicAPIProperties properties;

	@Autowired
	@Lazy
	private RequestMappingHandlerMapping requestMappingHandlerMapping;


	public MagicDynamicRegistryConfiguration(MagicAPIProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	public ApiInfoMagicResourceStorage apiInfoMagicResourceStorage() {
		return new ApiInfoMagicResourceStorage(properties.getPrefix());
	}

	@Bean
	@ConditionalOnMissingBean
	public RequestMagicDynamicRegistry magicRequestMagicDynamicRegistry(ApiInfoMagicResourceStorage apiInfoMagicResourceStorage) throws NoSuchMethodException {
		return new RequestMagicDynamicRegistry(apiInfoMagicResourceStorage, Mapping.create(requestMappingHandlerMapping, properties.getWeb()), properties.isAllowOverride(), properties.getPrefix());
	}

	@Bean
	@ConditionalOnMissingBean
	public FunctionInfoMagicResourceStorage functionInfoMagicResourceStorage() {
		return new FunctionInfoMagicResourceStorage();
	}

	@Bean
	@ConditionalOnMissingBean
	public FunctionMagicDynamicRegistry functionMagicDynamicRegistry(FunctionInfoMagicResourceStorage functionInfoMagicResourceStorage) {
		return new FunctionMagicDynamicRegistry(functionInfoMagicResourceStorage);
	}

	@Bean
	@ConditionalOnMissingBean
	public DataSourceInfoMagicResourceStorage dataSourceInfoMagicResourceStorage() {
		return new DataSourceInfoMagicResourceStorage();
	}

	@Bean
	@ConditionalOnMissingBean
	public DataSourceMagicDynamicRegistry dataSourceMagicDynamicRegistry(DataSourceInfoMagicResourceStorage dataSourceInfoMagicResourceStorage, MagicDynamicDataSource magicDynamicDataSource) {
		return new DataSourceMagicDynamicRegistry(dataSourceInfoMagicResourceStorage, magicDynamicDataSource);
	}

}
