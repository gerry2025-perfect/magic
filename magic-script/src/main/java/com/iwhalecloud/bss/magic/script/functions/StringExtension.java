package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;

import java.util.regex.Pattern;

/**
 * String 扩展
 */
public class StringExtension {

	@Comment("Validate text for regular expressions")
	public boolean match(String source, @Comment(name = "pattern", value = "Regular expression") Pattern pattern) {
		return pattern.matcher(source).find();
	}

	@Comment("Regular expression replacement of a string")
	public String replace(String source, @Comment(name = "pattern", value = "Regular expression") Pattern pattern,
						  @Comment(name = "replacement", value = "Replace strings") String replacement){
		return pattern.matcher(source).replaceAll(replacement);
	}
}
