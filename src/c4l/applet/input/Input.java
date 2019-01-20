package c4l.applet.input;

import c4l.applet.input.arduino.WingController;
import c4l.applet.main.Constants;

/**
 * Manages all inputs to the program (hardware, server, MIDI, other APIs) and filters out what to adjust.
 * This especially includes figuring out, which input stream on the same item (e.g. fader value) has the latest update
 * 
 * @author Timon
 *
 */
public class Input {
	private WingController hardware;
	@SuppressWarnings("unused")
	private DashboardInput server;
	
	private int[] h_faders;
	private int[] h_xfaders;
	private int[] h_rotary;
	
	public Input(String arduinoPort) {
		this.hardware = new WingController(arduinoPort);
		this.server = new DashboardInput(); //TODO modify Constructor if necessary
		
		this.h_faders = new int[16];
		this.h_xfaders = new int[4];
		this.h_faders = new int[3];
	}
	
	public void tick() {
		int temp = 0;
		//Tick other objects
		hardware.tick();
		
		//check wing-faders
		for (int i = 0; i < 16; i++) {
			temp = hardware.getFader(i);
			if (Math.abs(temp - h_faders[i]) > Constants.FADER_TOLERANCE) {
				h_faders[i] = temp;
				//TODO Push new value to Main (remember mapping form 0..1023 to 0..255)
			} /* if */
		} /* for */
		//check wing-x-faders
		for (int i = 0; i < 4; i++) {
			temp = hardware.getXFader(i);
			if (Math.abs(temp - h_xfaders[i]) > Constants.FADER_TOLERANCE) {
				h_xfaders[i] = temp;
				//TODO Push new value to Main (remember mapping form 0..1023 to 0..255)
			} /* if */
		} /* for */
		//check rotary encoders
		for (int i = 0; i < 3; i++) {
			temp = hardware.getRotary(i);
			
			//TODO adjust value in main by (temp - h_rotary[i]) maybe add/subtract Constants.ROTARY_RANGE
			
			h_rotary[i] = temp;
		} /* for */
		//TODO B-faders
		
		
		//TODO check dashboard
	}

}
