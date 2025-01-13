package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
