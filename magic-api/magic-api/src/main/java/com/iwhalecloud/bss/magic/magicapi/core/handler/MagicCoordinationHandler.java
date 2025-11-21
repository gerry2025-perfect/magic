package com.iwhalecloud.bss.magic.magicapi.core.handler;

import com.iwhalecloud.bss.magic.magicapi.core.annotation.Message;
import com.iwhalecloud.bss.magic.magicapi.core.config.MessageType;
import com.iwhalecloud.bss.magic.magicapi.core.config.WebSocketSessionManager;
import com.iwhalecloud.bss.magic.magicapi.core.config.Constants;
import com.iwhalecloud.bss.magic.magicapi.core.context.MagicConsoleSession;

public class MagicCoordinationHandler {

	@Message(MessageType.SET_FILE_ID)
	public void setFileId(MagicConsoleSession session, String fileId) {
		session.setAttribute(Constants.WEBSOCKET_ATTRIBUTE_FILE_ID, fileId);
		WebSocketSessionManager.sendToOther(session.getClientId(), MessageType.INTO_FILE_ID, session.getAttribute(Constants.WEBSOCKET_ATTRIBUTE_CLIENT_ID), fileId);
	}
}
