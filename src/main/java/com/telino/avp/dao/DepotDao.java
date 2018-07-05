package com.telino.avp.dao;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterDepotRepository;
import com.telino.avp.dao.mirrordao.MirrorDepotRepository;
import com.telino.avp.entity.archive.Depot;

@Repository
@Transactional
public class DepotDao {

	@Autowired
	private MasterDepotRepository masterDepotRepository;

	@Autowired
	private MirrorDepotRepository mirrorDepotRepository;

	public void saveDepot(final Depot depot) {
		if (Objects.isNull(depot.getIdDepot()))
			depot.setIdDepot(UUID.randomUUID());
		
		masterDepotRepository.save(depot);
		mirrorDepotRepository.save(depot);
	}

	public Depot findByDepotId(UUID depotId) {
		return masterDepotRepository.findById(depotId).orElseThrow(EntityNotFoundException::new);
	}
}
