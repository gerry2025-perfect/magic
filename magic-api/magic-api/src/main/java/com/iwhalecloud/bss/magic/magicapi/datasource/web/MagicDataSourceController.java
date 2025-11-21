package com.iwhalecloud.bss.magic.magicapi.datasource.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.iwhalecloud.bss.magic.magicapi.core.web.MagicExceptionHandler;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicConfiguration;
import com.iwhalecloud.bss.magic.magicapi.datasource.model.DataSourceInfo;
import com.iwhalecloud.bss.magic.magicapi.core.model.JsonBean;
import com.iwhalecloud.bss.magic.magicapi.core.web.MagicController;
import com.iwhalecloud.bss.magic.magicapi.utils.JdbcUtils;

import java.sql.Connection;

public class MagicDataSourceController extends MagicController implements MagicExceptionHandler {

	public MagicDataSourceController(MagicConfiguration configuration) {
		super(configuration);
	}

	@RequestMapping("/datasource/jdbc/test")
	@ResponseBody
	public JsonBean<String> test(@RequestBody DataSourceInfo properties) {
		try {
			Connection connection = JdbcUtils.getConnection(properties.getDriverClassName(), properties.getUrl(), properties.getUsername(), properties.getPassword());
			JdbcUtils.close(connection);
		} catch (Exception e) {
			return new JsonBean<>(e.getMessage());
		}
		return new JsonBean<>("ok");
	}
}
