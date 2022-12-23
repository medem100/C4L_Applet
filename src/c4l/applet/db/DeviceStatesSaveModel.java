package c4l.applet.db;

import c4l.applet.device.Device;
import com.google.gson.annotations.SerializedName;

public class DeviceStatesSaveModel {
    @SerializedName(value = "devices")
    private final DeviceStateSaveModel[] deviceStateSaveMoodles;

    public DeviceStatesSaveModel(Device[] devices) {
        deviceStateSaveMoodles = new DeviceStateSaveModel[devices.length];
        for(int i = 0; i < deviceStateSaveMoodles.length ; i++){
            deviceStateSaveMoodles[i] = new DeviceStateSaveModel(devices[i]);
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
