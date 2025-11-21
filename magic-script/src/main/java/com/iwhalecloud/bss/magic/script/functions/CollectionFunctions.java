package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.annotation.Function;

import java.util.Iterator;

/**
 * 集合相关函数
 */
public class CollectionFunctions {

	@Function
	@Comment("区间迭代器")
	public Iterator<Integer> range(@Comment(name = "from", value = "起始编号") int from,
								   @Comment(name = "to", value = "结束编号") int to) {
		return new Iterator<Integer>() {
			int idx = from;

			@Override
			public boolean hasNext() {
				return idx <= to;
			}

			@Override
			public Integer next() {
				return idx++;
			}
		};
	}
}
