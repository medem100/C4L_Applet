package c4l.applet.input;

import c4l.applet.db.OldEffects;
import c4l.applet.db.Scene;
import c4l.applet.device.Device;
import c4l.applet.device.Effect;
import c4l.applet.input.arduino.WingController;
import c4l.applet.main.C4L_Launcher;
import c4l.applet.main.Constants;
import c4l.applet.device.Effect_ID;
import c4l.applet.device.Effect_Representative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.*;

import com.sun.javafx.collections.MappingChange.Map;

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
	private int[] currentFaderValues = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private int currentSize;
	private int currentSpeed;
	Effect[] oEffects = new Effect[30]; // test
	// boolean setOldeffects = false;

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
		this(parent, (WingController) null, true);
	}

	// public Input(C4L_Launcher parent, Boolean ServerAvailable) {
	//// this(parent, new WingController(new Properties()), ServerAvailable);
	// this(parent,null, ServerAvailable);
	// }

	public Input(C4L_Launcher parent, WingController wing, Boolean ServerAvailable) {
		this.ServerAvailable = ServerAvailable;
		this.wing = wing;
		this.server = new DashboardInput(); // TODO modify Constructor if necessary
		this.parent = parent;

		this.h_faders = new int[16];
		this.h_xfaders = new int[4];
		this.h_rotary = new int[3];
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
			// boolean[] change = wing.checkActivity();
			// for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++)
			// active[i] ^= change[i];
			// wing.setActiveDevices(active); // Tell wing, which devices are active

			// check wing-faders
			// for (int i = 0; i < 16; i++) {
			for (int i = 0; i < 8; i++) { // remove this line for normal code, this is tweaked for MVP
				temp = wing.getFader(i);
				if (Math.abs(temp - h_faders[i]) > wing.FADER_TOLERANCE) {
					if (temp <= wing.FADER_TOLERANCE)
						temp = 0; //for a correction factor larger than the toleranc this should happen implicitly when dividing by the first... TODO: Think wheter to remove this for optimization
					h_faders[i] = temp;
					for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
						if (active[j])
							parent.deviceHandle[j].setInput(i, h_faders[i] / Constants.CORRECTIONDIVISOR);
					} /* for */
				} /* if */
			} /* for */
			// check wing-x-faders
			// for (int i = 0; i < 4; i++) {
			// temp = wing.getXFader(i);
			// if (Math.abs(temp - h_xfaders[i]) > wing.FADER_TOLERANCE) {
			// h_xfaders[i] = temp;
			// switch (i) {
			// case 0:
			// for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
			// if (active[j])
			// parent.deviceHandle[j].setSpeed(h_xfaders[i] / Constants.CORRECTIONDIVISOR);
			// } /* for */
			// case 1:
			// for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
			// if (active[j])
			// parent.deviceHandle[j].setSize(h_xfaders[i] / Constants.CORRECTIONDIVISOR);
			// } /* for */
			// } /* switch */
			// // TODO Define use of fader 3 and specify 4
			// } /* if */
			// } /* for */
			// check rotary encoders
			// for (int i = 0; i < Constants.ROTARY_COUNT; i++) {
			// temp = wing.getRotary(i) - h_rotary[i];
			// h_rotary[i] += temp;
			// if (temp > wing.ROTARY_RANGE / 2)
			// temp -= wing.ROTARY_RANGE;
			// if (temp < -wing.ROTARY_RANGE / 2)
			// temp += wing.ROTARY_RANGE;
			// for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
			// if (active[j])
			// parent.deviceHandle[j].applyRotary(i, temp);
			// } /* for devices */
			// } /* for rotary encoders */
			// TODO B-faders
		} /* if wing exists */

		// TODO check dashboard

		if (ServerAvailable) {
			server.tick();

			// set old effects

			// Only when there are new data from the Dashboard
			if (!(server.usedRespons.toString().equals(OldResponse.toString()))) { // TODO
				// log.debug("New Respons");

				// if(setOldeffects) {
				// for (int i = 0; i < oEffects.length; i++) {
				// if (oEffects[i] != null) {
				// if (!(parent.deviceHandle[i].main_effect.isEmpty()))
				// parent.deviceHandle[i].deleteMainEffect(0);
				// parent.deviceHandle[i].addMainEffect(oEffects[i], 0);
				// }
				// }
				// setOldeffects = false;
				// }

				if (currentSceneId != server.getScenenID().get(0)) {
					loadScene(server.getScenenID().get(0));
					currentSceneId = server.getScenenID().get(0);
				} else {

					for (int i = 0; i < active.length; i++) {
						active[i] = false;
					}

					HashMap<Integer, Integer> changFader = new HashMap<>();
					for (int i = 0; i < currentFaderValues.length; i++) {

						if (currentFaderValues[i] != server.getFader(i)) {
							changFader.put(i, server.getFader(i));
							currentFaderValues[i] = server.getFader(i);
						}
					}

					boolean changeSize = false;
					boolean changeSpeed = false;

					if (currentSize != server.getEffectSize()) {
						changeSize = true;
						currentSize = server.getEffectSize();
					}

					if (currentSpeed != server.getEffectSpeed()) {
						changeSpeed = true;
						currentSpeed = server.getEffectSpeed();
					}

					for (int i : server.getChosenDevices()) {

						active[i] = true;

						String effectId = server.getEffectID();
						//
						// if(effectId != 0 ) {
						// String effect = String.valueOf(effectId);
						// int eId1 = Integer.valueOf(effect.substring(0, 1));
						// int eid2 = Integer.valueOf(effect.substring(1));
						//
						if (!(effectId.equals("99"))) {
							Effect_ID eid = new Effect_ID(Integer.valueOf(effectId.substring(0, 1)),
									Integer.valueOf(effectId.substring(1, 2)));
							Effect e = Effect_ID.generateEffectFromID(eid, server.getEffectSize(),
									server.getEffectSize(), 0,
									new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
							// if (Effect_ID.getEffectID(e)
							// .equals(Effect_ID.getEffectID(parent.deviceHandle[i].main_effect.get(0)))) {
							// parent.deviceHandle[i].deleteMainEffect(0);
							// } else {
							if (!(parent.deviceHandle[i].main_effect.isEmpty()))
								parent.deviceHandle[i].deleteMainEffect(0);
							parent.deviceHandle[i].addMainEffect(e, 0);
							// }
						}

						// if (effectId != 0) {
						// Effect_ID eid = null;
						// switch (effectId) {
						// case 1:
						// eid = new Effect_ID(0, 0);
						// break;
						// case 2:
						// eid = new Effect_ID(0, 1);
						// break;
						// case 3:
						// eid = new Effect_ID(1, 0);
						// break;
						// case 4:
						// eid = new Effect_ID(1, 1);
						// break;
						// } // TODO testen !

						// if (Effect_ID.getEffectID(e)
						// .equals(Effect_ID.getEffectID(parent.deviceHandle[i].main_effect.get(0)))) {
						// parent.deviceHandle[i].deleteMainEffect(0);
						// } else {
						// parent.deviceHandle[i].addMainEffect(e);
						// }

						// parent.deviceHandle[i].effects.
						// parent.deviceHandle[i]

						// parent.deviceHandle[i].

						// }
						// parent.deviceHandle[i].setSpeed(server.getEffectSpeed());
						// parent.deviceHandle[i].setSize(server.getEffectSize());
						if (changeSpeed)
							parent.deviceHandle[i].setMainSpeed(server.getEffectSpeed());
						if (changeSize)
							parent.deviceHandle[i].setMainSize(server.getEffectSize());

						for (int key : changFader.keySet()) {
							parent.deviceHandle[i].setInput(key, changFader.get(key));
						}
						;

					}
					// for (int j = 0; j < Constants.DEVICE_CHANNELS; j++) {
					// parent.deviceHandle[i].setInput(j, server.getFader(j));
					// }

					// parent.deviceHandle[i].addEffect(e);

				}

			}

			if (server.isSavePresst()) {
				saveScene();
				server.setSaveRead();
			}

			OldResponse = server.usedRespons;

		}
	}

	// Help Funcktions

	private void loadScene(int id) {
		System.out.println("load Scene " + id);
		String payload = parent.db.Select.scene(id);
		String eff = parent.db.Select.effects(id);
		if (!(payload.isEmpty())) {
			payload = payload.replace("\\", "");
			Scene scene = parent.gson.fromJson(payload, Scene.class);
			OldEffects oEff = parent.gson.fromJson(eff, OldEffects.class);
			// System.out.println(devs[0].getOutput_unticked().toString());
			Device[] oDev = scene.getDevices();
			Effect_Representative[] toSetEff = oEff.getEffects();

			// Effect_ID.getEffectID(e)

			// for (int i = 0; i < oEffects.length; i++) {
			// if (oDev[i].main_effect.isEmpty()) {
			// oEffects[i] = null;
			// } else {
			// oEffects[i] = oDev[i].main_effect.get(0);
			// oDev[i].deleteMainEffect(0);
			// // oEffects[i].
			// }
			// }
			//
			parent.deviceHandle = oDev;
			// setOldeffects = true;

			// set effects new

			for (int i = 0; i < toSetEff.length; i++) {
				if (toSetEff[i] != null) {
					// Effect_ID eid = Effect_ID.getEffectID(toSetEff[i]);
					// Effect e = Effect_ID.generateEffectFromID(eid, toSetEff[i].getSpeed(),
					// toSetEff[i].getSize(), 0,
					// new int[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

					parent.deviceHandle[i].addMainEffect(toSetEff[i].generateEffect(), 0);
				}
			}
		}

	}

	private void saveScene() {
		System.out.println("save scene " + server.getScenenID().get(0));
		Effect[] Effects = new Effect[30];
		Device[] oDev = Arrays.copyOf(parent.deviceHandle, 30);
		
		for (int i = 0; i < oEffects.length; i++) {
			if (oDev[i].main_effect.isEmpty()) {
				Effects[i] = null;
			} else {
				Effects[i] = oDev[i].main_effect.get(0);
				oDev[i].deleteMainEffect(0);
				// oEffects[i].
			}
		}

		Scene scene = new Scene(oDev);
		String payload = parent.gson.toJson(scene);

		Effect_Representative[] ER = new Effect_Representative[30];

		for (int i = 0; i < Effects.length; i++) {
			if (Effects[i] != null) {
				ER[i] = new Effect_Representative(Effects[i]);
			}
		}

		OldEffects ef = new OldEffects(ER);
		String saveEf = parent.gson.toJson(ef);

		int id = server.getScenenID().get(0);
		parent.db.Update.scen(id, payload, saveEf);

	}
}
