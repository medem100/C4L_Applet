package c4l.applet.input;

import c4l.applet.db.Chase;
import c4l.applet.device.Effect;
import c4l.applet.input.arduino.WingController;
import c4l.applet.main.C4L_Launcher;
import c4l.applet.main.Constants;
import c4l.applet.device.Effect_ID;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
	/** object holding and managing the hardware-wing-pult */
	private WingController wing;
	private Boolean ServerAvailable;
	private GuiInput server;
	private JSONObject OldResponse = new JSONObject("{}");
	/** reference to the main-object, used to access stuff beyond input */
	C4L_Launcher parent;

	private int currentSceneId;
	private int[] oldFaderValues = { 0, 0, 0, 0, 0, 0, 0, 0};
	private boolean[] oldselectDevice = new boolean[Constants.DYNAMIC_DEVICES];
	/**
	 * values to init new effects with, written by all inputs, but only on change
	 * (so last wins)
	 */
	private int currentSize, currentSpeed;
	/** last known (and processed) position of server-effect-faders */
	private int serverSize, serverSpeed;
	/** current load chase **/
	private Chase currentChase = null;
	/** current pos in the chase **/
	private int chaseIdx;
	Effect[] oEffects = new Effect[30]; // test
	// boolean setOldeffects = false;

	/** last know (and processed) hardware-fader position */
	private int[] h_faders;
	/** last know (and processed) hardware-fader position */
	private int[] h_bfaders;
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
	public Input(C4L_Launcher parent, Properties arduinoProperties, Boolean ServerAvailable) {
		this(parent, new WingController(arduinoProperties), ServerAvailable);
	}

	public Input(C4L_Launcher parent, String arduinoPropertiesPath, Boolean ServerAvailable) {
		this(parent, new WingController(WingController.openPropertiesFile(arduinoPropertiesPath)), ServerAvailable);
	}

	public Input(C4L_Launcher parent, WingController wing, Boolean ServerAvailable) {
		this.ServerAvailable = ServerAvailable;
		this.wing = wing;
		if (ServerAvailable) {
			try {
				this.server = new NewServerInput();
			} catch (Exception e) {
				e.printStackTrace();
				this.ServerAvailable = false;
			} /* try/catch */ // TODO modify Constructor if necessary
		} /* if */
		this.parent = parent;

		this.h_faders = new int[8];
		this.h_bfaders = new int[8];
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

	public void tick() throws IOException, InterruptedException {
		int temp = 0;

		// Handle the HardwareWing
		if (wing != null) {

			wing.tick();

			// check Wingcontroller for changes in device activity
			boolean[] change = wing.checkActivity();

			for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++)
				active[i] ^= change[i];
			wing.setActiveDevices(active); // Tell wing, which devices are active (for indication-LEDs)

			// check wing-faders
			int offset = 0;
			if (wing.getAnalogBank() != 0)
				offset = 8;
			for (int i = 0; i < 8; i++) {
				temp = wing.getFader(i);
				System.out.println(i + ": " + temp);
				if (Math.abs(temp - h_faders[i]) > wing.FADER_TOLERANCE) {
					// log.debug("finder fade change wing");
					if (temp <= wing.FADER_TOLERANCE)
						temp = 0; // for a correction factor larger than the tolerance this should happen
									// implicitly when dividing by the first... TODO: Think whether to remove this
									// for optimization
					h_faders[i] = temp;
					for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
						if (active[j])
							parent.state.getDevice(j).setInput(i + offset, h_faders[i] / wing.CORRECTION_DIVISOR);
					} /* for */
				} /* if */
			} /* for */

			// check wing-x-faders
			for (int i = 0; i < 4; i++) {
				temp = wing.getXFader(i);
				if (Math.abs(temp - h_xfaders[i]) > wing.FADER_TOLERANCE) {
					h_xfaders[i] = temp;
					switch (i) {
					case 0:
						for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
							if (active[j])
								parent.state.getDevice(j).setMainSpeed(h_xfaders[i] / wing.CORRECTION_DIVISOR);
						} /* for */
						currentSpeed = h_xfaders[i] / wing.CORRECTION_DIVISOR;
						break;
					case 1:
						for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
							if (active[j])
								parent.state.getDevice(j).setMainSize(h_xfaders[i] / wing.CORRECTION_DIVISOR);
						} /* for */
						currentSize = h_xfaders[i] / wing.CORRECTION_DIVISOR;
						break;
					} /* switch */
					// TODO Define use of fader 3 and specify 4
				} /* if */
			} /* for */

			// check rotary encoders
			for (int i = 0; i < wing.NUM_ROTARYS; i++) {
				temp = wing.getRotary(i) - h_rotary[i];
				h_rotary[i] += temp;
				if (temp > wing.ROTARY_RANGE / 2)
					temp -= wing.ROTARY_RANGE;
				if (temp < -wing.ROTARY_RANGE / 2)
					temp += wing.ROTARY_RANGE;
				for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++) {
					if (active[j])
						parent.state.getDevice(j).applyRotary(i, temp);
				} /* for devices */
			} /* for rotary encoders */

			// TODO B-faders
			for (int i = 0; i < wing.NUM_BFADERS; i++) {
				temp = wing.getBFader(i);
				if (Math.abs(temp - h_bfaders[i]) > wing.FADER_TOLERANCE) {
					h_bfaders[i] = temp;
					parent.state.getStaticDevice().setInput(h_bfaders[i] / wing.CORRECTION_DIVISOR, i);
				} /* if */
			} /* for bfaders */
		} /* if wing exists */

		// TODO check dashboard

		if (ServerAvailable) {
			server.tick();
			// Only when there are new data from the Dashboard
			if (server.hasChanged()) {
				System.out.println("new Values !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//	log.debug("New Respons");
			//	log.debug(Arrays.toString(active));
				// start Scene
				if (!server.getSceneIds().isEmpty() && currentSceneId != server.getSceneIds().get(0)) {

					loadScene(server.getSceneIds().get(0));
					currentSceneId = server.getSceneIds().get(0);

				}
			/*	else if (0 != server.getIntValue("startChase")) {
					loadChase(server.getIntValue("startChase"));
					parent.state.newFade(currentChase.getSCENES()[chaseIdx].getId(), server.getIntValue("defaultFadeTime")); // TODO Fade Time
				} else if (server.getBooleanValue("stepChase")) {
					if (chaseIdx == currentChase.getSCENES().length) {
						chaseIdx = 0;
					} else {
						chaseIdx++;
					}
					parent.state.newFade(currentChase.getSCENES()[chaseIdx].getId(), server.getIntValue("defaultFadeTime")); // TODO Fade Time
				}
			*/	else {

				//	log.debug("cange devices");

					boolean[] newSelectDevice = server.getChosenDevices();

					
					for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++) {
						if(newSelectDevice[i] != oldselectDevice[i])
							active[i] = new Boolean(newSelectDevice[i]);
					}
					oldselectDevice = newSelectDevice.clone();

					if (wing != null) {
						wing.setActiveDevices(active, true);
					}

					HashMap<Integer, Integer> changFader = new HashMap<>();
					for (int i = 0; i < oldFaderValues.length; i++) {

						if (oldFaderValues[i] != server.getFaderValue(i)) {
							changFader.put(i, server.getFaderValue(i));
							oldFaderValues[i] = server.getFaderValue(i);
						}
					}

					boolean changeSize = false;
					boolean changeSpeed = false;

					if (serverSize != server.getEffectSize()) {
						changeSize = true;
						serverSize = server.getEffectSize();
						currentSize = serverSize;
					}

					if (serverSpeed != server.getEffectSpeed()) {
						changeSpeed = true;
						serverSpeed = server.getEffectSpeed();
						currentSpeed = serverSpeed;
					}

					String effectId = server.getEffectID().trim();

					// Handel inputs for devices
					for (int i = 0; i < active.length; i++) {
						if (active[i]) {
							
							if (server.getDeleteMainEffect()) {
								log.error("delete Effect");
								if(!parent.state.getDevice(i).main_effect.isEmpty()) {
									// main effect is only on first positon
									parent.state.getDevice(i).deleteMainEffect(0);
								}
							}

							if (!(effectId.equals("99"))) {
								log.debug("set Effect " + effectId);
								Effect_ID eid = new Effect_ID(Integer.valueOf(effectId.substring(0, 1)),
										Integer.valueOf(effectId.substring(1, 2)));
							
								// Check Effect Channels
								int[] effectChannels = parent.state.getDevice(i).getMainEffetChannels().clone();
							/*	if(!(Arrays.equals(DashboardInput.defaultChannels,
										server.getSelectChannels()))) {
									effectChannels = server.getSelectChannels().clone();
								}*/
																
								Effect e = Effect_ID.generateEffectFromID(eid, currentSpeed, currentSize, 0,
										effectChannels);

								// add every effect to First Main effect
								if (!(parent.state.getDevice(i).main_effect.isEmpty()))
									parent.state.getDevice(i).deleteMainEffect(0);
								parent.state.getDevice(i).addMainEffect(e, 0);

							}
							if (changeSpeed)
								parent.state.getDevice(i).setMainSpeed(server.getEffectSpeed());
							if (changeSize)
								parent.state.getDevice(i).setMainSize(server.getEffectSize());

							for (int key : changFader.keySet()) {
								parent.state.getDevice(i).setInput(key, changFader.get(key));
							}


						}
					}

				}
				if (server.isSavePresst()) {
					saveScene();
				}

				if (-1 != server.CrateNewSceneWithIdPresst()) {
					crateNewScene(server.CrateNewSceneWithIdPresst());
				}

			//	server.resetValues();
			//	OldResponse = server.usedRespons;

			}
			//

		}
	}

	// Help Funcktions

	private void loadChase(int chaseId) {
		currentChase = parent.db.Select.chase(chaseId, 1);
	}

	private void loadScene(int id) throws IOException, InterruptedException {
		log.error("new Scene");
		log.debug("load scene: " + id + " in setup: " + server.getSetupID());
		// parent.deviceHandle = parent.db.Select.scene(id);
		// parent.state.newScene(id);
		parent.state.newFade(id, server.getDefaultFadeTime());
		currentSceneId = id;

	}

	private void crateNewScene(int sceneID) {
		// logger.debug("crate new scene");
		try {
			log.debug("Create new Scene");
			parent.db.Insert.devicesToScene(parent.state.getDevices().clone(), sceneID);
			// server.set
		} catch (Exception e) {
			log.error(e);
		}
	}

	private void saveScene() throws IOException, InterruptedException {
		log.debug("save scene: " + currentSceneId + " in setup: " + server.getSetupID());
		parent.db.Insert.devicesToScene(parent.state.getDevices().clone(), currentSceneId);

	}
}
