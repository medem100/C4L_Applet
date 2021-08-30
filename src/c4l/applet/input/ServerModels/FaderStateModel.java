package c4l.applet.input.ServerModels;

import com.google.gson.annotations.SerializedName;

public class FaderStateModel {
    private int lum;
    private String channel;
    @SerializedName(value = "device")
    private String name;
    private boolean isSelect;
    private int value;

    public int getLum() {
        return lum;
    }

    public String getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public int getValue() {
        return value;
    }
}
