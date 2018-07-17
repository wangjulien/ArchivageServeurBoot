package com.telino.avp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.telino.avp.dao.ProfileDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.dao.paramdao.SystInitPasswordDao;
import com.telino.avp.entity.context.DocType;
import com.telino.avp.entity.context.ParRight;
import com.telino.avp.entity.context.Profile;
import com.telino.avp.entity.context.Type;
import com.telino.avp.entity.context.User;
import com.telino.avp.entitysyst.SystInitPassword;
import com.telino.avp.service.archivage.UserProfileRightService;
import com.telino.avp.tools.ServerProc;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TestUserProfileRightService {

	private static final Integer PROFILE_ID = -30;
	private static final Integer DOCTYPE_ID = -10;
	private static final String USER_ID = "ADMIN_TEST_M";

	@InjectMocks
	private UserProfileRightService userProfileRightService;

	@Mock
	private UserDao userDao;

	@Mock
	private ProfileDao profileDao;
	
	@Mock
	private SystInitPasswordDao systInitPasswordDao;

	private User user;
	
	@Before
	public void buildEntity() {

		// DocType
		DocType docType = new DocType();
		docType.setDocTypeId(DOCTYPE_ID);
		Type type = new Type();
		type.setDocTypeArchivage("DocumentDocType");
		docType.setDocTypeArchivage(type);
		docType.setCategorie("Facture");
		
		// Profile 1
		Profile profile1 = new Profile();
		profile1.setParId(PROFILE_ID);
		profile1.setArProfile("DocumentAr");
		profile1.addDocType(docType);

		// Profile 2
		Profile profile2 = new Profile();
		profile2.setParId(PROFILE_ID - 1);
		profile2.setArProfile("DocumentAr2");
		profile2.addDocType(docType);

		// User
		user = new User();
		user.setUserId(USER_ID);
		user.setNom("Telino");

		// ParRight 1 with canRead
		ParRight parRight1 = new ParRight();
		parRight1.setParCanRead(true);

		profile1.addParRight(parRight1);
		user.addParRight(parRight1);

		// ParRight 2 with canCommunicate
		ParRight parRight2 = new ParRight();
		parRight2.setParCanCommunicate(true);

		profile2.addParRight(parRight2);
		user.addParRight(parRight2);

		// Mock Given
		when(userDao.findByUserId(USER_ID)).thenReturn(user);
		when(profileDao.findByParId(PROFILE_ID)).thenReturn(profile1);
		
		// initParam
		SystInitPassword systInitPassword = new SystInitPassword();
		systInitPassword.setPasswordId(1);
		systInitPassword.setHash("");
		
		when(systInitPasswordDao.findById(1)).thenReturn(Optional.empty());
		when(systInitPasswordDao.findById(2)).thenReturn(Optional.empty());
		when(systInitPasswordDao.save(any(SystInitPassword.class))).thenReturn(systInitPassword);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void get_rights_for_user() {

		// When
		Map<String, Object> resultat = new HashMap<>();
		userProfileRightService.getRights(USER_ID, resultat);

		// Then
		assertTrue(((Map<Integer, Map<String, Boolean>>) resultat.get("rights")).get(PROFILE_ID).get("par_canread"));
		assertEquals("DocumentAr", ((Map<Integer, String>) resultat.get("profils")).get(PROFILE_ID));
		assertEquals("DocumentDocType-Facture", ((Map<Integer, String>) resultat.get("documents")).get(PROFILE_ID));

		// repo to be called once with correct param
		verify(userDao).findByUserId(USER_ID);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void get_user_read_profiles() {

		// When
		Map<String, Object> resultat = new HashMap<>();
		userProfileRightService.getUserReadProfiles(USER_ID, resultat);

		// Then
		assertEquals(1, ((List<String>) resultat.get("profiles")).size());
		assertEquals(PROFILE_ID.toString(), ((List<String>) resultat.get("profiles")).get(0));

		// repo to be called once with correct param
		verify(userDao).findByUserId(USER_ID);
	}

	@Test
	public void can_do_the_prediction() {

		// Then
		assertTrue(userProfileRightService.canDoThePredict(PROFILE_ID, USER_ID, ParRight::isParCanRead));
		assertFalse(userProfileRightService.canDoThePredict(PROFILE_ID, USER_ID, ParRight::isParCanCommunicate));

		// repo to be called twice with correct param
		verify(profileDao, times(2)).findByParId(PROFILE_ID);
	}

	@Test
	public void store_password() {
		ServerProc.password1 = null;
		ServerProc.password2 = null;
		
		// When
		Map<String, Object> input = new HashMap<>();
		input.put("password1", "TestPassword1");
		input.put("password2", "TestPassword2");
		
		Map<String, Object> result = new HashMap<>();
		userProfileRightService.storePassword(input, result);
		
		// Psw are stored
		assertEquals("TestPassword1", ServerProc.password1);
		assertEquals("TestPassword2", ServerProc.password2);
		
		// The daos are called correctly
		verify(systInitPasswordDao).findById(1);
		verify(systInitPasswordDao).findById(2);
		verify(systInitPasswordDao, times(2)).save(any(SystInitPassword.class));		
	}
}
