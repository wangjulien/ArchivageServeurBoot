package com.telino.avp.dao.masterdao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.archive.Chiffrement;

public interface MasterChiffrementRepository extends JpaRepository<Chiffrement, UUID> {
}
