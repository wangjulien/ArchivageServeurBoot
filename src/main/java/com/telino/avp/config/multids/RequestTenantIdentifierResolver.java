/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telino.avp.config.multids;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * Permet de récupérer le l'id du tenant à utiliser par Hibernate à partir d'une
 * requete HTTP
 * 
 * @author sylvain.cailleau
 */
public class RequestTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

	/**
	 * La requete a utiliser pour retrouver l'id La requete est fournie par Spring
	 * Pour cela cet objet doit etre déclaré comme bean avec le scope 'request'
	 */
	@Autowired
	private HttpServletRequest request;

	/**
	 * Identifiant à fournir si aucun identifiant n'est trouve dans la requete
	 */
	@Value("${datasource.multi.master.defaultid:AVPNAV}")
	private String defaultTenantIdentifier;

	/**
	 * Le parametre de la requete à utiliser pour recupérer l'id
	 */
	@Value("${datasource.multi.master.rqtparam:nomBase}")
	private String requestParameter;

	@Override
	public String resolveCurrentTenantIdentifier() {
		String tenantIdentifier = null;

		if (request != null) {
			tenantIdentifier = request.getParameter(requestParameter);
		}

		if (tenantIdentifier == null || tenantIdentifier.isEmpty()) {
			tenantIdentifier = defaultTenantIdentifier;
		}

		return tenantIdentifier;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

	public void setDefaultTenantIdentifier(String defaultTenantIdentifier) {
		this.defaultTenantIdentifier = defaultTenantIdentifier;
	}

	public void setRequestParameter(String requestParameter) {
		this.requestParameter = requestParameter;
	}

}
