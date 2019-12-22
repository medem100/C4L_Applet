package c4l.applet.scenes;

import c4l.applet.device.Device;
import c4l.applet.device.Static_Device;
import c4l.applet.main.Constants;

public class Scene {
	public Device[] devices;
	public Static_Device static_device;
	
	private int[] output;
	
	//Constructor
	public Scene(Device[] devices, Static_Device static_Device) {
		this.devices = devices;
		this.static_device = static_Device;
	}
	
	/** Tick everything in the scene */
	public void tick() {
		for (Device d : devices) d.tick();
	}
	
	/**
	 * Bring everything together and create a 512-byte-array, which could serve as a DMX-Output
	 * @return
	 */
	public int[] generateOutput() {
		for (int i = 0; i < Constants.DYNAMIC_DEVICES; i++) {
			int[] device_out = devices[i].getOutput(false);
			System.arraycopy(device_out, 0, output, i*Constants.DEVICE_CHANNELS, Constants.DEVICE_CHANNELS);
		}
		int[] static_device_out = static_device.getOutput();
		System.arraycopy(static_device_out, 0, output, Constants.DYNAMIC_DEVICES*Constants.DEVICE_CHANNELS, Constants.STATIC_CHANNELS);
		
		//TODO: add permutation calculation, either here or passing it from setup to devices
		return output;
	}
}
