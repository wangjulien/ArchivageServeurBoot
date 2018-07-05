package com.telino.avp.dao;

import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterChiffrementRepository;
import com.telino.avp.dao.mirrordao.MirrorChiffrementRepository;
import com.telino.avp.entity.archive.Chiffrement;
import com.telino.avp.entity.archive.EncryptionKey;

/**
 * Persistence cle de Chiffrement, par default Algo AES
 * 
 * @author jwang
 *
 */
@Service
@Transactional
public class ChiffrementDao {

	@Autowired
	private MasterChiffrementRepository masterChiffrementRepository;

	@Autowired
	private MirrorChiffrementRepository mirrorChiffrementRepository;
	
	
	public Chiffrement findChiffrementByCrytId(final UUID cryptId) {
		return masterChiffrementRepository.findById(cryptId).orElseThrow(EntityNotFoundException::new);
	}

	
	/**
	 * Initialisation de Chiffrement par SecretKey et le persister
	 * 
	 * @param secretKey
	 * @return
	 */
	public Chiffrement initializeAES(final EncryptionKey secretKey) {

		Chiffrement chiffrement = new Chiffrement();
		chiffrement.setCryptId(UUID.randomUUID());
		chiffrement.setAlgorythm(secretKey.getAlgorythm());
		chiffrement.setEncryptionKey(secretKey);

		// Persister dans les 2 DB
		chiffrement = masterChiffrementRepository.save(chiffrement);
		mirrorChiffrementRepository.save(chiffrement);

		return chiffrement;
	}
}