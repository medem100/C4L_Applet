package c4l.applet.main;

import org.apache.log4j.PropertyConfigurator;

import com.google.gson.Gson;

import c4l.applet.db.DB;
import c4l.applet.device.Device;
import c4l.applet.device.Effect;
import c4l.applet.device.Effect_Generator;
import c4l.applet.device.Effect_ID;
import c4l.applet.input.Input;
import c4l.applet.output.DmxOut;

/**
 * @author Timon
 *
 */
public class C4L_Launcher {
	DmxOut dmxHandle;
	Input inputHandle;
	public Device[] deviceHandle;
	String resourcePath;
	public Gson gson = new Gson();
	public DB db = c4l.applet.db.DB.getInstance();

	static boolean quit = false;

	/**
	 * Constructor
	 */
	C4L_Launcher() {
		// This is some weird solution: definitely ugly, but may work
		resourcePath = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
		resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("/"));
		resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("/")) + "/resources/";
		System.out.println("Resource path:");
		System.out.println(resourcePath);

		// PropertyConfigurator.configure(resourcePath + Constants.PROPERTIES_PATH +
		// Constants.LOG4J_PROPERTIES_PATH); // not nice , but the system variable don´t
		// work now
		PropertyConfigurator.configure("resources/properties/log4j.properties"); // Ugly But work Now
		// TODO : check for ServerAvalibale
		dmxHandle = new DmxOut();
		// inputHandle = new Input(this, resourcePath + Constants.PROPERTIES_PATH +
		// Constants.ARDUINO_PROPERTIES_PATH,true);
		//inputHandle = new Input(this, Util.getServerAvalibal()); // Dev Dasboard input
		inputHandle = new Input(this);
		deviceHandle = new Device[Constants.DYNAMIC_DEVICES];
		for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++) {
			deviceHandle[i] = new Device(Constants.STANDART_PERMUTATION, i * Constants.DEVICE_CHANNELS);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long last_time = System.currentTimeMillis();
		long time = last_time;
		C4L_Launcher program = new C4L_Launcher();
		// program.deviceHandle[0].setInput(0, 100);
		// program.deviceHandle[0].setInput(1, 50);
		//
		// Effect_ID id = new Effect_ID(1, 1);
		// Effect e = Effect_ID.generateEffectFromID(id, 100, 150, 1, new int[]
		// {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
		//
		// program.deviceHandle[0].addEffect(e,0);
		// program.deviceHandle[0].deleteEffect(0);
		//
		// program.deviceHandle[0].deleteEffect(0);
		while (!quit) {
			program.inputHandle.tick();
			program.dmxHandle.setOutput(program.deviceHandle);
			time = System.currentTimeMillis();
			if (time - last_time > Constants.EFFECTTICKMILLIS) {
				last_time += Constants.EFFECTTICKMILLIS;
				// last_time =time;
				for (Device d : program.deviceHandle) {

					d.tick();
				}
			}
		}
	}
}
