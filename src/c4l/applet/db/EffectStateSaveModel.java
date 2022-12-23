package c4l.applet.db;

import c4l.applet.device.Effect_ID;

public class EffectStateSaveModel {
    private Effect_ID effectId;
    private int size;
    private int speed;
    private int offset;
    private boolean acceptInput;
    private int[] channels;

    public EffectStateSaveModel(Effect_ID effectId, int size, int speed, int offset,
            boolean acceptInput, int[] channels) {
        this.effectId = effectId;
        this.size = size;
        this.speed = speed;
        this.offset = offset;
        this.acceptInput = acceptInput;
        this.channels = channels;
    }

    public Effect_ID getEffectId() {
        return effectId;
    }

    public int getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isAcceptInput() {
        return acceptInput;
    }

    public int[] getChannels() {
        return channels;
    }
}
