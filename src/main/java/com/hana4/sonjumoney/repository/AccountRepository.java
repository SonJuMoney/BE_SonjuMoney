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

	@Query(value = """
						select acc.id, acc.accountNum, acc.balance, acc.account_type_id
								from Account acc\s
						inner join
						User u
						on u.residentNum = acc.deputyResidentNum
						where u.id = :userId
						
		""", nativeQuery = true)
	Optional<List<Account>> findSavingAccountsByUserId(Long userId);
}
