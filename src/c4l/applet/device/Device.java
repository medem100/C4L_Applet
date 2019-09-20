package c4l.applet.device;

import java.util.LinkedList;
import java.util.ListIterator;

import c4l.applet.db.DB;
import c4l.applet.main.Constants;

/**
 * Definition of a single Device, consisting of Input values, a list of Effects and an Output permutation
 * 
 * @author Timon
 * @author Andre
 */

public class Device {
	private int[] inputs;
	private int[] outputs;
	private int[] p_outputs;
	private int[] perm; //Input -> Output mapping (applied after effects)
	private int[] rotary_channels; //which channels shall be affected by the rotary encoders
	private int startAddress;
	
	public LinkedList<Effect> effects;
	public LinkedList<Effect> main_effect;
	
	//Constructors
	public Device(int[] permutation, int startAddress) { //for backwards compatibility with MAin and TestObjeckte
		this(permutation, Constants.STANDART_ROTARY_CHANNELS, startAddress);
	}
	public Device(int[] permutation, int[] rotarys, int startAddress) {
		this.startAddress = startAddress;
		this.inputs = new int[Constants.DEVICE_CHANNELS];
		this.p_outputs = new int[Constants.DEVICE_CHANNELS];
		this.perm = permutation;
		this.rotary_channels = rotarys;
		
		this.effects = new LinkedList<Effect>();
		this.main_effect = new LinkedList<Effect>();
	}
	
	//Getters and Setters
	public int[] getInputs() {
		return inputs;
	}
	public void setInputs(int[] inputs) {
		this.inputs = inputs;
	}
	public void setInput(int index, int value) {
		this.inputs[index] = value;
	}
	public int[] getPerm() {
		return perm;
	}
	public void setPerm(int[] perm) {
		this.perm = perm;
	}
	public int[] getRotary_channels() {
		return rotary_channels;
	}
	public void setRotary_channels(int[] rotary_channels) {
		this.rotary_channels = rotary_channels;
	}
	public int getStartAddres() {
		return this.startAddress;
	}
	public void setStartAddress(Integer address) {
		this.startAddress = address;
	}
	
	public void applyRotary(int index, int value) {
		inputs[rotary_channels[index]] = Effect.cutOff(inputs[rotary_channels[index]] + value);
	}

	/*
	 * Manage Effects. This does not offer anything you need, therefore effects is public,
	 * to allow you to work on it. This may change in a future version, so you're encouraged
	 * to add other getter/setter-functions to provide better access to the list instead off
	 * taking direct access.
	 */
	/*
	 * There is one main_effect, which may be overwritten without warning or looking back by some clients.
	 * The list effects is supposed to be more resistant and to be cleared or modified only after a closer look.
	 */
	//functions for main_efect
	public void addMainEffect(Effect e) {
		main_effect.add(e);
	}
	public void addMainEffect(Effect e, int index) {
		main_effect.add(index, e);
	}
	public void deleteMainEffect(int index) {
		main_effect.remove(index);
	}
	public void clearMainEffect() {
		main_effect.clear();
	}
	public void setMainSpeed(int value) {
		for (ListIterator<Effect> it = main_effect.listIterator(); it.hasNext(); it.next().setSpeed(value));
	}
	public void setMainSize(int value) {
		for (ListIterator<Effect> it = main_effect.listIterator(); it.hasNext(); it.next().setSize(value));
	}
	
	
	//functions for effects
	public void addEffect(Effect e) {
		effects.add(e);
	}
	public void addEffect(Effect e, int index) {
		effects.add(index, e);
	}
	public void deleteEffect(int index) {
		effects.remove(index);
	}
	public void setSpeed(int value) {
		for (ListIterator<Effect> it = effects.listIterator(); it.hasNext(); it.next().setSpeed(value));
	}
	public void setSize(int value) {
		for (ListIterator<Effect> it = effects.listIterator(); it.hasNext(); it.next().setSize(value));
	}
	
	/**
	 * Compute output-values of the device. This includes Effects and Output-Patch.
	 * The Effects are ticked here. To avoid this use {@link #getOutput_unticked() getOutput_unticked}.
	 * @return Array of integers with output values
	 */
	public int[] getOutput() {
		return getOutput(true);
	}
	
	/**
	 * Compute output-values of the device. This includes Effects and Output-Patch.
	 * The Effects aren't ticked here. To do this use {@link #getOutput() getOutput}.
	 * @return Array of integers with output values
	 */
	public int[] getOutput_unticked() {
		return getOutput(false);
	}
	
	/**
	 * Tick effects
	 */
	public void tick() {
		for (ListIterator<Effect> it = effects.listIterator(); it.hasNext(); it.next().tick());
		for (ListIterator<Effect> it = main_effect.listIterator(); it.hasNext(); it.next().tick());
	}
	
	/**
	 * 
	 * Compute output-values of the device. This includes Effects and Output-Patch.
	 * 
	 * @param tick_effects 	whether Effects should be ticked
	 * @return 				Array of integers with output values
	 */
	private int[] getOutput(Boolean tick_effects) {
		outputs = inputs.clone();
		//apply Effects
		Effect e;
		//Main Effect
		for (ListIterator<Effect> it = main_effect.listIterator(); it.hasNext(); ) {
			e = it.next(); 
			if(tick_effects)
				e.tick();
			outputs = e.apply(outputs);
		}
		//Other effects
		for (ListIterator<Effect> it = effects.listIterator(); it.hasNext(); ) {
			e = it.next(); 
			if(tick_effects)
				e.tick();
			outputs = e.apply(outputs);
		}
		
		//Apply output-patch
		for (int i = 0; i < Constants.DEVICE_CHANNELS; i++) {
			p_outputs[i] = outputs[perm[i]];
		}
		
		return p_outputs;
		
	}
}
