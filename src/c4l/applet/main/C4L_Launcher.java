package c4l.applet.main;

import c4l.applet.device.Device;
import c4l.applet.input.Input;
import c4l.applet.output.DmxOut;

/**
 * @author Timon
 *
 */
public class C4L_Launcher {
	DmxOut dmxHandle;
	Input inputHandle;
	public Device[] deviceHandle;
	
	static boolean quit = false;
	
	/**
	 * Constructor
	 */
	C4L_Launcher () {
		dmxHandle = new DmxOut();
		inputHandle = new Input(Constants.ARDUINO_PORT, this);
		
		deviceHandle = new Device[Constants.DYNAMIC_DEVICES];
		for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++) {
			deviceHandle[i] = new Device(Constants.STANDART_PERMUTATION, i*Constants.DEVICE_CHANNELS);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long last_time = System.currentTimeMillis();
		long time = last_time;
		C4L_Launcher program = new C4L_Launcher();
		
		while (!quit) {
			program.inputHandle.tick();
			program.dmxHandle.setOutput(program.deviceHandle);
			
			time = System.currentTimeMillis();
			if (time-last_time > Constants.EFFECTTICKMILLIS) {
				last_time += Constants.EFFECTTICKMILLIS;
				for(Device d : program.deviceHandle) d.tick();
			}
		}
	}

}
