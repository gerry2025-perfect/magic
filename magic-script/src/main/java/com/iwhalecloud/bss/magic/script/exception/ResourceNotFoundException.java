package com.iwhalecloud.bss.magic.script.exception;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String module) {
		super(module);
	}
}
