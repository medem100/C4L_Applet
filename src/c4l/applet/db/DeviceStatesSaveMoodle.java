package c4l.applet.db;

import c4l.applet.device.Device;
import com.google.gson.annotations.SerializedName;

public class DeviceStatesSaveMoodle {
    @SerializedName(value = "devices")
    private final DeviceStateSaveMoodle[] deviceStateSaveMoodles;

    public DeviceStatesSaveMoodle(Device[] devices) {
        deviceStateSaveMoodles = new DeviceStateSaveMoodle[devices.length];
        for(int i = 0; i < deviceStateSaveMoodles.length ; i++){
            deviceStateSaveMoodles[i] = new DeviceStateSaveMoodle(devices[i]);
        }

    }

    public Device[] getDevices() {
        Device[] res = new Device[deviceStateSaveMoodles.length];
        for (int i = 0; i < res.length; i++){
            res[i] = deviceStateSaveMoodles[i].toDevice();
        }
        return res;
    }

}
