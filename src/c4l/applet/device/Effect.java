package c4l.applet.device;

import c4l.applet.main.Constants;

/**
 * Effect class. Gets an device and applies an effect on its output-values
 * @author Timon
 *
 */
public class Effect {
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
	
	/**
	 * Set current state of the effect to start state when loaded again.
	 */
	public void makeThisStateDefault() {
		offset = (state + offset) % Constants.EFFECTRANGE;
	}
	/**
	 * Let effect progress further. Should be called on a regular basis if effect is used
	 */
	public void tick() {
		state = (state + speed) % Constants.EFFECTRANGE;
	}
	public int[] apply(int[] in) {
		System.out.println("Applying Effect." + String.valueOf(size));
		return in; //TODO
	}
	
	
	//Test-Main
	public static void main(String[] args) {
		int[] perm = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		Device d = new Device(perm);
		d.setInputs(perm);
		Effect e = new Effect(0, 0, 0);
		Effect e1 = new Effect(1, 0, 0);
		d.addEffect(e);
		d.addEffect(e1);
		d.getOutput();
	}	
}
