package c4l.applet.db;

import java.util.Arrays;

import c4l.applet.device.Device;

public class Scene {
	
	private Device[] devices;

	public Scene(Device[] dev) {
		this.devices = dev;
	}
	
	public Device[] getDevices() {
		return devices;
	}

	@Override public String toString() {
		return "Scene{" + "dev=" + Arrays.toString(devices) + '}';
	}
}
