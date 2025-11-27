package com.iwhalecloud.bss.magic.magicapi.core.model;

/**
 * 接口选项信息
 *
 * @author mxd
 */
public enum Options {

	/**
	 * 包装请求参数到一个变量中
	 */
	WRAP_REQUEST_PARAMETERS("Wrap request parameters into a variable", "wrap_request_parameter"),

	/**
	 * 配置默认数据源的key
	 */
	DEFAULT_DATA_SOURCE("Configures the key for the default data source", "default_data_source"),

	/**
	 * 允许拥有该权限的访问
	 */
	PERMISSION("Allows access for users with this permission", "permission"),

	/**
	 * 允许拥有该权限的查看
	 */
	PERMISSION_VISIBLE("Allows viewing for users with this permission", "permission_visible"),

	/**
	 * 允许拥有该角色的访问
	 */
	ROLE("Allows access for users with this role", "role"),

	/**
	 * 允许拥有该角色的可查看
	 */
	ROLE_VISIBLE("Allows viewing for users with this role", "role_visible"),

	/**
	 * 该接口需要登录才允许访问
	 */
	REQUIRE_LOGIN("This API requires login to access", "require_login", "true"),

	/**
	 * 该接口不需要登录也可访问
	 */
	ANONYMOUS("This API can be accessed without login", "anonymous", "true"),

	/**
	 * 不接收未经定义的参数
	 */
	DISABLED_UNKNOWN_PARAMETER("Does not accept undefined parameters", "disabled_unknown_parameter", "true"),

	/**
	 * 禁止验证requestBody
	 */
	DISABLED_VALIDATE_REQUEST_BODY("Disables RequestBody validation", "disabled_validate_request_body", "false");

	private final String name;
	private final String value;
	private final String defaultValue;

	Options(String name, String value) {
		this(name, value, null);
	}

	Options(String name, String value, String defaultValue) {
		this.name = name;
		this.value = value;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
}
