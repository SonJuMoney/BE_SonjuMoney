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
	List<TransactionHistory> findByAccountIdAndOpponentAccountIdOOrderByCreatedAtDesc(Long AccountId,
		Long OpponentAccountId,
		Pageable pageable);

	Boolean hasNext(@Param("account_id") Long accountId, @Param("opponent_account_id") Long opponentAccountId,
		@Param("last_id") Long lastId);

	@Query("SELECT DISTINCT DATE(th.createdAt) FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId AND th.opponentAccountId = :opponentAccountId " +
		"ORDER BY th.createdAt DESC")
	List<LocalDate> findDistinctDates(@Param("accountId") Long accountId,
		@Param("opponentAccountId") Long opponentAccountId,
		Pageable pageable);

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
