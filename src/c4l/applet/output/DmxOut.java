package c4l.applet.output;

import com.juanjo.openDmx.OpenDmx;
import c4l.applet.main.Constants;
import c4l.applet.device.*;


/**
 * @author Andre
 * klasse für die Ausgabe mit Entec OpenDMX
 * 
 * 19.01.26 : Konfiguration der DLL und implementierung der Constants
 */

public class DmxOut {
	
	public DmxOut() {
		if(!OpenDmx.connect(OpenDmx.OPENDMX_TX)){
			System.out.println("Open Dmx widget not detected!");
			return;
		}
	}
	
	public static void main(String[] args) {
		DmxOut DMXobjeckt = new DmxOut();
		DMXobjeckt.setValue(0, 200);
		try {
			Thread.sleep(1000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DMXobjeckt.Close();
	
	}
	
	
	public void Close() {
		OpenDmx.disconnect();
	}

	public String setValue(int Channel , int Value) {
		if(Channel<Constants.MINCHANNEL)Channel=0;
		if(Channel>Constants.MAXCHANNEL)Channel=511;
		if(Value<Constants.MINVALUE)Value=0;
		if(Value>Constants.MAXVALUE)Value=255;
		try {
			OpenDmx.setValue(Channel,Value); // evtl -1 kommt drauf an wie intern gezählt wird
		}catch (Exception e) {
			return e.toString();
		}
		return "OK";
		
	}
	
	/**
	 * Send the Output valus From the Devices Objekts to The entec Dongel 
	 * TO DO : Apfangen wen geräte keine start adresse haben 
	 * @param devices
	 */
	public void setOutput(Device[] devices) {
		for (Device device : devices) {
			int[] Output = device.getOutput_unticked();
			int addres = device.getStartAddres();
			for (int i = 0 ; i < Constants.DEVICE_CHANNELS;i++) {
				setValue(addres+i, Output[i]);
			}
		}
	}
	
}
