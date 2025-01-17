package com.hana4.sonjumoney.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.EventParticipant;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
	@Query("SELECT ep FROM EventParticipant ep JOIN FETCH ep.event e WHERE e.family.id = :familyId AND e.startDateTime BETWEEN :startDateTime AND :endDateTime ORDER BY e.startDateTime ASC")
	List<EventParticipant> findAllParticipantsByFamilyIdAndEventDateRange(
		@Param("familyId") Long familyId,
		@Param("startDateTime") LocalDateTime startDateTime,
		@Param("endDateTime") LocalDateTime endDateTime
	);

	@Query("SELECT ep FROM EventParticipant ep JOIN FETCH ep.event e WHERE e.id =:eventId")
	List<EventParticipant> findAllParticipantsByEventId(@Param("eventId") Long eventId);

}
