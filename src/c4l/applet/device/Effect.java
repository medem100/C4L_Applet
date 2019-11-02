package c4l.applet.device;

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
	protected int[] channels;
	
	protected int state;
	protected int last_state;
		

	public Effect(int size, int speed, int offset, int[] channels) {
		this(size, speed, offset, true, channels); //call larger Constructor
	}
	public Effect(int size, int speed, int offset, boolean acceptInput, int[] channels) {
		super();
		this.size = size;
		this.speed = speed;
		this.offset = offset;
		this.state = offset;
		this.channels = channels;
		
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
	public int[] getChannels() {
		return channels;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getLast_state() {
		return last_state;
	}
	public void setLast_state(int last_state) {
		this.last_state = last_state;
	}
	/**
	 * Let effect progress further. Should be called on a regular basis if effect is used
	 */
	public void tick() {
		//state = (state + speed) % Constants.EFFECTRANGE;
		state = (state + speed_lookup[speed]) % Constants.EFFECTRANGE;
	}
	
	/**
	 * Apply effect.
	 * @param in	Array of length {@value Constants.#DEVICE_CHANNELS}, which is modified by the effect
	 * @return		Array of same length with modified values.
	 */
	public int[] apply(int[] in) {
		return in; //Real Effects are implemented in subclasses
	}
	
	/** Intern function to pack any value into the DMX-Protocol-Interval */
	protected static int cutOff(int x) {
		if (x < Constants.MINVALUE) return Constants.MINVALUE;
		if (x > Constants.MAXVALUE) return Constants.MAXVALUE;
		return x;
	}
	
	private static final int[] speed_lookup = {0, 1, 2, 3, 4, 6, 7, 8, 9, 11, 12, 13, 15, 16, 18, 19, 21, 22, 24, 25, 27, 28, 30, 31, 33, 35, 36, 38, 40, 42, 43, 45, 47, 49, 51, 53, 55, 57, 59, 61, 63, 65, 67, 69, 72, 74, 76, 79, 81, 83, 86, 88, 91, 93, 96, 98, 101, 104, 106, 109, 112, 115, 118, 121, 124, 127, 130, 133, 136, 139, 142, 146, 149, 153, 156, 160, 163, 167, 170, 174, 178, 182, 186, 190, 194, 198, 202, 206, 210, 214, 219, 223, 228, 232, 237, 242, 247, 251, 256, 261, 266, 272, 277, 282, 287, 293, 298, 304, 310, 316, 321, 327, 334, 340, 346, 352, 359, 365, 372, 379, 385, 392, 399, 406, 414, 421, 429, 436, 444, 452, 459, 468, 476, 484, 492, 501, 510, 518, 527, 536, 545, 555, 564, 574, 584, 593, 603, 614, 624, 634, 645, 656, 667, 678, 689, 701, 712, 724, 736, 748, 761, 773, 786, 799, 812, 825, 839, 852, 866, 880, 895, 909, 924, 939, 954, 969, 985, 1001, 1017, 1033, 1050, 1067, 1084, 1101, 1119, 1137, 1155, 1173, 1192, 1211, 1230, 1249, 1269, 1289, 1310, 1330, 1351, 1373, 1394, 1416, 1439, 1461, 1484, 1508, 1531, 1555, 1580, 1604, 1630, 1655, 1681, 1707, 1734, 1761, 1788, 1816, 1844, 1873, 1902, 1932, 1962, 1992, 2023, 2054, 2086, 2118, 2151, 2184, 2218, 2252, 2287, 2322, 2358, 2395, 2431, 2469, 2507, 2545, 2585, 2624, 2665, 2705, 2747, 2789, 2832, 2875, 2919, 2964, 3009, 3055, 3102, 3150, 3198, 3247, 3296};
}
