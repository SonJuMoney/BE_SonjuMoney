package com.hana4.sonjumoney.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
	@Query("SELECT F FROM Family F WHERE F.id = :familyId")
	List<Family> findAllByFamilyId(Long familyId);
}
