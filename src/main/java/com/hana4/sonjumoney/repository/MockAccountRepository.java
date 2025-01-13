package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.MockAccount;

public interface MockAccountRepository extends JpaRepository<MockAccount, Long> {
}
