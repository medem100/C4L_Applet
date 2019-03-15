package c4l.applet.main;

import c4l.applet.output.DmxOut;
import cc.arduino.Arduino;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

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
	 * @param args
	 */
	public static void main(String[] args) {
		/*JSONObject DasboardValues = DashboardInput.getResponse();
		JSONArray faderValues = DasboardValues.getJSONArray("fader");
		System.out.println(faderValues.get(1)); */
		
		 PropertyConfigurator.configure("resources/properties/log4j.properties");
		 Logger logger = Logger.getLogger(TestAndre.class);
		 System.out.println(System.getProperty("test"));
		 logger.debug("test debuge");
		 logger.info("test Info ");
		 logger.error("test Error");
		 
	}
	
	/**
	 * catcht den Arduino am vorgeben comPort und den Entec Dongel
	 * TO DO: Com Port für Arduino mus in die Props/ oder VM argumente ?
	 */
	private void setup() {
		/*
		 * TO DO : Aktuelle Konfiguratzion für den Arduino 
		 * 
		System.out.println(Arduino.list().toString());
		arduino = new Arduino(this, "COM6", 57600);
		arduino.pinMode(ledPin, Arduino.OUTPUT);
		arduino.pinMode(pin, Arduino.INPUT);
		// arduino.pinMode(pin, Arduino.INPUT);
		*/
		DMXobjeckt = new DmxOut();
	}
	
}
