package c4l.applet.input;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.io.*;

import org.apache.log4j.Logger;
import org.json.*;

import c4l.applet.db.Select;
import c4l.applet.main.Constants;
import c4l.applet.main.PropertyManager;
import sun.util.logging.resources.logging;

/**
 * Get Data from The C4L Server
 * <p>
 * https://stackoverflow.com/questions/57629401/deserializing-json-using-java-11-httpclient-and-custom-bodyhandler-with-jackson
 */
public class DashboardInput {

    public static int[] defaultChannels = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private boolean[] devices = new boolean[Constants.DYNAMIC_DEVICES];
    private int[] channels = new int[Constants.DEVICE_CHANNELS];

    public int[] faders = new int[Constants.DEVICE_CHANNELS];
    private int effectSize;
    private int effectSpeed;
    private String effect; // 99 = kein Effect
    private int caseID;
    public JSONObject usedRespons = new JSONObject();
    ArrayList<Integer> scenenID = new ArrayList<>();
    private static boolean savePresst;
    private static boolean crateNewScenePresst;

    private Logger Log = Logger.getLogger(DashboardInput.class);
    private PropertyManager.Server prop;
    private int setupid = 1;

    // all addresse wherer the API must be reset

    private HashMap<String, ResetValue[]> resetValues = new HashMap<>();

    // Constructor
    public DashboardInput() throws Exception {
        prop = PropertyManager.getInstance().SERVER;
        resetValues.put(prop.SAVEPATH, new ResetValue[] { new ResetValue("save", "false") });
        resetValues.put(prop.EFFECTPATH, new ResetValue[] { new ResetValue("effect", "99") });
        resetValues.put(prop.CREATENEWSCENE, new ResetValue[] { new ResetValue("save", "false") });
        resetValues.put(prop.STARTSCENE, new ResetValue[] { new ResetValue("scene", "0") });
        resetValues.put(prop.STARTCHASE, new ResetValue[] { new ResetValue("chase", "0") });
        resetValues.put(prop.STEPCHASE, new ResetValue[] { new ResetValue("value", "false") });
        resetValues.put(prop.DELETEMAINEFFECT,
                new ResetValue[] { new ResetValue("value", "false") });

    }

    public int[] getFaders() {
        return faders;
    }

    // Getter

    public int getsetupID() {
        return setupid;
    }

    public boolean isSavePresst() {
        // setSaveRead();
        return savePresst;
    }

    public boolean isCrateNewScenePresst() {
        return crateNewScenePresst;
    }

    public boolean[] getDevices() {
        return devices;
    }

    public int[] getFader() {
        return faders;
    }

    public int getEffectSize() {
        setEffectRead(); // TODO thats Ugly
        return effectSize;
    }

    public int getEffectSpeed() {
        return effectSpeed;
    }

    public String getEffectID() {
        setEffectRead();
        return effect;
    }

    public int getCaseID() {
        return caseID;
    }

    public int[] getSelectChannels() {
        return channels;
    }

    public ArrayList<Integer> getScenenID() {
        return scenenID;
    }

    public int getFader(int index) {
        if ((index < 0) || (index > Constants.DEVICE_CHANNELS - 1)) {
            throw new IndexOutOfBoundsException("You can only get a Fader for index 0 to 15");
        }
        return faders[index];
    }

    public int getDevice(int index) {
        if ((index < 0) || (index > Constants.DYNAMIC_DEVICES - 1)) {
            throw new IndexOutOfBoundsException("You can only get a device for index 0 to 29");
        }
        return faders[index];
    }

    // public int setCurrentScene()

    /**
     * only single value
     *
     * @param param
     * @return booelan value of the key in the respons
     * @throws JSONException
     */
    public boolean getBooleanValue(String param) throws JSONException {
        return usedRespons.getBoolean(param);
    }

    /**
     * only use for single values
     *
     * @param param name
     * @return String value of the parameter
     * @throws JSONException
     */
    public String getStringValue(String param) throws JSONException {
        return usedRespons.getString(param);
    }

    /**
     * only use for single values
     *
     * @param param name
     * @return Int Value of the paramter
     * @throws JSONException
     */
    public int getIntValue(String param) throws JSONException {
        return usedRespons.getInt(param);
    }

    /**
     * Return All chosen Values in the Dashboard
     *
     * @return all chosen Devices
     */
    public boolean[] getChosenDevices() {
        return devices;
    }

    /**
     * Set new Values to the dasboard
     */
    public void tick() {
        usedRespons = getResponse().getJSONObject("values");
        JSONArray jsonFader = usedRespons.getJSONArray("channels");
        JSONArray jsonDevices = usedRespons.getJSONArray("devices");
        JSONArray jsonScenenID = usedRespons.getJSONArray("sceneIds");

        JSONArray jsonChannels = new JSONArray();
        for (int i = 0; i < jsonFader.length(); i++) {
            jsonChannels.put(jsonFader.getJSONObject(i).getBoolean("isSelect"));
        }

        // JSONArray jsonChannels = usedRespons.getJSONArray("selectChannels");

        effect = usedRespons.getString("effectId");
        effectSpeed = usedRespons.getInt("effectSpeed");
        effectSize = usedRespons.getInt("effectSize");
        caseID = usedRespons.getInt("caseId");
        savePresst = usedRespons.getBoolean("savePressed");
        crateNewScenePresst = usedRespons.getBoolean("createNewScenePressed");

        // Log.debug(usedRespons.toString());

        for (int i = 0; i < jsonFader.length(); i++) {
            faders[i] = jsonFader.getJSONObject(i).getInt("lum");
        }

        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int i = 0; i < jsonScenenID.length(); i++) {
            temp.add(jsonScenenID.getInt(i));
        }

        scenenID = temp;

        for (int i = 0; i < jsonChannels.length(); i++) {
            channels[i] = jsonChannels.getBoolean(i) ? 1 : 0;
        }

        // a device what wasn´t used is null
        for (int i = 0; i < jsonDevices.length(); i++) {
            devices[i] = jsonDevices.getJSONObject(i).getBoolean("active");
/*
            String value = jsonDevices.get(i).toString();
            switch (value) {
                case "null":
                case "false":
                    devices[i] = false;
                    break;
                case "true":
                    devices[i] = true;
                    break;
                default:
                    Log.error("Allowed device values only null/true/false");
                    throw new NullPointerException("Allowed device values only null/true/false");
               }

 */

        }

    }

    // Help Functions

    private String readStringFromUrl(String url) {
        URL readURL;
        String response = "";
        try {
            readURL = new URL(url);

            BufferedReader in = new BufferedReader(new InputStreamReader(readURL.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response = response + inputLine;
            }
            in.close();
            // Log.debug(response);
            return response;
        } catch (Exception e) {
            Log.error("fail to Read Respons ", e);
            return "{}";
        }
    }

    public JSONObject getResponse() {
        String ResponsString;
        String URL = prop.ADDRESS + prop.INFORMATIONPATH;
        // Log.debug("ServerURL :" + URL);
        ResponsString = readStringFromUrl(URL);
        return new JSONObject(ResponsString);
    }

    @Deprecated public void setEffectRead() {
        try {
          /*  URL url = new URL(prop.ADDRESS + prop.EFFECTPATH);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect(); */
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request =
                    HttpRequest.newBuilder().uri(URI.create(prop.ADDRESS + prop.EFFECTPATH))
                            .method("PATCH", HttpRequest.BodyPublishers.ofString("{\"id\":\"99\"}"))
                            .header("Content-Type", "application/json").build();

            HttpResponse<String> respons =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (respons.statusCode() != 200) {
                Log.error("Effect can´t reset");
                throw new RuntimeException();
            }

        } catch (IOException | InterruptedException e) {
            Log.error("Fail to check Server avalibale -> wrong path", e);
        }
    }

    @Deprecated public void setSaveRead() {
        try {
            URL url = new URL(prop.ADDRESS + prop.SAVEPATH + "?save=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != 200) {
                Log.error("save can´t reset");
                ;
            }

        } catch (IOException e) {
            Log.error("Fail to check Server avalibale -> wrong path", e);
        }
    }

    @Deprecated public void setCreateNewSceneRead() {
        try {
            URL url = new URL(prop.ADDRESS + "/" + prop.WEB_APP + "/rest/set/setCrateNewScene"
                    + "?save=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != 200) {
                Log.error("CrateNewScene can´t reset");

            }

        } catch (IOException e) {
            Log.error("Fail to check Server avalibale -> wrong path", e);
        }
    }

    /**
     * Reset All Fields at the API
     */
    public void resetValues() {
        try {
            for (Entry<String, ResetValue[]> e : resetValues.entrySet()) {
                String values = "?";
                for (ResetValue rv : e.getValue()) {
                    values += rv.getPARAMETER() + "=" + rv.getVALUE() + "&";
                }
                values = values.substring(0, values.length() - 1); // cut of the last &
                URL url = new URL(prop.ADDRESS + e.getKey() + values);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (300 > connection.getResponseCode() && connection.getResponseCode() > 199) {
                    Log.error(prop.ADDRESS + " can´t reset");

                }
            }
        } catch (IOException e) {
            Log.error("Fail to check Server avalibale -> wrong path", e);
        }
    }

}
