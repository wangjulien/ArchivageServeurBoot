package com.telino.avp.dao;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterUserRepository;
import com.telino.avp.dao.mirrordao.MirrorUserRepository;
import com.telino.avp.entity.context.User;

@Repository
@Transactional
public class UserDao {
	
	public static final String ENCRYPT_INIT_KEY = "MailObserver1";

	@Autowired
	private MasterUserRepository masterLoginRepository;

	@Autowired
	private MirrorUserRepository mirrorLoginRepository;

	public void saveUser(final User user) {
		user.setUserPassword(String.valueOf((user.getUserId() + user.getUserPassword() + ENCRYPT_INIT_KEY).hashCode()));

		masterLoginRepository.save(user);
		mirrorLoginRepository.save(user);
		
		// Update password with Bcrypt de Postgres
		masterLoginRepository.updatePassword(user.getUserId(), user.getUserPassword());
		mirrorLoginRepository.updatePassword(user.getUserId(), user.getUserPassword());
	}
	
	public String getToken(final String userId, final String encryptedPassword) {
		return masterLoginRepository.getToken(userId, encryptedPassword);
	}

	public User findByUserId(final String userId) {
		return masterLoginRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
	}
}
