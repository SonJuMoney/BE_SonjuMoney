package com.hana4.sonjumoney.repository;

import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByAuthId(String authId);

	Optional<User> findByResidentNum(String residentNum);

	List<User> findUsersByPhone(String phoneNum);
}
