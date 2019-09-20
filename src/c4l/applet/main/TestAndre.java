package c4l.applet.main;

import c4l.applet.output.DmxOut;
import cc.arduino.Arduino;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import c4l.applet.device.Device;
import c4l.applet.input.DashboardInput;
import c4l.applet.main.TestObjeckte;

/**
 * Hauptablauf
 * 
 * @version 0.1
 * @author Andre 19.01.16 : Output implementierung
 */
public class TestAndre {

	static DmxOut DMXobjeckt = null;
	
	private static String effects = "[ "
			+ "{'name':'WILD', 			'id':'10', 'category':'Random' , 'Description': 'Move to random spots (range size)'}, "
			+ "{'name':'JUMP', 			'id':'11', 'category':'Random' , 'Description': 'Jump to random spots (range size)'},"
			+ "{'name':'SINUS', 		'id':'00', 'category':'Simpel' , 'Description': 'Add a sine-wave of amplitude size/2'},"
			+ "{'name':'RAMP', 			'id':'01', 'category':'Simpel' , 'Description': 'linear fade from -size/2 to +size/2, jump back'},"
			+ "{'name':'REVRAMP', 		'id':'02', 'category':'Simpel' , 'Description': 'linear fade from +size/2 to -size/2, jump back'},"
			+ "{'name':'LINEAR', 		'id':'03', 'category':'Simpel' , 'Description': 'linear fade from +size/2 to -size/2, fade back'},"
			+ "{'name':'LINEAR_HOLDL', 	'id':'04', 'category':'Simpel' , 'Description': 'like linear, but pausing at low-point'},"
			+ "{'name':'LINEAR_HOLDH', 	'id':'05', 'category':'Simpel' , 'Description': 'like linear, but pausing at high-point'},"
			+ "{'name':'STROBO', 		'id':'06', 'category':'Simpel' , 'Description': 'Channel is active size/256 parts of effect time, zero afterwards'},"
			+ "{'name':'STROBO_HOLD', 	'id':'07', 'category':'Simpel' , 'Description': 'Strobo without changing colors when on'},"
			+ "{'name':'CIRCLE', 		'id':'08', 'category':'Simpel' , 'Description': 'sine on first channel, cosine on second'},"
			+ "{'name':'RAINBOW', 		'id':'09', 'category':'Simpel' , 'Description': 'fade through a rainbow (assuming 1,2,3 as RGB)'}"
			+ "]";

	/*
	 * Einsammeln der Input werte aus c4l.applet.input
	 * 
	 * Verarbeitung mit c4l.applet.function
	 * 
	 * Ausgabe mit c4l.applet.dmxout
	 * 
	 * 
	 */

	/**
	 * Start
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getEffects());
		
		
//		 JSONObject jo1 = new JSONObject(); 
//		 JSONObject jo2 = new JSONObject(); 
//		 JSONArray ja = new JSONArray();
//		 
//		 jo1.put("lum", 0);
//		 jo1.put("channel", "1");
//		 jo1.put("device", "Red");
//		 jo1.put("mute", false);
//		 jo1.put("value", 0);
//		 
//		 jo2.put("lum", 0);
//		 jo2.put("channel", "1");
//		 jo2.put("device", "Green");
//		 jo2.put("mute", false);
//		 jo2.put("value", 0);
//		 
//		 ja.put(jo1);
//		 ja.put(jo2);
//		 
//		 System.out.println(ja.toString());
//		 
		
		
//		PropertyConfigurator.configure("resources/properties/log4j.properties");
//		Logger logger = Logger.getLogger(TestAndre.class);
//		DashboardInput Dashboard = new DashboardInput();
//		//Dashboard.tick();
//		
//		Boolean[] Devices = Dashboard.getDevices();
//		for(Boolean Device:Devices) {
//			logger.debug(Device);
//		}
//		
//		logger.debug(Dashboard.getEffectSize());
//		logger.debug(Dashboard.getEffectSpeed());
//		int[] faders = Dashboard.getFader();
//		for(int fader:faders) {
//			logger.debug(fader);
//		}
//		
//		logger.debug(Util.getServerAvalibal());
//		

		// System.out.println(System.getProperty("test"));
//		logger.debug("test debuge");
//		logger.info("test Info ");
//		logger.error("test Error");
		
		Gson gson = new Gson();
		
		Device obj = new Device(Constants.STANDART_PERMUTATION,1);
		
		String dev = gson.toJson(obj);
		System.out.println(dev);
		
		Device dev2 = gson.fromJson(dev,Device.class);
		
		System.out.println(dev2.getPerm());
		
		
		

		
		

	}

	/**
	 * catcht den Arduino am vorgeben comPort und den Entec Dongel TO DO: Com Port
	 * für Arduino mus in die Props/ oder VM argumente ?
	 */
	private void setup() {
		/*
		 * TO DO : Aktuelle Konfiguratzion für den Arduino
		 * 
		 * System.out.println(Arduino.list().toString()); arduino = new Arduino(this,
		 * "COM6", 57600); arduino.pinMode(ledPin, Arduino.OUTPUT); arduino.pinMode(pin,
		 * Arduino.INPUT); // arduino.pinMode(pin, Arduino.INPUT);
		 */
		DMXobjeckt = new DmxOut();
	}
	
	
	
	public static JSONArray getEffects() {
		return new JSONArray(effects);
	}

}
