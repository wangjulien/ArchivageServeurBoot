package com.telino.avp.service.archivage;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telino.avp.dao.ApplicationDao;
import com.telino.avp.dao.ProfileDao;
import com.telino.avp.dao.UserDao;
import com.telino.avp.dao.paramdao.SystInitPasswordDao;
import com.telino.avp.entity.context.ParRight;
import com.telino.avp.entity.context.Profile;
import com.telino.avp.entity.context.User;
import com.telino.avp.entitysyst.SystInitPassword;
import com.telino.avp.exception.AvpDaoException;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.Commande;
import com.telino.avp.protocol.AvpProtocol.ReturnCode;
import com.telino.avp.tools.ServerProc;
import com.telino.avp.utils.Sha;

@Service
public class UserProfileRightService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileRightService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private ProfileDao profileDao;

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private SystInitPasswordDao systInitPasswordDao;

	/**
	 * Verification de MdP
	 * 
	 * @param user
	 * @param password
	 * @param encryptedPassword
	 * @return
	 */
	public String getToken(final String user, final String password, String encryptedPassword) {

		if (Objects.isNull(encryptedPassword))
			encryptedPassword = String.valueOf((user + password + UserDao.ENCRYPT_INIT_KEY).hashCode());

		return userDao.getToken(user, encryptedPassword);

	}

	/**
	 * @param applicationCode
	 * @param commande
	 * @param processus
	 * @param resultat
	 * @return
	 */
	public boolean checkApplication(final Object applicationCode, final Commande commande, final Object processus,
			final Map<String, Object> resultat) {

		if (Objects.isNull(applicationCode) && Commande.LOG_EVENT != commande) {
			resultat.put("codeRetour", "5");
			resultat.put("message", "Le code application est obligatoire");
			return false;
		}

		if (Commande.LOG_EVENT == commande) {
			if (Objects.isNull(processus)) {
				resultat.put("codeRetour", "5");
				resultat.put("message", "Le code processus est obligatoire");
				return false;
			} else
				return true;
		}

		try {
			applicationDao.findApplication(applicationCode.toString());
			return true;
		} catch (Exception e) {
			resultat.put("codeRetour", "99");
			resultat.put("message", e.getMessage());
			return false;
		}
	}

	/**
	 * Recupere les Droits d'un User et les profiles d'archivage
	 * 
	 * @param userId
	 * @param resultat
	 */
	public void getRights(final String userId, final Map<String, Object> resultat) {
		User user = userDao.findByUserId(userId);

		Map<Integer, String> profils = new HashMap<>();
		Map<Integer, Map<String, Boolean>> rights = new HashMap<>();
		Map<Integer, String> documents = new HashMap<>();

		for (ParRight pr : user.getParRights()) {
			// The profiles of User
			Profile profile = pr.getProfile();
			Objects.requireNonNull(profile, user.getUserId() + " should get a Profile");
			Integer parId = profile.getParId();

			profils.put(parId, profile.getArProfile());

			// Add the rights
			if (rights.get(parId) == null)
				rights.put(parId, new HashMap<String, Boolean>());
			rights.get(parId).put("par_candeposit", pr.isParCanDeposit());
			rights.get(parId).put("par_candelay", pr.isParCanDelay());
			rights.get(parId).put("par_candestroy", pr.isParCanDestroy());
			rights.get(parId).put("par_canread", pr.isParCanRead());
			rights.get(parId).put("par_cancommunicate", pr.isParCanCommunicate());
			rights.get(parId).put("par_canrestitute", pr.isParCanRestitute());

			// Add all docTypes
			String docTypes = profile.getDocTypes().stream()
					.map(dt -> dt.getDocTypeArchivage().getDocTypeArchivage()
							+ (Objects.isNull(dt.getCategorie()) ? "" : "-" + dt.getCategorie()))
					.collect(Collectors.joining(","));

			documents.put(parId, docTypes);
		}

		resultat.put("rights", rights);
		resultat.put("documents", documents);
		resultat.put("profils", profils);
	}

	/**
	 * Recupere les ID profiles d'un User qui pourrait READ
	 * 
	 * @param userId
	 * @param resultat
	 */
	public void getUserReadProfiles(final String userId, final Map<String, Object> resultat) {

		User user = userDao.findByUserId(userId);

		// Form all the profiles ID in a linked list
		List<String> profils = user.getParRights().stream().filter(ParRight::isParCanRead)
				.map(pr -> pr.getProfile().getParId().toString()).collect(Collectors.toCollection(LinkedList::new));

		resultat.put("profiles", profils);
	}

	/**
	 * methode pour verifier le droit d'un utilisateur v-a-v un document
	 * 
	 * @param doc
	 * @param userId
	 * @param predicate
	 * @return
	 */
	public boolean canDoThePredict(final Integer parId, final String userId, final Predicate<ParRight> predicate) {

		// Get all the rights' criteria by profile
		Profile profile = profileDao.findByParId(parId);

		// Filter the rights concerning the user, and filter the right for a prediction
		Optional<ParRight> parRightOpt = profile.getParRights().stream()
				.filter(e -> e.getUser().getUserId().equals(userId)).filter(predicate).findFirst();

		// If after 2 filter, still get the right
		return parRightOpt.isPresent();
	}

	/**
	 * Enregistre en mémoire le mot de passe d'initialisation du serveur si
	 * cohérent.
	 * 
	 * @param input
	 * @param resultat
	 *            les données envoyés dans la requete à la servlet
	 */
	public void storePassword(final Map<String, Object> input, final Map<String, Object> resultat) {
		try {
			if (ServerProc.password1 == null && input.get("password1") != null) {
				String password1 = (String) input.get("password1");

				ReturnCode codeRetour = checkPasswordServer(1, password1);

				if (ReturnCode.OK == codeRetour) {
					ServerProc.password1 = password1;
				} else if (ReturnCode.INIT == codeRetour) {
					updatePasswordServer(1, password1);
					ServerProc.password1 = password1;
				} else {
					resultat.put("codeRetour", ReturnCode.KO.toString());
					resultat.put("message", "Le mot de passe ne correspond pas");
					return;
				}
			}

			if (ServerProc.password2 == null && input.get("password2") != null) {
				String password2 = (String) input.get("password2");

				ReturnCode codeRetour = checkPasswordServer(2, password2);

				if (ReturnCode.OK == codeRetour) {
					ServerProc.password2 = password2;
				} else if (ReturnCode.INIT == codeRetour) {
					updatePasswordServer(2, password2);
					ServerProc.password2 = password2;
				} else {
					resultat.put("codeRetour", ReturnCode.KO.toString());
					resultat.put("message", "Le mot de passe ne correspond pas");
					return;
				}
			}
		} catch (AvpDaoException e) {
			LOGGER.error(e.getMessage());
			resultat.put("codeRetour", "99");
			resultat.put("message", e.getMessage());
		}
	}

	/**
	 * Vérifie si le mot de passe entré correspond au hash existant en base (si
	 * l'entrée en base existe)
	 * 
	 * @param id
	 * @param password
	 * @return
	 */
	private ReturnCode checkPasswordServer(final int id, final String password) {

		try {
			Optional<SystInitPassword> initPswOpt = systInitPasswordDao.findById(id);
			if (initPswOpt.isPresent()) {
				String hash = initPswOpt.get().getHash();
				if (hash.equals(Sha.encode(password, "utf-8"))) {
					return ReturnCode.OK;
				} else {
					return ReturnCode.KO;
				}
			} else {
				return ReturnCode.INIT;
			}
		} catch (AvpDaoException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new AvpExploitException(, e, "Controler password du serveur");
		}
	}

	/**
	 * Mettre à jour la base données avec le hash du password d'initialisation du
	 * serveur
	 * 
	 * @param id
	 * @param password
	 */
	private void updatePasswordServer(final int id, final String password) {
		try {
			SystInitPassword initPsw = new SystInitPassword();
			initPsw.setPasswordId(id);
			initPsw.setHash(Sha.encode(password, "utf-8"));
			systInitPasswordDao.save(initPsw);
		} catch (AvpDaoException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new AvpExploitException(, e, "Mettre a jour password du serveur");
		}

	}

}
