package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Relationship;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
}
