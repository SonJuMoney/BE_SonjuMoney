package com.hana4.sonjumoney.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByAuthId(String authId);

	@Query("SELECT U FROM User U WHERE U.id = :userId")
	Optional<User> findByUserId(Long userId);
}
