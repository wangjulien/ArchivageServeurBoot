package com.telino.avp.dao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterDocumentRepository;
import com.telino.avp.dao.mirrordao.MirrorDocumentRepository;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.context.Profile;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.DbEntityProtocol.DocumentStatut;

@Repository
@Transactional
public class DocumentDao {

	@Autowired
	private MasterDocumentRepository masterDocumentRepository;

	@Autowired
	private MirrorDocumentRepository mirrorDocumentRepository;

	@Autowired
	private EntityManagerFactory masterEntityManagerFactory;

	/**
	 * Calculer l'identifiant de stockage du document et l'associer au document cet
	 * identifiant est l'empreinte du document suivi d'un numéro d'ordre si non
	 * unique
	 * 
	 * @param document
	 *            le document
	 */
	public void fillEmpreinteUnique(final Document document) {

		String empreinte = document.getEmpreinte().getEmpreinte();

		final long nombre = masterDocumentRepository.countByEmpreinteEmpreinte(empreinte);

		if (0 == nombre) {
			document.getEmpreinte().setEmpreinteUnique(empreinte);
		} else {
			document.getEmpreinte().setEmpreinteUnique(empreinte + String.valueOf(nombre));
		}
	}

	/**
	 * Stocker en base de donner les métadonnées du document
	 * 
	 * @param document
	 *            le document
	 */
	public void saveMetaDonneesDocument(final Document document) {
		if (Objects.isNull(document.getDocId()))
			document.setDocId(UUID.randomUUID());

		// Timestamp to have a chrono order
		if (Objects.isNull(document.getTimestamp()))
			document.setTimestamp(ZonedDateTime.now());

		masterDocumentRepository.save(document);

		mirrorDocumentRepository.save(document);
	}

	public void saveAll(final List<Document> documents) {
		documents.forEach(d -> {
			if (Objects.isNull(d.getDocId()))
				d.setDocId(UUID.randomUUID());
		});

		masterDocumentRepository.saveAll(documents);
		mirrorDocumentRepository.saveAll(documents);
	}

	/**
	 * Supprimer de la base de données les métadonnées liées à un document
	 * 
	 * @param document
	 *            le document
	 */
	public void deleteMetaDonneesDocument(final Document document) {

		masterDocumentRepository.delete(document);

		mirrorDocumentRepository.delete(document);
	}

	/**
	 * Générer un objet Document à partir d'un identifiant
	 * 
	 * @param docid
	 * @param isMirror
	 * @return l'objet Document associé à cet identifiant de document, si trouve
	 */
	public Document get(final UUID docid, final boolean isMirror) {

		if (isMirror)
			return mirrorDocumentRepository.findById(docid).orElseThrow(EntityNotFoundException::new);
		else
			return masterDocumentRepository.findById(docid).orElseThrow(EntityNotFoundException::new);
	}

	public List<Document> findAllByDocIdIn(final List<UUID> docIds) {
		return masterDocumentRepository.findAllById(docIds);
	}

	/**
	 * Mettre à jour le titre d'un document
	 * 
	 * @param title
	 *            le nouveau titre
	 * @param docid
	 *            l'identifiant du document à modifier
	 */
	public void setTitleForDocId(final String title, final UUID docid) {

		Document doc = masterDocumentRepository.findById(docid).orElseThrow(EntityNotFoundException::new);

		doc.setTitle(title);

		saveMetaDonneesDocument(doc);
	}

	/**
	 * Recupere une liste de documents avec leurs meta-donnees
	 * 
	 * @param docIds
	 *            : liste de identifiants des documents
	 * @param isMirror
	 * @return : une liste de DocumentBean
	 */
	public List<Document> getDocumentToCreateDto(final List<UUID> docIds, final boolean isMirror) {

			if (isMirror)
			return mirrorDocumentRepository.findAllById(docIds);
		else
			return masterDocumentRepository.findAllById(docIds);
	}

	/**
	 * Recuperer une page d'identifiant des documents à contrôler
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Document> getDocListToCheck(final int page, final int size) {
		return masterDocumentRepository.findAllByStatutOrderByTimestampDesc(DocumentStatut.ARCHIVED.getStatutCode(),
				PageRequest.of(page, size));
	}

	/**
	 * Calculer le nombre total de documents archivés
	 * 
	 * @return
	 */
	public int getTotalArchiveNum() {
		return masterDocumentRepository.countByStatut(DocumentStatut.ARCHIVED.getStatutCode());
	}

	/**
	 * Restaurer méta-données de DB maître depuis ceux de DB mirror
	 * 
	 * @param docId
	 * @throws AvpExploitException
	 */
	public void restoreTheMetaDataFrom(final UUID docId, final boolean isMirror) {

		Document docFrom = null, docTo = null;

		// try {
		if (isMirror) {
			docFrom = masterDocumentRepository.findById(docId).orElseThrow(EntityNotFoundException::new);
			docTo = mirrorDocumentRepository.findById(docId).orElseThrow(EntityNotFoundException::new);

			docTo.setTitle(docFrom.getTitle());
			docTo.setArchiveDate(docFrom.getArchiveDate());

			mirrorDocumentRepository.save(docTo);
		} else {
			docFrom = mirrorDocumentRepository.findById(docId).orElseThrow(EntityNotFoundException::new);
			docTo = masterDocumentRepository.findById(docId).orElseThrow(EntityNotFoundException::new);

			docTo.setTitle(docFrom.getTitle());
			docTo.setArchiveDate(docFrom.getArchiveDate());

			masterDocumentRepository.save(docTo);
		}
		// } catch (PersistenceException e) {
		// throw new AVPExploitException("621", e, "Restaurer meta donnée d'un document
		// ", null, docId.toString(),
		// null);
		// }
	}

	/**
	 * Restaurer hash dans Empreintes de DB maître
	 * 
	 * @param docId
	 * @param hash
	 */
	public void restoreTheHash(final UUID docId, final String hash, final boolean isMirror) {
		Document doc = null;

		if (isMirror) {
			doc = mirrorDocumentRepository.findById(docId).orElseThrow(EntityNotFoundException::new);
			doc.getEmpreinte().setEmpreinte(hash);
			mirrorDocumentRepository.save(doc);
		} else {
			doc = masterDocumentRepository.findById(docId).orElseThrow(EntityNotFoundException::new);
			doc.getEmpreinte().setEmpreinte(hash);
			masterDocumentRepository.save(doc);
		}
	}

	/**
	 * Recupere tous les DocId ou docment avec un statut "0"
	 * 
	 * @return une liste de DocId trouve
	 */
	public List<Document> getAllDocIdReadyForArchive() {

		return masterDocumentRepository.findAllDocIdByStatut(DocumentStatut.REARDY_FOR_ARCHIVE.getStatutCode());
	}

	/**
	 * Recupere tout document expire
	 * 
	 * @return
	 */
	public List<Document> getAllDocToDelete() {
		return masterDocumentRepository.getAllByArchiveEndBeforeAndLogicaldeleteIsFalse(ZonedDateTime.now());
	}

	/**
	 * Equevalant GetKeywords view
	 * 
	 * @param profiles
	 * @return
	 */
	public List<Document> findTop2ByProfileInOrderByTimestampDesc(final List<Profile> profiles) {
		return masterDocumentRepository.findTop2ByProfileInOrderByTimestampDesc(profiles);
	}

	/**
	 * Verifier si document est unique
	 * 
	 * @param md5
	 * @param domnNom
	 * @return
	 */
	public Optional<Document> findByMd5AndDomnNom(final String md5, final String domnNom) {
		return masterDocumentRepository.findByMd5AndDomnNom(md5, domnNom);
	}

	// TODO : ??? Dynamic query
	@SuppressWarnings("unchecked")
	public List<Document> getDocumentsByQuery(final String request) {
		return masterEntityManagerFactory.createEntityManager().createNativeQuery(request, Document.class)
				.getResultList();
	}

}
