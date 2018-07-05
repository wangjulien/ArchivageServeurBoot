package com.telino.avp.servlets;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.telino.avp.service.SwitchDataSourceService;
import com.telino.avp.tools.CdmsApiServletRequestIO;

import CdmsApi.client.CdmsApi_Out;
import CdmsApi.client.CdmsApi_in;
import CdmsApi.client.CdmsTransaction;

@Controller
@RequestMapping("/startCdmsServiceArchivage")
public class StartCdmsServiceArchivageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartCdmsServiceArchivageController.class);

	@Resource
	private DataSource masterDynamicDs;

	@Resource
	private DataSource mirrorDynamicDs;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private SwitchDataSourceService switchDataSourceService;

	// nomBase is the principal DB
	@RequestMapping(params = { "nomBase" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void doGetAndPost(@RequestParam("nomBase") String nomBase, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			if (nomBase != null && nomBase.length() > 0) {
				
				//
				// TODO : Switch DataSource par AOP intercepter
				//
				switchDataSourceService.switchDataSourceFor(nomBase);
				
				// lecture de la trame
				CdmsApi_in trame = CdmsApiServletRequestIO.lectureCdmsObj(request);

				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@SuppressWarnings("unchecked")
					@Override
					public void doInTransactionWithoutResult(TransactionStatus status) {

						try {
							CdmsTransaction J = new CdmsTransaction(masterDynamicDs.getConnection(),
									mirrorDynamicDs.getConnection());

							CdmsApi_Out sortie = J.JavaCall(trame);
							if (!sortie.getRetour().startsWith("0")) {
								// Mannully declencher rollback
								status.setRollbackOnly();
								CdmsApiServletRequestIO.ecritureCdmsObj(response, sortie);
								return;
							}

							ArrayList<Object> lignes = sortie.getList();
							for (int i = 0; i < lignes.size(); i++) {
								if (lignes.get(i) instanceof HashMap) {
									((HashMap<String, Object>) lignes.get(i)).remove("Connection");
									((HashMap<String, Object>) lignes.get(i)).remove("connMirror");
								}
								LOGGER.info((String) lignes.get(i));
							}
							CdmsApiServletRequestIO.ecritureCdmsObj(response, sortie);
						} catch (Exception e) {
							LOGGER.error("erreur servlet startCdmsService" + e.getMessage());
							throw new RuntimeException(e);
						}
					}
				});

			}
		} catch (Exception e) {
			LOGGER.error("erreur servlet startCdmsService");
			CdmsApi_Out sortie = new CdmsApi_Out();
			sortie.setRetour("9");
			try {
				CdmsApiServletRequestIO.ecritureCdmsObj(response, sortie);
			} catch (Exception ee) {
				LOGGER.error("erreur d'ecrire une reponse pour startCdmsAnalysis" + ee.getMessage());
			}
		}
	}

}
