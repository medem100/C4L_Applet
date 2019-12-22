package c4l.applet.output;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.juanjo.openDmx.OpenDmx;
import c4l.applet.main.Constants;
import c4l.applet.main.Util;
import c4l.applet.device.*;

/**
 * @author Andre for the output whit the ENTEC USB DMX plug.
 * 
 *         19.01.26 : Konfiguration der DLL und implementierung der Constants
 */

public class DmxOut {

	private Logger Log = Logger.getLogger(DmxOut.class);
	

	public DmxOut() {
		if (!(Util.getTestRun())) {
			if (!OpenDmx.connect(OpenDmx.OPENDMX_TX)) {
				Log.error("Open Dmx widget not detected!");
				return;
			}
		}else {
			Log.warn("TEST RUN Output !!!");
		}

	}

	public void Close() {
		OpenDmx.disconnect();
	}

	/**
	 * Send value to ENTEC Dongel
	 * 
	 * @param Channel
	 * @param Value
	 * @return
	 */
	public void setValue(int Channel, int Value) {
		if (Channel < Constants.MINCHANNEL)
			Channel = 0;
		if (Channel > Constants.MAXCHANNEL)
			Channel = 511;
		if (Value < Constants.MINVALUE)
			Value = 0;
		if (Value > Constants.MAXVALUE)
			Value = 255;
		try {
			OpenDmx.setValue(Channel, Value);
			//Log.debug("channel: " + Channel + " Value: " + Value);
		} catch (Exception e) {
			Log.error("Fail to commit the channel :" + Channel + " for the Value : " + Value, e);
		}

	}

	/**
	 * Send the Output values From the Devices Objects to The entec Dongel
	 * 
	 * @param devices
	 */
	public void setOutput(Device[] devices, Static_Device staticDevice) {
//		Log.debug(devices.toString());
		for (Device device : devices) {
			int[] Output = device.generateOutput_unticked(); // TODO change in the Future
			//Log.debug(Output[0] + " " + Output[1] + " " + Output[2]);
			try {
				int addres = device.getStartAddres();
				for (int i = 0; i < Constants.DEVICE_CHANNELS; i++) {
					// for Debug runs Without Entec dongel
					if (!(Util.getTestRun())) {
						// no test Run
						setValue(addres + i, Output[i]);
					//	System.out.println(addres + i +" : " + Output[i]);
					} else {
						// test Run
						Log.info("addresse :" + addres + i + " " + Output[i]);
					}
				}
			} catch (Exception e) {
				Log.error("Fail to interpret the Output of all devices, faild by output : " + Output, e);
			}
		}
		
		//Output for static channels
		int[] Output = staticDevice.getOutput();
		int addres = staticDevice.getStartAddress();
		try {
			for (int i = 0; i < Constants.STATIC_CHANNELS; i++) {
				// for Debug runs Without Entec dongel
				if (!(Util.getTestRun())) {
					// no test Run
					setValue(addres + i, Output[i]);
					//System.out.println(addres + i +" : " + Output[i]);
				} else {
					// test Run
					Log.info("addresse :" + addres + i + " " + Output[i]);
				}
			}
		} catch (Exception e) {
			Log.error("Failed to interpret the Output for static device, faild by output : " + Output, e);
		}
	}

}
