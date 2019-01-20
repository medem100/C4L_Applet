package c4l.applet.output;

import com.juanjo.openDmx.OpenDmx;
import c4l.applet.main.Constans;


/*
 * klasse für die Ausgabe mit Entec OpenDMX
 * 
 */

public class DmxOut {
	
	DmxOut() {
		if(!OpenDmx.connect(OpenDmx.OPENDMX_TX)){
			System.out.println("Open Dmx widget not detected!");
			return;
		}
	}
	
	public static void main(String[] args) {
		DmxOut DMXobjeckt = new DmxOut();
		DMXobjeckt.setValue(1, 200);
		DMXobjeckt.Close();
	
	}
	
	
	public void Close() {
		OpenDmx.disconnect();
	}

	public String setValue(int Channel , int Value) {
		if(Channel<Constans.MINCHANNEL)Channel=1;
		if(Channel>Constans.MAXCHANNEL)Channel=512;
		if(Value<Constans.MINVALUE)Value=0;
		if(Value>Constans.MAXVALUE)Value=255;
		try {
			OpenDmx.setValue(Channel-1,Value);
		}catch (Exception e) {
			return e.toString();
		}
		return "OK";
		
	}
	
}
