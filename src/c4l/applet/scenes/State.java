package c4l.applet.scenes;

import java.util.Arrays;

import c4l.applet.db.DB;
import c4l.applet.device.Device;
import c4l.applet.device.Static_Device;
import c4l.applet.main.Constants;

public class State {
	private Setup setup;
	private Scene old_scene;
	private Scene new_scene;

	/** determines how fast a fade goes: every tick adds this to fade_state */
	private int fade_speed;
	/**
	 * 0 corresponds to old_scene, Constants.SCENE_FADE_LENGTH to new_scene, is
	 * incremented when ticking
	 */
	private int fade_state;
	/**
	 * declares channels (on pre-permutation-level) that shouldn't be faded and are
	 * static on the new scene (e.g. cause they don't change in the scene)
	 */
	private boolean noFade[];

	private int[] output;

	// Constructor
	public State(Setup setup) {
		this.setup = setup;

		fade_state = Constants.SCENE_FADE_LENGTH;
		fade_speed = 0;
		noFade = new boolean[512];

		output = new int[Constants.OUTPUT_LENGTH];

		this.new_scene = new Scene(setup);
		this.old_scene = new Scene(setup);
	}

	// Getters and Setters
	public int getFade_speed() {
		return fade_speed;
	}

	public void setFade_speed(int fade_speed) {
		this.fade_speed = fade_speed;
	}

	public int getFade_state() {
		return fade_state;
	}

	public void setFade_state(int fade_state) {
		this.fade_state = fade_state;
	}

	// Device-Getter
	/**
	 * Gives reference to a device object so that Input can manipulate it.
	 * 
	 * @param index
	 *            of the requested device (0 <= index < Contants.DYNAMIC_DEVICES
	 *            (THIS IS NOT CHECKED HERE AND MY CAUSE UNEXPECTED BEHAVIOUR IF
	 *            VIOLATED))
	 * @return reference to new_scene.devices[index]
	 */
	public Device getDevice(int index) {
		return new_scene.devices[index];
	}

	/**
	 * Gives reference to a device object so that Input can manipulate it.
	 * 
	 * @param index
	 *            of the requested device (0 <= index < Contants.DYNAMIC_DEVICES
	 *            (THIS IS NOT CHECKED HERE AND MY CAUSE UNEXPECTED BEHAVIOUR IF
	 *            VIOLATED))
	 * @return reference to new_scene.devices
	 */
	public Device[] getDevices() {
		return new_scene.devices;
	}

	/**
	 * Gives reference to the static_device object so that Input can manipulate it.
	 * 
	 * @return reference to new_scene.static_device
	 */
	public Static_Device getStaticDevice() {
		return new_scene.static_device;
	}

	// other functions
	/**
	 * Initiate a new scene fade
	 * 
	 * @param sceneId
	 *            storage id of the new Scene
	 * @param speed
	 *            speed with which the fade should happen
	 */
	public void newFade(int sceneId, int speed) {
		this.fade_speed = speed;
		this.fade_state = 0;
		this.old_scene = this.new_scene;

		loadNewScene(sceneId);
	}

	/**
	 * Jump to a new Scene without fading
	 * 
	 * @param sceneId
	 *            storage id of the new Scene
	 */
	public void newScene(int sceneId) {
		this.fade_speed = 0;
		this.fade_state = Constants.SCENE_FADE_LENGTH;
		this.old_scene = this.new_scene;

		loadNewScene(sceneId);
	}

	/**
	 * Loads a scene from db into new_scene, using old_scene to fill empty spots
	 */
	private void loadNewScene(int scenenId) {
		// TODO Andre: Load the new scene into this.new_scene
		/*
		 * Function parameters can be adjusted to provided needed information. Such
		 * changes should then also made to newFade() and newScene() and the function
		 * calls to this function that happen there.
		 * 
		 * Loading from DB should probably happen here, as Scene-Class does not support
		 * missing objects and here you can just copy/keep the old ones. When such
		 * copying occurs one shall make sure to copy the objects (not just referencing
		 * them) to avoid double-ticking. In order to avoid weird side-effects than
		 * noFade can be set to true for such channels that aren't affected by the fade.
		 */

		// TODO Scnene Bundel
		// TODO Storage of Static Devices
		this.new_scene = new Scene(setup, DB.getInstance().Select.scene(scenenId), this.new_scene.static_device);

	}

	/**
	 * ticks everything inside and advances fading
	 */
	public void tick() {
		old_scene.tick();
		new_scene.tick();

		fade_state += fade_speed;
		if (fade_state > Constants.SCENE_FADE_LENGTH) {
			fade_state = Constants.SCENE_FADE_LENGTH;
			fade_speed = 0;
		}
	}

	/**
	 * fades the outputs of the two scenes together
	 * 
	 * @return an 512-int-array of values to be outputted via DMX.
	 */
	public int[] generateOutput() {
		if (this.fade_state >= Constants.SCENE_FADE_LENGTH) { // if you're done with fading take this shortcut
			output = new_scene.generateOutput();	
			return output;
		}

		int[] old_scene_out = old_scene.generateOutput();
		int[] new_scene_out = new_scene.generateOutput();
		double lambda = ((double) fade_state) / Constants.SCENE_FADE_LENGTH;

		// set default-output (for non-fadeable channels) either to old or new scene,
		// depending on fade-position
		if (fade_state > Constants.SCENE_FADE_LENGTH / 2) {
			output = new_scene_out;
		} else {
			output = old_scene_out;
		}

		for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++) {
			if (setup.fadeable[i]) {
				output[i] = (int) Math.round(lambda * new_scene_out[i] + (1 - lambda) * old_scene_out[i]);
			}
			if (noFade[i]) {
				output[i] = new_scene_out[i];
			}
		}
	//	System.out.println(Arrays.toString(output));
		return output;
		
	}
}
