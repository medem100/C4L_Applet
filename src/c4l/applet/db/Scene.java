package c4l.applet.db;

import java.util.Arrays;

import c4l.applet.device.Device;

public class Scene {
	
	private Device[] dev;
	private String name;
	private int id;
	
	public Scene(Device[] dev) {
		this.dev = dev;
	}
	
	public Device[] getDevices() {
		return dev;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Scene [dev=" + Arrays.toString(dev) + ", name=" + name + ", id=" + id + "]";
	}
	
	
	

}
