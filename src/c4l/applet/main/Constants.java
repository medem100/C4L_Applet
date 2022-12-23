package c4l.applet.main;

/**
 * Constants for the whole project
 */
public class Constants {
	//Ports and addresses
	/** Path of property-Folder inside the resource-Folder */
	public static final String MAIN_PROPERTIES = "properties/main.properties";
		
	//General
	/** Number of channels a device has. */
	public final static int DEVICE_CHANNELS = 16;
	/** Number of full-featured devices */
	public final static int DYNAMIC_DEVICES = 30;
	
	/** Number of output channels for static device */
	public final static int STATIC_CHANNELS = 32;
	/** Number of input channels on static device */
	public final static int STATIC_INPUT = 8;
	
	//State constants
	public final static int SCENE_FADE_LENGTH = 65536;
	public final static int OUTPUT_LENGTH = 512;
	
	//Device generation
	public final static int[] STANDART_PERMUTATION = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	public final static int[] STANDART_ROTARY_CHANNELS = {0,1,2};
	public final static int[] STANDART_MAIN_EFFECT_CHANNELS = {1,2,3,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	// Output constants
	/** Last Channel for Enttec-Output **/
	public final static int  MAXCHANNEL = 511;
	/** First Channel for Enttec-Output */
	public final static int  MINCHANNEL = 0;
	/** Maximum output-value for DMX-protocol */
	public final static int  MAXVALUE = 255;
	/** Minimum output-value for DMX-protocol */
	public final static int  MINVALUE = 0;

	
	//Effects constants
	/** Range of the state of an effect. Loopover-point for it. */
	public final static int EFFECTRANGE = 65536;
	/** length of one interval for random effects */
	public final static int EFFECTSTEP = 16384*2;
	/** Time in Milliseconds between Effect-Ticks */
	public final static int EFFECTTICKMILLIS = 25;
}
