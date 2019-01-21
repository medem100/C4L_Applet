package c4l.applet.device;

import c4l.applet.main.Constants;

/**
 * Effect class. Gets an device and applies an effect on its output-values
 * @author Timon
 *
 */
public class Effect {
	@SuppressWarnings("unused")
	private int size;
	private int speed;
	private int offset;
	
	private int state;

	public Effect(int size, int speed, int offset) {
		super();
		this.size = size;
		this.speed = speed;
		this.offset = offset;
		this.state = offset;
	}
	
	public void makeThisStateDefault() {
		offset = (state + offset) % Constants.EFFECTRANGE;
	}
	public void tick() {
		state = (state + speed) % Constants.EFFECTRANGE;
	}
	public int[] apply(int[] in) {
		return in; //TODO
	}
	
}
