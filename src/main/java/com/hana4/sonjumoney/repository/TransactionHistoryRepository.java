package com.hana4.sonjumoney.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.TransactionHistory;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

	@Query("SELECT CASE WHEN COUNT(th) > 0 THEN true ELSE false END " +
		"FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId AND th.opponentAccountId = :opponentAccountId " +
		"AND th.id < :lastTransactionId")
	Boolean hasNext(@Param("accountId") Long accountId,
		@Param("opponentAccountId") Long opponentAccountId,
		@Param("lastTransactionId") Long lastTransactionId);

	/*@Query("SELECT CASE WHEN COUNT(th) > 0 THEN true ELSE false END " +
		"FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId AND th.opponentAccountId = :opponentAccountId " +
		"AND th.createdAt < :lastTransactionDate")
	Boolean hasNext(@Param("accountId") Long accountId,
		@Param("opponentAccountId") Long opponentAccountId,
		@Param("lastTransactionDate") LocalDateTime lastTransactionDate);*/

	@Query("SELECT DISTINCT DATE(th.createdAt) " +
		"FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId AND th.opponentAccountId = :opponentAccountId " +
		"GROUP BY DATE(th.createdAt) " +
		"ORDER BY MAX(th.createdAt) DESC")
	List<Object[]> findDistinctDatesAsObjects(
		@Param("accountId") Long accountId,
		@Param("opponentAccountId") Long opponentAccountId,
		Pageable pageable
	);

	@Query("SELECT th FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId " +
		"AND th.opponentAccountId = :opponentAccountId " +
		"AND DATE(th.createdAt) IN :dates " +
		"ORDER BY th.createdAt DESC")
	List<TransactionHistory> findByDates(
		@Param("accountId") Long accountId,
		@Param("opponentAccountId") Long opponentAccountId,
		@Param("dates") List<LocalDate> dates
	);

}
