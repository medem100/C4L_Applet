package c4l.applet.input;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface GuiInput {

    int getSetupID();

    boolean isSavePresst();

    boolean isCrateNewScenePresst();

    boolean[] getDevicesSelectionState();

    int getEffectSize();

    int getEffectSpeed();

    String getEffectID();

    int getCaseID();

    int[] getSelectChannels();

    List<Integer> getSceneIds();

    int getFaderValue(int index);

    boolean getBooleanValue(String param) throws JSONException;

    String getStringValue(String param) throws JSONException;

    int getIntValue(String param) throws JSONException;

    boolean[] getChosenDevices();

    int getDefaultFadeTime();

    boolean getDeleteMainEffect();

    void tick() throws IOException, InterruptedException;

    boolean hasChanged();
}
