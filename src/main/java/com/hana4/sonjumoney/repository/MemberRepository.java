package com.hana4.sonjumoney.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Family;
import com.hana4.sonjumoney.domain.Member;
import com.hana4.sonjumoney.domain.User;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	@Query("SELECT m FROM Member m JOIN FETCH m.user WHERE m.id IN :ids")
	List<Member> findAllWithUserByIds(@Param("ids") List<Long> ids);

	List<Member> findAllByUserId(Long userId);

	List<Member> findByFamilyId(Long familyId);

	@Query("SELECT m " +
		"FROM Member m " +
		"JOIN Family f ON m.family.id = f.id " +
		"WHERE m.family.id = :familyId AND m.user.id != :userId")
	List<Member> findFamilyExceptUser(Long userId, Long familyId);

	@Query("select m from Member m where m.family.id = :familyId and (m.memberRole = 'SON' or m.memberRole = 'DAUGHTER') ")
	List<Member> findChildren(Long familyId);

	Optional<Member> findByUser_IdAndFamily(Long userId, Family family);

	Optional<Member> findByUserIdAndFamilyId(Long userId, Long familyId);

	boolean existsByUserIdAndFamilyId(Long userId, Long familyId);

	Long user(User user);
}
