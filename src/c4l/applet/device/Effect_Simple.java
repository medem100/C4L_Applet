package c4l.applet.device;

import java.awt.Color;

import c4l.applet.main.Constants;

/**
 * Defines a simple, deterministic effect.
 * An effect of this class may affect output values in one or multiple ways.
 * Each of this affections can be applied to none, one ore multiple channels
 * 
 * @author Timon
 */
public class Effect_Simple extends Effect {
	public enum Effecttype_det {
		//single channel
		/** Add a sine-wave of amplitude size/2 */								SINUS,
		/** linear fade from -size/2 to +size/2, jump back */					RAMP,
		/** linear fade from +size/2 to -size/2, jump back */					REVRAMP,
		/** linear fade from +size/2 to -size/2, fade back */					LINEAR,
		/** like linear, but pausing at low-point */							LINEAR_HOLDL,
		/** like linear, but pausing at high-point */							LINEAR_HOLDH,
		/** Channel is active size/256 parts of effect time, zero afterwards */	STROBO,
		/** Strobo without changing colors when on */							STROBO_HOLD,
		//two channels
		/** sine on first channel, cosine on second */							CIRCLE,
		/** triangular movement on two channels */								TRIANGLE,
		//three channels
		/** fade through a rainbow (assuming 1,2,3 as RGB) */					RAINBOW,
	}
	
	private Effecttype_det type;

	//Overloading Constructors
	public Effect_Simple(int size, int speed, int offset, Effecttype_det type, int[] channels) {
		this(size, speed, offset, true, type, channels);
	}
	public Effect_Simple(int size, int speed, int offset, Effecttype_det type, int[] channels, boolean acceptInput) {
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
	 * 
	 */
	public Effect_Simple(int size, int speed, int offset, boolean acceptInput, Effecttype_det type, int[] channels) {
		super(size, speed, offset, acceptInput, channels);
		if (type == null) throw new NullPointerException("Make sure to define a effect-type.");
		this.type = type;
		this.last_state = Constants.EFFECTRANGE + 1;
	}
		
	public Effecttype_det getType() {
		return type;
	}
	
	@Override public int[] apply(int[] in) {
		Color color = null; int[] out = null;
		if (type == Effecttype_det.RAINBOW) {
			color = Color.getHSBColor(((float) state)/Constants.EFFECTRANGE, 1, 1);
		} /* if */
		if (type == Effecttype_det.STROBO_HOLD) {
			if (state < last_state) out = in.clone();
		}
		
		for (int i = 0; i < Constants.DEVICE_CHANNELS; i++) {
			switch (type) {
			//Single-channel-effects
			case SINUS:
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] + size/2*Math.sin(state*2*Math.PI/Constants.EFFECTRANGE)));
				break;
			case RAMP:
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] + size*(state/Constants.EFFECTRANGE - 0.5)));
				break;
			case REVRAMP:
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] - size*(state/Constants.EFFECTRANGE - 0.5)));
				break;
			case LINEAR:
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] + size*(2*Math.abs(state/Constants.EFFECTRANGE - 0.5) - 0.5)));
				break;
			case LINEAR_HOLDL:
				if (channels[i] == 1) {if (state < Constants.EFFECTRANGE/4 || state >= Constants.EFFECTRANGE*3/4) in[i] = cutOff((int) (in[i] + size*(4*Math.abs(state/Constants.EFFECTRANGE - 0.5) - 1.5))); else in[i] = cutOff(in[i] - size/2);}
				break;
			case LINEAR_HOLDH:
				if (channels[i] == 1) {if (state < Constants.EFFECTRANGE/2) in[i] = cutOff((int) (in[i] + size*(2*Math.abs(2*state/Constants.EFFECTRANGE - 0.5) - 0.5))); else in[i] = cutOff(in[i] + size/2);}
				break;
			case STROBO:
				if (channels[i] == 1) {if (size*(Constants.EFFECTRANGE/(Constants.MAXVALUE + 1)) <= state) in[i] = in[i]; else in[i] = 0;}
				break;
			case STROBO_HOLD:
				if (channels[i] == 1) {if (size*(Constants.EFFECTRANGE/(Constants.MAXVALUE + 1)) <= state) in[i] = out[i]; else in[i] = 0;}
				break;
				
			//Multi-channel-effect
			case CIRCLE:
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] + size/2*Math.sin(state*2*Math.PI/Constants.EFFECTRANGE)));
				if (channels[i] == 2) in[i] = cutOff((int) (in[i] + size/2*Math.cos(state*2*Math.PI/Constants.EFFECTRANGE)));
				break;
			case TRIANGLE:
				if (channels[i] == 1) {if (state < Constants.EFFECTRANGE*2/3) in[i] = cutOff((int) (in[i] + size*(3/2*state/Constants.EFFECTRANGE - 0.5))); else in[i] = cutOff((int) (in[i] - size*(3*state/Constants.EFFECTRANGE - 2.5)));}
				if (channels[i] == 2) {if (state < Constants.EFFECTRANGE*2/3) in[i] = cutOff((int) (in[i] + size*(2*Math.abs(3/2*state/Constants.EFFECTRANGE - 0.5) - 0.5))); else in[i] = cutOff(in[i] + size/2);}
				break;
			case RAINBOW:
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] + (size*(color.getRed() - 0.5*Constants.MAXVALUE))/Constants.MAXVALUE));
				if (channels[i] == 2) in[i] = cutOff((int) (in[i] + (size*(color.getGreen() - 0.5*Constants.MAXVALUE))/Constants.MAXVALUE));
				if (channels[i] == 3) in[i] = cutOff((int) (in[i] + (size*(color.getBlue() - 0.5*Constants.MAXVALUE))/Constants.MAXVALUE));
				break;
			} /* switch */
		}/* for*/
		last_state = state;
		return in;
	} /* apply*/
	
}
