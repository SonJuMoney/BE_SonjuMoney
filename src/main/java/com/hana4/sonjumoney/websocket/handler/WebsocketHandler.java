package com.hana4.sonjumoney.websocket.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.exception.CommonException;
import com.hana4.sonjumoney.exception.ErrorCode;
import com.hana4.sonjumoney.repository.FamilyRepository;
import com.hana4.sonjumoney.repository.MemberRepository;
import com.hana4.sonjumoney.repository.UserRepository;
import com.hana4.sonjumoney.websocket.dto.AlarmDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {
	private final ObjectMapper objectMapper;

	private final Set<WebSocketSession> sessions = new HashSet<>();

	private final Map<Long, Set<WebSocketSession>> familyAlarmSessionMap = new HashMap<>();
	private final Map<Long, WebSocketSession> memberAlarmSessionMap = new HashMap<>();
	private final FamilyRepository familyRepository;
	private final MemberRepository memberRepository;

	public void sendFamilyAlarm(AlarmDto alarmDto) {
		Long familyAlarmSessionId = alarmDto.alarmSessionId();
		Set<WebSocketSession> alarmSession = familyAlarmSessionMap.get(familyAlarmSessionId);
		for (WebSocketSession session : alarmSession) {
			try {
				session.sendMessage(new TextMessage(alarmDto.alarmType()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void sendMemberAlarm(AlarmDto alarmDto) {

	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		log.info("payload {}", payload);
		session.sendMessage(message);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session)throws Exception {
		sessions.add(session);
		UriComponents uriComponents =
			UriComponentsBuilder.fromUriString(Objects.requireNonNull(session.getUri()).toString()).build();
		log.info("session id: " + session.getId() + " session uri: " + session.getUri()+" uid: "+uriComponents.getQueryParams().getFirst("uid"));
		Long userId = (Long)session.getAttributes().get("userId");
		List<Member> members = memberRepository.findAllByUser_Id(userId);
		for (Member member : members) {
			Long memberId = member.getId();
			if (!memberAlarmSessionMap.containsKey(memberId)) {
				memberAlarmSessionMap.put(memberId, session);
			}
			Family family = familyRepository.findById(member.getFamily().getId())
				.orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DATA));
			Long familyId = family.getId();
			if (!familyAlarmSessionMap.containsKey(familyId)) {
				familyAlarmSessionMap.put(familyId, new HashSet<>());
			}
			Set<WebSocketSession> familySession = familyAlarmSessionMap.get(familyId);
			familySession.add(session);
		}
		session.sendMessage(new TextMessage(userId + " 웹소켓 연결 성공"));
	}

	private void addAlarmSession() {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		sessions.remove(session);
		familyAlarmSessionMap.values().forEach(sessions -> sessions.remove(session));
		memberAlarmSessionMap.values().remove(session);
	}
}
