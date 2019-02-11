package c4l.applet.input;

import c4l.applet.input.arduino.WingController;
import c4l.applet.main.C4L_Launcher;
import c4l.applet.main.Constants;

/**
 * Manages all inputs to the program (wing, server, MIDI, other APIs) and filters out what to adjust.
 * This especially includes figuring out, which input stream on the same item (e.g. fader value) has the latest update
 * 
 * @author Timon
 *
 */
public class Input {
	private WingController wing;
	@SuppressWarnings("unused")
	private DashboardInput server;
	C4L_Launcher parent;
	
	/** last know (and processed) hardware-fader position */
	private int[] h_faders;
	/** last know (and processed) hardware-x-fader position */
	private int[] h_xfaders;
	/** last know (and processed) hardware-rotary encoder position */
	private int[] h_rotary;
	/** Array indicating, which devices are active, that is to say they are  affected by current inputs */
	private boolean[] active;
	
	public Input(String arduinoPort, C4L_Launcher main) {
		this.wing = new WingController(arduinoPort);
		this.server = new DashboardInput(); //TODO modify Constructor if necessary
		this.parent = main;
		
		this.h_faders = new int[16];
		this.h_xfaders = new int[4];
		this.h_faders = new int[3];
		this.active = new boolean[30]; //should be initialized with false
		
		wing.setActiveDevices(active, true);
	}
	
	public void tick() {
		int temp = 0;
		//Tick other objects
		wing.tick();
		
		//check Wingcontroller for changes in device activity
		boolean[] change = wing.checkActivity();
		for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++) active[i] ^= change[i];
		wing.setActiveDevices(active); //Tell wing, which devices are active
		
		//check wing-faders
		for (int i = 0; i < 16; i++) {
			temp = wing.getFader(i);
			if (Math.abs(temp - h_faders[i]) > Constants.FADER_TOLERANCE) {
				h_faders[i] = temp;
				for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
					if (active[j]) parent.deviceHandle[j].setInput(i, h_faders[i]/Constants.CORRECTIONDIVISOR);
				} /* for */
			} /* if */
		} /* for */
		//check wing-x-faders
		for (int i = 0; i < 4; i++) {
			temp = wing.getXFader(i);
			if (Math.abs(temp - h_xfaders[i]) > Constants.FADER_TOLERANCE) {
				h_xfaders[i] = temp;
				switch (i) {
				case 0:
					for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
						if (active[j]) parent.deviceHandle[j].setSpeed(h_xfaders[i]/Constants.CORRECTIONDIVISOR);
					} /* for */
				case 1:
					for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
						if (active[j]) parent.deviceHandle[j].setSize(h_xfaders[i]/Constants.CORRECTIONDIVISOR);
					} /* for */
				} /*switch */
				//TODO Define use of fader 3 and specify 4
			} /* if */
		} /* for */
		//check rotary encoders
		for (int i = 0; i < Constants.ROTARY_COUNT; i++) {
			temp = wing.getRotary(i) - h_rotary[i];
			h_rotary[i] += temp;
			if (temp > Constants.ROTARY_RANGE/2) temp -= Constants.ROTARY_RANGE;
			if (temp < -Constants.ROTARY_RANGE/2) temp += Constants.ROTARY_RANGE;
			for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
				if (active[j]) parent.deviceHandle[j].applyRotary(i, temp/Constants.ROTARY_CORRECTIONDIVISOR);
			} /* for devices */
		} /* for rotary encoders */
		//TODO B-faders
		
		
		//TODO check dashboard
		
		
	}

}
