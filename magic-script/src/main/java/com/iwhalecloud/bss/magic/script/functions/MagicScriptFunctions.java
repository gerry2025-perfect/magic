package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.annotation.Function;

import java.util.UUID;

public class MagicScriptFunctions {

	@Comment("Generate UUID string, excluding hyphens")
	@Function
	public String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Comment("Check if the object is not NULL")
	@Function
	public boolean not_null(@Comment(name = "value", value = "Target object") Object value) {
		return value != null;
	}

	@Comment("Check if an object is NULL")
	@Function
	public boolean is_null(@Comment(name = "value", value = "Target object") Object value) {
		return value == null;
	}

	@Comment("Print")
	@Function
	public void print(@Comment(name = "value", value = "Target object") Object target) {
		System.out.print(target);
	}

	@Comment("Print with a newline character")
	@Function
	public void println(@Comment(name = "value", value = "Target object") Object target) {
		System.out.println(target);
	}

	@Comment("Formatted printing")
	@Function
	public void printf(@Comment(name = "format", value = "Print format") String format, @Comment(name = "args", value = "Print parameters") Object... args) {
		System.out.printf(format, args);
	}

}
