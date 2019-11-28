package c4l.applet.main;

import org.apache.log4j.PropertyConfigurator;

import com.google.gson.Gson;
import org.apache.log4j.Logger;


import c4l.applet.db.DB;
import c4l.applet.device.Device;
import c4l.applet.device.Static_Device;
import c4l.applet.input.Input;
import c4l.applet.input.arduino.WingController;
import c4l.applet.output.DmxOut;

/**
 * @author Timon
 *
 */
public class C4L_Launcher {
	DmxOut dmxHandle;
	Input inputHandle;
	public Device[] deviceHandle;
	public Static_Device staticDevice;
	String resourcePath;
	public Gson gson = new Gson();
	public DB db = c4l.applet.db.DB.getInstance();
	public PropertyManager propM;
	
	static Logger logger = Logger.getLogger(C4L_Launcher.class);

	static boolean quit = false;
 
	/**
	 * Constructor
	 */
	C4L_Launcher() {
		//Get own path to start reading properties
		resourcePath = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
		resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("/"));
		resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("/")) + "/resources/";


		//Initialize Property-Manager
		try {
			PropertyManager.init(resourcePath, Constants.MAIN_PROPERTIES);
			propM = PropertyManager.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Initialize Logging
		PropertyConfigurator.configure(propM.getLog4jPropPath());
		
		//Generate dmxHandle
		dmxHandle = new DmxOut();
		
		// TODO : check for ServerAvalibale //Util.getServerAvalibal() ?
		boolean serverOnline = true;
		
		//Generate inputHandle
		if (propM.getArduinoPropPath() != null) {
			inputHandle = new Input(this, propM.getArduinoPropPath(), serverOnline);
		} else {
			inputHandle = new Input(this, (WingController) null, serverOnline);
		}
		
		//Generate deviceHandle
		deviceHandle = new Device[Constants.DYNAMIC_DEVICES];
		for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++) {
			deviceHandle[i] = new Device(Constants.STANDART_PERMUTATION, i * Constants.DEVICE_CHANNELS);
		}

		logger.debug("Test Debug");
		float[][] matrix = new float[Constants.STATIC_CHANNELS][Constants.STATIC_INPUT];
		for (int i = 0; i < Math.min(Constants.STATIC_CHANNELS, Constants.STATIC_INPUT); i++)
			matrix[i][i] = 1;
		staticDevice = new Static_Device(matrix, Constants.DYNAMIC_DEVICES*Constants.DEVICE_CHANNELS);

	}


	public static void main(String[] args) {
		long last_time = System.currentTimeMillis();
		long time = last_time;
		C4L_Launcher program = new C4L_Launcher();
		
		while (!quit) {
			program.inputHandle.tick();
			program.dmxHandle.setOutput(program.deviceHandle, program.staticDevice);
			time = System.currentTimeMillis();
			if (time - last_time > Constants.EFFECTTICKMILLIS) {
				last_time += Constants.EFFECTTICKMILLIS;
				for (Device d : program.deviceHandle) d.tick();
			} /* if */
		} /* while */
	} /* main */
}
