package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.AccountType;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
}
