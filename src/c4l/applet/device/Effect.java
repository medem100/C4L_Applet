package c4l.applet.device;

import c4l.applet.device.Effect_Simple.Effecttype_det;
import c4l.applet.main.Constants;

/**
 * Effect class. Gets an device and applies an effect on its output-values
 * @author Timon
 *
 */
public class Effect {
	protected int size;
	private int speed;
	private int offset;
	private boolean acceptInput;
	
	protected int state;
	protected int last_state;
		

	public Effect(int size, int speed, int offset) {
		this(size, speed, offset, true); //call larger Constructor
	}
	public Effect(int size, int speed, int offset, boolean acceptInput) {
		super();
		this.size = size;
		this.speed = speed;
		this.offset = offset;
		this.state = offset;
		
		this.acceptInput = acceptInput;
	}
	
	/**
	 * Set the size of an effect. The effect may discard the change, if intern variable acceptInput says so.
	 * Use setSize(int, bool) to force change.
	 * @param size	new size
	 */
	public void setSize(int size) {
		if (acceptInput) this.size = size;
	}
	/**
	 * Set the size of an effect.
	 * The effect may discard an unforced change, if intern variable acceptInput says so.
	 * @param size	new size
	 * @param force	force change
	 */
	public void setSize(int size, boolean force) {
		if (acceptInput || force) this.size = size;
	}
	public int getSize() {
		return size;
	}
	/**
	 * Set the speed of an effect. The effect may discard the change, if intern variable acceptInput says so.
	 * Use setSpeed(int, bool) to force change.
	 * @param speed	new speed
	 */
	public void setSpeed(int speed) {
		if (acceptInput) this.speed = speed;
	}
	/**
	 * Set the speed of an effect.
	 * The effect may discard an unforced change, if intern variable acceptInput says so.
	 * @param speed	new speed
	 * @param force	force change
	 */
	public void setSpeed(int speed, boolean force) {
		if (acceptInput || force) this.speed = speed;
	}
	public int getSpeed() {
		return speed;
	}
	/**
	 * Set current state of the effect to start state when loaded again.
	 */
	public void makeThisStateDefault() {
		offset = (state + offset) % Constants.EFFECTRANGE;
	}
	public int getOffset() {
		return offset;
	}	

	public boolean isAcceptInput() {
		return acceptInput;
	}
	public void setAcceptInput(boolean acceptInput) {
		this.acceptInput = acceptInput;
	}
	/**
	 * Let effect progress further. Should be called on a regular basis if effect is used
	 */
	public void tick() {
		state = (state + speed) % Constants.EFFECTRANGE;
	}
	
	/**
	 * Apply effect.
	 * @param in	Array of length {@value Constants.#DEVICE_CHANNELS}, which is modified by the effect
	 * @return		Array of same length with modified values.
	 */
	public int[] apply(int[] in) {
		System.out.println("Applying Effect." + String.valueOf(size));
		return in; //Real Effects are implemented in subclasses
	}
	
	/** Intern function to pack any value into the DMX-Protocol-Interval */
	protected static int cutOff(int x) {
		if (x < Constants.MINVALUE) return Constants.MINVALUE;
		if (x > Constants.MAXVALUE) return Constants.MAXVALUE;
		return x;
	}
	
	//Test-Main
	public static void main(String[] args) {
		int[] perm = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		Device d = new Device(perm);
		d.setInputs(perm);
		Effect e = new Effect(0, 0, 0);
		Effect e1 = new Effect_Simple(1, 0, 0, Effecttype_det.RAMP, perm);
		d.addEffect(e);
		d.addEffect(e1);
		d.getOutput();
	}	
}
