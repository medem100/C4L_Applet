package c4l.applet.input;

import c4l.applet.db.DBConstants;
import c4l.applet.db.DeviceStatesSaveModel;
import c4l.applet.input.ServerModels.DashboardValuesModel;
import c4l.applet.main.Constants;
import c4l.applet.main.PropertyManager;
import com.google.gson.Gson;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NewServerInput implements GuiInput {

    private DashboardValuesModel allValues;
    private PropertyManager.Server PROPS = PropertyManager.getInstance().SERVER;
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    private int lastResponseCode;

    public NewServerInput() throws Exception {
    }

    public int[] getFaders() {
        int[] faderValues = new int[allValues.getFaderValues().length];
        for (int i = 0; i < faderValues.length; i++) {
            faderValues[i] = allValues.getFaderValues()[i].getLum();
        }

        return faderValues;
    }

    @Override public int getSetupID() {
        return allValues.getSetupId();
    }

    @Override public boolean isSavePresst() {
        return allValues.isSavePressed();
    }

    @Override public int CrateNewSceneWithIdPresst() {
        return allValues.isCreateNewScenePressed();
    }

    @Override public boolean[] getDevicesSelectionState() {
        boolean[] deviceSelectionStates = new boolean[allValues.getDevices().length];
        for (int i = 0; i < deviceSelectionStates.length; i++) {
            deviceSelectionStates[i] = allValues.getDevices()[i].isActive();
        }
        return deviceSelectionStates;
    }

    @Override public int getEffectSize() {
        return allValues.getEffectSize();
    }

    @Override public int getEffectSpeed() {
        return allValues.getEffectSpeed();
    }

    @Override public String getEffectID() {
        return allValues.getEffectId();
    }

    @Override public int getCaseID() {
        return allValues.getCaseId();
    }

    @Override public int[] getSelectChannels() {
        int[] faderSelectValues = new int[Constants.DEVICE_CHANNELS];
        for (int i = 0; i < allValues.getFaderValues().length; i++) {
            faderSelectValues[i] = allValues.getFaderValues()[i].isSelect() ? 1 : 0;
        }
        return faderSelectValues;
    }

    @Override public List<Integer> getSceneIds() {
        return allValues.getSceneIds();
    }

    @Override public int getFaderValue(int index) {
        return allValues.getFaderValues()[index].getLum();
    }

    @Override public boolean getBooleanValue(String param) throws JSONException {
        throw new UnsupportedOperationException("get Boolean");
    }

    @Override public String getStringValue(String param) throws JSONException {
        throw new UnsupportedOperationException("get Boolean");
    }

    @Override public int getIntValue(String param) throws JSONException {
        throw new UnsupportedOperationException("get Boolean");
    }

    @Override public boolean[] getChosenDevices() {
        boolean[] chosenDevices = new boolean[allValues.getDevices().length];
        for (int i = 0; i < chosenDevices.length; i++) {
            chosenDevices[i] = allValues.getDevices()[i].isActive();
        }
        return chosenDevices;
    }

    @Override public int getDefaultFadeTime() {
        return allValues.getDefaultFadeTime();
    }

    @Override public boolean getDeleteMainEffect() {
        return allValues.isDeleteMainEffect();
    }

    @Override public void tick() throws IOException, InterruptedException {

        HttpRequest httpRequest =
                HttpRequest.newBuilder().uri(URI.create(PROPS.ADDRESS + PROPS.INFORMATIONPATH))
                        .GET().build();
        HttpResponse<String> response =
                CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        allValues = GSON.fromJson(response.body(), DashboardValuesModel.class);
        lastResponseCode = response.statusCode();

    }

    @Override public boolean hasChanged() {
        return lastResponseCode != 304;
    }
}
