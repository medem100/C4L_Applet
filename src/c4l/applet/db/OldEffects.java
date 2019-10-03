package c4l.applet.db;

import c4l.applet.device.Device;
import c4l.applet.device.Effect;
import c4l.applet.device.Effect_Representative;

public class OldEffects {
	
private Effect_Representative[] dev;
	
	public OldEffects(Effect_Representative[] dev) {
		this.dev = dev;
	}
	
	public Effect_Representative[] getEffects() {
		return dev;
	}


}
