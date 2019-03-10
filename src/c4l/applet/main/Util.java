package c4l.applet.main;

public final class Util {

	public static Boolean getTestRun() {
		if (!(System.getProperty("TestRun") == null)) {
			return Boolean.valueOf(System.getProperty("TestRun"));
		} else {
			return false; // it isn´t a Test Run When the the Property isn´t set  
		}

	}
	/*
	public static Boolean getServerAvailable() {
	//	httpResponse.getStatusLine().getStatusCode()
	}*/

}
