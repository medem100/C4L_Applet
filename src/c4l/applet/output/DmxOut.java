package c4l.applet.output;

import com.juanjo.openDmx.OpenDmx;
import c4l.applet.main.Constants;


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
		DMXobjeckt.setValue(1, 200);
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
		if(Channel<Constants.MINCHANNEL)Channel=1;
		if(Channel>Constants.MAXCHANNEL)Channel=512;
		if(Value<Constants.MINVALUE)Value=0;
		if(Value>Constants.MAXVALUE)Value=255;
		try {
			OpenDmx.setValue(Channel-1,Value);
		}catch (Exception e) {
			return e.toString();
		}
		return "OK";
		
	}
	
}
