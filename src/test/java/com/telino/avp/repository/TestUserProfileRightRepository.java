package com.telino.avp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.ProfileDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.entity.context.DocType;
import com.telino.avp.entity.context.MimeType;
import com.telino.avp.entity.context.ParRight;
import com.telino.avp.entity.context.Profile;
import com.telino.avp.entity.context.Type;
import com.telino.avp.entity.context.User;

/**
 * Test chargement et mise a jour Param et StorageParam
 * 
 * @author jwang
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TestUserProfileRightRepository {

	private static final Integer PROFILE_ID = -20;
	private static final Integer DOCTYPE_ID = -10;
	private static final String USER_ID = "ADMIN_TEST";

	@Autowired
	private UserDao userDao;

	@Autowired
	private ProfileDao profileDao;

	private User user;

	private Profile profile;
	
	private String encryptedPassword;

	@Before
	public void buildEntity() {

		// DocType
		DocType docType = new DocType();
		docType.setDocTypeId(DOCTYPE_ID);
		Type type = new Type();
		type.setDocTypeArchivage("DocumentDocType");
		docType.setDocTypeArchivage(type);
		docType.setCategorie("Facture");
		
		MimeType mimeType = new MimeType();
		mimeType.setMimeTypeId(-30);
		mimeType.setContentType("application/pdf");
		docType.addMimeType(mimeType);

		// Profile
		profile = new Profile();
		profile.setParId(PROFILE_ID);
		profile.setArProfile("DocumentAr");
		profile.addDocType(docType);

		// Persist profile
		profileDao.saveProfile(profile);

		// User
		user = new User();
		user.setUserId(USER_ID);
		user.setNom("Telino");
		user.setUserPassword("Test password");
		encryptedPassword = String.valueOf((USER_ID + user.getUserPassword() + UserDao.ENCRYPT_INIT_KEY).hashCode());

		// ParRight
		ParRight parRight = new ParRight();
		parRight.setParCanRead(true);

		profile.addParRight(parRight);
		user.addParRight(parRight);

		// Persistence of User persist also parRight
		userDao.saveUser(user);		
	}

	@Test
	public void find_user_get_all_right() {
		// Check if User is persisted correctly
		User foundUser = userDao.findByUserId(user.getUserId());
		
		// Check if Profile_Right associated with User is loaded
		assertFalse(foundUser.getParRights().isEmpty());
		ParRight parRight = foundUser.getParRights().stream().findFirst().orElseThrow(EntityNotFoundException::new);
		assertTrue(parRight.isParCanRead());
		
		// Check if Profile is loaded
		Profile loadProfile = parRight.getProfile();
		assertNotNull(loadProfile);
		assertEquals(PROFILE_ID, loadProfile.getParId());
				
		// Check if DocType is loaded well
		DocType loadedDocType = loadProfile.getDocTypes().stream().findFirst()
				.orElseThrow(EntityNotFoundException::new);
		
		assertEquals("DocumentDocType", loadedDocType.getDocTypeArchivage().getDocTypeArchivage());
		
		/// Check if MimeType is loaded well
		assertEquals("application/pdf", loadedDocType.getMimeTypes().stream().findFirst().get().getContentType());
	}
	
	@Test
	public void get_token_should_found_user() {
		String tokenFound = userDao.getToken(USER_ID, encryptedPassword);
		
		assertEquals(USER_ID, tokenFound);
	}
}
