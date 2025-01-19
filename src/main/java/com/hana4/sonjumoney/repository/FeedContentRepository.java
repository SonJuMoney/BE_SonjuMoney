package com.hana4.sonjumoney.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Feed;
import com.hana4.sonjumoney.domain.FeedContent;

@Repository
public interface FeedContentRepository extends JpaRepository<FeedContent, Long> {
	void deleteFeedContentsByFeedId(Long feedId);

	List<FeedContent> findAllByFeed(Feed feed);
}
