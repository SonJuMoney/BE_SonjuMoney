package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.EventParticipant;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
}
