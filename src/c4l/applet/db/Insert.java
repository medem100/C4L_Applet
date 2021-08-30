package c4l.applet.db;

/*
 * AS: 21.05.18 17:00 erstellend er ersten notwendigenm insert funcktzionen
 */

import c4l.applet.device.Device;
import c4l.applet.device.Effect;
import c4l.applet.device.Effect_ID;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

public class Insert {
    private Create dbCreate = new Create();
    private Connection conn = null;

    private Select select = new Select();
    static Logger logger = Logger.getLogger(Insert.class);

    private HttpClient CLIENT;
    private Gson GSON;

    public Insert() {
    }

    public Insert(HttpClient CLIENT, Gson GSON) {
        this.CLIENT = CLIENT;
        this.GSON = GSON;
    }
    // private Logger dLogger = dbCreate.initLogger();

    // public String setSchueler(String Name, String Vorname, String Klasse, String
    // Telephone) {
    // try {
    // // dLogger.log(Level.FINER,"Name2 " + Name);
    //
    // conn = dbCreate.getInstance();
    // // dLogger.log(Level.INFO, "setSchueler");
    // java.sql.Statement st = conn.createStatement();
    // // dLogger.log(Level.FINER, st.toString());
    //
    // String SQL = "INSERT INTO sdSchueler (Name, Vorname, Klasse, Telephone)" + "
    // VALUES (?, ?, ?, ?)";
    //
    // PreparedStatement preparedStmt = conn.prepareStatement(SQL);
    // preparedStmt.setString(1, Name);
    // preparedStmt.setString(2, Vorname);
    // preparedStmt.setString(3, Klasse);
    // preparedStmt.setString(4, Telephone);
    // // dLogger.log(Level.FINER, SQL);
    // // dLogger.log(Level.FINER, preparedStmt.toString());
    // preparedStmt.execute();
    //
    // String SQL2 = "INSERT INTO sdSchueler (Name, Vorname, Klasse, Telephone)" + "
    // VALUES (?, ?, ?, ?)";
    //
    // return "ok";
    //
    // } catch (Exception e) {
    // return e.toString();
    // }
    //
    // }

    public int devicesToScene(Device[] devices, int sceneId)
            throws IOException, InterruptedException {

        String jsonPayload = GSON.toJson(new DeviceStatesSaveModel(devices));

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(
                        DBConstants.DB_SERVER_BASE + "/" + DBConstants.SCENES + "/" + sceneId + "/"
                                + DBConstants.DEVICES))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonPayload))
                .header("Content-Type", "application/json").build();
        HttpResponse<Void> response = CLIENT.send(httpRequest, BodyHandlers.discarding());
        return response.statusCode();
    }

    public Integer scene(Device[] Devices, int setupID) throws Exception {
        return scene(Devices, setupID, DBConstants.DEFAULT_NEW_SCENE_NAME,
                DBConstants.DEFAULT_NEW_DESCRIPTIOM);
    }

    /**
     * scene Scene to setup
     *
     * @param Devices
     * @param setupID
     * @return
     * @throws Exception
     */
    public Integer scene(Device[] Devices, int setupID, String name, String description)
            throws Exception {
        logger.debug("insert new scene");

        try {
            conn = dbCreate.getInstance();
            String SELECT_DEVICE_ID =
                    "select d.device_id from device d " + "inner join setup_has_device shd "
                            + "on shd.device_id = d.device_id " + "where shd.setup_id =" + setupID
                            + " and d.start_address =";

            String INSERT_DEVICE_STATUS = "insert into device_status(input, device_id) values(?,?)";

            PreparedStatement insertDevceStatusStatment =
                    conn.prepareStatement(INSERT_DEVICE_STATUS, Statement.RETURN_GENERATED_KEYS);
            ArrayList<Integer> deviceStatusIDs = new ArrayList<>();
            // insert the device statis
            for (int i = 0; i < Devices.length; i++) {
                Device device = Devices[i];
                String dId = select.getOneData(
                        SELECT_DEVICE_ID + i * c4l.applet.main.Constants.DEVICE_CHANNELS + ";",
                        "device_id");
                if (dId != null) {
                    int deviceID = Integer.valueOf(dId);
                    logger.debug("deviceId: " + deviceID);
                    insertDevceStatusStatment.setString(1, toSaveString(device.getInputs()));
                    insertDevceStatusStatment.setInt(2, deviceID);
                    insertDevceStatusStatment.execute();

                    ResultSet keys = insertDevceStatusStatment.getGeneratedKeys();
                    keys.next();
                    int deviceSID = keys.getInt(1);
                    deviceStatusIDs.add(deviceSID);
                    // save main effects
                    insertEffectStatis(device.main_effect, deviceSID, true);
                    // save effects
                    insertEffectStatis(device.effects, deviceSID, false);

                } else {
                    throw new Exception("Device not Found: sid:" + setupID + " addres: "
                            + i * c4l.applet.main.Constants.DEVICE_CHANNELS);
                }

            }
            // Insert new Scene

            int sceneID = createScene(name, description);
            addSceneToSetUp(setupID, sceneID);

            // add Device status to scene
            addDeviceStatusToScene(deviceStatusIDs, sceneID);

            return sceneID;

        } catch (SQLException e) {
            logger.error(e);
            return null;

        }
    }

    /**
     * insert new effect status
     */
    protected ResultSet insertEffectStatis(LinkedList<Effect> effects, int deviceStatusId,
            boolean isMain) {
        logger.debug("insert effect status for device statusID: " + deviceStatusId);
        String INSERT_EFFECT_STATUS =
                "insert into effect_status(size,speed,channels,accept_input,state,Device_status_id,Effect_id,is_main)"
                        + "values(?,?,?,?,?,?,?,?);";
        // PreparedStatement insertNewSceneStatment =
        // conn.prepareStatement(INSERT_DS_TO_SCENE);
        try {
            conn = dbCreate.getInstance();
            PreparedStatement insertNewEffectStatusStatment =
                    conn.prepareStatement(INSERT_EFFECT_STATUS, Statement.RETURN_GENERATED_KEYS);
            for (Effect effect : effects) {
                insertNewEffectStatusStatment.setInt(1, effect.getSize());
                insertNewEffectStatusStatment.setInt(2, effect.getSpeed());
                insertNewEffectStatusStatment.setString(3, toSaveString(effect.getChannels()));
                insertNewEffectStatusStatment.setInt(4, effect.isAcceptInput() ? 1 : 0);
                insertNewEffectStatusStatment.setInt(5, effect.getState());
                insertNewEffectStatusStatment.setInt(6, deviceStatusId);
                insertNewEffectStatusStatment.setString(7,
                        Effect_ID.getEffectID(effect).toString());
                insertNewEffectStatusStatment.setInt(8, isMain ? 1 : 0);
                insertNewEffectStatusStatment.addBatch();
            }
            insertNewEffectStatusStatment.executeBatch();
            return insertNewEffectStatusStatment.getGeneratedKeys();
        } catch (SQLException e) {
            logger.error(e);
            return null;

        }

    }

    /**
     * add device statis to an scene
     *
     * @param deviceStatusIds
     * @param sceneID
     */
    public void addDeviceStatusToScene(ArrayList<Integer> deviceStatusIds, int sceneID) {
        logger.debug("add device status to scene :" + sceneID);
        String INSERT_DS_TO_SCENE =
                "insert into scene_has_device_status(scene_id,device_status_id) values(?,?)";
        try {
            conn = dbCreate.getInstance();
            PreparedStatement insertNewSceneStatment = conn.prepareStatement(INSERT_DS_TO_SCENE);
            for (int id : deviceStatusIds) {
                insertNewSceneStatment.setInt(1, sceneID);
                insertNewSceneStatment.setInt(2, id);
                insertNewSceneStatment.addBatch();
            }
            insertNewSceneStatment.executeBatch();

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    /**
     * crate new scene entry returns the new id
     *
     * @param name
     * @param description
     * @return id of the new scene
     */
    private Integer createScene(String name, String description) {
        logger.debug("create new scene");
        String INSERT_SCENE = "insert into scene(scene_name,scene_description) values(?,?)";
        try {
            conn = dbCreate.getInstance();
            PreparedStatement insertNewSceneStatment =
                    conn.prepareStatement(INSERT_SCENE, Statement.RETURN_GENERATED_KEYS);

            insertNewSceneStatment.setString(1, name);
            insertNewSceneStatment.setString(2, description);
            insertNewSceneStatment.execute();

            ResultSet rs = insertNewSceneStatment.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            logger.error(e);
            return null;
        }

    }

    /**
     * add new Scene to a setup
     *
     * @param setUpID
     * @param sceneID
     */
    private void addSceneToSetUp(int setUpID, int sceneID) {
        logger.debug("scene: " + sceneID + " to setup: " + setUpID);
        String INSERT_SCENE_TO_SETUP = "insert into setup_has_scene(setUp_id,scene_id) values(?,?)";
        try {
            conn = dbCreate.getInstance();
            PreparedStatement insertNewSceneStatment = conn.prepareStatement(INSERT_SCENE_TO_SETUP);
            insertNewSceneStatment.setInt(1, setUpID);
            insertNewSceneStatment.setInt(2, sceneID);
            insertNewSceneStatment.execute();
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    /**
     * insert new device
     *
     * @param permutation
     * @param description
     * @param name
     * @param rotaryChannels
     * @param startAddress
     * @param virtualDimmerChannel
     * @param virtualDimming
     * @param deviceCategory
     * @param mainEffectChanels
     * @return new Device id
     */
    public Integer device(int[] permutation, String description, String name, int[] rotaryChannels,
            int startAddress, int virtualDimmerChannel, int[] virtualDimming, int deviceCategory,
            int[] mainEffectChanels) {
        logger.debug("insert device");
        try {
            conn = dbCreate.getInstance();
            String SQL =
                    "insert into device (permutation,device_description,device_name,start_address,virtual_dimmer_channel,virtual_dimming,device_category_id,rotary_channels,main_effect_channels)"
                            + "values(?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStmt =
                    conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            preparedStmt.setString(1, toSaveString(permutation));
            preparedStmt.setString(2, description);
            preparedStmt.setString(3, name);
            preparedStmt.setInt(4, startAddress);
            preparedStmt.setInt(5, virtualDimmerChannel);
            preparedStmt.setString(6, toSaveString(virtualDimming));
            preparedStmt.setInt(7, deviceCategory);
            preparedStmt.setString(8, toSaveString(rotaryChannels));
            preparedStmt.setString(9, toSaveString(mainEffectChanels));

            preparedStmt.execute();

            ResultSet rs = preparedStmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            logger.error("can´t insert device", e);
            return null;

        }

    }

    protected void chaseHasScene(int chaseid, int[] scnenIds, int[] fadeTimes, int[] showTimes) {
        logger.debug("insert chaseHaseScene");

        try {
            conn = dbCreate.getInstance();
            String SQLInsertChaseHasScene =
                    "insert into chase_has_scene (case_id,scene_id,pos,running_time) values (?,?,?,?);";

            for (int i = 0; i < scnenIds.length; i++) {
                PreparedStatement preparedStmt2 = conn.prepareStatement(SQLInsertChaseHasScene);
                preparedStmt2.setInt(1, chaseid);
                preparedStmt2.setInt(2, scnenIds[i]);
                preparedStmt2.setInt(3, i);
                preparedStmt2.setInt(4, showTimes[i]);
                preparedStmt2.execute();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int chase(String name, String description, int setupID, int[] sceneIds, int[] fadeTimes,
            int[] showTimes) {
        logger.debug("insert Case");

        try {
            conn = dbCreate.getInstance();
            String SQLInsertChase =
                    "insert into chase (chase_name,chase_description) values (?, ?);";
            String SQLInsertChaseHasScene =
                    "insert into chase_has_scene (case_id,scene_id,pos,running_time) values (?,?,?,?);";
            String SQLInsertSetupHasChase =
                    "insert into setup_has_chase( setUp_id, case_id) values(?,?)";

            PreparedStatement preparedStmt =
                    conn.prepareStatement(SQLInsertChase, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setString(1, name);
            preparedStmt.setString(2, description);
            preparedStmt.execute();
            ResultSet rs = preparedStmt.getGeneratedKeys();
            rs.next();
            int chaseId = rs.getInt(1);

            chaseHasScene(chaseId, sceneIds, fadeTimes, showTimes);

            PreparedStatement preparedStmt3 = conn.prepareStatement(SQLInsertSetupHasChase);
            preparedStmt3.setInt(1, setupID);
            preparedStmt3.setInt(2, chaseId);
            preparedStmt3.execute();

            return chaseId;

        } catch (SQLException e) {
            logger.error("can´t insert Chase", e);
            e.printStackTrace();
            return 0;
        }

    }

    @Deprecated public boolean scene(int scenenID, String scenenName, String info, String payload) {

        try {
            conn = dbCreate.getInstance();
            String SQL = "INSERT INTO scenes (scenenID, scenenName, info, payload)"
                    + " VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStmt = conn.prepareStatement(SQL);
            preparedStmt.setInt(1, scenenID);
            preparedStmt.setString(2, scenenName);
            preparedStmt.setString(3, info);
            preparedStmt.setString(4, payload);
            preparedStmt.execute();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;

    }

    private String toSaveString(int[] array) {
        String result = "";
        for (int e : array) {
            result += e + DBConstants.DELIMITER;
        }
        return result.substring(0, result.length() - 1);
    }

    private String toSaveString(LinkedList<Integer> list) {
        String result = "";
        for (int e : list) {
            result += e + DBConstants.DELIMITER;
        }
        return result.substring(0, result.length() - 1);
    }

}
