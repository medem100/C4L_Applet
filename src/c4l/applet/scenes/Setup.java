package c4l.applet.scenes;

public class Setup {
	//public Device_Setup[] d_setup;
	
	/** 512-array saying, if a value can be faded in a sensible way (example for true: red; for false: effect-preset) */
	public final boolean[] fadeable;
	/** array containing the output-permutation (saying for each output-channel, which pre-perm-channel he is) */
	public final int[] perm;
	
	/**
	 * 
	 * @param fadeability
	 * @param permutation	output-permutation. CHannel 512 sends constant-0;
	 */
	public Setup(boolean[] fadeability, int[] permutation) {
		this.fadeable = fadeability;
		this.perm = permutation;
	}
}
