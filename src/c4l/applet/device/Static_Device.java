package c4l.applet.device;

import c4l.applet.main.Constants;

public class Static_Device {
	private int inputs[];
	private int outputs[];
	
	private float matrix[][];

	//Constructors
	/**
	 * @param matrix
	 */
	public Static_Device(float[][] matrix) {
		this.inputs = new int[Constants.STATIC_INPUT];
		this.outputs = new int[Constants.STATIC_CHANNELS];
		this.matrix = matrix;
	}

	//Getters and Setters
	public int[] getInputs() {
		return inputs;
	}
	public void setInputs(int[] inputs) {
		this.inputs = inputs;
	}

	public float[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(float[][] matrix) {
		//TODO: Test if dimensions are fine or just don't care?
		this.matrix = matrix;
	}
	public void setMatrixEntry(float value, int i, int j) {
		matrix[i][j] = value;
	}
	
	//Other functions
	public int[] getOutput() {
		for (int i = 0; i < Constants.STATIC_CHANNELS; i++) {
			outputs[i] = 0;
			for (int j = 0; j < Constants.STATIC_INPUT; j++) {
				outputs[i] += matrix[i][j]*inputs[j];
			}
			if (outputs[i] > Constants.MAXVALUE) outputs[i] = Constants.MAXVALUE;
		}
		
		return outputs;
	}
}
