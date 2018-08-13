package com.telino.avp.dao;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterDraftRepository;
import com.telino.avp.dao.mirrordao.MirrorDraftRepository;
import com.telino.avp.entity.archive.Draft;
import com.telino.avp.protocol.DbEntityProtocol.DraftStatut;
import com.telino.avp.service.journal.TamponHorodatageService;

/**
 * @author julie.maran
 *
 */
@Repository
@Transactional
public class DraftDao {

	@Autowired
	private MasterDraftRepository masterDraftRepository;

	@Autowired
	private MirrorDraftRepository mirrorDraftRepository;

	/**
	 * Permet de récupérer un objet draft à partir de l'id du draft
	 * 
	 * @param idDraft
	 *            l'id du draft
	 * @return l'objet Draft associé à cet id
	 * @throws Exception
	 */
	public Draft get(final UUID docId) {
		return masterDraftRepository.findById(docId).orElseThrow(EntityNotFoundException::new);
	}

	public List<Draft> findAllByDocId(List<UUID> draftDocIds) {
		return masterDraftRepository.findAllById(draftDocIds);
	}

	/**
	 * met à jour un draft
	 * 
	 * @param draft
	 *            le draft à mettre à jour
	 */
	public void saveDraft(Draft draft) {

		if (Objects.isNull(draft.getDocId()))
			draft.setDocId(UUID.randomUUID());

		masterDraftRepository.save(draft);

		mirrorDraftRepository.save(draft);
	}

	public void saveAll(List<Draft> drafts) {
		drafts.forEach(d -> {
			if (Objects.isNull(d.getDocId()))
				d.setDocId(UUID.randomUUID());
		});

		masterDraftRepository.saveAll(drafts);

		mirrorDraftRepository.saveAll(drafts);
	}

	/**
	 * recupere un draft par son docId (archiveid)
	 * 
	 * @param archiveid
	 * @return
	 */
	public Optional<Draft> hasDraft(final UUID archiveid) {

		return masterDraftRepository.findByArchiveId(archiveid);
	}

	/**
	 * mettre a jour statut/motif d'un draft
	 * 
	 * @param draftid
	 * @param docid
	 * @param statut
	 * @param motif
	 */
	public void updateStoredDraft(final UUID draftid, final UUID docid, final DraftStatut statut, final String motif) {
		Draft draft = masterDraftRepository.findById(draftid).orElseThrow(EntityNotFoundException::new);

		draft.setTransmis(false);
		draft.setStatut(statut.toString());
		draft.setMotif(motif);
		draft.setArchiveId(docid);
		draft.setDraftdate(ZonedDateTime.now());

		saveDraft(draft);
	}

	/**
	 * Suppression une liste de draft par ID
	 * 
	 * @param docIds
	 */
	public void deleteAllByDocId(List<UUID> docIds) {
		masterDraftRepository.deleteAllByDocIdIn(docIds);
		mirrorDraftRepository.deleteAllByDocIdIn(docIds);
	}

	/**
	 * permet de faire la correspondance entre un input d'API et un objet Draft.
	 * 
	 * @param draft
	 *            le draft à initialiser à partir de l'input
	 * @param input
	 *            les infos données par l'API
	 * @return l'objet Draft instancié
	 */
	public Draft mapValues(final Draft draft, final Map<String, Object> input) {

		if (input.get("user") != null)
			draft.setUserid((String) input.get("user"));
		if (input.get("content") != null)
			draft.setContent((byte[]) input.get("content"));
		if (input.get("doctype") != null)
			draft.setDoctype((String) input.get("doctype"));
		if (input.get("categorie") != null)
			draft.setCategorie((String) input.get("categorie"));
		if (input.get("keywords") != null)
			draft.setKeywords((String) input.get("keywords"));
		if (input.get("content_length") != null)
			draft.setContentLength((Integer) input.get("content_length"));
		if (input.get("content_type") != null)
			draft.setContentType((String) input.get("content_type"));
		if (input.get("domaineowner") != null)
			draft.setDomaineowner((String) input.get("domaineowner"));
		if (input.get("organisationversante") != null)
			draft.setOrganisationversante((String) input.get("organisationversante"));
		if (input.get("docsdate") != null)
			draft.setDocsdate(TamponHorodatageService.convertToSystemZonedDateTime((Date) input.get("docsdate")));
		if (input.get("description") != null)
			draft.setDescription((String) input.get("description"));
		if (input.get("title") != null)
			draft.setTitle((String) input.get("title"));
		if (input.get("domnnom") != null)
			draft.setDomnNom((String) input.get("domnnom"));
		if (input.get("mailowner") != null)
			draft.setMailowner((String) input.get("mailowner"));
		if (input.get("transmis") != null)
			draft.setTransmis((Boolean) input.get("transmis"));
		if (input.get("statut") != null)
			draft.setStatut((String) input.get("statut"));
		if (input.get("motif") != null)
			draft.setMotif((String) input.get("motif"));
		if (input.get("draftdate") != null)
			draft.setDraftdate(ZonedDateTime.parse((String) input.get("draftdate")));			
		if (input.get("pronom_type") != null)
			draft.setPronomType((String) input.get("pronom_type"));
		if (input.get("pronom_id") != null)
			draft.setPronomType((String) input.get("pronom_id"));

		return draft;
	}

}
