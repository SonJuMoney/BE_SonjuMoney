package com.hana4.sonjumoney.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.hana4.sonjumoney.domain.enums.InvitationStatus;
import com.hana4.sonjumoney.domain.enums.MemberRole;

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
@Getter
@Table(name = "Invitation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invitation_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inviter_id", nullable = false)
	private User inviter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invitee_id", nullable = false)
	private User invitee;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "family_id", nullable = false)
	private Family family;

	@Column(name = "member_role", nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberRole memberRole;

	@Column(name = "invitation_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private InvitationStatus invitationStatus;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public Invitation(User inviter, User invitee, Family family, MemberRole memberRole,
		InvitationStatus invitationStatus) {
		this.inviter = inviter;
		this.invitee = invitee;
		this.family = family;
		this.memberRole = memberRole;
		this.invitationStatus = invitationStatus;
	}

}
