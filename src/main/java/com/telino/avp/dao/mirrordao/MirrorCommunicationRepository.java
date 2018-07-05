package com.telino.avp.dao.mirrordao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.archive.Communication;

public interface MirrorCommunicationRepository extends JpaRepository<Communication, UUID> {
}
