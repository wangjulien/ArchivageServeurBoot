package com.telino.avp.dao.masterdao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.archive.Restitution;

public interface MasterRestitutionRepository extends JpaRepository<Restitution, UUID> {
}
