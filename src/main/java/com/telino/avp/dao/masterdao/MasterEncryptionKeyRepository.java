package com.telino.avp.dao.masterdao;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.archive.EncryptionKey;

public interface MasterEncryptionKeyRepository extends JpaRepository<EncryptionKey, UUID> {
}
