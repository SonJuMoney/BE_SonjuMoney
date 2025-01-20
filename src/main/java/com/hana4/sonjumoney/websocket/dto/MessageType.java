package com.hana4.sonjumoney.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
	MSG_TYPE_OFFER("offer"),
	MSG_TYPE_ANSWER("answer"),
	MSG_TYPE_ICE("ice"),
	MSG_TYPE_JOIN("join"),
	MSG_TYPE_LEAVE("leave"),
	;
	private final String message;
}
