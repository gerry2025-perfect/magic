package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.util.regex.Pattern;

/**
 * Pattern 扩展
 */
public class PatternExtension {

	@Comment("Validate text for regular expressions")
	public boolean test(Pattern pattern, @Comment(name = "source", value = "Target string") String source) {
		return source != null && pattern.matcher(source).find();
	}
}
