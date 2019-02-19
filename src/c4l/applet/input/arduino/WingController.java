/**
 * Package for communication with custom console via Arduino. Create a WingController-Object to use functionality.
 * @see WingController
 */
package c4l.applet.input.arduino;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import c4l.applet.main.Constants;
import cc.arduino.Arduino;

/**
 * Manages communication with the Arduino inside of the custom hardware console.
 * The Arduino has to be flashed with a compatible firmware (known: MEGAv0.1).
 * 
 * @author Timon
 * @version v1pre
 */
public class WingController {
	private ProFirmata hardware;
	private Arduino arduino;	
	
	//Faders
	private int[] faders;
	int[] bFaders;
	
	//Device Choice
	private boolean[] lastState; //last state of DeviceTransmission
	private boolean[] activityChanged; //whether there was a change to lastState
	private boolean[] lastSelection; //last transmitted DeviceSelection
	
	//Properties of the Hardware
	public final int NUM_FADERS;
	public final int NUM_FADERS_PER_BANK;
	public final int NUM_BFADERS;
	public final int NUM_XFADERS;
	public final int NUM_ROTARYS;
	public final int NUM_DEVICES;
	
	public final int OFFSET_FADERS;
	public final int OFFSET_ROTARY;
	public final int OFFSET_X;
	public final int OFFSET_MULTIPLEX;
	
	public final int FADER_TOLERANCE;
	
	public final String ARDUINO_PORT;
	
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
	 * @param prop Property-Object, containing all relvant information about the harware
	 */
	public WingController(Properties prop) {
		//Read Properties
		NUM_FADERS			= Integer.parseInt(prop.getProperty("NUM_FADERS", "16"));
		NUM_FADERS_PER_BANK	= Integer.parseInt(prop.getProperty("NUM_FADERS_PER_BANK", "8"));
		NUM_BFADERS			= Integer.parseInt(prop.getProperty("NUM_BFADERS", "8"));
		NUM_XFADERS			= Integer.parseInt(prop.getProperty("NUM_XFADERS", "4"));
		NUM_ROTARYS			= Integer.parseInt(prop.getProperty("NUM_ROTARYS", "3"));
		NUM_DEVICES			= Integer.parseInt(prop.getProperty("NUM_DEVICES", "30"));

		OFFSET_FADERS		= Integer.parseInt(prop.getProperty("OFFSET_FADERS", "0"));
		OFFSET_ROTARY		= Integer.parseInt(prop.getProperty("OFFSET_ROTARY", "13"));
		OFFSET_X			= Integer.parseInt(prop.getProperty("OFFSET_X", "8"));
		OFFSET_MULTIPLEX	= Integer.parseInt(prop.getProperty("OFFSET_MULTIPLEX", "12"));
		
		FADER_TOLERANCE		= Integer.parseInt(prop.getProperty("FADER_TOLERANCE", "0"));
		
		ARDUINO_PORT		= prop.getProperty("ARDUINO_PORT");
		
		
		this.faders = new int[Constants.DEVICE_CHANNELS];
		this.bFaders = new int[NUM_BFADERS];
		this.lastState = new boolean[NUM_DEVICES];
		this.activityChanged = new boolean[NUM_DEVICES];
		this.lastSelection = new boolean[NUM_DEVICES];
		
		this.hardware = new ProFirmata(ARDUINO_PORT);
		this.arduino = hardware.device;
	}
	
	public static Properties openPropertiesFile(String filePath) {
		Properties prop = new Properties();
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filePath));
			prop.load(stream);
			stream.close();
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
		return prop;
	}
	
	//Functions
	public int[] getFaders() {
		return faders;
	}
	public int getFader(int index) {
		if ((index < 0) || (index >= NUM_FADERS)) throw new IndexOutOfBoundsException("You can only get a Fader for index 0 to " + NUM_FADERS);
		return faders[index];
	}
	public int getXFader(int index) {
		if ((index < 0) || (index >= NUM_XFADERS)) throw new IndexOutOfBoundsException("You can only get a xFader for index 0 to " + NUM_XFADERS);
		return arduino.analogRead(index + OFFSET_X);
	}
	public int[] getBFaders() {
		return bFaders;
	}
	public int getBFader(int index) {
		if ((index < 0) || (index >= NUM_BFADERS)) throw new IndexOutOfBoundsException("You can only get a Fader for index 0 to " + NUM_BFADERS);
		return bFaders[index];
	}
	public int getRotary(int index) {
		if ((index < 0) || (index > NUM_ROTARYS)) throw new IndexOutOfBoundsException("You can only get a Rotary for index 0 to " + NUM_ROTARYS);
		return arduino.analogRead(index + OFFSET_ROTARY);
	}
//	public boolean[] getActiveDevices() {
//		boolean[] activeDevices = new boolean[30];
//		for (int i = 0; i < 30; i++) {
//			activeDevices[i] = isactive(i);
//		}	
//		return activeDevices;
//	}
//	public boolean isactive(int index) {
//		if ((index < 0) || (index > 29)) throw new IndexOutOfBoundsException("You can only get device activity for index 0 to 29");
//		return (arduino.digitalRead(index + 16) == Arduino.HIGH);
//	}

	/**
	 * Check for input on device selection.
	 * @param use	if true, changes reported this time won't be reported again (default)
	 * @return Boolean array, indicating true if an odd number of inputs occurred for this device since last check.
	 */
	public boolean[] checkActivity(boolean use) {
		boolean state;
		for (int i = 0; i < NUM_DEVICES; i++) {
			state = (arduino.digitalRead(i + ProFirmata.FIRST_DEVICE_TRANSMISSION) == Arduino.HIGH);
			activityChanged[i] = (state != lastState[i]);
			if (use) lastState[i] = state;
		} /* for */
		
		return activityChanged;
	}
	public boolean[] checkActivity() {
		return checkActivity(true);
	}
	
	/**
	 * Give active devices to wing.
	 * @param isActive	Boolean array containing activity-state for each device
	 * @param force		force transmission
	 */
	public void setActiveDevices(boolean[] isActive, boolean force) {
		for (int i = 0; i < NUM_DEVICES; i++) {
			if (force || (lastSelection[i] != isActive[i])) {
				arduino.digitalWrite(i + ProFirmata.FIRST_DEVICE_SELECTION, (isActive[i]) ? Arduino.HIGH : Arduino.LOW);
				lastSelection[i] = isActive[i];
			} /* if */
		} /* for */
	}
	public void setActiveDevices(boolean[] isActive) {
		setActiveDevices(isActive, false);
	}
	
	public void tick() {
		//Read (and save) analog banked faders
		int aBank = arduino.digitalRead(0);
		for (int i = 0; i < NUM_FADERS_PER_BANK; i++) {
			faders[i+aBank*NUM_FADERS_PER_BANK] = arduino.analogRead(i);
		}
		
		//Read Multiplexed
		int m0 = arduino.digitalRead(1);
		int m1 = arduino.digitalRead(2);
		int m2 = arduino.digitalRead(3);
		bFaders[m0 + 2*m1 + 4*m2] = arduino.analogRead(OFFSET_MULTIPLEX);
	}
	public void setStatusLED(boolean value) {
		if (value) arduino.digitalWrite(56, Arduino.HIGH); else arduino.digitalWrite(56, Arduino.LOW);
	}
}
