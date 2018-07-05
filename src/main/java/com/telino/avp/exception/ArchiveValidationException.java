/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.telino.avp.exception;

/**
 * Erreur lors de la création, mise à jour oou validation d'un objet Archive
 * 
 * @author sylvain.cailleau
 */
public class ArchiveValidationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8742964193225264070L;

	public ArchiveValidationException() {
    }

    public ArchiveValidationException(String message) {
        super(message);
    }

    public ArchiveValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchiveValidationException(Throwable cause) {
        super(cause);
    }

    public ArchiveValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
