package com.telino.avp.service.storage;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.telino.avp.dao.ProfileDao;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.auxil.Journal;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.protocol.DbEntityProtocol.DocumentStatut;
import com.telino.avp.service.journal.AbstractJournalService;
import com.telino.avp.tools.FillPdfForm;

public abstract class AbstractStorageService {
	
	@Autowired
	protected ProfileDao profileDao;
	
	/**
	 * Archivage d'un document, persister meta-donnee dans les DB et fichier dans les Units Storage
	 * 
	 * @param document
	 * @return
	 * @throws AvpExploitException
	 */
	public abstract boolean archive(final Document document) throws AvpExploitException;

	/**
	 * Supresssion definitve d'un document
	 * 
	 * @param document
	 * @return
	 * @throws AvpExploitException
	 */
	public abstract boolean delete(final Document document) throws AvpExploitException;

	/**
	 * Controle de l'integralite d'un document
	 * 
	 * @param docId
	 * @param toArchive
	 * @return
	 * @throws AvpExploitException
	 */
	public abstract boolean check(final UUID docId, final boolean toArchive) throws AvpExploitException;

	/**
	 * Controle de l'integralite d'une liste de document par les Units Storage
	 * 
	 * @param docIds
	 * @param badDocsInUnit1
	 * @param badDocsInUnit2
	 * @return
	 * @throws AvpExploitException
	 */
	public abstract boolean checkFiles(final List<UUID> docIds, final Map<UUID, FileReturnError> badDocsInUnit1,
			Map<UUID, FileReturnError> badDocsInUnit2) throws AvpExploitException;

	/**
	 * Charge d'un document et ses metat-donnee depuis principal (Master)
	 * 
	 * @param docId
	 * @return
	 * @throws Exception
	 */
	public abstract Document get(final UUID docId) throws AvpExploitException;
	
	
	/**
	 * Archivage d'une attestation en tant que document
	 * 
	 * @param operation
	 * @param doc
	 * @return
	 * @throws AvpExploitException
	 */
	public Document archive(final String operation, final Document doc) throws AvpExploitException {
		byte[] content = FillPdfForm.getAttestationFilled(operation, doc);
		
		Document attestation = new Document();
		
		attestation.setArchiveDate(ZonedDateTime.now());
		attestation.setContent(content);
		attestation.setDate(ZonedDateTime.now());
		attestation.setTitle(operation + "_" + doc.getTitle().replaceAll("\\.", "_") + ".pdf");
		attestation.setContentType("application/pdf");
		attestation.setDomnNom(doc.getDomaineowner());
		attestation.setServiceverseur(doc.getServiceverseur());
		attestation.setOrganisationversante(doc.getOrganisationversante());
		attestation.setServiceverseur(doc.getServiceverseur());
		int contentLength = content.length;
		attestation.setContentLength(contentLength);
		attestation.setStatut(DocumentStatut.ATTESTATION.getStatutCode());
		attestation.setDepot(null);	// Depot ID 0
		attestation.setProfile(doc.getProfile());
		if (!archive(attestation)) {
			try {
				throw new Exception(
						"Impossible d'archiver l'attestation de " + operation + " de l'archive " + doc.getDocId());
			} catch (Exception e) {
				throw new AvpExploitException("520", e, "Archivage des attestations de " + operation, null,
						"" + doc.getDocId(), null);
			}
		} else {
			return attestation;
		}
	}

	/**
	 * Archivage d'un journal en tant que document
	 * 
	 * @param jService
	 * @param journal
	 * @return
	 * @throws AvpExploitException
	 */
	public Document archive(final AbstractJournalService jService, final Journal journal) throws AvpExploitException {
		ZonedDateTime now = ZonedDateTime.now();
		byte[] content = jService.buildStorageFormat(journal);

		Document logDoc = new Document();
		logDoc.setArchiveDate(now);
		logDoc.setContent(content);
		logDoc.setDate(now);
		logDoc.setTitle("journal_" + now.toString() + ".xml");
		logDoc.setContentType("application/xml");
		int contentLength = content.length;
		logDoc.setProfile(profileDao.findByParId(1)); 
		logDoc.setContentLength(contentLength);
		logDoc.setStatut(DocumentStatut.LOG_DOC.getStatutCode());
		logDoc.setDepot(null);
		if (!archive(logDoc)) {
			throw new AvpExploitException("520",
					new Exception(
							"Impossible d'archiver le journal " + journal.getClass() + " numéro " + journal.getLogId()),
					"Archivage des journaux", null, null, "" + journal.getLogId());
		} else {
			return logDoc;
		}
	}

}
