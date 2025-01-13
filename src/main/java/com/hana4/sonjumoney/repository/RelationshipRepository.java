package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.Relationship;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
}
