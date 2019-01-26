package c4l.applet.main;

import c4l.applet.output.DmxOut;
import cc.arduino.Arduino;

/**
 * Hauptablauf
 * 
 * @version 0.1
 * @author Andre 19.01.16 : Output implementierung
 */
public class Main {

	DmxOut DMXobjeckt = null;

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
		Main C4l = new Main();
		C4l.setup();
		while(true) {
			C4l.setOutput(1, 200);
		}
	}
	
	
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
	
	
	private void setOutput(Integer Addresse, Integer value) {
		DMXobjeckt.setValue(Addresse, value);
	}
	
	
	

}
