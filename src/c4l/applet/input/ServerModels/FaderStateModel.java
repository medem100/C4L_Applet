package c4l.applet.input.ServerModels;

import com.google.gson.annotations.SerializedName;

public class FaderStateModel {
    private int lum;
    private String channel;
    @SerializedName(value = "device")
    private String name;
    private int selectState;
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

    public int getSelectState() {
        return selectState ;
    }

    public int getValue() {
        return value;
    }
}
