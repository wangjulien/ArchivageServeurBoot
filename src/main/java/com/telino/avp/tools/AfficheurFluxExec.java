package com.telino.avp.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AfficheurFluxExec implements Runnable {

	private final InputStream inputStream;
	private String retour;

	public AfficheurFluxExec(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	private BufferedReader getBufferedReader(InputStream is) {
		return new BufferedReader(new InputStreamReader(is));
	}

	@Override
	public void run() {
		BufferedReader br = getBufferedReader(inputStream);
		String ligne = "";
		String array = null;
//		int i=0;
		try {
			while ((ligne = br.readLine()) != null) {
				System.out.println(ligne);
				array += ligne;
//				i++;
			}
			this.setRetour(array);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getRetour() {
		return retour;
	}

	public void setRetour(String retour) {
		this.retour = retour;
	}
}