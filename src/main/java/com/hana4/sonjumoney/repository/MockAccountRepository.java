package com.hana4.sonjumoney.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hana4.sonjumoney.domain.MockAccount;
import com.hana4.sonjumoney.dto.response.MockAccountResponse;

@Repository
public interface MockAccountRepository extends JpaRepository<MockAccount, Long> {

	@Query("SELECT m FROM MockAccount m WHERE m.holderResidentNum = :residentNum")
	List<MockAccount> findUserMockAccounts(String residentNum);

	@Query("SELECT m FROM MockAccount m WHERE m.holderResidentNum = :childResidentNum and m.deputyResidentNum = :parentResidentNum")
	List<MockAccount> findChildMockAccounts(String parentResidentNum, String childResidentNum);
}
