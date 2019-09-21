package c4l.applet.device;

import c4l.applet.device.Effect_Simple.Effecttype_det;
import c4l.applet.main.Constants;

/**
 * Representing a Effect in a stable form to store in DB.
 * 
 * @author Timon
 */

public class Effect_Representative {
	private int size;
	private int speed;
	private int offset;
	private boolean acceptInput;
	private int[] channels;
	
	private int state;
	private int last_state;
	
	private Effect_ID id;
	
	public Effect_Representative(Effect e) {
		this.size = e.getSize();
		this.speed = e.getSpeed();
		this.offset = e.getOffset();
		this.acceptInput = e.isAcceptInput();
		this.channels = e.getChannels();
		
		this.state = e.getState();
		this.last_state = e.getLast_state();
		
		this.id = Effect_ID.getEffectID(e);
	}
	
	public Effect generateEffect() {
		Effect e = Effect_ID.generateEffectFromID(id, size, speed, offset, acceptInput, channels);
		e.setState(state);
		e.setLast_state(last_state);
		return e;
	}
}
