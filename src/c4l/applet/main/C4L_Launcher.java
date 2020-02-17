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
import c4l.applet.scenes.Setup;
import c4l.applet.scenes.State;

/**
 * @author Timon
 *
 */
public class C4L_Launcher {
	DmxOut dmxHandle;
	Input inputHandle;
	public State state;
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
		
		state = new State(new Setup());
	}


	public static void main(String[] args) {
		long last_time = System.currentTimeMillis();
		long time = last_time;
		C4L_Launcher program = new C4L_Launcher();
		
		while (!quit) {
			program.inputHandle.tick();
			program.dmxHandle.setOutput(program.state.generateOutput());
			time = System.currentTimeMillis();
			if (time - last_time > Constants.EFFECTTICKMILLIS) {
				last_time += Constants.EFFECTTICKMILLIS;
				program.state.tick();
			} /* if */
		} /* while */
		
	} /* main */
}
