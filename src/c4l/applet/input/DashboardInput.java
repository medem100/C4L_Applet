package c4l.applet.input;


import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.io.*;
import org.json.*;

import c4l.applet.main.Constants;
/**
 * Fragt die Api vom c4l Server ab und sammelt die 
 */
public final class DashboardInput {
	
	private Boolean[] devices = new Boolean[Constants.DYNAMIC_DEVICES];
	private int[] faders = new int[Constants.DEVICE_CHANNELS];
	private int effectSize;
	private int effectSpeed;
	private int effectID = 0 ; // 0 = kein Effect  
	private int caseID;
	private int[] scenenID;
	
	
	public int[] getFaders() {
		return faders;
	}
	
	
	// Getter
	
	public Boolean[] getDevices() {
		return devices;
	}
	
	public int[] getFader() {
		return faders;
	}
	
	public int getEffectSize() {
		return effectSize ;
	}
	
	public int getEffectSpeed() {
		return effectSpeed ;
	}
	
	public int getEffectID() {
		return effectID ;
	}
	
	public int getCaseID() {
		return caseID ;
	}
	
	public int[] getScenenID() {
		return scenenID ;
	}
	
	public int getFader(int index) {
		if ((index < 0) || (index > Constants.DEVICE_CHANNELS -1)) throw new IndexOutOfBoundsException("You can only get a Fader for index 0 to 15");
		return faders[index];
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
    private static String readStringFromUrl(String url) { 
    URL readURL;
    String response = "";
	try {
		readURL = new URL(url);
	
    BufferedReader in = new BufferedReader(
    new InputStreamReader(readURL.openStream()));

    String inputLine;
    while ((inputLine = in.readLine()) != null)
    	response = response + inputLine ;
    in.close();
    
    return response;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
    }
    
    public static JSONObject getResponse() {
    	String ResponsString;
		ResponsString = readStringFromUrl(Constants.SERVER_ADDRESS+"/"+Constants.INFORMATIONPFAD);
    	return new JSONObject(ResponsString);
    }
    

}
