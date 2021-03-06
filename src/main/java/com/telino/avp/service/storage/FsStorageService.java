package com.telino.avp.service.storage;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Resource;

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
import com.telino.avp.exception.AvpDaoException;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
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

		// Only and only if the 2 application passwords are initialized, we can
		// initialize storage and cryptage
		if (Objects.nonNull(ServerProc.password1) && Objects.nonNull(ServerProc.password2)) {
			//
			// Initialization des unite de stockage, ss
			// - idStorage est vide et Hostname renseigne
			//

			// Master storage unit
			fsprocMaster.init(appParam.getMasterStorageParam());
			// Mirror storage unit
			fsprocMirror.init(appParam.getMirrorStorageParam());
			// Update idStorage parameters
			paramDao.saveParam(appParam);

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
						throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_INIT_ENCRYPT_ERROR, e,
								"Initialisation du module de chiffrement");
					}
				}
			}
		}
	}

	@Override
	public void archive(final Document document) throws AvpExploitException {
		// Valoriser les empreintes
		if (Objects.isNull(document.getEmpreinte()))
			document.setEmpreinte(new Empreinte());

		document.getEmpreinte().setEmpreinteInterne(documentService.computeTelinoPrint(document));
		document.getEmpreinte().setEmpreinte(documentService.computePrint(document));

		// Encryption
		Param appParam = SwitchDataSourceService.CONTEXT_APP_PARAM.get();
		if (appParam.isCryptage()) {
			try {
				Chiffrement chiffrement = chiffrementDao.findChiffrementByCrytId(appParam.getCryptageid());
				// Chiffrement
				Map<String, byte[]> resultCrypt = documentService.encrypt(document.getContent(),
						chiffrement.getEncryptionKey());

				document.setCryptage(true);
				document.setContent(resultCrypt.get("crypted"));
				document.setCryptageIv(resultCrypt.get("iv"));
				document.setCryptageAlgo(chiffrement.getAlgorythm());
				document.setChiffrement(chiffrement);

			} catch (AvpDaoException | AesCipherException e) {
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_ENCRYPT_ERROR, e, "Crypter document",
						document.getDocId().toString(), null);
			}
		}

		try {
			documentDao.fillEmpreinteUnique(document);

			// Si archivage mirror echoue, on s'arrete
			archiveFS(document, fsprocMirror);

			// Si archivage master echoue, on supprime mirror et s'arrete
			try {
				archiveFS(document, fsprocMaster);
			} catch (Exception e) {
				// suppression le doc archive dans mirror
				fsprocMirror.deleteFile(document.getEmpreinte().getEmpreinteUnique());
				throw e;
			}

			// Persister document
			documentDao.saveMetaDonneesDocument(document);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.ARCHIVE_DOC_DAO_ERROR, e,
					"Archiver document meta-donnee", document.getDocId().toString(), null);
		}
	}

	@Override
	public void delete(final Document document) throws AvpExploitException {

		// Lancer suppression de fichier du document dans unite mirror
		fsprocMirror.deleteFile(document.getEmpreinte().getEmpreinteUnique());

		// Lancer suppression de fichier du document dans unite mirror
		fsprocMaster.deleteFile(document.getEmpreinte().getEmpreinteUnique());

	}

	@Override
	public void check(final UUID docId, final boolean toArchive) throws AvpExploitException {

		//
		// Recuperer object Document en memoire avec contenue
		//
		Document documentMaster;
		Document documentMirror;
		try {
			documentMaster = get(docId, fsprocMaster, false);
		} catch (Exception e) {
			throw new AvpExploitException(AvpExploitExceptionCode.CHECK_FILE_GET_ERROR, e,
					"Récupération des données de l'archive sur le stockage principal", docId.toString(), null);
		}

		try {
			documentMirror = get(docId, fsprocMirror, true);
		} catch (Exception e) {
			throw new AvpExploitException(AvpExploitExceptionCode.CHECK_FILE_GET_ERROR, e,
					"Récupération des données de l'archive sur le stockage secondaire", docId.toString(), null);
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
				throw new AvpExploitException(AvpExploitExceptionCode.CHECK_FILE_PRINT_CONFLICT_ERROR, null,
						"Confrontation avec l'empreinte scellée dans le journal des archives lors du dépôt",
						docId.toString(), null);
			}
		}

		//
		// Controler les empreintes
		//

		// 1. Empreinte calcule du master equale a empreiente enregistre
		if (!empreinteMasterCalculated.equals(empreinteMasterToCheck)) {
			if (toArchive)
				throw new AvpExploitException(AvpExploitExceptionCode.CHECK_FILE_PRINCIPAL_PRINT_ERROR, null,
						"Confrontation de l'archive principale avec son empreinte ADELIS", docId.toString(), null);
			else
				throw new AvpExploitException(AvpExploitExceptionCode.CHECK_FILE_PRINCIPAL_PRINT_ERROR, null,
						"Confrontation de l'archive principale avec son empreinte", docId.toString(), null);
		}

		// 2. Empreinte calcule du master equale a empreinte calcule mirror
		if (!empreinteMasterCalculated.equals(empreinteMirrorCalculated)) {
			throw new AvpExploitException(AvpExploitExceptionCode.CHECK_FILE_MIRROR_PRINT_ERROR, null,
					"Confrontation de l'archive secondaire avec son empreinte ADELIS", docId.toString(), null);
		}
	}

	@Override
	public boolean checkFiles(final List<UUID> docIds, final Map<UUID, FileReturnError> badDocsInUnit1,
			final Map<UUID, FileReturnError> badDocsInUnit2) throws AvpExploitException {
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
		Document document = null;
		try {
			// Recupere les meta donnee du document
			document = documentDao.get(docId, isMirror);
		} catch (AvpDaoException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.DOC_GET_DAO_ERROR, e,
					"Recuperer meta-donnee du document", docId.toString(), null);
		}

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
				throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_DECRYPT_ERROR, e,
						"Décryptage du contenu d'une archive", docId.toString(), null);
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
	private void archiveFS(final Document document, final FSProc fsproc) throws AvpExploitException {
		String contentBase64 = Base64.getEncoder().encodeToString(document.getContent());

		fsproc.writeFile(document.getEmpreinte().getEmpreinteUnique(), contentBase64);
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
