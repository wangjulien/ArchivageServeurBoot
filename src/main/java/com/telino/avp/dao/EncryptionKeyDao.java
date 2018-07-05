package com.telino.avp.dao;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterEncryptionKeyRepository;
import com.telino.avp.dao.mirrordao.MirrorEncryptionKeyRepository;
import com.telino.avp.entity.archive.EncryptionKey;

@Repository
@Transactional
public class EncryptionKeyDao {
	
	@Autowired
	private MasterEncryptionKeyRepository masterEncryptionKeyRepository;
	
	@Autowired
	private MirrorEncryptionKeyRepository mirrorEncryptionKeyRepository;

	/**
	 * Créer une clé pour un algorithme donné
	 * @param typeEncryption l'algorythme de chiffrement souhaité
	 * @return l'identifiant de la clé créée
	 * @throws NoSuchAlgorithmException
	 * 
	 */
	public EncryptionKey createKey(final String typeEncryption) throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance(typeEncryption);
		keyGen.init(128);
		SecretKey key = keyGen.generateKey();
		
		EncryptionKey newSecretKey = new EncryptionKey();
		newSecretKey.setKeyId(UUID.randomUUID());
		newSecretKey.setEncodedkey(key.getEncoded());
		newSecretKey.setAlgorythm(key.getAlgorithm());
		
		newSecretKey = masterEncryptionKeyRepository.save(newSecretKey);
		mirrorEncryptionKeyRepository.save(newSecretKey);
		
		return newSecretKey;
	}

	/**
	 * Initialise une clé de chiffrement en recupérant son contenu et l'algorithme associé
	 * @param idKey l'identifiant de la clé
	 * @return true or false si la clé à été initialisé ou non
	 * @throws Exception
	 */
//	public EncryptionKey initKey(final UUID idKey) {
//		
//		return masterEncryptionKeyRepository.findById(idKey).orElseThrow(EntityNotFoundException::new);
//	}
}
