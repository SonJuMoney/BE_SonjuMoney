package com.hana4.sonjumoney.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family,Long> {
}
