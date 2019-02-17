package c4l.applet.device;

import c4l.applet.device.Effect_Simple;
import c4l.applet.device.Effect_Simple.Effecttype_det;
import c4l.applet.device.Effect_Random;
import c4l.applet.device.Effect_Random.Effecttype_Random;

public class Effect_ID {
	public int id1;
	public int id2;
	
	public Effect_ID(int id1, int id2) {
		this.id1 = id1;
		this.id2 = id2;
	}
	
	
	public enum Effect_class {
		SIMPLE, RANDOM
	}
	public static Effect_ID getEffectID(Effect e) {
		if (e instanceof Effect_Simple) {
			Effect_Simple es = (Effect_Simple) e;
			return new Effect_ID(0, es.getType().ordinal());
		}
		if (e instanceof Effect_Random) {
			Effect_Random es = (Effect_Random) e;
			return new Effect_ID(1, es.getType().ordinal());
		}
		return new Effect_ID(-1, -1); //Invalid input
	}
	
	public static Effect generateEffectFromID(Effect_ID id, int size, int speed, int offset, int[] channels) {
		return generateEffectFromID(id, size, speed, offset, true, channels);
	}
	public static Effect generateEffectFromID(Effect_ID id, int size, int speed, int offset, boolean acceptInput, int[] channels) {
		switch (id.id1) {
		case -1:
			//INVALID ID
			return null;
		case 0: //Effect det
			return (Effect) new Effect_Simple(size, speed, offset, acceptInput, Effecttype_det.values()[id.id2], channels);
		case 1: //Effect Random
			return (Effect) new Effect_Random(size, speed, offset, acceptInput, Effecttype_Random.values()[id.id2], channels);
		default:
			//INVALID ID
			return null;
		}
	}
}