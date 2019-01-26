package c4l.applet.main;

import c4l.applet.device.*;
import c4l.applet.main.*;

public class TestObjeckte {
	
	/** Device array zum testen **/
	private static Device[] Devices = new Device[Constants.ALL_DEVICES];
	private static int[] perm = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}; // endweder nul oder 16 ist zuviel 
	

	public static Device[] generateDevices() {
		for(byte i = 0; i < Constants.ALL_DEVICES ;i++) {
			Device device = new Device(perm,(i*16) );
			device.setInput(0, 200); // Sollte noch durch Random erweitert wertden 
			device.setInput(1, 100);
			Devices[i] = device;
		}
		
		return	Devices;	
	}
}
