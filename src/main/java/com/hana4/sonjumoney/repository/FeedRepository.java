package com.hana4.sonjumoney.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Feed;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

	@Query(
		"""
			select f
			from Feed f
			join f.member m
			where m.family.id = :family_id
			order by f.id desc
			"""
	)
	List<Feed> findFeedsByFamilyId(@Param("family_id") Long familyId, Pageable pageable);

	@Query(
		"""
			select case when exists
				(select 1
				   from Feed f
				   join f.member m
				  where m.family.id = :family_id
				    and f.id < :last_id)
			then true else false end
			"""
	)
	Boolean hasNext(@Param("family_id") Long familyId, @Param("last_id") Long lastId);
}
