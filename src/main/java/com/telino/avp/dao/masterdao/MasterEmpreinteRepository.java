package com.telino.avp.dao.masterdao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.archive.Empreinte;

public interface MasterEmpreinteRepository extends JpaRepository<Empreinte, UUID> {
}
