package com.telino.avp.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import CdmsApi.client.CdmsApi_Out;
import CdmsApi.client.CdmsApi_in;

/**
 * Class utilitaire de lecture et ecriture de HTTP requet avec CdmsApi_in/out
 * objet
 * 
 * @author jwang
 *
 */
public class CdmsApiServletRequestIO {

	private static final Logger LOGGER = LoggerFactory.getLogger(CdmsApiServletRequestIO.class);

	public static CdmsApi_in lectureCdmsObj(final HttpServletRequest request) throws Exception {
		try (InputStream in = request.getInputStream(); ObjectInputStream inputFromApplet = new ObjectInputStream(in)) {
			CdmsApi_in trame = (CdmsApi_in) inputFromApplet.readObject();
			LOGGER.info("Lecture de http request : " + trame);
			return trame;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	public static void ecritureCdmsObj(final HttpServletResponse response, final CdmsApi_Out mareponse)
			throws IOException {
		LOGGER.info("Ecriture de http request : " + mareponse.getRetour());

		try (OutputStream outstr = response.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outstr);) {
			oos.writeObject(mareponse);
			oos.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	public static Object lecture(final HttpServletRequest request) throws Exception {
		try (InputStream in = request.getInputStream(); ObjectInputStream inputFromApplet = new ObjectInputStream(in)) {
			Object trame = inputFromApplet.readObject();
			LOGGER.info("Lecture de http request : " + trame);
			return trame;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	public static void ecriture(final HttpServletResponse response, final Object mareponse) throws IOException {
		LOGGER.info("Ecriture de http request : " + mareponse);

		try (OutputStream outstr = response.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outstr);) {
			oos.writeObject(mareponse);
			oos.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public static void ecriture(final HttpServletResponse response, Object mareponse, final boolean isMap) throws IOException {

		if (!isMap) {
			mareponse = mapToJson((HashMap<String, Object>) mareponse);
		}
		ecriture(response, mareponse);
	}

	private static String mapToJson(final Map<String, Object> mareponse) {
		JSONObject jsonObject = new JSONObject();
		Iterator<String> IT = mareponse.keySet().iterator();

		while (IT.hasNext()) {

			String key = IT.next();
			Object value = mareponse.get(key);

			if (value != null) {
				if (value instanceof String)
					jsonObject.accumulate(key, value);
				else if (value instanceof Integer)
					jsonObject.accumulate(key, value);
				else if (value instanceof Boolean)
					jsonObject.accumulate(key, value);
				else if (value instanceof LinkedList) {
					// CdmsLogger.log(logger,key + " is linkedList");
					@SuppressWarnings("unchecked")
					List<HashMap<String, Object>> data = (LinkedList<HashMap<String, Object>>) value;

					// JSONArray array = new JSONArray("list");
					for (int i = 0; i < data.size(); i++) {
						JSONObject line = new JSONObject();
						Iterator<String> IT1 = data.get(i).keySet().iterator();
						while (IT1.hasNext()) {
							String fieldName = IT1.next();
							line.accumulate(fieldName, data.get(i).get(fieldName));
						}
						// CdmsLogger.log(logger,jsonObject.toString());
						jsonObject.accumulate("list", line);
					}

				} else {
					LOGGER.info("non traité - nfz42013");
				}
			}
		}
		// CdmsLogger.log(logger,jsonObject.toString());
		return jsonObject.toString();
	}

	@SuppressWarnings("unchecked")
	public static Object formatToMap(final Object inputObject, final HttpServletRequest request) {
		Object input = null;

		if (((String) inputObject).length() == 0) {
			// cas ou le post ne donne pas les valeurs dans le stream
			Map<String, String[]> B = request.getParameterMap();
			Iterator<String> IT0 = B.keySet().iterator();
			while (IT0.hasNext()) {
				String key0 = IT0.next();
				if (key0.contains("{")) {
					key0 = key0.substring(key0.indexOf("{"));
					input = getMap(new JSONObject((String) key0));
				}
				// else input.put(key0, B.get(key0));
			}
		} else {
			JSONObject jsonObject = new JSONObject((String) inputObject);

			boolean multiFound = false;
			Object tst = jsonObject.get("multi");
			if (tst != null)
				multiFound = true;

			if (multiFound) {
				JSONArray jsonArray = jsonObject.getJSONArray("multi");
				input = new LinkedList<HashMap<String, Object>>();
				for (int i = 0; i < jsonArray.length(); i++) {
					((LinkedList<Map<String, Object>>) input)
							.add(getMap(new JSONObject((String) jsonArray.get(i).toString())));
				}
			} else {
				input = getMap(jsonObject);
			}
		}
		return input;
	}

	private static Map<String, Object> getMap(final JSONObject jsonObject) throws JSONException {

		Map<String, Object> trame = new HashMap<>();
		Iterator<String> IT = jsonObject.keys();

		while (IT.hasNext()) {
			String key = IT.next();
			Object value = jsonObject.get(key);
			if (value != null) {
				if (value.toString().equals("true"))
					value = true;
				else if (value.toString().equals("false"))
					value = false;
				else if (key.equals("fileContent")) {
					// value = Base64.getDecoder().decode((byte[])
					// value.toString().getBytes());
					// elasticTaille = ((byte[]) value).length;
				}
			}
			trame.put(key, value);
		}
		return trame;
	}
	

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toHashMap(String request) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		request = request.substring(1, request.length() - 1);
		int indexMap = request.indexOf("{");
		HashMap<String, String> map1 = new HashMap<String, String>();
		if (indexMap > -1) {
			int indexMapF = request.indexOf("}");
			String debut = request.substring(0, indexMap);
			int indexDebMapName = debut.lastIndexOf(" ");
			int indexFinMapName = debut.lastIndexOf("=");
			String mapName = debut.substring(indexDebMapName, indexFinMapName).trim();
			if (indexMapF > -1) {
				String valuemap = request.substring(indexMap + 1, indexMapF);
				// CdmsLogger.log(logger,map1);
				if (valuemap.length() > 0) {
					request = request.substring(0, indexMap + 1) + "$" + mapName + request.substring(indexMapF);
					map1.put(mapName, valuemap);
				}
			}
		}
		String[] terms = request.split(", ", -1);
		for (int i = 0; i < terms.length; i++) {
			// CdmsLogger.log(logger,terms[i], "nfz42013");
			int index = terms[i].indexOf("=");
			if (index > -1) {
				String param = terms[i].substring(0, index);
				String valeur = terms[i].substring(index + 1);
				Object value = valeur;
				if (valeur.equals("false"))
					value = false;
				if (valeur.equals("true"))
					value = true;
				if (valeur.startsWith("{")) {
					value = new HashMap<String, Object>();
					// valeur = valeur.substring(1,request.length()-1);
					if (map1.get(param) != null && map1.get(param).length() > 0) {
						String[] extraValues = map1.get(param).split(", ", -1);

						for (int j = 0; j < extraValues.length; j++) {
							String[] subTerms = extraValues[j].split("=", -1);
							String subParam = subTerms[0];
							String subValeur = subTerms[1];
							Object subValue = subValeur;
							if (subValeur.equals("false"))
								subValue = false;
							if (subValeur.equals("true"))
								subValue = true;
							((HashMap<String, Object>) value).put(subParam, subValue);
						}
					}

				}
				if (param.equals("secuLevel") && valeur.length() > 0)
					value = Integer.parseInt(valeur);
				if (param.equals("searchType") && valeur.length() > 0)
					value = Integer.parseInt(valeur);
				if (param.equals("elasticFrom") && valeur.length() > 0)
					value = Integer.parseInt(valeur);
				if (param.equals("communicationid") && valeur.length() > 0)
					value = Integer.parseInt(valeur);
				// communicationid
				map.put(param, value);
			}
		}
		// CdmsLogger.log(logger,map);
		return map;

	}
}
