package com.telino.avp.tools;

public class ServerProc {

	public static String password1 = "PASSWORD1";
	public static String password2 = "PASSWORD2";

	public static String GetUrl(String port, String host) {
		String http = "http://";
		if (port.contains("44")) http = "https://";
   		String url = "";
		if (host.contains("telino") && port.contains("44")) {
			url = http + host+ "/";
		}
		else {
			url = http + host+ ":"+ port+ "/";
		}
		return url;

	}
}
