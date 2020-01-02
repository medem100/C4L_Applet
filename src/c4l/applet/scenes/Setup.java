package c4l.applet.scenes;

import java.util.Arrays;
import java.util.stream.IntStream;

import c4l.applet.main.Constants;

public class Setup {
	public Device_Setup[] d_setup;
	
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
	/**
	 * Argumentless constructor, allowing fading on all channels with trivial permutation.
	 */
	public Setup() {
		this.fadeable = new boolean[512];
		Arrays.fill(fadeable, true);
		
		this.perm = IntStream.range(0, Constants.OUTPUT_LENGTH).toArray();
		this.d_setup = new Device_Setup[Constants.DYNAMIC_DEVICES];
		for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++) {
			this.d_setup[i] = new Device_Setup();
		}
	}
}
