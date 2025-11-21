package com.iwhalecloud.bss.magic.magicapi.task.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.iwhalecloud.bss.magic.magicapi.core.config.MagicConfiguration;
import com.iwhalecloud.bss.magic.magicapi.core.config.WebSocketSessionManager;
import com.iwhalecloud.bss.magic.magicapi.core.logging.MagicLoggerContext;
import com.iwhalecloud.bss.magic.magicapi.core.model.DebugRequest;
import com.iwhalecloud.bss.magic.magicapi.core.model.JsonBean;
import com.iwhalecloud.bss.magic.magicapi.core.model.MagicEntity;
import com.iwhalecloud.bss.magic.magicapi.core.servlet.MagicHttpServletRequest;
import com.iwhalecloud.bss.magic.magicapi.core.web.MagicController;
import com.iwhalecloud.bss.magic.magicapi.core.web.MagicExceptionHandler;
import com.iwhalecloud.bss.magic.magicapi.utils.ScriptManager;
import com.iwhalecloud.bss.magic.script.MagicScriptDebugContext;


public class MagicTaskController extends MagicController implements MagicExceptionHandler {

	public MagicTaskController(MagicConfiguration configuration) {
		super(configuration);
	}

	@PostMapping("/task/execute")
	@ResponseBody
	public JsonBean<Object> execute(String id, MagicHttpServletRequest request){
		MagicEntity entity = MagicConfiguration.getMagicResourceService().file(id);
		notNull(entity, FILE_NOT_FOUND);
		String script = entity.getScript();
		DebugRequest debugRequest = DebugRequest.create(request);
		MagicLoggerContext.SESSION.set(debugRequest.getRequestedClientId());
		String sessionAndScriptId = debugRequest.getRequestedClientId() + debugRequest.getRequestedScriptId();
		try {
			MagicScriptDebugContext magicScriptContext = debugRequest.createMagicScriptContext(configuration.getDebugTimeout());
			WebSocketSessionManager.addMagicScriptContext(sessionAndScriptId, magicScriptContext);
			magicScriptContext.setScriptName(MagicConfiguration.getMagicResourceService().getScriptName(entity));
			return new JsonBean<>(ScriptManager.executeScript(script, magicScriptContext));
		} finally {
			WebSocketSessionManager.removeMagicScriptContext(sessionAndScriptId);
			MagicLoggerContext.SESSION.remove();
		}
	}
}
