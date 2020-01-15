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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import c4l.applet.device.Device;
import c4l.applet.device.Effect;
import c4l.applet.device.Effect_ID;
import c4l.applet.main.Constants;
import c4l.applet.scenes.Device_Setup;

public class Select {

	private Create dbCreate = new Create();
	private Connection conn = null;
	// public static String[] felderSDschueler = { "ID", "Name", "Vorname",
	// "Klasse", "Telephone" };
	// private Logger dLogger = dbCreate.initLogger();
	static Logger logger = Logger.getLogger(Select.class);

	public Select() {
		try {
			this.conn = dbCreate.getInstance();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// public String getSchuelerAnwesend(String Name ,String Vorname){
	// dLogger.log(Level.INFO, "getSchuelerAnwesend");
	// String[] felder = {"Vorname","Name","Anwesend"}; // mus warscheinlich noch
	// klasse rein
	// String SQL = " SELECT sdSchueler.Vorname,sdSchueler.Name, anwesend.Anwesend"+
	// " FROM sdSchueler"+
	// " INNER JOIN anwesend ON sdSchueler.ID = anwesend.SusID AND
	// sdSchueler.Vorname = '" + Vorname + "' AND sdSchueler.Name = '"+ Name +"'"+
	// " ORDER BY sdSchueler.Vorname";
	// dLogger.log(Level.FINER, SQL);
	// return getDbData(SQL,felder);
	//
	// }
	//
	// /**
	// * giebt eine klassen liste aus der DB zurück
	// * @param Klasse
	// * @return name vorname satus der anwesenheit in einem json string
	// */
	// public String getKlasseAnwesend(String Klasse){
	//
	// dLogger.log(Level.INFO, "getKlasseAnwesend");
	// String[] felder = {"Vorname","Name","Anwesend","ID"};
	// String SQL = " SELECT sdSchueler.Vorname,sdSchueler.ID,sdSchueler.Name,
	// anwesend.Anwesend"+
	// " FROM sdSchueler"+
	// " INNER JOIN anwesend ON sdSchueler.ID = anwesend.SusID AND sdSchueler.Klasse
	// = '"+Klasse+"'"+
	// " ORDER BY sdSchueler.Vorname";
	// dLogger.log(Level.FINER, SQL);
	// return getDbData(SQL,felder);
	// }
	//
	// /**
	// * abfrage von Schüler Stammdaten mit beiden oder nur einem parameter
	// *
	// * @param name
	// * @param vorname
	// * @return String mit den werten aus der DB in Json String
	// */
	// public String getSchueler(String name, String vorname){
	// dLogger.log(Level.INFO, "getSchueler");
	// String SQL = null;
	// if(name.equals("") && (!vorname.equals(""))){
	// SQL = "SELECT * FROM "+ constant.TSDSchueler + " where Vorname
	// ='"+vorname+"'"; // Ungetestet
	// }else if((!name.equals("")) && vorname.equals("")){
	// SQL = "SELECT * FROM "+ constant.TSDSchueler + " where Name ='"+name+"'";
	// }else{
	// SQL = "SELECT * FROM "+ constant.TSDSchueler + " where Name ='"+name+"' AND
	// Vorname ='"+vorname+"'";
	// }
	// dLogger.log(Level.FINER, SQL);
	// return getDbData(SQL, constant.felderSDschueler);
	// }
	//
	//

	/*
	 * TODO Implement
	 * 
	 * - Select scenen - select all scen ides an descripts - select infos for setup
	 * -
	 * 
	 * - insert scene / chase / device -
	 * 
	 */

	/*
	 * public ArrayList<HashMap<String, String>> scene(int id) { //String SQL =
	 * "SELECT payload FROM " + "scenes" + " where scenenID =" + id + ""; String SQL
	 * =
	 * "select ds.device_status_id, ds.input , d.device_description, es.effect_status_id from device_status ds "
	 * + "inner join effect_status es "+
	 * "on ds.device_status_id = es.Device_status_id "+ "inner join device d "+
	 * "on ds.Device_id = d.Device_id "+ "inner join scene_has_device_status shds "+
	 * "on ds.device_status_id = shds.Device_status_id "+
	 * "where shds.Scene_id ="+id+";"; ArrayList<HashMap<String, String>> answer =
	 * getDbData(SQL, Constants.FIELDS_SCENE); // return an JSON array // return
	 * first element return answer; }
	 * 
	 * public String effects(int id) { String SQL = "SELECT effects FROM " +
	 * "scenes" + " where scenenID =" + id + ""; String answer = getOneData(SQL,
	 * "effects"); // return an JSON array return answer; }
	 */
	// TODO refactor without Start Addres use a.s IDX

	/**
	 * load scene
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
		logger.trace(SQL);
		try {
			conn = dbCreate.getInstance();
			Statement query = conn.createStatement();
			ResultSet res = query.executeQuery(SQL);
			while (res.next()) {
				// Check for devices withe mor than 1 effect
				logger.trace(res.getInt("start_address"));
				if (lastStartaddres != res.getInt("start_address")) {
					logger.debug("iterate device : " + iterator + " DS : " + res.getInt("device_status_id"));
					
					Device_Setup DS  = new Device_Setup(toIntArray(res.getString("main_effect_channels")), toIntArray(res.getString("rotary_channels")), res.getInt("virtual_dimmer_channel"), toLinkedList(res.getString("virtual_dimming")));
					
//					Device device = new Device(toIntArray(res.getString("permutation")),
//							res.getInt("virtual_dimmer_channel"), toLinkedList(res.getString("virtual_dimming")),
//							toIntArray(res.getString("rotary_channels")), res.getInt("start_address"),
//							toIntArray(res.getString("main_effect_channels")));

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
	 * @param rs
	 *            result set with the fields for an effect
	 */
	private void addEffect(Device device, ResultSet rs) {
//		logger.debug("add effect for device " + device.getStartAddres());
		try {
			Effect e = Effect_ID.generateEffectFromID(new Effect_ID(rs.getString("effect_id")), rs.getInt("size"),
					rs.getInt("speed"), rs.getInt("state"), rs.getBoolean("accept_input"),
					toIntArray(rs.getString("channels")));
			if (rs.getBoolean("is_main")) {
				device.addMainEffect(e);
				;
			} else {
				device.addEffect(e);
			}
		} catch (SQLException e) {
	//		logger.error("can´t add effect for " + device.getStartAddres());
			logger.error(e);
		}
	}

	protected ArrayList<Integer> getDeviceStatusIdsForScene(int id) {
		String SQL = "select ds.device_status_id , d.start_address from device_status ds" + " inner join device d"
				+ " on d.device_id = ds.device_id" + " inner join scene_has_device_status shds"
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
		String SQL = "select s.scene_id , s.scene_name from setup_has_scene shs " + " inner join scene s"
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
	 * get names of Devices in an setup
	 * 
	 * @param setupId
	 * @return
	 */
	public HashMap<Integer, String> deviceInfos(int setupId) {
		String SQL = "select d.device_id , d.device_name from device d" + " inner join setup_has_device shd"
				+ " on shd.device_id = d.device_id" + " where shd.setup_id = " + setupId;

		HashMap<Integer, String> devices = new HashMap<>();

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
	 * 
	 * @param SQL
	 * @param felde
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
	@SuppressWarnings("unchecked")
	private ArrayList<HashMap<String, String>> getDbData(String SQL, String[] felder) {
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
			String[] ary = str.split(c4l.applet.db.Constants.DELIMITER);
			for (String s : ary) {
				ll.add(Integer.valueOf(s));
			}
		}
		return ll;
	}

	// TODO verry Bad !!!!
	private int[] toIntArray(String str) {
		String[] strArray = str.split(c4l.applet.db.Constants.DELIMITER);
		int[] res = new int[strArray.length];

		for (int i = 0; i < strArray.length; i++) {
			res[i] = Integer.valueOf(strArray[i]);
		}
		return res;

	}

}
