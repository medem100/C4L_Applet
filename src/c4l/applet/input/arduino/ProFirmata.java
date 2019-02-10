/**
 * 
 */
package c4l.applet.input.arduino;

import cc.arduino.Arduino;
import processing.core.PApplet;

/**
 * Use @see WingController
 * 
 * @author Timon
 * @version v1pre
 */
public class ProFirmata extends PApplet {
	final int baud_rate;
	
	Arduino device;
	
	//Constructors
	public ProFirmata(String port) {
		this(port, 57600);
	}
	public ProFirmata(String port, int rate) {
		baud_rate = rate;
		device = new Arduino(this, port, baud_rate);
		device.pinMode(13, Arduino.OUTPUT);
	}
	
	/** First Channel of DeviceTransmission */
	public static final int FIRST_DEVICE_TRANSMISSION = 16;
	
	/** First Channel of DeviceSelection */
	public static final int FIRST_DEVICE_SELECTION = 64;
}
