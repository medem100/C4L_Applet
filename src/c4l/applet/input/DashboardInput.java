package c4l.applet.input;


import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.io.*;
import org.json.*;

import c4l.applet.main.Constants;
/**
 * Fragt die Api vom c4l Server ab und sammelt die 
 */
public class DashboardInput {
	
	private Boolean[] devices = new Boolean[Constants.DYNAMIC_DEVICES];
	private int[] faders = new int[Constants.DEVICE_CHANNELS];
	private int effectSize;
	private int effectSpeed;
	private int effect= 0 ; // 0 = kein Effect  
	private int caseID;
	ArrayList<Integer> scenenID = new ArrayList<>();
	
	
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
		return effect ;
	}
	
	public int getCaseID() {
		return caseID ;
	}
	
	public ArrayList<Integer> getScenenID() {
		return scenenID ;
	}
	
	public int getFader(int index) {
		if ((index < 0) || (index > Constants.DEVICE_CHANNELS -1)) throw new IndexOutOfBoundsException("You can only get a Fader for index 0 to 15");
		return faders[index];
	}
	
	public int getDevice(int index) {
		if ((index < 0) || (index > Constants.DYNAMIC_DEVICES -1)) throw new IndexOutOfBoundsException("You can only get a device for index 0 to 29");
		return faders[index];
	}
	
	
	public void tick() {
		// TODO senden das die EFFect taste gelesen wurde
		JSONObject newValues = getResponse();
		JSONArray jsonFader = newValues.getJSONArray("fader");
		JSONArray jsonDevices = newValues.getJSONArray("devices");
		JSONArray jsonScenenID = newValues.getJSONArray("scenenID");
		
		effect = newValues.getInt("effect");
		effectSpeed = newValues.getInt("effectSpeed");
		effectSize = newValues.getInt("effectSize");
		caseID = newValues.getInt("caseID");
		
		for (int i = 0 ; i <= jsonFader.length(); i++)
			faders[i] = jsonFader.getInt(i);
		
		for (int i = 0 ; i <= jsonScenenID.length(); i++)
			scenenID.add(jsonScenenID.getInt(i));
		
		
		// ein bisher nicht min. 1 mal angewähltes device hat null 
		for (int i = 0 ; i <= jsonDevices.length(); i++) {
			if(jsonDevices.get(i) == null || !(jsonDevices.getBoolean(i))) {
				devices[i] = false;
			}else {
				devices[i] = true;
			}
		}
		
	}
	
	
	
	
	
	// Hilfs funcktzionen 
	
	
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
