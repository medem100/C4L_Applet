package c4l.applet.device;

import c4l.applet.device.Effect_Simple.Effecttype_det;
import c4l.applet.main.Constants;

//Maybe move (something like) this to the server
/**
 * A collection of static functions, that help you generating specific effects and/or distributing them to Devices
 * @author Timon
 */
public class Effect_Generator {
	/**
	 * Create a runninglight effect.
	 * @param devices	Devices to work on. First will be on first, second second, tbc.
	 * @param speed		Speed of the effect. Defines how fast one cycle of the runninglight goes
	 * @param channels	The channels that shall be affected by the running light
	 * @param hold		If true, other Effects won't change the values of a device while it's active
	 */
	public static void runningLight(Device[] devices, int speed, int[] channels, boolean hold) {
		int n = devices.length;
		Effecttype_det type = Effecttype_det.STROBO; if (hold) type = Effecttype_det.STROBO_HOLD;
		
		for (int i = 0; i < n; i++) {
			devices[i].addEffect(new Effect_Simple((Constants.MAXVALUE + 1)/n, speed, i*Constants.EFFECTRANGE/n, type, channels, false));
		}
	}
	
	/**
	 * Create a triangle-effect (similar to the one known from the Scanoperator FX II)
	 * @param devices	device(s), on which effect shall be added
	 * @param speed
	 * @param size
	 * @param offset
	 * @param channels	the channels the effect works on. Triangle supports two different channels (1 and 2) all others are ignored (default for that purpose should be 0)
	 */
	public static void triangle(Device[] devices, int speed, int size, int offset, int channels[]) {
		//Remap channel-association
		int[] channels1 = channels.clone();
		for (int i = 0; i < channels.length; i++) {
			if (channels1[i] == 1) channels1[i] = 0; 
			if (channels[i] == 2) {
				channels[i] = 0;
				channels1[i] = 1;
			}
		}
		
		//Add LINEAR and LINEAR_HOLDL effect, that combine to triangle
		for (int i = 0; i < devices.length; i++) {
			devices[i].addEffect(new Effect_Simple(size, speed, offset, Effecttype_det.LINEAR, channels, true));
			devices[i].addEffect(new Effect_Simple(size, speed, offset, Effecttype_det.LINEAR_HOLDL, channels1, true));
		}
	}

	
}
