package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
