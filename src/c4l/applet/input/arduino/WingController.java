/**
 * Package for communication with custom console via Arduino. Create a WingController-Object to use functionality.
 * @see WingController
 */
package c4l.applet.input.arduino;

import cc.arduino.Arduino;

/**
 * Manages communication with the Arduino inside of the custom hardware console.
 * The Arduino has to be flashed with a compatible firmware (known: MEGAv0).
 * 
 * @author Timon
 * @version v1pre
 */
public class WingController {
	
	//TODO direct Buttons
	
	private int[] faders;
	int[] bFaders;
	
	private ProFirmata hardware;
	private Arduino arduino;
	
	/**
	 * Returns list of active COM-Ports
	 * @return
	 */
	public static String[] getDevices() {
		return Arduino.list();
	}
	
	//Constructor
	/**
	 * Constructor
	 * @param port Port, where the Arduino is connected
	 */
	public WingController(String port) {
		super();
		this.faders = new int[16];
		this.bFaders = new int[8];
		
		this.hardware = new ProFirmata(port);
		this.arduino = hardware.device;
	}
	
	//Functions
	public int[] getFaders() {
		return faders;
	}
	public int getFader(int index) {
		if ((index < 0) || (index > 15)) throw new IndexOutOfBoundsException("You can only get a Fader for index 0 to 15");
		return faders[index];
	}
	public int getXFader(int index) {
		if ((index < 0) || (index > 3)) throw new IndexOutOfBoundsException("You can only get a xFader for index 0 to 3");
		return arduino.analogRead(index + 8);
	}
	public int[] getBFaders() {
		return bFaders;
	}
	public int getBFader(int index) {
		if ((index < 0) || (index > 7)) throw new IndexOutOfBoundsException("You can only get a Fader for index 0 to 7");
		return bFaders[index];
	}
	public int getRotary(int index) {
		if ((index < 0) || (index > 2)) throw new IndexOutOfBoundsException("You can only get a Rotary for index 0 to 2");
		return arduino.analogRead(index + 13);
	}
	public boolean[] getActiveDevices() {
		boolean[] activeDevices = new boolean[30];
		for (int i = 0; i < 30; i++) {
			activeDevices[i] = isactive(i);
		}	
		return activeDevices;
	}
	public boolean isactive(int index) {
		if ((index < 0) || (index > 29)) throw new IndexOutOfBoundsException("You can only get device activity for index 0 to 29");
		return (arduino.digitalRead(index + 16) == Arduino.HIGH);
	}
	
	public void tick() {
		//Read (and save) analog banked faders
		int aBank = arduino.digitalRead(0);
		for (int i = 0; i < 8; i++) {
			faders[i+aBank*8] = arduino.analogRead(i);
		}
		
		//Read Multiplexed
		int m0 = arduino.digitalRead(1);
		int m1 = arduino.digitalRead(2);
		int m2 = arduino.digitalRead(3);
		bFaders[m0 + 2*m1 + 4*m2] = arduino.analogRead(12);
	}
	public void setStatusLED(boolean value) {
		if (value) arduino.digitalWrite(56, Arduino.HIGH); else arduino.digitalWrite(56, Arduino.LOW);
	}
}
