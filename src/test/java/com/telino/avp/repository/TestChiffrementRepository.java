package com.telino.avp.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.ChiffrementDao;
import com.telino.avp.dao.EncryptionKeyDao;
import com.telino.avp.dao.mirrordao.MirrorChiffrementRepository;
import com.telino.avp.entity.archive.Chiffrement;
import com.telino.avp.entity.archive.EncryptionKey;
import com.telino.avp.service.archivage.DocumentService;
import com.telino.avp.utils.AesCipher;
import com.telino.avp.utils.AesCipherException;

/**
 * Test chargement et mise a jour Param et StorageParam
 * 
 * @author jwang
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TestChiffrementRepository {

	@Autowired
	private ChiffrementDao chiffrementDao;

	@Autowired
	private MirrorChiffrementRepository mirrorChiffrementRepository;

	@Autowired
	private EncryptionKeyDao encryptionKeyDao;

	private Chiffrement chiffrement;

	private EncryptionKey secretKey;

	@Before
	public void buildEntity() {

		// Persister
		try {
			secretKey = encryptionKeyDao.createKey(DocumentService.getCrypteAlgo());
			chiffrement = chiffrementDao.initializeAES(secretKey);

		} catch (NoSuchAlgorithmException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void initialize_AES_create_chiffrement() {
		// Check in master DS
		Chiffrement chiffrementMaster = chiffrementDao.findChiffrementByCrytId(chiffrement.getCryptId());

		assertTrue(chiffrementMaster.getEncryptionKey().getEncodedkey().length > 0);
		assertTrue(Arrays.equals(secretKey.getEncodedkey(), chiffrementMaster.getEncryptionKey().getEncodedkey()));

		// Check also in mirror DS
		Chiffrement chiffrementMirror = mirrorChiffrementRepository.findById(chiffrement.getCryptId())
				.orElseThrow(PersistenceException::new);

		assertTrue(chiffrementMirror.getEncryptionKey().getEncodedkey().length > 0);
		assertTrue(Arrays.equals(secretKey.getEncodedkey(), chiffrementMirror.getEncryptionKey().getEncodedkey()));
	}

	@Test
	public void AES_encrypt_decrypt_should_equal() {
		String stringToEncode = "Bonjour, ceci est une journée pourrie.";

		try {
			Map<String, byte[]> resultCryptage = AesCipher.encrypt(chiffrement.getEncryptionKey().getEncodedkey(),
					stringToEncode.getBytes(StandardCharsets.UTF_8));

			String stringDecoded = new String(
					AesCipher.decrypt(chiffrement.getEncryptionKey().getEncodedkey(),
							resultCryptage.get(AesCipher.IV_KEY), resultCryptage.get(AesCipher.CRYPTED_KEY)),
					StandardCharsets.UTF_8);
			
			assertTrue(stringToEncode.equals(stringDecoded));
			
		} catch (AesCipherException e) {
			fail(e.getMessage());
		}

	}
}
