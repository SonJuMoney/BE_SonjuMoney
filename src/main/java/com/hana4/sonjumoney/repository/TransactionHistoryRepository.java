package com.hana4.sonjumoney.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.TransactionHistory;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
	List<TransactionHistory> findByAccountIdAndOpponentAccountId(Long AccountId, Long OpponentAccountId);
}
