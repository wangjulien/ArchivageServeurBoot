package com.telino.avp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telino.avp.config.AppSpringConfig;
import com.telino.avp.config.multids.MasterDsContextHolder;
import com.telino.avp.config.multids.MirrorDsContextHolder;
import com.telino.avp.dao.ParamDao;
import com.telino.avp.entity.param.Param;
import com.telino.avp.exception.AvpDaoException;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.exception.AvpExploitExceptionCode;
import com.telino.avp.service.storage.FsStorageService;

@Service
public class SwitchDataSourceService {

	public static final ThreadLocal<Param> CONTEXT_APP_PARAM = new ThreadLocal<>();

	@Autowired
	private ParamDao paramDao;

	@Autowired
	private FsStorageService storageService;

	/**
	 * Changement dynamique de Datasource sur Master et Mirror
	 * 
	 * @param nomBase
	 * @throws AvpExploitException
	 */
	public void switchDataSourceFor(final String nomBase) throws AvpExploitException {
		try {
			// Switch master DataSource
			MasterDsContextHolder.setCurrentDsId(nomBase);
			Param appParam = paramDao.getInitialParam(AppSpringConfig.APP_PARAM_ID);
			// Switch mirror Datasource
			MirrorDsContextHolder.setCurrentDsId(appParam.getMirroringurl());
			// Initiate storageService which contain 2 FSProc (master, mirror)
			storageService.initFsStorageService(appParam);
			// Save the param for the thread(request)
			CONTEXT_APP_PARAM.set(appParam);
		} catch (AvpDaoException | NullPointerException e) {
			throw new AvpExploitException(AvpExploitExceptionCode.STORAGE_PARAM_ERROR, e,
					"Switch data source pour DB : " + nomBase);
		}
	}
}
