package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
}
