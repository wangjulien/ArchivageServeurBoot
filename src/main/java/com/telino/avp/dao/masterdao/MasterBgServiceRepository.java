package com.telino.avp.dao.masterdao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.param.BgService;

public interface MasterBgServiceRepository extends JpaRepository<BgService, Long> {
}
