package com.iwhalecloud.bss.magic.magicapi.modules.db.provider;

import com.iwhalecloud.bss.magic.magicapi.modules.db.model.Page;
import com.iwhalecloud.bss.magic.script.runtime.RuntimeContext;

/**
 * 分页对象提取接口
 *
 * @author mxd
 */
public interface PageProvider {

	/**
	 * 从请求中获取分页对象
	 *
	 * @param context 脚本上下文
	 * @return 返回分页对象
	 */
	public Page getPage(RuntimeContext context);
}
