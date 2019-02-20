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
	/** Buffer for normal fader bank */
	private int[] faders;
	/** Buffer for multiplexed faders */
	private int[] bFaders;
	
	//Device Choice
	/** Last transmitted state of for device selection from hardware (transmitted in DeviceTransmission).
	 * This is independent (and may differ) from DeviceSelection. Input-class should change DeviceSelection on flanks of DeviceTransmission */
	private boolean[] lastState;
	/** Whether there was a change to lastState */
	private boolean[] activityChanged;
	/** last transmitted DeviceSelection. Used to prevent sending redundant information */
	private boolean[] lastSelection;
	
	//Properties of the Hardware
	/** Number of normal faders at Controller */			public final int NUM_FADERS;
	/** Number of faders in one bank at Controller */		public final int NUM_FADERS_PER_BANK;
	/** Number of multiplexed faders at Controller */		public final int NUM_BFADERS;
	/** Number of special purpose faders at Controller */	public final int NUM_XFADERS;
	/** Number of rotary encoders at Controller */			public final int NUM_ROTARYS;
	/** Number of selectable devices at Controller */		public final int NUM_DEVICES;
	
	/** First analog channel used for normal faders */		public final int OFFSET_FADERS;
	/** First analog channel used for rotary encoders */	public final int OFFSET_ROTARY;
	/** First analog channel used for special faders */		public final int OFFSET_X;
	/** First analog channel used for multiplexed faders*/	public final int OFFSET_MULTIPLEX;
	/** First Channel of DeviceTransmission */				public final int FIRST_DEVICE_TRANSMISSION;
	/** First Channel of DeviceSelection */					public final int FIRST_DEVICE_SELECTION;
	/** Channel of DMX-Status-LED */						public final int DMX_STATUS_LED;
	/** First Channel of multiplex information pins */		public final int OFFSET_MULTIPLEX_PINS;
	
	/** Fader movement, which is interpreted as noise */	public final int FADER_TOLERANCE;
	/** Range of the rotary value. */						public final int ROTARY_RANGE;
	
	/** Port, where the Arduino is connected */				public final String ARDUINO_PORT;
	
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
		NUM_FADERS					= Integer.parseInt(prop.getProperty("NUM_FADERS", "16"));
		NUM_FADERS_PER_BANK			= Integer.parseInt(prop.getProperty("NUM_FADERS_PER_BANK", "8"));
		NUM_BFADERS					= Integer.parseInt(prop.getProperty("NUM_BFADERS", "8"));
		NUM_XFADERS					= Integer.parseInt(prop.getProperty("NUM_XFADERS", "4"));
		NUM_ROTARYS					= Integer.parseInt(prop.getProperty("NUM_ROTARYS", "3"));
		NUM_DEVICES					= Integer.parseInt(prop.getProperty("NUM_DEVICES", "30"));

		OFFSET_FADERS				= Integer.parseInt(prop.getProperty("OFFSET_FADERS", "0"));
		OFFSET_ROTARY				= Integer.parseInt(prop.getProperty("OFFSET_ROTARY", "13"));
		OFFSET_X					= Integer.parseInt(prop.getProperty("OFFSET_X", "8"));
		OFFSET_MULTIPLEX			= Integer.parseInt(prop.getProperty("OFFSET_MULTIPLEX", "12"));
		FIRST_DEVICE_TRANSMISSION	= Integer.parseInt(prop.getProperty("FIRST_DEVICE_TRANSMISSION", "16"));
		FIRST_DEVICE_SELECTION		= Integer.parseInt(prop.getProperty("FIRST_DEVICE_SELECTION", "64"));
		DMX_STATUS_LED				= Integer.parseInt(prop.getProperty("DMX_STATUS_LED", "56"));
		OFFSET_MULTIPLEX_PINS		= Integer.parseInt(prop.getProperty("OFFSET_MULTIPLEX_PINS", "1"));
		
		FADER_TOLERANCE				= Integer.parseInt(prop.getProperty("FADER_TOLERANCE", "0"));
		ROTARY_RANGE				= Integer.parseInt(prop.getProperty("ROTARY_RANGE", "1024"));
		
		
		ARDUINO_PORT				= prop.getProperty("ARDUINO_PORT");
		
		
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
			state = (arduino.digitalRead(i + FIRST_DEVICE_TRANSMISSION) == Arduino.HIGH);
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
				arduino.digitalWrite(i + FIRST_DEVICE_SELECTION, (isActive[i]) ? Arduino.HIGH : Arduino.LOW);
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
		int m0 = arduino.digitalRead(OFFSET_MULTIPLEX_PINS);
		int m1 = arduino.digitalRead(OFFSET_MULTIPLEX_PINS + 1);
		int m2 = arduino.digitalRead(OFFSET_MULTIPLEX_PINS + 2);
		bFaders[m0 + 2*m1 + 4*m2] = arduino.analogRead(OFFSET_MULTIPLEX);
	}
	public void setStatusLED(boolean value) {
		if (value) arduino.digitalWrite(DMX_STATUS_LED, Arduino.HIGH); else arduino.digitalWrite(DMX_STATUS_LED, Arduino.LOW);
	}
}
