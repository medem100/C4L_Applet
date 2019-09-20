package c4l.applet.input;

import c4l.applet.device.Effect;
import c4l.applet.input.arduino.WingController;
import c4l.applet.main.C4L_Launcher;
import c4l.applet.main.Constants;
import c4l.applet.device.Effect_ID;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.*;

/**
 * Manages all inputs to the program (wing, server, MIDI, other APIs) and
 * filters out what to adjust. This especially includes figuring out, which
 * input stream on the same item (e.g. fader value) has the latest update
 * 
 * @author Timon
 *
 */
public class Input {
	private Logger log = Logger.getLogger(Input.class);
	private WingController wing;
	private Boolean ServerAvailable;
	private DashboardInput server;
	private JSONObject OldResponse = new JSONObject("{}");
	C4L_Launcher parent;
	private int currentSceneId;

	/** last know (and processed) hardware-fader position */
	private int[] h_faders;
	/** last know (and processed) hardware-x-fader position */
	private int[] h_xfaders;
	/** last know (and processed) hardware-rotary encoder position */
	private int[] h_rotary;
	/**
	 * Array indicating, which devices are active, that is to say they are affected
	 * by current inputs
	 */
	private boolean[] active;

	// Constructors
	public Input(C4L_Launcher parent, Properties arduinoProperties) {
		this(parent, new WingController(arduinoProperties), false);
	}

	public Input(C4L_Launcher parent, Properties arduinoProperties, Boolean ServerAvailable) {
		this(parent, new WingController(arduinoProperties), ServerAvailable);
	}

	public Input(C4L_Launcher parent, String arduinoPropertiesPath) {
		this(parent, new WingController(WingController.openPropertiesFile(arduinoPropertiesPath)), false);
	}

	public Input(C4L_Launcher parent, String arduinoPropertiesPath, Boolean ServerAvailable) {
		this(parent, new WingController(WingController.openPropertiesFile(arduinoPropertiesPath)), ServerAvailable);
	}

	public Input(C4L_Launcher parent) {
		this(parent, (WingController) null, false);
	}

	public Input(C4L_Launcher parent, Boolean ServerAvailable) {
		this(parent, (WingController) null, ServerAvailable);
	}

	public Input(C4L_Launcher parent, WingController wing, Boolean ServerAvailable) {
		this.ServerAvailable = ServerAvailable;
		this.wing = wing;
		this.server = new DashboardInput(); // TODO modify Constructor if necessary
		this.parent = parent;

		this.h_faders = new int[16];
		this.h_xfaders = new int[4];
		this.h_faders = new int[3];
		this.active = new boolean[30]; // should be initialized with false
		if (wing != null)
			wing.setActiveDevices(active, true);
	}

	public void deleteWing() {
		this.wing = null;
	}

	public void setWing(WingController wing) {
		this.wing = wing;
	}

	public void tick() {
		int temp = 0;

		// Handle the HardwareWing
		if (wing != null) {
			wing.tick();

			// check Wingcontroller for changes in device activity
//			boolean[] change = wing.checkActivity();
//			for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++)
//				active[i] ^= change[i];
//			wing.setActiveDevices(active); // Tell wing, which devices are active

			// check wing-faders
//			for (int i = 0; i < 16; i++) {
			for (int i = 0; i < 8; i++) { //remove this line for normal code, this is tweaked for MVP
				temp = wing.getFader(i);
				if (Math.abs(temp - h_faders[i]) > wing.FADER_TOLERANCE) {
					h_faders[i] = temp;
					for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
						if (active[j])
							parent.deviceHandle[j].setInput(i, h_faders[i] / Constants.CORRECTIONDIVISOR);
					} /* for */
				} /* if */
			} /* for */
			// check wing-x-faders
//			for (int i = 0; i < 4; i++) {
//				temp = wing.getXFader(i);
//				if (Math.abs(temp - h_xfaders[i]) > wing.FADER_TOLERANCE) {
//					h_xfaders[i] = temp;
//					switch (i) {
//					case 0:
//						for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
//							if (active[j])
//								parent.deviceHandle[j].setSpeed(h_xfaders[i] / Constants.CORRECTIONDIVISOR);
//						} /* for */
//					case 1:
//						for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
//							if (active[j])
//								parent.deviceHandle[j].setSize(h_xfaders[i] / Constants.CORRECTIONDIVISOR);
//						} /* for */
//					} /* switch */
//					// TODO Define use of fader 3 and specify 4
//				} /* if */
//			} /* for */
			// check rotary encoders
//			for (int i = 0; i < Constants.ROTARY_COUNT; i++) {
//				temp = wing.getRotary(i) - h_rotary[i];
//				h_rotary[i] += temp;
//				if (temp > wing.ROTARY_RANGE / 2)
//					temp -= wing.ROTARY_RANGE;
//				if (temp < -wing.ROTARY_RANGE / 2)
//					temp += wing.ROTARY_RANGE;
//				for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
//					if (active[j])
//						parent.deviceHandle[j].applyRotary(i, temp);
//				} /* for devices */
//			} /* for rotary encoders */
			// TODO B-faders
		} /* if wing exists */

		// TODO check dashboard

		if (ServerAvailable) {
			server.tick();
			// Only when there are new data from the Dashboard
			if (!(server.usedRespons.toString().equals(OldResponse.toString()))) { // TODO
				// log.debug("New Respons");

				if (currentSceneId != server.getScenenID().get(0)) {
					loadScene(server.getScenenID().get(0));
					currentSceneId = server.getScenenID().get(0);
				} else {

					for (int i : server.getChosenDevices()) {

						String effectId = server.getEffectID();
//						
//						if(effectId != 0 ) {
//						String effect = String.valueOf(effectId);
//						int eId1 = Integer.valueOf(effect.substring(0, 1));
//						int eid2 = Integer.valueOf(effect.substring(1));
//						
						if(!(effectId.equals("99"))) {
							Effect_ID eid = new Effect_ID(Integer.valueOf(effectId.substring(0, 1)), Integer.valueOf(effectId.substring(1,2)));
							Effect e = Effect_ID.generateEffectFromID(eid, server.getEffectSize(),
									server.getEffectSize(), 0,
									new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
//							if (Effect_ID.getEffectID(e)
//									.equals(Effect_ID.getEffectID(parent.deviceHandle[i].main_effect.get(0)))) {
//								parent.deviceHandle[i].deleteMainEffect(0);
//							} else {
								parent.deviceHandle[i].addMainEffect(e);
//							}
						}
						
						
//						if (effectId != 0) {
//							Effect_ID eid = null;
//							switch (effectId) {
//							case 1:
//								eid = new Effect_ID(0, 0);
//								break;
//							case 2:
//								eid = new Effect_ID(0, 1);
//								break;
//							case 3:
//								eid = new Effect_ID(1, 0);
//								break;
//							case 4:
//								eid = new Effect_ID(1, 1);
//								break;
//							} // TODO testen !
							
//							if (Effect_ID.getEffectID(e)
//									.equals(Effect_ID.getEffectID(parent.deviceHandle[i].main_effect.get(0)))) {
//								parent.deviceHandle[i].deleteMainEffect(0);
//							} else {
//								parent.deviceHandle[i].addMainEffect(e);
//							}

							// parent.deviceHandle[i].effects.
							// parent.deviceHandle[i]

							// parent.deviceHandle[i].

//						}
						parent.deviceHandle[i].setSpeed(server.getEffectSpeed());
						parent.deviceHandle[i].setSize(server.getEffectSize());
						
						parent.deviceHandle[i].setMainSpeed(server.getEffectSpeed());
						parent.deviceHandle[i].setMainSize(server.getEffectSize());
						

						for (int j = 0; j < Constants.DEVICE_CHANNELS; j++) {
							parent.deviceHandle[i].setInput(j, server.getFader(j));
							// parent.deviceHandle[i].addEffect(e);

						}
						if(server.isSavePresst()) saveScene();
						
						OldResponse = server.usedRespons;
					}

				}
			}

		}

	}

	// Help Funcktions

	private void loadScene(int id) {
		System.out.println("load Scene "+id);

	}
	
	private void saveScene() {
		System.out.println("save scene");
	}
}
