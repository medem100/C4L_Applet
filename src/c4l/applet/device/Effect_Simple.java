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
		SINUS, RAMP, REVRAMP, LINEAR, //single channel
		CIRCLE, //two channels
		RAINBOW, //three channels
	}
	
	private Effecttype_det type;
	private int[] channels;

	/**
	 * Constructor
	 * @param size		passed to super
	 * @param speed		passed to super
	 * @param offset	passed to super
	 * @param type		type of effect (see Effect.Effect1_type)
	 * @param channels	Int-Array defining how each channel is modified:
	 * 					0 or invalid: no change;
	 * 					1, 2, ... output-values of the effect.
	 * 
	 */
	public Effect_Simple(int size, int speed, int offset, Effecttype_det type, int[] channels) {
		super(size, speed, offset);
		if (type == null) throw new NullPointerException("Make sure to define a effect-type.");
		this.type = type;
		this.channels = channels;
	}
		
	@Override public int[] apply(int[] in) {
		System.out.println("Applying Single-Effect." + String.valueOf(size));
		Color color = null;
		if (type == Effecttype_det.RAINBOW) {
			color = Color.getHSBColor(((float) state)/Constants.EFFECTRANGE, 1, 1);
		} /* if */
		
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
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] + size*(2*Math.abs(state/Constants.EFFECTRANGE - 0.5)) - 0.5));
				break;
				
			//Multi-channel-effect
			case CIRCLE:
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] + size/2*Math.sin(state*2*Math.PI/Constants.EFFECTRANGE)));
				if (channels[i] == 2) in[i] = cutOff((int) (in[i] + size/2*Math.cos(state*2*Math.PI/Constants.EFFECTRANGE)));
				break;
			case RAINBOW:
				if (channels[i] == 1) in[i] = cutOff((int) (in[i] + (size*(color.getRed() - 0.5*Constants.MAXVALUE))/Constants.MAXVALUE));
				if (channels[i] == 2) in[i] = cutOff((int) (in[i] + (size*(color.getGreen() - 0.5*Constants.MAXVALUE))/Constants.MAXVALUE));
				if (channels[i] == 3) in[i] = cutOff((int) (in[i] + (size*(color.getBlue() - 0.5*Constants.MAXVALUE))/Constants.MAXVALUE));
				break;
			} /* switch */
		}/* for*/
		return in;
	} /* apply*/
	
}
