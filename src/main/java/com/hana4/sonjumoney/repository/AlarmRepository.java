package com.hana4.sonjumoney.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Alarm;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

	List<Alarm> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
