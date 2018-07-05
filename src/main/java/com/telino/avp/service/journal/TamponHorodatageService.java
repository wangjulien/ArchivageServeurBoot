package com.telino.avp.service.journal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertStoreException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.bc.BcRSASignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPValidationException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.telino.avp.entity.auxil.Journal;

@Component
public class TamponHorodatageService {
	
//	public final static DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ");

	private static final Logger LOGGER = LoggerFactory.getLogger(TamponHorodatageService.class);

	private static final String OCS_URL = "http://time.certum.pl/";

	/**
	 * Envoie une requete au service d'horodatage pour récupérer un TimeStamp et
	 * l'affecte à l'object journal
	 * 
	 * @throws Exception
	 */
	public void demanderTamponHorodatage(final Journal journal) throws Exception {
		if (null == journal.getHash()) {
			throw new Exception("Impossible de demander un tampon d'horodatage sans digest");
		}

		TimeStampRequestGenerator reqgen = new TimeStampRequestGenerator();
		reqgen.setCertReq(true);
		TimeStampRequest req = reqgen.generate(TSPAlgorithms.SHA1, journal.getHash().getBytes());
		byte request[] = req.getEncoded();
		// byte[] test = req.getMessageImprintDigest(); // DEBUG info

		URL url = new URL(OCS_URL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-type", "application/timestamp-query");
		con.setRequestProperty("Content-length", String.valueOf(request.length));

		try (OutputStream out = con.getOutputStream()) {
			out.write(request);
			out.flush();
		}

		LOGGER.debug("HTTP return code is : {}", con.getResponseCode());
		if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException("Received HTTP error: " + con.getResponseCode() + " - " + con.getResponseMessage());
		}

		TimeStampToken token = null;
		try (InputStream in = con.getInputStream(); ASN1InputStream asnIs = new ASN1InputStream(in)) {
			TimeStampResp resp = TimeStampResp.getInstance(asnIs.readObject());
			TimeStampResponse response = new TimeStampResponse(resp);
			response.validate(req);
			token = response.getTimeStampToken();
		}

		if (null == token) {
			throw new Exception("Aucun TimeStampToken récupéré");
		} else {
			if (token.getTimeStampInfo() == null) {
				throw new Exception("Aucun TimeStampInfo récupéré");
			} else {
				journal.setTimestampToken(token);
				journal.setHorodatage(token.getTimeStampInfo().getGenTime());
			}
		}

	}

	/**
	 * Verifie que la validité de la signature et du tampon d'horodatage
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws CMSException
	 * @throws CertStoreException
	 * @throws OperatorCreationException
	 * @throws TSPException
	 * @throws TSPValidationException
	 */
	public void verifyTamponHorodatage(final Journal journal)
			throws TSPValidationException, TSPException, OperatorCreationException, CMSException {
		Security.addProvider(new BouncyCastleProvider());
		Collection<SignerInformation> signers = journal.getTimestampToken().toCMSSignedData().getSignerInfos().getSigners();
		SignerInformation timeStampSigner = (SignerInformation) signers.iterator().next();

		Store<X509CertificateHolder> store = journal.getTimestampToken().getCertificates();
		X509CertificateHolder cert = store.getMatches((Selector<X509CertificateHolder>) journal.getTimestampToken().getSID())
				.iterator().next();

		SignerInformationVerifier verifier = new BcRSASignerInfoVerifierBuilder(
				new DefaultCMSSignatureAlgorithmNameGenerator(), new DefaultSignatureAlgorithmIdentifierFinder(),
				new DefaultDigestAlgorithmIdentifierFinder(), new BcDigestCalculatorProvider()).build(cert);
		if (!timeStampSigner.verify(verifier)) {
			throw new RuntimeException("Signature is not OK");
		}
		journal.getTimestampToken().validate(verifier);
	}

	/**
	 * Récupère les informations du timestamp stocké en base et affecte au journal
	 * l'objet timestamptoken
	 * 
	 * @throws CMSException
	 * @throws TSPException
	 * @throws IOException
	 */
	public void initTamponHorodatage(final Journal journal)
			throws CMSException, TSPException, IOException {
		CMSSignedData dataToken = new CMSSignedData(journal.getTimestampTokenBytes());
		journal.setTimestampToken(new TimeStampToken(dataToken));
	}
	
	
	public static String getSystemIsoFormatZonedTimeStamp() {
		return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
	
	public static ZonedDateTime convertToSystemZonedDateTime(final Date date) {		
		return Objects.isNull(date) ? null : date.toInstant().atZone(ZoneId.systemDefault());
	}
}
