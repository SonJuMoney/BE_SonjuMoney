package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.AutoTransfer;

@Repository
public interface AutoTransferRepository extends JpaRepository<AutoTransfer, Long> {
}