package c4l.applet.device;

import c4l.applet.main.Constants;

/**
 * Defines Effects, that work randomly.
 * Note: This will look slightly different every time you execute it. This class provides no tools to fix one random outcome for future uses.
 * 
 * @author Timon
 */
public class Effect_Random extends Effect {
	public enum Effecttype_Random {
		WILD, JUMP
	}
	
	private Effecttype_Random type;
	private int[] channels;
	
	private int n_channels = 0; //holds the largest numbered channel we need to hold and generate values for
	private double[] last; //holds randomness applied at last call (or last call to Math.random())
	private double[] next; //additional value-holder for some effects

	/**
	 * Constructor
	 * @param size		passed to super
	 * @param speed		passed to super
	 * @param offset	passed to super
	 * @param type		type of effect (see Effect.Effect1_type)
	 * @param channels	Int-Array defining how each channel is modified:
	 * 					0 or invalid: no change;
	 * 					1, 2, ... output-values of the effect.
	 */
	public Effect_Random(int size, int speed, int offset, Effecttype_Random type, int[] channels) {
		super(size, speed, offset);
		if (type == null) throw new NullPointerException("Make sure to define a effect-type.");
		this.type = type;
		this.channels = channels;
		
		for (int i = 0; i < Constants.DEVICE_CHANNELS; i++) {
			if (channels[i] > n_channels) n_channels = channels[i];
		}
		this.last = new double[n_channels];
		for (int i = 0; i < n_channels; i++) last[i] = Math.random();
		this.next = new double[n_channels];
		for (int i = 0; i < n_channels; i++) next[i] = Math.random();
		this.last_state = 0;
	}

	
	@Override public int[] apply(int[] in) {
		System.out.println("Applying Random-Effect." + String.valueOf(size));
		if ((state > last_state + Constants.EFFECTSTEP) || (state < last_state)) {
			switch (type) { //change random values
			case JUMP:
				last_state = (last_state + Constants.EFFECTSTEP) % Constants.EFFECTRANGE;
				for (int j = 0; j < n_channels; j++) {
					last[j] = Math.random();
				}
				break;
			case WILD:
				last_state = (last_state + Constants.EFFECTSTEP) % Constants.EFFECTRANGE;
				for (int j = 0; j < n_channels; j++) {
					last[j] = next[j];
					next[j] = Math.random();
				}
				break;
			} /* switch */
		} /* if */
		
		for (int i = 0; i < Constants.DEVICE_CHANNELS; i++) {
			switch (type) {
			//Single-channel-effects
			case JUMP: //Steadily adding last, then jump, when it changes
				if (channels[i] > 0) in[i] = cutOff((int) (in[i] + size*(last[i] - 0.5)));
				break;
			case WILD: //Convex-combination of last and next
				if (channels[i] > 0) in[i] = cutOff((int) (in[i] + size*(((state - last_state)*last[i] + (Constants.EFFECTSTEP - state + last_state)*next[i]) - 0.5))/Constants.EFFECTSTEP);
				break;
			} /* switch */
		} /* for */
		return in;
	} /* apply */
	
}
