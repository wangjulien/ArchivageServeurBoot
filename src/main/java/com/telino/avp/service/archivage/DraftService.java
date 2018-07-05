package com.telino.avp.service.archivage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.results.ValidationResult;

import com.telino.avp.dao.DraftDao;
import com.telino.avp.entity.archive.Draft;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.protocol.DbEntityProtocol.DraftStatut;
import com.telino.avp.service.journal.TamponHorodatageService;
import com.telino.avp.tools.AfficheurFluxExec;

import fr.cines.Format;
import fr.cines.format.validator.UnknownFormatException;
import fr.cines.format.validator.Validator;
import fr.cines.validator.ValidatorFactory;
import tools.ApercuManager;

@Service
public class DraftService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DraftService.class);

	@Autowired
	private DraftDao draftDao;

	/**
	 * Suppression d'un/liste de drafts
	 * 
	 * @param input
	 */
	public void deleteDraft(final Map<String, Object> input) {
		List<UUID> docIds = null;

		// one draft or a list of drafts
		if (Objects.nonNull(input.get("docid")))
			docIds = Arrays.asList(UUID.fromString(input.get("docid").toString()));
		else if (Objects.nonNull(input.get("idlist")) && !input.get("idlist").toString().isEmpty()) {
			// a list of drafts separated by ','
			docIds = Arrays.asList((input.get("idlist").toString().replaceAll("\\s", "").split(","))).stream()
					.map(UUID::fromString).collect(Collectors.toList());
		}

		if (Objects.nonNull(docIds))
			draftDao.deleteAllByDocId(docIds);
		else
			LOGGER.error("input doesn't contain any docId : " + input);
	}

	/**
	 * Refus d'un/liste de drafts
	 * 
	 * @param input
	 */
	public void refusDraft(final Map<String, Object> input) {
		List<UUID> draftDocIds = null;

		// one draft or a list of drafts
		if (Objects.nonNull(input.get("docid")))
			draftDocIds = Arrays.asList(UUID.fromString(input.get("docid").toString()));
		else if (Objects.nonNull(input.get("idlist")) && !input.get("idlist").toString().isEmpty()) {
			// a list of drafts separated by ','
			draftDocIds = Arrays.asList((input.get("idlist").toString().replaceAll("\\s", "").split(","))).stream()
					.map(UUID::fromString).collect(Collectors.toList());
		}

		if (Objects.nonNull(draftDocIds)) {
			List<Draft> drafts = draftDao.findAllByDocId(draftDocIds);
			drafts.forEach(d -> {
				d.setTransmis(false);
				d.setMotif(input.get("motif").toString());
				d.setStatut(DraftStatut.REFUSED);
				d.setDraftdate(ZonedDateTime.now());
			});

			// Persistenc the Update
			draftDao.saveAll(drafts);
		} else
			LOGGER.error("input doesn't contain any docId : " + input);
	}

	/**
	 * @param input
	 */
	public void updateDraft(final Map<String, Object> input) {
		// Create of Draft object according to Input Map
		List<Draft> drafts = new ArrayList<>();

		// one draft or a list of drafts
		if (Objects.nonNull(input.get("idlist")) && !input.get("idlist").toString().isEmpty()) {
			// a list of drafts separated by ','
			List<UUID> draftDocIds = Arrays.asList((input.get("idlist").toString().replaceAll("\\s", "").split(",")))
					.stream().map(UUID::fromString).collect(Collectors.toList());

			for (UUID draftId : draftDocIds) {
				Draft draftUnitaire = new Draft();
				draftUnitaire.setDocId(draftId);

				draftDao.mapValues(draftUnitaire, input);
				draftUnitaire.setDraftdate(ZonedDateTime.now());
				drafts.add(draftUnitaire);
			}
		} else if (Objects.nonNull(input.get("docid"))) {
			Draft draftUnitaire = new Draft();
			draftUnitaire.setDocId(UUID.fromString((String) input.get("docid")));
			
			draftDao.mapValues(draftUnitaire, input);
			draftUnitaire.setDraftdate(ZonedDateTime.now());
			drafts.add(draftUnitaire);
		}

		// Persistence of all drafts
		draftDao.saveAll(drafts);
	}

	/**
	 * @param input
	 */
	public void valideDraft(final Map<String, Object> input) {
		List<UUID> draftDocIds = null;

		// one draft or a list of drafts
		if (Objects.nonNull(input.get("docid")))
			draftDocIds = Arrays.asList(UUID.fromString(input.get("docid").toString()));
		else if (Objects.nonNull(input.get("idlist")) && !input.get("idlist").toString().isEmpty()) {
			// a list of drafts separated by ','
			draftDocIds = Arrays.asList((input.get("idlist").toString().replaceAll("\\s", "").split(","))).stream()
					.map(UUID::fromString).collect(Collectors.toList());
		}
		
		if (Objects.nonNull(draftDocIds)) {
			// Get all the draft for modification
			List<Draft> drafts = draftDao.findAllByDocId(draftDocIds);
			drafts.forEach(d -> {
				d.setTransmis(true);
				d.setMotif("Transmis");
				d.setStatut(DraftStatut.TRANSMIS);
				d.setDraftdate(ZonedDateTime.now());
			});

			// Persistenc the Update
			draftDao.saveAll(drafts);
		} else
			LOGGER.error("input doesn't contain any docId : " + input);	
		
	}

	/**
	 * Recupere un draft depuis db
	 * 
	 * @param input
	 * @param resultat
	 */
	public void getDraftInfo(final UUID docId, final Map<String, Object> resultat) {

		Draft draft = draftDao.get(docId);

		resultat.put("docid", draft.getDocId().toString());
		resultat.put("draftdate", Objects.isNull(draft.getDraftdate()) ? "" : draft.getDraftdate().toString());
		resultat.put("doctype", draft.getDoctype());
		resultat.put("categorie", draft.getCategorie());
		resultat.put("title", draft.getTitle());
		resultat.put("description", draft.getDescription());
		resultat.put("docsdate", Objects.isNull(draft.getDocsdate()) ? "" : draft.getDocsdate().toString());
		resultat.put("content_type", draft.getContentType());
		resultat.put("content_length", Objects.isNull(draft.getContentLength()) ? 0 : draft.getContentLength().intValue());
		resultat.put("keywords", Objects.isNull(draft.getKeywords()) ? "" : draft.getKeywords().replaceAll("<", "").replaceAll(">", ""));
		resultat.put("mailowner", draft.getMailowner());
		resultat.put("domnnom", draft.getDomnNom());
		resultat.put("domaineowner", draft.getDomaineowner());
		resultat.put("organisationversante", draft.getOrganisationversante());

		LOGGER.debug(resultat.toString());

		// TODO : } else {
		// resultat.put("codeRetour", "10");
		// resultat.put("message", "Document inexistant");
		// }
	}

	/**
	 * recupere contenu d'un draft
	 * 
	 * @param input
	 * @param resultat
	 */
	public void readDraft(final Map<String, Object> input, final Map<String, Object> resultat) {

		Draft draft = draftDao.get(UUID.fromString(input.get("docid").toString()));

		// Only getAsPdf (PDF) works
		if (input.get("getAsPdf") != null && input.get("getAsPdf").equals("true")) {
			if (draft.getTitle().endsWith(".eml")) {
				// TODO : ??? Here is always empty? What's ApercManager for?
			} else {
				// System.out.println("calling translater");
				ApercuManager AM = new ApercuManager();
				// content = AM.convert(content, "Visualisation impossible", type, title,
				// openofficepath, taille,
				// maxconvertsize);
				draft.setContentType(AM.type);
				// System.out.println("coming back translater");
			}
		}

		resultat.put("content", draft.getContent());
		resultat.put("content_length", Objects.isNull(draft.getContentLength()) ? 0 : draft.getContentLength().intValue());
		resultat.put("content_type", draft.getContentType());
		resultat.put("title", draft.getTitle());

		// TODO : } else {
		// resultat.put("codeRetour", "10");
		// resultat.put("message", "Document inexistant");
		// }
	}

	/**
	 * Enregitrer un draft
	 * 
	 * @param input
	 * @param resultat
	 * @throws AvpExploitException
	 */
	public void draftStore(final Map<String, Object> input, final Map<String, Object> resultat)
			throws AvpExploitException {

		// Save in a temporary dir the draft
		File file = new File(getClass().getResource("/antivirus/tmp").getFile() + "/" + input.get("title"));
		try {
			Files.write(file.toPath(), (byte[]) input.get("content"));
		} catch (IOException e1) {
			throw new AvpExploitException("901", e1, "Écriture du fichier dans un répertoire temporaire", null, null,
					null);
		}

		try {
			ProcessBuilder pb = new ProcessBuilder("clamdscan", file.getAbsolutePath());
			Process p = pb.start();

			AfficheurFluxExec fluxSortie = new AfficheurFluxExec(p.getInputStream());
			AfficheurFluxExec fluxErreur = new AfficheurFluxExec(p.getErrorStream());
			fluxSortie.run();
			fluxErreur.run();

			if (!(p.waitFor() == 0)) {
				LOGGER.error("erreur chelou");
			}

			String result = fluxSortie.getRetour();
			LOGGER.debug("result :" + result);

			if (result != null && result != "") {
				LOGGER.debug("result non nul");

				// Scan antivirus
				Map<String, String> scanAntiVirus = analyseScan(result);
				if (scanAntiVirus != null) {
					if (scanAntiVirus.get("nbErrors") != null && !scanAntiVirus.get("nbErrors").equals("0")) {
						file.delete();
						throw new AvpExploitException("903", null, "Scan antivirus du fichier " + input.get("title"),
								null, null, null);
					} else if (scanAntiVirus.get("nbInfected") == null
							|| !scanAntiVirus.get("nbInfected").equals("0")) {
						file.delete();
						throw new AvpExploitException("904", null, "Scan antivirus du fichier " + input.get("title"),
								null, null, null);
					}
				} else {
					file.delete();
					throw new AvpExploitException("902", null, "Scan antivirus du fichier " + input.get("title"), null,
							null, null);
				}
			} else {
				file.delete();
				throw new AvpExploitException("902", new Exception("" + fluxErreur.toString()),
						"Scan antivirus du fichier " + input.get("title"), null, null, null);
			}

		} catch (IOException e) {
			throw new AvpExploitException("902", e, "Scan antivirus du fichier " + input.get("title"), null, null,
					null);
		} catch (InterruptedException e) {
			throw new AvpExploitException("902", e, "Scan antivirus du fichier " + input.get("title"), null, null,
					null);
		}

		// Validation of format
		Format format = null;
		if (file.getName().equals(input.get("title"))) {
			ValidatorFactory factory = new ValidatorFactory();
			Validator v = null;
			try {
				try {
					v = factory.createValidator(file);
				} catch (UnknownFormatException e1) {
					if ("application/pdf".equals((String) input.get("content_type"))) {
						
						VeraGreenfieldFoundryProvider.initialise();
						try (PDFAParser parser = Foundries.defaultInstance()
								.createParser(new ByteArrayInputStream((byte[]) input.get("content")))) {
							PDFAValidator validator = Foundries.defaultInstance().createValidator(parser.getFlavour(),
									false);
							ValidationResult result = validator.validate(parser);
							if (!result.isCompliant()) {
								resultat.put("codeRetour", "4");
								resultat.put("message", "Le fichier" + input.get("title") + " n'est pas conforme");
								return;
							}
						} catch (NoSuchElementException | ModelParsingException | EncryptedPdfException
								| ValidationException e) {
							resultat.put("codeRetour", "4");
							resultat.put("message", "Le format du fichier " + input.get("title").toString()
									+ " est inconnu ou ne peut être déterminé car ne respectant pas les conventions. ");
							return;
						}

					} else {
						resultat.put("codeRetour", "4");
						resultat.put("message", "Le format du fichier " + input.get("title").toString()
								+ " est inconnu ou ne peut être déterminé car ne respectant pas les conventions. ");
						return;
					}
				}
				format = v.identify();
				LOGGER.info(file.getName() + " ==> format identifié : " + format + ". Valide ? " + v.isValid()
						+ " ..." + v.getMessage());
				
				if (!v.isValid()) {
					resultat.put("codeRetour", "4");
					resultat.put("message", "Le fichier " + input.get("title").toString() + " n'est pas valide. ");
					return;
				}
			} catch (IOException e1) {
				throw new AvpExploitException("402", e1, "Identification du format du fichier " + input.get("title"),
						null, null, null);
			} catch (UnknownFormatException e1) {
				throw new AvpExploitException("402", e1, "Identification du format du fichier " + input.get("title"),
						null, null, null);
			} finally {
				file.delete();
			}
		}

		// Save draft meta data dans les DB
		Draft draft = new Draft();
		draft.setDoctype((String) input.get("doctype"));
		draft.setCategorie((String) input.get("categorie"));
		draft.setKeywords((String) input.get("keywords"));
		draft.setContent((byte[]) input.get("content"));
		draft.setContentLength((Integer) input.get("content_length"));
		draft.setContentType((String) input.get("content_type"));
		draft.setDomaineowner((String) input.get("domaineowner"));
		draft.setOrganisationversante((String) input.get("organisationversante"));
		draft.setDocsdate(TamponHorodatageService.convertToSystemZonedDateTime((Date) input.get("date")));
		draft.setDescription((String) input.get("description"));
		draft.setTitle((String) input.get("title"));
		draft.setDomnNom((String) input.get("domnnom"));
		draft.setMailowner((String) input.get("mailowner"));
		draft.setUserid((String) input.get("user"));
		draft.setTransmis(false);
		draft.setDraftdate(ZonedDateTime.now());
		draft.setPronomType(format.toString());
		draft.setPronomId(format.getId());

		draftDao.saveDraft(draft);
	}

	private Map<String, String> analyseScan(String result) {
		if (result.contains("Total errors:")) {
			String nbErrors = result.substring(result.indexOf("Total errors: ") + 13,
					result.indexOf("Total errors: ") + 15);
			LOGGER.info("Total errors: " + nbErrors);

			Map<String, String> scan = new HashMap<>();
			scan.put("nbErrors", nbErrors);
			return scan;
		} else if (result.contains("Infected files:")) {
			String nbInfected = result.substring(result.indexOf("Infected files: ") + 16,
					result.indexOf("Infected files: ") + 17);
			LOGGER.info("Infected files: " + nbInfected);

			Map<String, String> scan = new HashMap<>();
			scan.put("nbInfected", nbInfected);
			return scan;
		} else {
			return null;
		}
	}
}
