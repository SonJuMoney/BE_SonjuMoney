package com.hana4.sonjumoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.sonjumoney.domain.Invitation;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
