package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.AutoTransfer;

public interface AutoTransferRepository extends JpaRepository<AutoTransfer, Long> {
}
