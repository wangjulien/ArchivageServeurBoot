package com.telino.avp.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.telino.avp.entity.archive.Document;
import com.telino.avp.service.journal.TamponHorodatageService;

public class FillPdfForm {

	/**
	 * Création du pdf de l'attestation concernant uen archive
	 * 
	 * @param document
	 *            archive concernée par l'attestation
	 * @param operation
	 *            type d'attestation à créer
	 * @return le contenu de l'attestation
	 */
	public static byte[] getAttestationFilled(final String operation, final Document document) {
		try (InputStream is = FillPdfForm.class.getResourceAsStream("/attestation/attestation_form.pdf");
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

			Map<String, String> inputs = new HashMap<>();
			inputs.put("operation", operation);
			inputs.put("timestamp", TamponHorodatageService.getSystemIsoFormatZonedTimeStamp());
			inputs.put("title", document.getTitle());
			inputs.put("docid", document.getDocId().toString());
			inputs.put("empreinte", document.getEmpreinte().getEmpreinte());
			inputs.put("metadata", document.getKeywords());

			fillPdfForm(is, byteArrayOutputStream, inputs);

			return byteArrayOutputStream.toByteArray();
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
			return null;
		}
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
	public static byte[] getAttestationFilled(final String operation, final String action) {
		try (InputStream is = FillPdfForm.class.getResourceAsStream("/attestation/attestation_form2.pdf");
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

			Map<String, String> inputs = new HashMap<>();
			inputs.put("operation", operation);
			inputs.put("timestamp", TamponHorodatageService.getSystemIsoFormatZonedTimeStamp());
			inputs.put("action", action);

			fillPdfForm(is, byteArrayOutputStream, inputs);

			return byteArrayOutputStream.toByteArray();
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void fillPdfForm(final InputStream is, final OutputStream out, final Map<String, String> inputs)
			throws IOException, DocumentException {
		PdfReader pdfTemplate = null;
		PdfStamper stamper = null;

		try {
			pdfTemplate = new PdfReader(is);
			stamper = new PdfStamper(pdfTemplate, out);
			stamper.setFormFlattening(true);
			// fill all the input information into the PDF template
			for (Entry<String, String> me : inputs.entrySet()) {
				stamper.getAcroFields().setField(me.getKey(), me.getValue());
			}
		} finally {
			try {
				stamper.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				pdfTemplate.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
