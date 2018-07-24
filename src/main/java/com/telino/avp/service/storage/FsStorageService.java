package com.telino.avp.service.storage;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telino.avp.dao.ChiffrementDao;
import com.telino.avp.dao.DocumentDao;
import com.telino.avp.dao.EncryptionKeyDao;
import com.telino.avp.dao.LogArchiveDao;
import com.telino.avp.dao.ParamDao;
import com.telino.avp.dto.DocumentDto;
import com.telino.avp.entity.archive.Chiffrement;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.entity.archive.Empreinte;
import com.telino.avp.entity.archive.EncryptionKey;
import com.telino.avp.entity.param.Param;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.AvpProtocol.FileReturnError;
import com.telino.avp.service.SwitchDataSourceService;
import com.telino.avp.service.archivage.DocumentService;
import com.telino.avp.tools.ServerProc;
import com.telino.avp.utils.AesCipherException;

@Service
public class FsStorageService extends AbstractStorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FsStorageService.class);

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private LogArchiveDao logArchiveDao;

	@Autowired
	private EncryptionKeyDao encryptionKeyDao;

	@Autowired
	private ChiffrementDao chiffrementDao;

	@Autowired
	private ParamDao paramDao;

	@Autowired
	private DocumentService documentService;

	@Resource
	private FSProc fsprocMaster;

	@Resource
	private FSProc fsprocMirror;

	/**
	 * Initialisation de 2 unites de stockage, s'il n'y a pas cree Initialisation de
	 * vecteur de Secret Key, s'il n'y a pas
	 * 
	 * @param appParam
	 * 
	 * @throws AvpExploitException
	 */
	public void initFsStorageService(final Param appParam) throws AvpExploitException {

		Objects.requireNonNull(appParam.getMasterStorageParam(),
				"Master FS parametre n'est pas bien configure dans 'param' et 'paramstorage'");

		Objects.requireNonNull(appParam.getMirrorStorageParam(),
				"Mirror FS parametre n'est pas bien configure dans 'param' et 'paramstorage'");

		//
		// Initialization des unite de stockage, ss
		// - idStorage est vide et Hostname renseigne
		//

		try {
			// Master storage unit
			fsprocMaster.init(appParam.getMasterStorageParam());
			// Mirror storage unit
			fsprocMirror.init(appParam.getMirrorStorageParam());

			// Update idStorage parameters
			paramDao.saveParam(appParam);
		} catch (Exception e) {
			throw new AvpExploitException("509", e, "Initialisation des connections avec les unités de stockage", null,
					null, null);
		}

		// TODO : why check passwords here ?
		if (ServerProc.password1 != null && ServerProc.password2 != null) {

			// Lire les parametres de appli Param
			if (appParam.isCryptage()) {
				// Si pas de algo de cryptage, en genere un
				if (Objects.isNull(appParam.getCryptageid())) {

					try {
						EncryptionKey secretKey = encryptionKeyDao.createKey(DocumentService.getCrypteAlgo());
						Chiffrement chiffrement = chiffrementDao.initializeAES(secretKey);

						// Mettre a jour Crptage ID dans Param
						appParam.setCryptageid(chiffrement.getCryptId());
						paramDao.saveParam(appParam);

					} catch (Exception e) {
						throw new AvpExploitException("605", e, "Initialisation du module de chiffrement", null, null,
								null);
					}
				}
			}
		}
		// else {
		// LOGGER.error("Les deux Passwords sont pas valorises, appli est bloque!");
		// throw new AVPExploitException("509", new Throwable(),
		// "Initialisation des connections avec les unités de stockage", null, null,
		// null);
		// }
	}

	@Override
	public boolean archive(final Document document) {
		// Valoriser les empreintes
		try {
			if (Objects.isNull(document.getEmpreinte()))
				document.setEmpreinte(new Empreinte());

			document.getEmpreinte().setEmpreinteInterne(documentService.computeTelinoPrint(document));
			document.getEmpreinte().setEmpreinte(documentService.computePrint(document));
		} catch (AvpExploitException e) {
			LOGGER.error("Erreur lors de hachage du document : " + document.getTitle());
			return false;
		}

		Param appParam = SwitchDataSourceService.CONTEXT_APP_PARAM.get();

		if (appParam.isCryptage()) {

			Chiffrement chiffrement = chiffrementDao.findChiffrementByCrytId(appParam.getCryptageid());

			try {
				// Chiffrement
				Map<String, byte[]> resultCrypt = documentService.encrypt(document.getContent(),
						chiffrement.getEncryptionKey());

				document.setCryptage(true);
				document.setContent(resultCrypt.get("crypted"));
				document.setCryptageIv(resultCrypt.get("iv"));
				document.setCryptageAlgo(chiffrement.getAlgorythm());
				document.setChiffrement(chiffrement);

			} catch (AesCipherException e) {
				LOGGER.error("Erreur lors de chiffrer document : " + document.getTitle());
				return false;
			}
		}

		documentDao.fillEmpreinteUnique(document);

		// TODO : commet archiveFS() toujour return true, il faut bien gerer l'EXCEPTION

		try {
			// Si archivage mirror echoue, on s'arrete
			archiveFS(document, fsprocMirror);
		} catch (Exception e) {
			return false;
		}
		// Si archivage master echoue, on supprime mirror et s'arrete
		try {
			archiveFS(document, fsprocMaster);
		} catch (Exception e) {
			try {
				fsprocMirror.deleteFile(document.getEmpreinte().getEmpreinteUnique());
			} catch (AvpExploitException e1) {
				LOGGER.error("impossible de supprimer les documents deja enregistrés pour la raison suivante : "
						+ e1.getMessage());
			}
			return false;
		}

		// Persister document
		documentDao.saveMetaDonneesDocument(document);

		return true;
	}

	@Override
	public boolean delete(final Document document) throws AvpExploitException {

		//
		// TODO : fsproc.deleteFile() toujours return TRUE, il faut gerer
		// l'AVPExploitException
		//

		// Lancer suppression de fichier du document dans unite mirror
		fsprocMirror.deleteFile(document.getEmpreinte().getEmpreinteUnique());

		// Lancer suppression de fichier du document dans unite mirror
		fsprocMaster.deleteFile(document.getEmpreinte().getEmpreinteUnique());

		try {
			documentDao.deleteMetaDonneesDocument(document);
		} catch (PersistenceException e) {
			LOGGER.error("impossible de supprimer les meta-donnee pour la raison suivante : " + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean check(final UUID docId, final boolean toArchive) throws AvpExploitException {

		//
		// Recuperer object Document en memoire avec contenue
		//
		Document documentMaster;
		Document documentMirror;
		try {
			documentMaster = get(docId, fsprocMaster, false);
		} catch (Exception e) {
			throw new AvpExploitException("508", e, "Récupération des données de l'archive sur le stockage principal",
					"Contrôle d'intégrité d'archive", docId.toString(), null);
		}

		try {
			documentMirror = get(docId, fsprocMirror, true);
		} catch (Exception e) {
			throw new AvpExploitException("508", e, "Récupération des données de l'archive sur le stockage secondaire",
					"Contrôle d'intégrité d'une archive", docId.toString(), null);
		}

		//
		// Valoriser les empreintes a controler
		//
		String empreinteMasterCalculated = null;
		String empreinteMirrorCalculated = null;

		String empreinteMasterToCheck = null;

		if (toArchive) {
			// On controle les empreintes internes (Telino)

			empreinteMasterCalculated = documentService.computeTelinoPrint(documentMaster);
			empreinteMirrorCalculated = documentService.computeTelinoPrint(documentMirror);

			empreinteMasterToCheck = documentMaster.getEmpreinte().getEmpreinteInterne();

		} else {
			// On controle les empreintes simple

			empreinteMasterCalculated = documentService.computePrint(documentMaster);
			empreinteMirrorCalculated = documentService.computePrint(documentMirror);

			empreinteMasterToCheck = documentMaster.getEmpreinte().getEmpreinte();

			// Le cas deja archive, on controle le Hash dans LogArchive
			// QUE pour le MASTER ???
			if (!empreinteMasterCalculated.equals(logArchiveDao.findHashForDocId(documentMaster.getDocId(), false))) {
				// a modifier
				throw new AvpExploitException("521", null,
						"Confrontation avec l'empreinte scellée dans le journal des archives lors du dépôt",
						"Contrôle d'intégrité d'une archive", docId.toString(), null);
			}
		}

		//
		// Controler les empreintes
		//

		// 1. Empreinte calcule du master equale a empreiente enregistre

		if (!empreinteMasterCalculated.equals(empreinteMasterToCheck)) {
			if (toArchive)
				throw new AvpExploitException("503", null,
						"Confrontation de l'archive principale avec son empreinte ADELIS",
						"Contrôle d'intégrité d'une archive", docId.toString(), null);
			else
				throw new AvpExploitException("503", null, "Confrontation de l'archive principale avec son empreinte",
						"Contrôle d'intégrité d'une archive", docId.toString(), null);
		}

		// 2. Empreinte calcule du master equale a empreinte calcule mirror

		if (!empreinteMasterCalculated.equals(empreinteMirrorCalculated)) {
			throw new AvpExploitException("513", null,
					"Confrontation de l'archive secondaire avec son empreinte ADELIS",
					"Contrôle d'intégrité d'une archive", docId.toString(), null);
		}

		// tout controle pass
		return true;
	}

	@Override
	public boolean checkFiles(final List<UUID> docIds, final Map<UUID, FileReturnError> badDocsInUnit1,
			final Map<UUID, FileReturnError> badDocsInUnit2) throws AvpExploitException {

		LOGGER.info("Checkfiles: " + docIds);

		boolean resultAllOk = true;

		//
		// 1. Contrôle de l'intégralité avec DB et unité storage princiale
		//

		// Récupérer les méta-données depuis DB master
		List<DocumentDto> documentDtos = convertToDocumentDto(docIds, false);

		// D'abord, contrôler si "hash" chargé de log_archive est identique que
		// empreinte
		// si NON, on n'a pas besoin passer ce document à Module Stockage pour le
		// contrôle d'après
		Iterator<DocumentDto> it = documentDtos.iterator();
		while (it.hasNext()) {
			DocumentDto c = it.next();
			if (null == c.getEmpreinteSimple() || !c.getEmpreinteSimple().equals(c.getHash())) {
				badDocsInUnit1.put(c.getDocid(), FileReturnError.HASH_NOT_MATCH_ERROR);
				it.remove();
			}
		}

		if (!badDocsInUnit1.isEmpty())

		{
			LOGGER.error(
					"Les archives dans serveur principal dont le hash du log_archive n'equale pas l'empreinte enregistrée : "
							+ badDocsInUnit1);
			resultAllOk = false;
		}

		// Lancer le contrôle sur module stockage
		if (!fsprocMaster.checkFiles(documentDtos, badDocsInUnit1)) {
			// S'il y des archives en problème, Récupère la liste de documents en question

			// Log badDocs
			LOGGER.error("Les archives dont l'intégralité ne sont pas validé dans serveur principal sont : "
					+ badDocsInUnit1);

			resultAllOk = false;
		} else {
			LOGGER.debug(
					"Les archives dont l'intégralité sont validés dans serveur principal sont : \n" + documentDtos);
		}

		//
		// 2. Contrôle de l'intégralité avec DB miroir et unité storage secondaire
		//

		// Récupérer les méta-données depuis DB slave
		documentDtos = convertToDocumentDto(docIds, true);

		// D'abord, contrôler si "hash" chargé de log_archive est identique que
		// empreinte
		it = documentDtos.iterator();
		while (it.hasNext()) {
			DocumentDto c = it.next();
			if (null == c.getEmpreinteSimple() || !c.getEmpreinteSimple().equals(c.getHash())) {
				badDocsInUnit2.put(c.getDocid(), FileReturnError.HASH_NOT_MATCH_ERROR);
				it.remove();
			}
		}

		if (!badDocsInUnit2.isEmpty()) {
			LOGGER.error(
					"Les archives dans serveur d'esclave dont le hash du log_archive n'equale pas l'empreinte enregistrée : "
							+ badDocsInUnit2);
			resultAllOk = false;
		}

		// Lancer le contrôle sur module stockage
		if (!fsprocMirror.checkFiles(documentDtos, badDocsInUnit2)) {
			// S'il y des archives en problème, Récupère la liste de documents en question

			// Log badDocs
			LOGGER.error("Les archives dont l'intégralité ne sont pas validés dans serveur d'esclave sont : "
					+ badDocsInUnit2);

			resultAllOk = false;
		} else {
			LOGGER.debug(
					"Les archives dont l'intégralité sont validés dans serveur d'esclave sont : \n" + documentDtos);
		}

		return resultAllOk;
	}

	@Override
	public Document get(final UUID docId) throws AvpExploitException {
		return get(docId, fsprocMaster, false);
	}

	/**
	 * Recuperer une object Document en memoire avec contenu
	 * 
	 * @param docId
	 * @param fsproc
	 * @param isMirror
	 * @return
	 * @throws AvpExploitException
	 */
	private Document get(final UUID docId, final FSProc fsproc, final boolean isMirror) throws AvpExploitException {

		// Recupere les meta donnee du document
		Document document = documentDao.get(docId, isMirror);

		// Recupere le contenu depuis FS
		byte[] content = fsproc.getFile(document.getEmpreinte().getEmpreinteUnique());

		// Dechiffrer le contenu si necessaire
		if (document.getCryptage()) {
			// Decryptage
			try {
				Objects.requireNonNull(document.getChiffrement(),
						"A document marked as crypted, mais without crypte object 'Chiffrement");

				// recupere le SecretKey
				Chiffrement chiffrement = document.getChiffrement();
				content = documentService.decrypt(content, chiffrement.getEncryptionKey(), document.getCryptageIv());

			} catch (Exception e) {
				throw new AvpExploitException("531", e, "Décryptage du contenu d''une archive", null, docId.toString(),
						null);
			}
		}

		document.setContent(content);

		return document;
	}

	/**
	 * Archivage d'un fichier venant de document
	 * 
	 * @param document
	 * @param fsproc
	 * @return
	 * @throws AvpExploitException
	 */
	private boolean archiveFS(Document document, FSProc fsproc) throws AvpExploitException {
		String contentBase64 = Base64.getEncoder().encodeToString(document.getContent());

		// TOUJOUR return true!!!
		return fsproc.writeFile(document.getEmpreinte().getEmpreinteUnique(), contentBase64);
	}

	/**
	 * Restauration de contenu du fichier dans FS
	 * 
	 * @param docId
	 * @throws AvpExploitException
	 */
	public void restoreMasterFileFromMirror(final UUID docId) throws AvpExploitException {
		restoreFile(docId, fsprocMirror, fsprocMaster);
	}

	/**
	 * Restauration de contenu du fichier dans FS
	 * 
	 * @param docId
	 * @throws AvpExploitException
	 */
	public void restoreMirrorFileFromMaster(final UUID docId) throws AvpExploitException {
		restoreFile(docId, fsprocMaster, fsprocMirror);
	}

	/**
	 * Restauration de contenu du fichier dans FS
	 * 
	 * @param docId
	 * @param fsOrigin
	 * @param fsDestin
	 * @throws AvpExploitException
	 */
	private void restoreFile(final UUID docId, final FSProc fsOrigin, final FSProc fsDestin)
			throws AvpExploitException {

		Document document = documentDao.get(docId, false);

		// Recupere contenu du document depuis une storage unit
		byte[] content = fsOrigin.getFile(document.getEmpreinte().getEmpreinteUnique());
		document.setContent(content);

		// ecrit dans une autre storage destinataire
		archiveFS(document, fsDestin);
	}

	/**
	 * @param docIds
	 * @param isMirror
	 * @return
	 */
	private List<DocumentDto> convertToDocumentDto(final List<UUID> docIds, final boolean isMirror) {
		List<Document> documents = documentDao.getDocumentToCreateDto(docIds, isMirror);
		List<DocumentDto> documentDtos = new ArrayList<>();

		// Creation of DTO objet
		for (Document doc : documents) {
			DocumentDto docDto = new DocumentDto();
			docDto.setDocid(doc.getDocId());
			docDto.setTitle(doc.getTitle());
			docDto.setArchiveDateMs(doc.getArchiveDate().toString());
			docDto.setCryptage(doc.getCryptage());
			if (Objects.nonNull(doc.getChiffrement())) {
				docDto.setCryptageAlgo(doc.getCryptageAlgo());
				docDto.setSecretKey(doc.getChiffrement().getEncryptionKey().getEncodedkey());
			}
			docDto.setInitVector(doc.getCryptageIv());
			docDto.setEmpreinteSimple(doc.getEmpreinte().getEmpreinte());
			docDto.setEmpreinteUnique(doc.getEmpreinte().getEmpreinteUnique());
			docDto.setEmpreinteTelino(doc.getEmpreinte().getEmpreinteInterne());
			docDto.setHash(logArchiveDao.findHashForDocId(doc.getDocId(), isMirror));

			documentDtos.add(docDto);
		}

		return documentDtos;
	}
}
