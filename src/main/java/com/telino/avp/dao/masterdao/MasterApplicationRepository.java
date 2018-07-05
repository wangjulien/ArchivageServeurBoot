package com.telino.avp.dao.masterdao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.param.Application;

public interface MasterApplicationRepository extends JpaRepository<Application, String> {

	Optional<Application> findByApplicationCodeAndApplicationValidationIsTrue(String applicationCode);
}
