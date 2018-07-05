package com.telino.avp.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;



public class LogArchivageEvent {

	private URLConnection  con = null;
	private ObjectOutputStream oos;
	private OutputStream outstream;


	/**
	 * Appel au web service
	 */
	public  String Call(String request, String tomcatlocation) {
		
		String  result = "";
		
		try {
			if (tomcatlocation.startsWith("https:"))
			con = (HttpsURLConnection) getSSLServletConnection(tomcatlocation);
			else con = (URLConnection) getServletConnection(tomcatlocation);
			con.connect();
			outstream = con.getOutputStream();
			oos = new ObjectOutputStream(outstream);
			oos.writeObject(request);
			oos.flush();
			oos.close();
			

			// receive result from servlet
			InputStream instr = con.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			result = (String ) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();
			return (result);

		} catch (Exception ex) {
			ex.printStackTrace();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.accumulate("codeRetour", "KO");
				jsonObject.accumulate("messageErreur", "Serveur non disponible");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result = jsonObject.toString();
		}
		return result;
	}
	
	/**
	 * Connexion au webservice (http)
	 * @param tomcatlocation
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static URLConnection getServletConnection(String tomcatlocation)
			throws MalformedURLException, IOException {
			String sep = "?";
			if (tomcatlocation.contains("?")) sep = "&";
			String url = tomcatlocation+ sep+"id="+ Thread.currentThread().getId();
			URL urlServlet = new URL(url);
			URLConnection con = urlServlet.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			
			con.setRequestProperty(
				"Content-Type",
				"application/x-java-serialized-object");

			return con;
		}

	private static HttpsURLConnection getSSLServletConnection(String tomcatlocation)
			throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException {
			String sep = "?";
			if (tomcatlocation.contains("?")) sep = "&";
			String url = tomcatlocation+ sep+"id="+ Thread.currentThread().getId();
		       TrustManager[] trustAllCerts = new X509TrustManager[] { new X509TrustManager() {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		            public void checkClientTrusted(X509Certificate[] certs, String authType) {
		            }
		            public void checkServerTrusted(X509Certificate[] certs, String authType) {
		            }
		        } };
		        // Install the all-trusting trust manager
		        final SSLContext sc = SSLContext.getInstance("SSL");
		        sc.init(null, trustAllCerts, new java.security.SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		        // Create all-trusting host name verifier
		        HostnameVerifier allHostsValid = new HostnameVerifier() {

					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
		        };

		        // Install the all-trusting host verifier
		        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			URL myurl = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			
			con.setRequestProperty(
				"Content-Type",
				"application/x-java-serialized-object");

			return con;
		}
	
	public static void main(String args[])  {
		

		//arg[0] url
		//arg[1] logfile
		
		if (args==null || args.length<7) {
			System.out.println("Paramètres incorrects");
			System.out.println("param1: url, param2: fichier log");
			System.out.println("param3: nombase, param4: origine");
			System.out.println("param5: application, param6: level");
			System.out.println("param7: action");
			return;
		}
		
		
	    FileInputStream ficin;
		String filename = args[1];
		try {
			ficin = new FileInputStream(filename);
			BufferedReader d = new BufferedReader(new InputStreamReader(ficin));
			String buff = d.readLine();
			while (buff != null) {
				LogArchivageEvent ES = new LogArchivageEvent();
				
//				HashMap<String,Object> map = new HashMap<String,Object>();
				JSONObject jsonObject = new JSONObject();
				
				
				
		
				//fichier à indexer
		
		
				try {
					jsonObject.accumulate("nomBase",args[2]);
					jsonObject.accumulate("origin",args[3]); 
					jsonObject.accumulate("application",args[4]); 
					jsonObject.accumulate("level",args[5]); 
					jsonObject.accumulate("action",args[6]);
					jsonObject.accumulate("détail",buff);
					
					jsonObject.accumulate("elasticRequest","put()");
				}
				catch (Exception e) {}
				
				//adresse du webservice
				
				String urlServeur = args[0];
				System.out.println(jsonObject.toString());
				//Appel du webservice avec la map d'appel - retourne un hashmap
				String result = ES.Call(jsonObject.toString(), urlServeur);
				
				
				if (result.length()>0) {
					try {
						JSONObject resultat;
					
						resultat = new JSONObject(result);
						String codeRetour = resultat.getString("codeRetour");
					
					
						if (codeRetour.equals("KO")) {
							String messageErreur = resultat.getString("messageErreur").toString();
							System.out.println(codeRetour + " reason : "+messageErreur);
						}
						else {
							System.out.println("OK");
							System.out.println("documentID: " + resultat.getString("documentID"));
						}
					} catch (JSONException e) {
						System.out.println(result);
						e.printStackTrace();
					}
		
				}
				buff = d.readLine();
			}
			d.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
