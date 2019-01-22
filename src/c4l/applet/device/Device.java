package c4l.applet.device;

import java.util.LinkedList;
import java.util.ListIterator;

import c4l.applet.main.Constants;

/*
 * Definition der einzelnen Geräte 
 */

public class Device {
	private int[] inputs;
	private int[] outputs;
	private int[] p_outputs;
	private int[] perm; //Input -> Output mapping (applied after effects)
	
	public LinkedList<Effect> effects;
	
	//Constructors
	public Device(int[] permutation) {
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
	
	//Other functions
	public int[] getOutput() {
		outputs = inputs;
		//apply Effects
		Effect e;
		for (ListIterator<Effect> it = effects.listIterator(); it.hasNext(); ) {
			e = it.next(); 
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
