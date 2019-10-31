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
	/** Number of devices with reduced features */
	public final static int STATIC_DEVICES = 2; //QUESTION Ist das sinnvoll, dass noch als Device zu handhaben? Würde am Anfang einfach stumpf die Werte durchschleusen
	/** Total number of Devices */
	public final static int ALL_DEVICES = DYNAMIC_DEVICES + STATIC_DEVICES;
	
	//Device generation
	public final static int[] STANDART_PERMUTATION = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	public final static int[] STANDART_ROTARY_CHANNELS = {0,1,2};
	
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
	public final static int EFFECTRANGE = 65536/16;
	/** length of one interval for random effects */
	public final static int EFFECTSTEP = 16384/16;
	/** Time in Milliseconds between Effect-Ticks */
	public final static int EFFECTTICKMILLIS = 25;
}
