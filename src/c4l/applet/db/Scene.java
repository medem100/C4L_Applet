package c4l.applet.db;

import c4l.applet.device.Device;

public class Scene {
	
	private Device[] dev;
	
	public Scene(Device[] dev) {
		this.dev = dev;
	}
	
	public Device[] getDevices() {
		return dev;
	}

}
