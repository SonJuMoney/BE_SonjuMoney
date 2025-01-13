package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
