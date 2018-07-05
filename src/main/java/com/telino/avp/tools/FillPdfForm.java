package com.telino.avp.tools;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.service.journal.TamponHorodatageService;

public class FillPdfForm {


	public static byte[] buildAttestation(String operation, Document document) {
		FillPdfForm fillForm = new FillPdfForm();
		byte[] attestation = fillForm.getAttestationFilled(document, operation);
		return attestation;
	}

	public static byte[] buildAttestation(String operation, String action) {
		FillPdfForm fillForm = new FillPdfForm();
		byte[] attestation = fillForm.getAttestationFilled(operation, action);
		return attestation;
	}

	/**
	 * Création du pdf de l'attestation concernant uen archive
	 * 
	 * @param document
	 *            archive concernée par l'attestation
	 * @param operation
	 *            type d'attestation à créer
	 * @return le contenu de l'attestation
	 */
	public byte[] getAttestationFilled(Document document, String operation) {
		PdfReader pdfTemplate = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		PdfStamper stamper = null;
		try {
			pdfTemplate = new PdfReader(getClass().getResource("/attestation/attestation_form.pdf").getFile());
			// pdfTemplate = new PdfReader("C:/Users/julie.maran/Desktop/test_form.pdf");
			byteArrayOutputStream = new ByteArrayOutputStream();
			stamper = new PdfStamper(pdfTemplate, byteArrayOutputStream);
			stamper.setFormFlattening(true);
			stamper.getAcroFields().setField("operation", operation);
			stamper.getAcroFields().setField("timestamp", TamponHorodatageService.getSystemIsoFormatZonedTimeStamp());
			stamper.getAcroFields().setField("title", document.getTitle());
			stamper.getAcroFields().setField("docid", document.getDocId().toString());
			stamper.getAcroFields().setField("empreinte", document.getEmpreinte().getEmpreinte());
			stamper.getAcroFields().setField("metadata", document.getKeywords());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			try {
				stamper.close();
			} catch (Exception e) {
			}
			try {
				pdfTemplate.close();
			} catch (Exception e) {
			}
			try {
				byteArrayOutputStream.close();
			} catch (Exception e) {
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Création du pdf de l'attestation concernant un profil d'archivage
	 * 
	 * @param operation
	 *            type d'attestation à créer
	 * @param action
	 *            modification du profil d'archivage
	 * @return le contenu de l'attestation
	 */
	public byte[] getAttestationFilled(String operation, String action) {
		PdfReader pdfTemplate = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		PdfStamper stamper = null;
		try {
			pdfTemplate = new PdfReader(getClass().getResource("/attestation/attestation_form2.pdf").getFile());
			byteArrayOutputStream = new ByteArrayOutputStream();
			stamper = new PdfStamper(pdfTemplate, byteArrayOutputStream);
			stamper.setFormFlattening(true);
			stamper.getAcroFields().setField("operation", operation);
			stamper.getAcroFields().setField("timestamp", TamponHorodatageService.getSystemIsoFormatZonedTimeStamp());
			stamper.getAcroFields().setField("action", action);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			try {
				stamper.close();
			} catch (Exception e) {
			}
			try {
				pdfTemplate.close();
			} catch (Exception e) {
			}
			try {
				byteArrayOutputStream.close();
			} catch (Exception e) {
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

}
