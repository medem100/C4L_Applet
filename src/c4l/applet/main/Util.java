package c4l.applet.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public final class Util {
	private static Logger Log = Logger.getRootLogger();
	/**
	 * Return the status of the Program
	 * @return
	 */
	public static Boolean getTestRun() {
		if (!(System.getProperty("TestRun") == null)) {
			return Boolean.valueOf(System.getProperty("TestRun"));
		} else {
			return false; // it isn´t a Test Run When the the Property isn´t set
		}

	}
	
	public static Boolean getServerAvalibal() {
		try {
		URL url = new URL("http://example.com");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		 if(connection.getResponseCode() == 200 ) {
			 return true;
		 }else {
			 return false;
		 }
		
		}catch (IOException e) {
			Log.error("Fail to check Server avalibale -> wrong path", e);
			return false;
		}
		
	}
	/*
	 * public static Boolean getServerAvailable() { //
	 * httpResponse.getStatusLine().getStatusCode() }
	 */

}
