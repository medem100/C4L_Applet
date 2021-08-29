/*
 * klasse zur abfrage der DB alle select funcktzionen
 *
 * AS: erstellt die klasse und baut eine Verknüpfung mit dbCreate und kopiert die select
 * funcktzionen aus database.class
 *
 *  14.05.18 11:50 Anpassend der KLasse für die Holder Klasse
 *
 */

package c4l.applet.db;

//import constant.constant;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import c4l.applet.device.Device;
import c4l.applet.device.Effect;
import c4l.applet.device.Effect_ID;
import c4l.applet.main.Constants;
import c4l.applet.scenes.Device_Setup;

import java.net.URI;

public class Select {

    private Create dbCreate = new Create();
    private Connection conn = null;
    // public static String[] felderSDschueler = { "ID", "Name", "Vorname",
    // "Klasse", "Telephone" };
    // private Logger dLogger = dbCreate.initLogger();
    static Logger logger = Logger.getLogger(Select.class);

    private HttpClient CLIENT;
    private Gson GSON;


    public Select(){};

    public Select(HttpClient CLIENT, Gson GSON) {
        this.CLIENT = CLIENT;
        this.GSON = GSON;
        try {
            this.conn = dbCreate.getInstance();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Device[] devicesFromScene(int id) throws IOException, InterruptedException {

		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(
				String.format(DBConstants.DB_SERVER_BASE +"/"+ DBConstants.SCENES + "/%s" + "/" + DBConstants.DEVICES ,
                        id))).GET().build();
        HttpResponse<String> response =
                CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        DeviceStatesSaveMoodle
                deviceStates = GSON.fromJson(response.body(), DeviceStatesSaveMoodle.class);
        return deviceStates.getDevices();
    }

    /**
     * load scene
     *
     * @param id
     * @return
     */
    public Device[] scene(int id) {
        logger.debug("load scene: " + id);
        String SQL = " select ds.device_id, ds.device_status_id, d.permutation, d.rotary_channels,"
                + " d.virtual_dimmer_channel,d.virtual_dimming,d.main_effect_channels, d.start_address,"
                + " ds.input ,es.size , es.speed,es.state,es.channels,es.accept_input,es.is_main ,"
                + " es.effect_id from device_status ds" + " left join effect_status es"
                + " on ds.device_status_id = es.Device_status_id" + " inner join device d"
                + " on d.device_id = ds.device_id" + " inner join scene_has_device_status shds"
                + " on ds.device_status_id = shds.Device_status_id" + " where shds.Scene_id =" + id
                + " ORDER BY d.start_address,es.is_main DESC;";
        Device[] devices = new Device[Constants.DYNAMIC_DEVICES];
        int lastStartaddres = -1;
        int iterator = 0;
        //logger.trace(SQL);
        try {
            conn = dbCreate.getInstance();
            Statement query = conn.createStatement();
            ResultSet res = query.executeQuery(SQL);
            while (res.next()) {
                // Check for devices withe mor than 1 effect
                logger.trace(res.getInt("start_address"));
                if (lastStartaddres != res.getInt("start_address")) {
                    logger.debug("iterate device : " + iterator + " DS : " + res.getInt(
                            "device_status_id"));

                    Device_Setup DS =
                            new Device_Setup(toIntArray(res.getString("main_effect_channels")),
                                    toIntArray(res.getString("rotary_channels")),
                                    res.getInt("virtual_dimmer_channel"),
                                    toLinkedList(res.getString("virtual_dimming")));

                    // Device device = new Device(toIntArray(res.getString("permutation")),
                    // res.getInt("virtual_dimmer_channel"),
                    // toLinkedList(res.getString("virtual_dimming")),
                    // toIntArray(res.getString("rotary_channels")), res.getInt("start_address"),
                    // toIntArray(res.getString("main_effect_channels")));

                    Device device = new Device(DS);

                    device.setInputs(toIntArray(res.getString("input")));
                    if (res.getString("effect_id") != null) {
                        addEffect(device, res);
                    }

                    devices[iterator] = device;
                    lastStartaddres = res.getInt("start_address");
                    iterator++;
                } else if (devices[iterator - 1] != null) {
                    addEffect(devices[iterator - 1], res);
                }

            }

            // fill the scene up with standart devices TODO for scenen bundels
            for (; iterator < Constants.DYNAMIC_DEVICES; iterator++) {
                logger.debug("fill up device : " + iterator);
                devices[iterator] = new Device(new Device_Setup());
            }

            return devices;
        } catch (SQLException e) {
            logger.debug(e);
            return null;
        }

    }

    /**
     * add Effect do Device
     *
     * @param device
     * @param rs     result set with the fields for an effect
     */
    private void addEffect(Device device, ResultSet rs) {
        // logger.debug("add effect for device " + device.getStartAddres());
        try {
            Effect e = Effect_ID.generateEffectFromID(new Effect_ID(rs.getString("effect_id")),
                    rs.getInt("size"), rs.getInt("speed"), rs.getInt("state"),
                    rs.getBoolean("accept_input"), toIntArray(rs.getString("channels")));
            if (rs.getBoolean("is_main")) {
                device.addMainEffect(e);
                ;
            } else {
                device.addEffect(e);
            }
        } catch (SQLException e) {
            // logger.error("can´t add effect for " + device.getStartAddres());
            logger.error(e);
        }
    }

    protected ArrayList<Integer> getDeviceStatusIdsForScene(int id) {
        String SQL = "select ds.device_status_id , d.start_address from device_status ds"
                + " inner join device d" + " on d.device_id = ds.device_id"
                + " inner join scene_has_device_status shds"
                + " on ds.device_status_id = shds.device_status_id" + " where shds.scene_id =" + id
                + " ORDER BY d.start_address;";
        ArrayList<Integer> ids = new ArrayList<>();

        try {
            conn = dbCreate.getInstance();
            Statement query = conn.createStatement();
            ResultSet result = query.executeQuery(SQL);
            while (result.next()) {
                ids.add(result.getInt("device_status_id"));
            }
            return ids;

        } catch (SQLException e) {
            logger.error(e);
            return null;
        }

    }

    /**
     * get names of scens from setup
     *
     * @param setupId
     * @return
     */
    public HashMap<Integer, String> sceneInfos(int setupId) {
        String SQL =
                "select s.scene_id , s.scene_name from setup_has_scene shs " + " inner join scene s"
                        + " on s.scene_id = shs.Scene_id" + " where shs.setUp_id =" + setupId;

        HashMap<Integer, String> scenen = new HashMap<>();

        try {
            conn = dbCreate.getInstance();
            Statement query = conn.createStatement();
            ResultSet result = query.executeQuery(SQL);
            while (result.next()) {
                scenen.put(result.getInt("scene_id"), result.getString("scene_name"));
            }
            return scenen;

        } catch (SQLException e) {
            logger.error(e);
            return null;
        }

    }

    /**
     * get a name of scene
     *
     * @param sceneId
     * @return name
     */
    public String nameOfScene(int sceneId) {
        String SQL = "select scene_name from scene where scene_id=" + sceneId + ";";
        return getOneData(SQL, "scene_name");
    }

    /**
     * get a name of a chase
     *
     * @param chaseId
     * @return
     */
    public String nameOfChase(int chaseId) {
        String SQL = "select chase_name from chase where chase_id =" + chaseId + ";";
        return getOneData(SQL, "chase_name");
    }

    /**
     * get a name of a chase
     *
     * @param chaseId
     * @return
     */
    public String descOfChase(int chaseId) {
        String SQL = "select chase_description from chase where chase_id =" + chaseId + ";";
        return getOneData(SQL, "chase_description");
    }

    /**
     * get names of Devices in an setup
     *
     * @param setupId
     * @return
     */
    public NavigableMap<Integer, String> deviceInfos(int setupId) {
        String SQL = "select d.device_id , d.device_name from device d"
                + " inner join setup_has_device shd" + " on shd.device_id = d.device_id"
                + " where shd.setup_id = " + setupId + " order by d.start_address";

        NavigableMap<Integer, String> devices = new TreeMap<>();

        try {
            conn = dbCreate.getInstance();
            Statement query = conn.createStatement();
            ResultSet result = query.executeQuery(SQL);
            while (result.next()) {
                devices.put(result.getInt("device_id"), result.getString("device_name"));
            }
            return devices;

        } catch (SQLException e) {
            logger.error(e);
            return null;
        }

    }

    /**
     * get the chases of the setup
     *
     * @param setupId
     * @return chase ides
     */
    protected ResultSet setupHasChase(int setupId) {
        String SQL = "select * from setup_has_chase where setUp_id=" + setupId + ";";
        try {
            conn = dbCreate.getInstance();
            Statement query = conn.createStatement();
            ResultSet result = query.executeQuery(SQL);
            return result;
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get the chases of the Setup
     *
     * @param setupId
     * @return get the chases
     */
    public Chase[] chasesOfsetup(int setupId) {
        ArrayList<Chase> chases = new ArrayList<>();
        ResultSet chasesList = setupHasChase(setupId);

        try {
            while (chasesList.next()) {
                chases.add(chase(chasesList.getInt("case_id"), setupId));
            }
            return chases.toArray(new Chase[chases.size()]);
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
            return null;
        }

    }

    /**
     * get ohne chase
     *
     * @param chaseId
     * @param setupId ( ist not requiert)
     * @return chase objeckt with all infos
     */
    public Chase chase(int chaseId, int setupId) {

        String chaseName = nameOfChase(chaseId);

        ResultSet sceneinfos = scenesToChase(chaseId);

        //	ArrayList<Pair<Scene, Integer>> unsortScens = new ArrayList<>();
        ArrayList<Scene> scenes = new ArrayList<>();

        try {
            while (sceneinfos.next()) {
                int sId = sceneinfos.getInt("scene_id");
                Device[] devices = scene(sId);
                Scene s = new Scene(devices);
                //	s.setId(sId);
                //		s.setName(nameOfScene(sId));
                scenes.add(s);
            }
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }

        return new Chase(scenes.toArray(new Scene[scenes.size()]), new int[scenes.size()],
                new int[scenes.size()], chaseName, descOfChase(chaseId), chaseId, setupId);

    }

    /**
     * get the scene informations of an chase
     *
     * @param chaseId
     * @return result set with lines ot chase has scene
     */
    protected ResultSet scenesToChase(int chaseId) {
        String SQL = "select * from chase_has_scene where case_id=" + chaseId + " order by pos;";

        try {
            conn = dbCreate.getInstance();
            Statement query = conn.createStatement();
            ResultSet result = query.executeQuery(SQL);
            return result;
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @param SQL
     * @param feld
     * @return
     */
    public String getOneData(String SQL, String feld) {
        try {
            logger.trace("get on datat: " + SQL);
            conn = dbCreate.getInstance();
            Statement query = conn.createStatement();
            ResultSet result = query.executeQuery(SQL);
            result.next();
            return result.getString(feld);

        } catch (SQLException e) {
            logger.error(e);
            return null;
        }

    }

    // /**
    // * zum ausführen von SQL auf die Datenbank übergeben würde aus das benötigte
    // SQL
    // * und aus den Konstanten werden die Felder übergeben
    // * @param SQL
    // * @param felder
    // * @return
    // */
    @SuppressWarnings("unchecked") private ArrayList<HashMap<String, String>> getDbData(String SQL,
            String[] felder) {
        ArrayList<HashMap<String, String>> answers = new ArrayList<>();
        try {

            Statement query = conn.createStatement();
            logger.trace(query);
            ResultSet result = query.executeQuery(SQL);
            while (result.next()) {
                HashMap<String, String> answer = new HashMap<>();
                for (String feld : felder) {
                    answer.put(feld, result.getString(feld));
                }
                answers.add(answer);
            }
            logger.debug(answers.toString());
        } catch (SQLException e) {
            logger.error("Fail to read data from the DB", e);

        }
        return answers;

    }

    private LinkedList<Integer> toLinkedList(String str) {
        LinkedList<Integer> ll = new LinkedList<>();
        if (str != null) {
            String[] ary = str.split(DBConstants.DELIMITER);
            for (String s : ary) {
                ll.add(Integer.valueOf(s));
            }
        }
        return ll;
    }

    // TODO verry Bad !!!!
    private int[] toIntArray(String str) {
        String[] strArray = str.split(DBConstants.DELIMITER);
        int[] res = new int[strArray.length];

        for (int i = 0; i < strArray.length; i++) {
            res[i] = Integer.valueOf(strArray[i]);
        }
        return res;

    }

}
