package com.telino.avp.service.journal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.springframework.beans.factory.annotation.Autowired;

import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.auxil.Journal;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.service.storage.AbstractStorageService;
import com.telino.avp.utils.Sha;

/**
 * @author julie.maran
 *
 */
public abstract class AbstractJournalService {

	@Autowired
	protected TamponHorodatageService tamponHorodatageService;

	@Autowired
	protected AbstractStorageService storageService;

	/**
	 * @param input
	 *            un hashmap contenant
	 *            <ul>
	 *            <li>docid</li>
	 *            <li>userid</li>
	 *            <li>attestation</li>
	 *            <li>timestamp</li>
	 *            <li>operation</li>
	 *            <li>mailid</li>
	 *            <li>docsname</li>
	 *            <li>logtype
	 *            </ul>
	 * @throws AvpExploitException
	 */
	public abstract void log(Map<String, Object> input) throws AvpExploitException;

	/**
	 * Scelle le journal dans Database Master
	 * 
	 * @throws AvpExploitException
	 */
	public void scellerJournal() throws AvpExploitException {
		// List de document sera valorise par Couche Persistence
		List<Document> listArchive = new ArrayList<>();

		traitementPreScellement(listArchive);

		// For the sack of TIMESTAMP order, We need to make Log of sealling after the Logs of "attestation d'archive"
		try {
			Thread.sleep(1); // 1 ms 
		} catch (InterruptedException e1) {
		}
		
		// Persist un nouveau Log
		Journal journal = bookLogId();
		// Recupere contenu a sceller depuis Database Master (isMirror = false)
		journal.setContenu(recupereContenu(journal.getLogId(), false));

		calculeDigest(journal);
		
		// Required a Timestamp from Timestamp Organism
		try {
			tamponHorodatageService.demanderTamponHorodatage(journal);
		} catch (Exception e) {
			throw new AvpExploitException("701", e, "Scellement du journal", null, null, "" + journal.getLogId());
		}
		
		// TODO : ??? verifyTamponHorodatage(); is needed?
		
		// Archivage d'un journal en tant que document
		Document journalXml = storageService.archive(this, journal);
		
		// Persiste le log
		logScellement(journalXml, journal);

		traitementPostScellement(listArchive, journal);
	}

	/**
	 * Verifie sur la base du timestamp enregistré si le journal a été modifié
	 * depuis son scellement
	 * 
	 * @return true si pas d'alteration, false sinon
	 * @throws AvpExploitException
	 */
	public void verifyJournal(final Journal journal, final boolean isMirror) throws AvpExploitException {
		
		// Reload the TimestampToken object from the Bytes loaded with journal
		try {
			tamponHorodatageService.initTamponHorodatage(journal);
		} catch (CMSException | TSPException | IOException e) {
			throw new AvpExploitException("518", null, "Erreur lors de valorisation de TamponHorodatage objet", null,
					null, journal.getLogId().toString());
		}
	
		// reload contenu
		journal.setContenu(recupereContenu(journal.getLogId(), isMirror));

		calculeDigest(journal);

		if (Arrays.equals(journal.getHash().getBytes(),
				journal.getTimestampToken().getTimeStampInfo().getMessageImprintDigest())) {

			// Recupere le contenu depuis l'autre DataBase
			if (!recupereContenu(journal.getLogId(), !isMirror).equals(journal.getContenu())) {
				throw new AvpExploitException("703", null, "Vérification du contenu du journal", null, null,
						"" + journal.getLogId());
			}

			try {
				// Verification de TamponHorodatage
				tamponHorodatageService.verifyTamponHorodatage(journal);

			} catch (OperatorCreationException | TSPException | CMSException e) {
				throw new AvpExploitException("519", e, "Vérification du tampon d'horodatage du journal", null, null,
						"" + journal.getLogId());
			}
		} else {
			throw new AvpExploitException("505", null,
					"Confrontation du contenu du journal et du hash du tampon d'horodatage", null, null,
					"" + journal.getLogId());
		}

		// !!! TimeStampToken vient de Horodatage ne contient pas de Time Zone
		//
		if (!journal.getHorodatage().isEqual(TamponHorodatageService
				.convertToSystemZonedDateTime(journal.getTimestampToken().getTimeStampInfo().getGenTime()))) {
			throw new AvpExploitException("504", null,
					"Confrontation de la date de scellement et de la date du tampon d'horodatage", null, null,
					"" + journal.getLogId());
		}
	}

	/**
	 * Génère le format de stockage du journal
	 * 
	 * @throws AvpExploitException
	 */
	public abstract byte[] buildStorageFormat(final Journal journal) throws AvpExploitException;

	/**
	 * Créer les données à mettre dans le journal pour signifier le scellement
	 * 
	 * @param journalXml
	 */
	protected abstract void logScellement(final Document journalXml, final Journal journal) throws AvpExploitException;

	/**
	 * Effectue le traitement nécessaire sur les informations du journal pour le
	 * scellement
	 * 
	 * @throws AvpExploitException
	 */
	protected abstract void traitementPreScellement(List<Document> listArchive) throws AvpExploitException;

	/**
	 * Effectue le traitement nécessaire une fois le scellement du journal complété
	 * 
	 * @throws AvpExploitException
	 */
	protected abstract void traitementPostScellement(final List<Document> listArchive, final Journal journal)
			throws AvpExploitException;

	
	/**
	 * TODO : Effectue le traitement suite à une erreur de scellement du journal
	 * @throws AVPExploitException 
	 */
	protected abstract void traitementPostErreur(final List<Document> attestationList) throws AvpExploitException;
	
	/**
	 * Récupère le contenu du journal à sceller
	 * 
	 * @param id
	 *            identifiant du journal ou 0 si journal en cours de création avec
	 *            toutes les lignes créées
	 * @param conn2
	 *            la base de donnée choisi pour récupérer le contenu
	 * @return
	 * @throws AvpExploitException
	 */
	protected abstract String recupereContenu(final UUID logId, final boolean isMirror) throws AvpExploitException;

	/**
	 * Réserver l'id du journal afin de ne pas locker la table log_archive par creer
	 * une occurence
	 * 
	 * @return l'id de l'entrée de scellement du journal
	 * @throws AvpExploitException
	 */
	protected abstract Journal bookLogId() throws AvpExploitException;

	/**
	 * Methode utilitaire
	 * 
	 * @param journal
	 * @throws AvpExploitException
	 */
	private void calculeDigest(final Journal journal) throws AvpExploitException {
		try {
			journal.setHash(Sha.encode(journal.getContenu(), "utf-8"));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO : AVPExploitException a gerer
			throw new AvpExploitException("1", e);
		}
	}
}
