package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
