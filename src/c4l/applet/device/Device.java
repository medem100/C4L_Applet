package c4l.applet.device;

import c4l.applet.main.Constants;

/*
 * Definition der einzelnen Geräte 
 */

public class Device {
	private int[] inputs;
	private int[] outputs;
	private int[] perm; //Input -> Output mapping (applied after effects)
	
	//Constructors
	public Device(int[] permutation) {
		this.inputs = new int[Constants.DEVICE_CHANNELS];
		this.outputs = new int[Constants.DEVICE_CHANNELS];
		this.perm = permutation;
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

	public int[] getOutputs() {
		return outputs;
	}
	public int[] getRealOutputs() {
		//QUESTION is there a prettier or even more efficient way to do this?
		int[] out = new int [Constants.DEVICE_CHANNELS];
		for (int i = 0; i < Constants.DEVICE_CHANNELS; i++) {
			out[i] = outputs[perm[i]];
		}
		return out;
	}
	public void setOutputs(int[] outputs) {
		this.outputs = outputs;
	}
	
	
	//Other functions
	public void resetTick() {
		outputs = inputs;
	}
}
