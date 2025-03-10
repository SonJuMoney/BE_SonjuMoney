package com.hana4.sonjumoney.domain;

import com.hana4.sonjumoney.domain.enums.FeedType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Feed")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "feed_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "allowance_id")
	private Allowance allowance;

	@Column(name = "receiver_id")
	private Long receiverId;

	@Column(name = "content_exist", nullable = false)
	private boolean contentExist;

	@Column(nullable = false)
	private Integer likes;

	@Column(name = "feed_message", length = 300, nullable = false)
	private String feedMessage;

	@Column(name = "feed_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private FeedType feedType;

	@Builder
	public Feed(Member member, Allowance allowance, Long receiverId, boolean contentExist, Integer likes,
		String feedMessage, FeedType feedType) {
		this.member = member;
		this.allowance = allowance;
		this.receiverId = receiverId;
		this.contentExist = contentExist;
		this.likes = likes;
		this.feedMessage = feedMessage;
		this.feedType = feedType;
	}

	public void plusLike() {
		this.likes += 1;
	}
}
