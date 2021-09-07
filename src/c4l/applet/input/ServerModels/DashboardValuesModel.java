package c4l.applet.input.ServerModels;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class DashboardValuesModel {
    @SerializedName(value = "channels")
    FaderStateModel[] faderValues;
    DeviceButtonStateModel[] devices;
    int effectSize;
    int effectSpeed;
    Integer[] sceneIds;
    String effectId;
    int caseId;
    boolean savePressed;
    int createNewScene;
    boolean deleteMainEffect;
    int defaultFadeTime;
    int setupId;

    public int getSetupId() {
        return setupId;
    }

    public FaderStateModel[] getFaderValues() {
        return faderValues;
    }

    public DeviceButtonStateModel[] getDevices() {
        return devices;
    }

    public int getEffectSize() {
        return effectSize;
    }

    public int getEffectSpeed() {
        return effectSpeed;
    }

    public List<Integer> getSceneIds() {
        return Arrays.asList( sceneIds );
    }

    public boolean isSavePressed() {
        return savePressed;
    }

    public int isCreateNewScenePressed() {
        return createNewScene;
    }

    public boolean isDeleteMainEffect() {
        return deleteMainEffect;
    }

    public int getDefaultFadeTime() {
        return defaultFadeTime;
    }

    public String getEffectId() {
        return effectId;
    }

    public int getCaseId() {
        return caseId;
    }
}
