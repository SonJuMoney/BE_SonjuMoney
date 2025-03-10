package com.hana4.sonjumoney.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
		"AND th.transactionType = 'WITHDRAW'" +
		"AND th.id < :lastTransactionId")
	Boolean hasNextSavings(@Param("accountId") Long accountId,
		@Param("opponentAccountId") Long opponentAccountId,
		@Param("lastTransactionId") Long lastTransactionId);

	@Query("SELECT DISTINCT DATE(th.createdAt) " +
		"FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId AND th.opponentAccountId = :opponentAccountId " +
		"AND th.transactionType = 'WITHDRAW'" +
		"GROUP BY DATE(th.createdAt) " +
		"ORDER BY MAX(th.createdAt) DESC")
	List<Object[]> findDistinctSavingsDatesAsObjects(
		@Param("accountId") Long accountId,
		@Param("opponentAccountId") Long opponentAccountId,
		Pageable pageable
	);

	@Query("SELECT th FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId " +
		"AND th.opponentAccountId = :opponentAccountId " +
		"AND th.transactionType = 'WITHDRAW'" +
		"AND DATE(th.createdAt) IN :dates " +
		"ORDER BY th.createdAt DESC")
	List<TransactionHistory> findSavingsByDates(
		@Param("accountId") Long accountId,
		@Param("opponentAccountId") Long opponentAccountId,
		@Param("dates") List<LocalDate> dates
	);

	@Query(
  		"""
  		SELECT COALESCE(SUM(t.amount),0)
  		FROM TransactionHistory t
  		WHERE t.account.id = :accountId
  		AND t.transactionType = 'WITHDRAW'
  		AND t.opponentAccountId = :opponentAccountId
  		"""
	)
	Long getTotalPayment(@Param("accountId") Long accountId, @Param("opponentAccountId") Long opponentAccountId);

	@Query(
		"""
       	SELECT COALESCE(SUM(t.amount),0)
       	FROM TransactionHistory t
       	WHERE t.account.id = :accountId
       	AND t.transactionType = 'DEPOSIT'
       	AND t.createdAt BETWEEN :startOfMonth AND :endOfMonth
       	"""
	)
	Integer getCurrentMonthPayment(
		@Param("accountId") Long accountId,
		// @Param("opponentAccountId") Long opponentAccountId,
		@Param("startOfMonth") LocalDateTime startOfMonth,
		@Param("endOfMonth") LocalDateTime endOfMonth);

	@Query("SELECT DISTINCT DATE(th.createdAt) " +
		"FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId " +
		"GROUP BY DATE(th.createdAt) " +
		"ORDER BY MAX(th.createdAt) DESC")
	List<Object[]> findDistinctDatesAsObjects(
		@Param("accountId") Long accountId,
		Pageable pageable
	);

	@Query("SELECT CASE WHEN COUNT(th) > 0 THEN true ELSE false END " +
		"FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId " +
		"AND th.id < :lastTransactionId")
	Boolean hasNext(@Param("accountId") Long accountId,
		@Param("lastTransactionId") Long lastTransactionId);

	@Query("SELECT th FROM TransactionHistory th " +
		"WHERE th.account.id = :accountId " +
		"AND DATE(th.createdAt) IN :dates " +
		"ORDER BY th.createdAt DESC")
	List<TransactionHistory> findByDates(
		@Param("accountId") Long accountId,
		@Param("dates") List<LocalDate> dates
	);

}
