package com.telino.avp.dao.paramdao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entitysyst.SystEnv;

public interface SystEnvDao extends JpaRepository<SystEnv, Integer> {
	
	public List<SystEnv> findAllByBgsOnIsTrue();
}
