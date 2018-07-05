package com.telino.avp.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.telino.avp.entity.auxil.Journal;
import com.telino.avp.exception.AvpExploitException;

public class BuildXmlFile {

	public static byte[] buildLogFile(Map<String, String> logData, Map<String, String> entryMatch,
			List<? extends Journal> dataEntry) throws AvpExploitException {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Log");
			doc.appendChild(rootElement);
			Element attributes = doc.createElement("Attributes");

			// affecte les métadata du journal
			Set<Entry<String, String>> setLogdata = logData.entrySet();
			Iterator<Entry<String, String>> itLogData = setLogdata.iterator();
			while (itLogData.hasNext()) {
				Entry<String, String> entryLogData = itLogData.next();
				Element element = doc.createElement(entryLogData.getKey());
				element.appendChild(doc.createTextNode(entryLogData.getValue()));
				attributes.appendChild(element);
			}

			rootElement.appendChild(attributes);

			// affecte les entré du journal
			for (Journal journal : dataEntry) {
				Element logEntry = doc.createElement("LogEntry");

				for (Entry<String, String> entryEntryMatch : entryMatch.entrySet()) {
					try {
						// Invoquer les methodes pour extraire attributs
						Class<?> clazz = journal.getClass();
						Method method = clazz.getMethod(entryEntryMatch.getValue());
						Object result = method.invoke(journal);

						Element element = doc.createElement(entryEntryMatch.getKey());

						if (entryEntryMatch.getKey().equals("Timestamp")) {
							// mise en forme de la date ISO
							element.appendChild(doc.createTextNode(
									((ZonedDateTime) result).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
						} else if (entryEntryMatch.getKey().equals("TimestampToken")) {
							element.appendChild(
									doc.createTextNode(Base64.getEncoder().encodeToString((byte[]) result)));
						} else {
							element.appendChild(doc.createTextNode(String.valueOf(result)));
						}

						logEntry.appendChild(element);
					} catch (NoSuchMethodException | SecurityException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException e) {
						throw new AvpExploitException("619", e, "Invoquer les methods de Journal", null, null,
								logData.get("LogID"));
					} 
				}

				rootElement.appendChild(logEntry);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				StreamResult result = new StreamResult(bos);
				// StreamResult result = new StreamResult(System.out);

				transformer.transform(source, result);

				return bos.toByteArray();
			} 

		} catch (IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AvpExploitException("619", e, "Création du fichier xml de stockage du journal", null, null,
					logData.get("LogID"));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AvpExploitException("619", e, "Recupération du contenu du fichier xml de stockage du journal",
					null, null, logData.get("LogID"));
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AvpExploitException("619", e, "Mise à jour des données du fichier xml de stockage du journal",
					null, null, logData.get("LogID"));
		}

	}
}
