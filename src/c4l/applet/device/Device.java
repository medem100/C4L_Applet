package c4l.applet.device;

import java.util.LinkedList;
import java.util.ListIterator;

import c4l.applet.main.Constants;
import c4l.applet.scenes.Device_Setup;

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
		
	private Device_Setup my_setup;
	
	public LinkedList<Effect> effects;
	public LinkedList<Effect> main_effect;
	
	//Constructors
	/**
	 * Main constructor to construct a new Device from scratch
	 * 
	 * @param v_dim_channel		Dimmer-Channel (if you don't use the virtual dimmer, you can give any int here)
	 * @param virtual_dimmer	Linked List of channels to be dimmed virtually (may be empty if not needed)
	 * @param setup				Device_Setup-reference for this device
	 */
	public Device(int v_dim_channel, LinkedList<Integer> virtual_dimmer, Device_Setup setup) {
		this.inputs = new int[Constants.DEVICE_CHANNELS];
		this.p_outputs = new int[Constants.DEVICE_CHANNELS];
		
		this.effects = new LinkedList<Effect>();
		this.main_effect = new LinkedList<Effect>();
		
		this.my_setup = setup;
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
	public int[] getMainEffetChannels() {
		return my_setup.getMain_effect_channels();
	}
	
	public void applyRotary(int index, int value) {
		inputs[my_setup.getRotary_channels()[index]] = Effect.cutOff(inputs[my_setup.getRotary_channels()[index]] + value);
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
	 * The Effects are ticked here. To avoid this use {@link #generateOutput_unticked() getOutput_unticked}.
	 * @return Array of integers with output values
	 */
	public int[] generateOutput() {
		return generateOutput(true);
	}
	
	/**
	 * Compute output-values of the device. This includes Effects and Output-Patch.
	 * The Effects aren't ticked here. To do this use {@link #generateOutput() getOutput}.
	 * @return Array of integers with output values
	 */
	public int[] generateOutput_unticked() {
		return generateOutput(false);
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
	public int[] generateOutput(Boolean tick_effects) {
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
		
		//Virtual dimmer
		for (ListIterator<Integer> it = my_setup.getVirtual_dimming().listIterator(); it.hasNext(); ) {
			int helper = it.next();
			p_outputs[helper] = (p_outputs[helper]*outputs[my_setup.getVirtual_dimmer_channel()])/Constants.MAXVALUE;
		}
		return p_outputs;
	}
}