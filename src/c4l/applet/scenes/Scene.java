package c4l.applet.scenes;

import c4l.applet.device.Device;
import c4l.applet.device.Static_Device;
import c4l.applet.main.Constants;

public class Scene {
	/** array of 30 devices making up most of the DMX-universe */
	Device[] devices;
	/** static-devices for bfaders and ... */
	Static_Device static_device;
	/** reference to Setup-object, main-owner is the State though */
	Setup setup;
	
	private int[] output;
	private int[] output_permutated;
	
	//Constructor
	public Scene(Setup setup, Device[] devices, Static_Device static_device) {
		this.setup = setup;
		this.devices = devices;
		this.static_device = static_device;
		
		output = new int[513]; //Hold a zero in output[512] to be able to map constant off in the permutation
		output_permutated = new int[512];
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
			int[] device_out = devices[i].generateOutput(false);
			System.arraycopy(device_out, 0, output, i*Constants.DEVICE_CHANNELS, Constants.DEVICE_CHANNELS);
		}
		int[] static_device_out = static_device.getOutput();
		System.arraycopy(static_device_out, 0, output, Constants.DYNAMIC_DEVICES*Constants.DEVICE_CHANNELS, Constants.STATIC_CHANNELS);
		
		for (int i = 0; i < Constants.OUTPUT_LENGTH; i++) {
			output_permutated[i] = output[setup.perm[i]];
		}
		
		return output_permutated;
	}
}
