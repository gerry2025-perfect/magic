package com.iwhalecloud.bss.magic.magicapi.spring.boot.starter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicAPIProperties;
import com.iwhalecloud.bss.magic.magicapi.utils.PathUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * 输出服务访问地址
 *
 * @author 冰点
 * @date 2021-6-3 12:08:59
 * @since 1.2.1
 */
@Component
@ConditionalOnProperty(name = "magic-api.show-url", havingValue = "true", matchIfMissing = true)
@Order
public class ApplicationUriPrinter implements CommandLineRunner {

	@Value("${server.port:8080}")
	private int port;

	@Value("${server.servlet.context-path:}")
	private String contextPath;

	@Autowired
	private MagicAPIProperties properties;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("********************************************Current Service Related Address************************************************");
		String ip = "IP";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("Failed to Retrieve Current Service Address");
		}
		String magicWebPath = properties.getWeb();
		String schema = "http://";
		String localUrl = schema + PathUtils.replaceSlash(String.format("localhost:%s/%s/%s/", port, contextPath, Objects.toString(properties.getPrefix(), "")));
		String externUrl = schema + PathUtils.replaceSlash(String.format("%s:%s/%s/%s/", ip, port, contextPath, Objects.toString(properties.getPrefix(), "")));
		System.out.printf(
				"Service started successfully, magic-api is now built-in! Access URLs:" +
				"\n\tLocal interface address:\t\t%s" +
				"\n\tExternal interface address:\t\t%s\n"
				, localUrl
				, externUrl
		);
		if (!StringUtils.isEmpty(magicWebPath)) {
			String webPath = schema + PathUtils.replaceSlash(String.format("%s:%s/%s/%s/index.html", ip, port, contextPath, magicWebPath));
			System.out.println("\tInterface configuration platform:\t\t" + webPath);
		}
		System.out.println("\tOutput can be disabled via configuration:\tmagic-api.show-url=false");
		System.out.println("********************************************Current Service Related Address************************************************");
	}
}
