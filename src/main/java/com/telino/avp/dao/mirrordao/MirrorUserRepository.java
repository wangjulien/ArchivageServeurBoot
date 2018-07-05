package com.telino.avp.dao.mirrordao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.telino.avp.entity.context.User;

public interface MirrorUserRepository extends JpaRepository<User, String> {

	@Modifying
	public void updatePassword(@Param("userId") String userId, @Param("encryptedPassword") String encryptedPassword);
}
