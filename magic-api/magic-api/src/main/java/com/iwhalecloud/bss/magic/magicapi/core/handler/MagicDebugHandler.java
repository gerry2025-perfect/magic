package com.iwhalecloud.bss.magic.magicapi.core.handler;

import org.apache.commons.lang3.StringUtils;
import com.iwhalecloud.bss.magic.magicapi.core.annotation.Message;
import com.iwhalecloud.bss.magic.magicapi.core.config.MessageType;
import com.iwhalecloud.bss.magic.magicapi.core.config.WebSocketSessionManager;
import com.iwhalecloud.bss.magic.magicapi.core.context.MagicConsoleSession;
import com.iwhalecloud.bss.magic.script.MagicScriptDebugContext;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * WebSocket Debug 处理器
 *
 * @author mxd
 */
public class MagicDebugHandler {


	/**
	 * 设置断点
	 * 当本机没有该Session时，通知其他机器处理
	 */
	@Message(MessageType.SET_BREAKPOINT)
	public boolean setBreakPoint(MagicConsoleSession session, String scriptId, String breakpoints) {
		MagicScriptDebugContext context = WebSocketSessionManager.findMagicScriptContext(session.getClientId() + scriptId);
		if (context != null) {
			context.setBreakpoints(Stream.of(breakpoints.split(",")).map(Integer::valueOf).collect(Collectors.toList()));
			return true;
		}
		return false;
	}

	/**
	 * 恢复断点
	 * 当本机没有该Session时，通知其他机器处理
	 */
	@Message(MessageType.RESUME_BREAKPOINT)
	public boolean resumeBreakpoint(MagicConsoleSession session, String scriptId, String stepInto, String breakpoints) {
		MagicScriptDebugContext context = WebSocketSessionManager.findMagicScriptContext(session.getClientId() + scriptId);
		if (context != null) {
			context.setStepInto("1".equals(stepInto));
			if (StringUtils.isNotBlank(breakpoints)) {
				context.setBreakpoints(Stream.of(breakpoints.split("\\|")).map(Integer::valueOf).collect(Collectors.toList()));
			} else {
				context.setBreakpoints(Collections.emptyList());
			}
			try {
				context.singal();
			} catch (InterruptedException ignored) {
			}
			return true;
		}
		return false;
	}
}
