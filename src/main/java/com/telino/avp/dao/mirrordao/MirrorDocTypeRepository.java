package com.telino.avp.dao.mirrordao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telino.avp.entity.context.DocType;

public interface MirrorDocTypeRepository extends JpaRepository<DocType, Integer> {

}
