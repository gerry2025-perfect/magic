package com.iwhalecloud.bss.magic.script.exception;

public class DebugTimeoutException extends RuntimeException {

	public DebugTimeoutException() {
		super("debug timeout");
	}

	public DebugTimeoutException(Throwable cause) {
		super(cause);
	}
}