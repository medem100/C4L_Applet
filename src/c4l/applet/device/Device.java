package c4l.applet.device;

import java.util.LinkedList;
import java.util.ListIterator;

import c4l.applet.main.Constants;

/**
 * 
 * Definition der einzelnen Geräte
 * AS: 19.01.26:  Einbau von Polymorphie für die Output untickt / Tickt funcktzion 
 */

public class Device {
	private int[] inputs;
	private int[] outputs;
	private int[] p_outputs;
	private int[] perm; //Input -> Output mapping (applied after effects)
	private int startAddress;
	
	public LinkedList<Effect> effects;
	
	//Constructors
	public Device(int[] permutation) {
		this.inputs = new int[Constants.DEVICE_CHANNELS];
		this.p_outputs = new int[Constants.DEVICE_CHANNELS];
		this.perm = permutation;
		
		this.effects = new LinkedList<Effect>();
	}
	// add Address
	public Device(int[] permutation, int startAddres ) {
		this.startAddress = startAddres;
		this.inputs = new int[Constants.DEVICE_CHANNELS];
		this.p_outputs = new int[Constants.DEVICE_CHANNELS];
		this.perm = permutation;
		
		this.effects = new LinkedList<Effect>();
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
	
	public void setStartAddres(Integer address) {
		this.startAddress = address;
	}
	
	public int getStartAddres() {
		return this.startAddress;
	}

	/*
	 * Manage Effects. This does not offer anything you need, therefore effects is public,
	 * to allow you to work on it. This may change in a future version, so you're encouraged
	 * to add other getter/setter-functions to provide better access to the list instead off
	 * taking direct access.
	 */
	public void addEffect(Effect e) {
		effects.add(e);
	}
	public void addEffect(Effect e, int index) {
		effects.add(index, e);
	}
	public void deleteEffect(int index) {
		effects.remove(index);
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
	 * 
	 * Compute output-values of the device . This includes Effects and Output-Patch.
	 * 
	 * @param tick_effects 	whether Effects should be ticked
	 * @return 			Array of integers with output values
	 */
	private int[] getOutput(Boolean tick_effects) {
		outputs = inputs;
		//apply Effects
		Effect e;
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
