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
		/** Move to random spots (range size) */ WILD,
		/** Jump to random spots (range size) */ JUMP
	}
	
	private Effecttype_Random type;
	
	private int n_channels = 0; //holds the largest numbered channel we need to hold and generate values for
	private double[] last; //holds randomness applied at last call (or last call to Math.random())
	private double[] next; //additional value-holder for some effects

	//Overloading Constructors
	public Effect_Random(int size, int speed, int offset, Effecttype_Random type, int[] channels) {
		this(size, speed, offset, true, type, channels);
	}
	public Effect_Random(int size, int speed, int offset, Effecttype_Random type, int[] channels, boolean acceptInput) {
		this(size, speed, offset, acceptInput, type, channels);
	}
	/**
	 * Constructor
	 * @param size		passed to super
	 * @param speed		passed to super
	 * @param offset	passed to super
	 * @param type		type of effect (see Effect.Effect1_type)
	 * @param acceptInput	whether the effect accepts unforced changes later on (default: True)
	 * @param channels	Int-Array defining how each channel is modified:
	 * 					0 or invalid: no change;
	 * 					1, 2, ... output-values of the effect.
	 */
	public Effect_Random(int size, int speed, int offset, boolean acceptInput, Effecttype_Random type, int[] channels) {
		super(size, speed, offset, acceptInput, channels);
		if (type == null) throw new NullPointerException("Make sure to define a effect-type.");
		this.type = type;
		
		n_channels = 0;
		for (int i = 0; i < Constants.DEVICE_CHANNELS; i++) {
			if (channels[i] > n_channels) n_channels = channels[i];
		}
		this.last = new double[n_channels];
		for (int i = 0; i < n_channels; i++) last[i] = Math.random();
		this.next = new double[n_channels];
		for (int i = 0; i < n_channels; i++) next[i] = Math.random();
		this.last_state = 0;
	}

	
	public Effecttype_Random getType() {
		return type;
	}
	
	
	@Override public int[] apply(int[] in) {
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
				if (channels[i] > 0) in[i] = cutOff((int) (in[i] + size*(last[channels[i] - 1] - 0.5)));
				break;
			case WILD: //Convex-combination of last and next
				if (channels[i] > 0) in[i] = cutOff((int) (in[i] + size*(((state - last_state)*last[channels[i] - 1] + (Constants.EFFECTSTEP - state + last_state)*next[channels[i] - 1]) - 0.5))/Constants.EFFECTSTEP);
				break;
			} /* switch */
		} /* for */
		return in;
	} /* apply */
	
}
