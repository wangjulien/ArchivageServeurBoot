package com.telino.avp.dao.mirrordao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.context.Profile;

public interface MirrorProfileRepository extends JpaRepository<Profile, Integer> {

}
