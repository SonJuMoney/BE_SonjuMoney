package com.hana4.sonjumoney.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Relationship;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
	List<Relationship> findAllByParent_Id(Long userId);

	Relationship findByChildId(Long childId);

	@Query("select r from Relationship r where r.parent.id = :parentId and r.child.id = :childId")
	Optional<Relationship> findRelationship(Long parentId, Long childId);

	@Query("""
		SELECT r
		  FROM Relationship r
		 WHERE r.parent.id = :user_id
		    OR r.parent.id = (SELECT r2.parent.id FROM Relationship r2 WHERE r2.child.id = :user_id)
		""")
	List<Relationship> findAllByUserId(@Param(value = "user_id") Long userId);
}
