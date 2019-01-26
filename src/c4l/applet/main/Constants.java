package c4l.applet.main;

/*
 * Constants for the whole project
 */

public class Constants {
	//General
	/** Number of channels a device has. */
	public final static int DEVICE_CHANNELS = 16;
	public final static int DYNAMIC_DEVICES = 30;
	public final static int STATIC_DEVICES = 2;
	public final static int ALL_DEVICES = DYNAMIC_DEVICES + STATIC_DEVICES;
	
	// Input constants
	/** Tolerance of variation on the hardware-faders, that are interpreted as side-effects, rather than as inputs. */
	public final static int FADER_TOLERANCE = 3;
	/** Range of the rotary value. */
	public final static int ROTARY_RANGE = 1024; //TODO maybe adjust value
	
	
	// Output constants
	/** constanten f�r den Output handling mit entec **/
	public final static int  MAXCHANNEL = 511;
	public final static int  MINCHANNEL = 0;
	public final static int  MAXVALUE = 255;
	public final static int  MINVALUE = 0;

	
	//Effects constants
	/** Range of the state of an effect. Loopover-point for it. */
	public final static int EFFECTRANGE = 65536;
	/** length of one interval for random effects */
	public final static int EFFECTSTEP = 16384;
}
