package com.telino.avp.tools;

import java.io.IOException;
import java.io.InputStream;
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

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class RemoteCall {

	// Il faut evider ces deux attributs
	private String limit = "500";
	private String maxTime = "5";

	public Object callServletWithParam(String request, String string, String nomBase, String limit, String maxTime) throws ClassNotFoundException, IOException {
		this.limit = limit;
		this.maxTime = maxTime;
		return callServlet(request, string, nomBase);

	}

	public Object callServlet(Object request, String tomcatlocation, String nomBase) throws IOException, ClassNotFoundException {
		Object result = null;
		URLConnection con = null;

		if (tomcatlocation.startsWith("https:")) {
			try {
				con = (HttpsURLConnection) getSSLServletConnection(tomcatlocation, nomBase);
			} catch (Exception e) {
				con = getServletConnection(tomcatlocation, nomBase);
			}
		} else {
			con = getServletConnection(tomcatlocation, nomBase);
		}
		con.connect();

		try (OutputStream outstream = con.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outstream)) {
			oos.writeObject(request);
			oos.flush();
		}

		// receive result from servlet
		try (InputStream instr = con.getInputStream();
				ObjectInputStream inputFromServlet = new ObjectInputStream(instr)) {
			result = inputFromServlet.readObject();
			inputFromServlet.close();
		}

		return result;
	}

	public Object callServletWithJsonObject(JSONObject jsondata, String tomcatlocation)
			throws IOException, ClassNotFoundException {
		Object result = null;
		URLConnection con = null;

		if (tomcatlocation.startsWith("https:")) {

			try {
				con = (HttpsURLConnection) getSSLServletConnection(tomcatlocation);
			} catch (Exception e) {
				con = getServletConnection(tomcatlocation);
			}
		} else {
			con = getServletConnection(tomcatlocation);
		}
		con.connect();

		try (OutputStream outstream = con.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outstream)) {
			oos.writeObject(jsondata.toString());
			oos.flush();
		}

		// receive result from servlet
		try (InputStream instr = con.getInputStream();
				ObjectInputStream inputFromServlet = new ObjectInputStream(instr)) {
			result = inputFromServlet.readObject();
			inputFromServlet.close();
		}

		return result;
	}

	private URLConnection getServletConnection(String tomcatlocation, String nomBase)
			throws MalformedURLException, IOException {
		String sep = "?";
		if (tomcatlocation.contains("?"))
			sep = "&";
		String url = tomcatlocation + sep + "id=" + Thread.currentThread().getId() + "&nomBase=" + nomBase;
		url += "&limit=" + limit + "&maxtime=" + maxTime;
		URL urlServlet = new URL(url);
		URLConnection con = urlServlet.openConnection();

		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);

		con.setRequestProperty("Content-Type", "application/x-java-serialized-object");

		return con;
	}

	private URLConnection getServletConnection(String tomcatlocation) throws MalformedURLException, IOException {
		String sep = "?";
		if (tomcatlocation.contains("?"))
			sep = "&";
		String url = tomcatlocation + sep + "id=" + Thread.currentThread().getId();
		URL urlServlet = new URL(url);
		URLConnection con = urlServlet.openConnection();

		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);

		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept-Charset", "UTF-8");

		return con;
	}

	private HttpsURLConnection getSSLServletConnection(String tomcatlocation, String nomBase)
			throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException {
		String sep = "?";
		if (tomcatlocation.contains("?"))
			sep = "&";
		String url = tomcatlocation + sep + "id=" + Thread.currentThread().getId() + "&nomBase=" + nomBase;
		url += "&limit=" + limit + "&maxtime=" + maxTime;
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

		con.setRequestProperty("Content-Type", "application/x-java-serialized-object");

		return con;
	}

	private HttpsURLConnection getSSLServletConnection(String tomcatlocation)
			throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException {
		String sep = "?";
		if (tomcatlocation.contains("?"))
			sep = "&";
		String url = tomcatlocation + sep + "id=" + Thread.currentThread().getId();
		url += "&limit=" + limit + "&maxtime=" + maxTime;
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

		con.setRequestProperty("Content-Type", "application/x-java-serialized-object");

		return con;
	}

}
