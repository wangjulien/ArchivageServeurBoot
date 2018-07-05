package com.telino.avp.dao.masterdao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telino.avp.entity.context.User;

public interface MasterUserRepository extends JpaRepository<User, String> {

	@Query(value = "select a.userid from login a where a.userid = :userId "
			+ "and a.userpassword = crypt(:encryptedPassword, a.userpassword)", nativeQuery = true)
	public String getToken(@Param("userId") String userId, @Param("encryptedPassword") String encryptedPassword);

	@Modifying
	public void updatePassword(@Param("userId") String userId, @Param("encryptedPassword") String encryptedPassword);
}
