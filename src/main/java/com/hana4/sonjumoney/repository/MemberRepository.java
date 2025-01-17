package com.hana4.sonjumoney.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	@Query("SELECT m FROM Member m JOIN FETCH m.user WHERE m.id IN :ids")
	List<Member> findAllWithUserByIds(@Param("ids") List<Long> ids);

	List<Member> findAllByUser_Id(Long userId);
}
