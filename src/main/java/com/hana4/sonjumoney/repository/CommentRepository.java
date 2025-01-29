package com.hana4.sonjumoney.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.Comment;
import com.hana4.sonjumoney.domain.Feed;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByFeed(Feed feed);

	void deleteAllByFeed(Feed feed);
}
