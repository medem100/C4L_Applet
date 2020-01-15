package c4l.applet.scenes;

import java.util.LinkedList;

import c4l.applet.main.Constants;

public class Device_Setup {
	private int[] main_effect_channels;
	private int[] rotary_channels;
	
	private int virtual_dimmer_channel; //Channel for the virtual dimmer (pre permutation)
	private LinkedList<Integer> virtual_dimming; //channels to be affected by a virtual dimmer (post permutation)

	/**
	 * @param main_effect_channels
	 * @param rotary_channels
	 * @param virtual_dimmer_channel
	 * @param virtual_dimming
	 */
	public Device_Setup(int[] main_effect_channels, int[] rotary_channels, int virtual_dimmer_channel, LinkedList<Integer> virtual_dimming) {
		this.main_effect_channels = main_effect_channels;
		this.rotary_channels = rotary_channels;
		this.virtual_dimmer_channel = virtual_dimmer_channel;
		this.virtual_dimming = virtual_dimming;
	}
	public Device_Setup(int[] main_effect_channels, int[] rotary_channels) {
		this(main_effect_channels, rotary_channels, 3, new LinkedList<Integer>());
	}
	public Device_Setup() {
		this(Constants.STANDART_ROTARY_CHANNELS,Constants.STANDART_ROTARY_CHANNELS);
	}
	
	//Getters and Setters
	public int[] getMain_effect_channels() {
		return main_effect_channels;
	}
	public void setMain_effect_channels(int[] main_effect_channels) {
		this.main_effect_channels = main_effect_channels;
	}
	public int[] getRotary_channels() {
		return rotary_channels;
	}
	public void setRotary_channels(int[] rotary_channels) {
		this.rotary_channels = rotary_channels;
	}
	public int getVirtual_dimmer_channel() {
		return virtual_dimmer_channel;
	}
	public void setVirtual_dimmer_channel(int virtual_dimmer_channel) {
		this.virtual_dimmer_channel = virtual_dimmer_channel;
	}
	public LinkedList<Integer> getVirtual_dimming() {
		return virtual_dimming;
	}
	public void setVirtual_dimming(LinkedList<Integer> virtual_dimming) {
		this.virtual_dimming = virtual_dimming;
	}
}
