package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.Family;

public interface FamilyRepository extends JpaRepository<Family,Long> {
}
