package com.telino.avp.servlets;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

	@Autowired
	private SwitchDataSourceService switchDataSourceService;

	// nomBase is the principal DB
	@SuppressWarnings("unchecked")
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public void doGetAndPost(@RequestParam("nomBase") String nomBase, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			if (Objects.nonNull(nomBase) && nomBase.length() > 0) {

				// Switch DataSource
				switchDataSourceService.switchDataSourceFor(nomBase);

				// lecture de la trame
				CdmsApi_in trame = CdmsApiServletRequestIO.lectureCdmsObj(request);

				try {
					// Get master connection by JNDI Datasource
					Connection conn = getConnexion(nomBase);

					// Get mirror connection similarly
					Connection connMirror = getConnexion(
							SwitchDataSourceService.CONTEXT_APP_PARAM.get().getMirroringurl());

					if (Objects.isNull(conn) || Objects.isNull(connMirror)) {
						LOGGER.error("8 Pas de connexion disponible");
						CdmsApi_Out sortie = new CdmsApi_Out();
						sortie.setRetour("9");
						CdmsApiServletRequestIO.ecritureCdmsObj(response, sortie);
						return;
					}

					CdmsTransaction J = new CdmsTransaction(conn, connMirror);

					CdmsApi_Out sortie = J.JavaCall(trame);
					if (!sortie.getRetour().startsWith("0")) {
						// Mannully declencher rollback
						invcommit(conn, connMirror);
						CdmsApiServletRequestIO.ecritureCdmsObj(response, sortie);
						return;
					}

					commit(conn, connMirror);

					ArrayList<Object> lignes = sortie.getList();
					for (int i = 0; i < lignes.size(); i++) {
						if (lignes.get(i) instanceof HashMap) {
							((HashMap<String, Object>) lignes.get(i)).remove("Connection");
							((HashMap<String, Object>) lignes.get(i)).remove("connMirror");
						}
						LOGGER.info(lignes.get(i).toString());
					}
					CdmsApiServletRequestIO.ecritureCdmsObj(response, sortie);
				} catch (Exception e) {
					LOGGER.error("erreur servlet startCdmsService" + e.getMessage());
					throw new RuntimeException(e);
				}
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

	/*
	 * Commit et rollback
	 */
	private boolean commit(final Connection conn, final Connection connMirror) {
		try {
			conn.commit();
			conn.close();
			LOGGER.info("Committ effectué", "nfz42013");
			if (Objects.nonNull(connMirror)) {
				connMirror.commit();
				LOGGER.info("Committ mirror effectué", "nfz42013");
				connMirror.close();
			}
			return true;
		} catch (Exception e) {
			LOGGER.error("Commit echoué : " + e.getCause());
		}
		return true;

	}

	private boolean invcommit(final Connection conn, final Connection connMirror) {
		try {
			conn.rollback();
			conn.close();
			if (Objects.nonNull(connMirror)) {
				connMirror.rollback();
				connMirror.close();
			}
			LOGGER.error(" rollback effectué", "nfz42013");
			return true;
		} catch (Exception e) {
			LOGGER.error("Rollback echoué : " + e.getCause());
		}
		
		return false;
	}

	private Connection getConnexion(String nomBase) {
		Connection conn = null;
		try {
			Context initCtx = new InitialContext();
			String lookupString = nomBase;
			if (lookupString != null)
				lookupString = "java:comp/env/jdbc/" + lookupString;
			else
				lookupString = "java:comp/env/jdbc/coriolis";
			DataSource ds = (DataSource) initCtx.lookup(lookupString);
			conn = ds.getConnection();
			conn.setAutoCommit(false); // pas d'autocommit
			conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
		} catch (Exception e) {
			LOGGER.error(Thread.currentThread().getId() + " CONNECT Z " + e.getMessage());
		}

		return conn;
	}

}
