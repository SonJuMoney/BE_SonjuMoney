package com.hana4.sonjumoney.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Account;
import com.hana4.sonjumoney.domain.User;
import com.hana4.sonjumoney.domain.enums.AccountProduct;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByUser_IdAndAccountType_AccountProduct(Long userId, AccountProduct accountProduct);

	Optional<Account> findByUserId(Long userId);

	Long user(User user);
}
