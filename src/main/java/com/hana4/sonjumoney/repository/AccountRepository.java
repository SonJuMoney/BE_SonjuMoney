package com.hana4.sonjumoney.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AccountProduct;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByUser_IdAndAccountType_AccountProduct(Long userId, AccountProduct accountProduct);

	Optional<Account> findByUserId(Long userId);

	Optional<Account> findByHolderResidentNum(String residentNum);

	Long user(User user);

	@Query("""
		    SELECT a
		    FROM Account a
		    WHERE a.id IN (
		        SELECT at.depositAccount.id
		        FROM AutoTransfer at
		        JOIN Account aw ON at.withdrawalAccount.id = aw.id
		        WHERE aw.user.id = :userId
		    )
		""")
	Optional<List<Account>> findSavingAccountsByUserId(Long userId);
}
