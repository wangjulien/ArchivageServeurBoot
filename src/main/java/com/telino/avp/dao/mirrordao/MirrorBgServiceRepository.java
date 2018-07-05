package com.telino.avp.dao.mirrordao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.param.BgService;

public interface MirrorBgServiceRepository extends JpaRepository<BgService, Long> {
}
