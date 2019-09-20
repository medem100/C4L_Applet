package c4l.applet.input;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.io.*;

import org.apache.log4j.Logger;
import org.json.*;

import c4l.applet.main.Constants;
import sun.util.logging.resources.logging;

/**
 * Get Data from The C4L Server
 */
public class DashboardInput {

	private Boolean[] devices = new Boolean[Constants.DYNAMIC_DEVICES];
	private int[] faders = new int[Constants.DEVICE_CHANNELS];
	private int effectSize;
	private int effectSpeed;
	private String effect; // 0 = kein Effect
	private int caseID;
	public JSONObject usedRespons = new JSONObject();
	ArrayList<Integer> scenenID = new ArrayList<>();
	private static boolean savePresst;

	private Logger Log = Logger.getLogger(DashboardInput.class);

	public int[] getFaders() {
		return faders;
	}

	// Getter
	
	public boolean isSavePresst() {
		setSaveRead();
		return savePresst;
	}

	public Boolean[] getDevices() {
		return devices;
	}

	public int[] getFader() {
		return faders;
	}

	public int getEffectSize() {
		setEffectRead(); //TODO thats Ugly
		return effectSize;
	}

	public int getEffectSpeed() {
		return effectSpeed;
	}

	public String getEffectID() {
		setEffectRead();
		return effect;
	}

	public int getCaseID() {
		return caseID;
	}

	public ArrayList<Integer> getScenenID() {
		return scenenID;
	}

	public int getFader(int index) {
		if ((index < 0) || (index > Constants.DEVICE_CHANNELS - 1))
			throw new IndexOutOfBoundsException("You can only get a Fader for index 0 to 15");
		return faders[index];
	}

	public int getDevice(int index) {
		if ((index < 0) || (index > Constants.DYNAMIC_DEVICES - 1))
			throw new IndexOutOfBoundsException("You can only get a device for index 0 to 29");
		return faders[index];
	}

	/**
	 * Return All chosen Values in the Dashboard
	 * 
	 * @return all chosen Devices
	 */
	public ArrayList<Integer> getChosenDevices() {
		ArrayList<Integer> chosenDevices = new ArrayList<>();
		for (int i = 0; i < devices.length; i++) {
			if (devices[i])
				chosenDevices.add(i);
		}
		return chosenDevices;
	}

	/**
	 * Set new Values to the dasboard
	 */
	public void tick() {
		// TODO senden das die EFFect taste gelesen wurde
		usedRespons = getResponse();
		JSONArray jsonFader = usedRespons.getJSONArray("fader");
		JSONArray jsonDevices = usedRespons.getJSONArray("devices");
		JSONArray jsonScenenID = usedRespons.getJSONArray("scenenID");


		effect = usedRespons.getString("effect");
		effectSpeed = usedRespons.getInt("effectSpeed");
		effectSize = usedRespons.getInt("effectSize");
		caseID = usedRespons.getInt("caseID");
		savePresst = usedRespons.getBoolean("save");

		//Log.debug(usedRespons.toString());

		for (int i = 0; i < jsonFader.length(); i++)
			faders[i] = jsonFader.getInt(i);

		
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for (int i = 0; i < jsonScenenID.length(); i++)
			temp.add(jsonScenenID.getInt(i));
		
		scenenID = temp;

		// a device what wasn´t used is null
		for (int i = 0; i < jsonDevices.length(); i++) {
			String value = jsonDevices.get(i).toString();
			switch (value) {
			case "null":
				devices[i] = false;
				break;
			case "false":
				devices[i] = false;
				break;
			case "true":
				devices[i] = true;
				break;
			default:
				Log.error("Allowed device values only null/true/false");
				throw new NullPointerException("Allowed device values only null/true/false");

			}

		}

	}

	// Help Functions

	

	private String readStringFromUrl(String url) {
		URL readURL;
		String response = "";
		try {
			readURL = new URL(url);

			BufferedReader in = new BufferedReader(new InputStreamReader(readURL.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response = response + inputLine;
			in.close();
	//		Log.debug(response);
			return response;
		} catch (Exception e) {
			Log.error("fail to Read Respons ", e);
			return "{}";
		}
	}

	public JSONObject getResponse() {
		String ResponsString;
		String URL = Constants.SERVER_ADDRESS + Constants.INFORMATIONPFAD;
	//	Log.debug("ServerURL :" + URL);
		ResponsString = readStringFromUrl(URL);
		return new JSONObject(ResponsString);
	}
	
	public void setEffectRead() {
		try {
			URL url = new URL(Constants.SERVER_ADDRESS+Constants.EFFECTPATH);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			 if(connection.getResponseCode() != 200 ) {
				 Log.error("Effect can´t reset");;
			 }
			
			}catch (IOException e) {
				Log.error("Fail to check Server avalibale -> wrong path", e);
			}
	}
	
	public void setSaveRead() {
		try {
			URL url = new URL(Constants.SERVER_ADDRESS+Constants.SAVEPATH+"?save=false");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			 if(connection.getResponseCode() != 200 ) {
				 Log.error("save can´t reset");;
			 }
			
			}catch (IOException e) {
				Log.error("Fail to check Server avalibale -> wrong path", e);
			}
	}

}
