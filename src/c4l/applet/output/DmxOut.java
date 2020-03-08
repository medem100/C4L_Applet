package c4l.applet.output;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.juanjo.openDmx.OpenDmx;
import c4l.applet.main.Constants;
import c4l.applet.main.Util;

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
			// TODO Ausbau nach Konfestivall 

		
			OpenDmx.setValue(Channel, Value);
		
			
			if (Channel >= 480 && Channel< 488) Log.debug("channel: " + Channel + " Value: " + Value);
		} catch (Exception e) {
			Log.error("Fail to commit the channel :" + Channel + " for the Value : " + Value, e);
		}

	}

	/**of a full universe to The entec Dongel
	 * 
	 * @param out 512-int-array of DMX-values.
	 */
	public void setOutput(int[] out) {	
		try {
			for (int i = 0; i < Constants.OUTPUT_LENGTH; i++) {
				// for Debug runs Without Entec dongel
				if (!(Util.getTestRun())) {
					// no test Run
					setValue(i, out[i]);
				} else {
					// test Run
					Log.info("addresse :" + i + " " + out[i]);
				}
			}
		} catch (Exception e) {
			Log.error("Failed to output array: " + out, e); //TODO: Andre: Is there anything inside try{} that could throw an exception?
		}
	}

}
