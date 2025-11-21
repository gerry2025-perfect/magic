package com.iwhalecloud.bss.magic.magicapi.core.exception;

import com.iwhalecloud.bss.magic.magicapi.core.model.JsonCode;

/**
 * 参数错误异常
 *
 * @author mxd
 */
public class InvalidArgumentException extends RuntimeException {

	private final transient JsonCode jsonCode;

	public InvalidArgumentException(JsonCode jsonCode) {
		super(jsonCode.getMessage());
		this.jsonCode = jsonCode;
	}

	public int getCode() {
		return jsonCode.getCode();
	}
}
