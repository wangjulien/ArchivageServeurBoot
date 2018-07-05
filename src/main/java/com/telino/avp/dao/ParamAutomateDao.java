package com.telino.avp.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterParamAutomateRepository;
import com.telino.avp.entity.param.ParamAutomate;

/**
 * 
 * @author Jiliang.WANG
 *
 */
@Repository
@Transactional
public class ParamAutomateDao {

	@Autowired
	private MasterParamAutomateRepository masterParamAutomateRepository;

	public Optional<String> getParaAutomate() {
		List<ParamAutomate> params = masterParamAutomateRepository.findAll();

		if (params.isEmpty()) {
			return Optional.empty();
		} else {
			ParamAutomate param = params.get(0);
			return Optional.of("http://" + param.getHostname() + ":" + param.getPort() + "/" + param.getServlet());
		}
	}
}
