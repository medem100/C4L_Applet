package c4l.applet.main;

/*
 * Constants for the whole project
 */

public class Constants {
	//General
	/** Number of channels a device has. */
	public final static int DEVICE_CHANNELS = 16;
	
	// Input constants
	/** Tolerance of variation on the hardware-faders, that are interpreted as side-effects, rather than as inputs. */
	public final static int FADER_TOLERANCE = 3;
	/** Range of the rotary value. */
	public final static int ROTARY_RANGE = 1024; //TODO maybe adjust value
	
	
	// Output constants
	/** constanten für den Output handling mit entec **/
	public final static int  MAXCHANNEL = 512;
	public final static int  MINCHANNEL = 1;
	public final static int  MAXVALUE = 255;
	public final static int  MINVALUE = 0;

	
	//Effects constants
	/** Range of the state of an effect. Loopover-point for it. */
	public final static int EFFECTRANGE = 65536;
	/** length of one interval for random effects */
	public final static int EFFECTSTEP = 16384;
}
