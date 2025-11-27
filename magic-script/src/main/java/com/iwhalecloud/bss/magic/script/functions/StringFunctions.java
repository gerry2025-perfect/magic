package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.annotation.Function;

public class StringFunctions {

	@Function
	@Comment("Checks if a string is not empty")
	public boolean not_blank(@Comment(name = "str", value = "Target string") CharSequence cs) {
		return !is_blank(cs);
	}

	@Function
	@Comment("Checks if a string is not empty")
	public boolean is_blank(@Comment(name = "str", value = "Target string") CharSequence cs) {
		if (cs == null) {
			return true;
		}
		int strLen = cs.length();
		if (strLen == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}