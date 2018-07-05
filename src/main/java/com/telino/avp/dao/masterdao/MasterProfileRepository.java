package com.telino.avp.dao.masterdao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.context.Profile;

public interface MasterProfileRepository extends JpaRepository<Profile, Integer> {

}
