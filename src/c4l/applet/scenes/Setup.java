package c4l.applet.scenes;

public class Setup {
	//public Device_Setup[] d_setup;
	
	/** 512-array saying, if a value can be faded in a sensible way (example for true: red; for false: effect-preset) */
	public final boolean[] fadeable;
	
	public Setup(boolean[] fadeability) {
		this.fadeable = fadeability;
	}
}
