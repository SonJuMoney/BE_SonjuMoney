package com.hana4.sonjumoney.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Alarm;
import com.hana4.sonjumoney.domain.enums.AlarmStatus;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

	List<Alarm> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

	@Query(
		"""
			select case when exists
				(select 1 from Alarm a where a.user.id = :user_id and a.id < :last_id)
			then true else false end
			"""
	)
	Boolean hasNext(@Param("user_id") Long userId, @Param("last_id") Long lastId);

	List<Alarm> findByUserIdAndAlarmStatus(Long userId, AlarmStatus alarmStatus);

	@Query("SELECT a FROM Alarm  a WHERE a.user.id=:userId AND a.alarmStatus=:alarmStatus ORDER BY a.createdAt DESC")
	Optional<Alarm> findLatestAlarmByUserIdAndAlarmStatus(@Param("userId") Long userId,
		@Param("alarmStatus") AlarmStatus alarmStatus);

}
