package com.telino.avp.dao.mirrordao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.archive.EncryptionKey;

public interface MirrorEncryptionKeyRepository extends JpaRepository<EncryptionKey, UUID> {
}
