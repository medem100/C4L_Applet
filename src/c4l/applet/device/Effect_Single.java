package c4l.applet.device;

import c4l.applet.main.Constants;

/**
 * @author Timon
 *
 * Defines an effect, which only modifies a single channel.
 * This may be applied to multiple channels within one object.
 */
public class Effect_Single extends Effect {
	public enum Effect1_type {
		SINUS, RAMP, LINEAR
	}
	
	private Effect1_type type;
	private int[] channels;

	/**
	 * Constructor
	 * @param size		passed to super
	 * @param speed		passed to super
	 * @param offset	passed to super
	 * @param type		type of effect (see Effect.Effect1_type)
	 * @param channels	Int-Array containing 1 if the effect should be applied on that channel, 0 otherwise
	 * 
	 */
	public Effect_Single(int size, int speed, int offset, Effect1_type type, int[] channels) {
		super(size, speed, offset);
		if (type == null) throw new NullPointerException("Make sure to define a effect-type.");
		this.type = type;
		this.channels = channels;
	}
		
	public int[] apply(int[] in) {
		System.out.println("Applying Single-Effect." + String.valueOf(size));
		for (int i = 0; i < Constants.DEVICE_CHANNELS; i++) {
			if (channels[i] == 1) {
				switch (type) {
				case SINUS:
					in[i] = cutOff((int) (in[i] + size/2*Math.sin(state*2*Math.PI/Constants.EFFECTRANGE)));
					break;
				case RAMP:
					in[i] = cutOff((int) (in[i] + size*(state/Constants.EFFECTRANGE - 0.5)));
					break;
				case LINEAR:
					in[i] = cutOff((int) (in[i] + size*(2*Math.abs(state/Constants.EFFECTRANGE - 0.5)) - 0.5));
					break;
				}
			}
		}
		return in; //TODO
	}
}
