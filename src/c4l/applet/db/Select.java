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

import org.apache.log4j.Logger;
import org.json.JSONArray;


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

	/**
	 * giebt den anwesenheits status zurück
	 * 
	 * @param Name
	 * @param Vorname
	 * @return anwesend oder nicht vorallem ob er anwesend ist
	 */
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
	
	/*TODO Implement
	 * 
	 * - Select scenen
	 * - select all scen ides an descripts 
	 * - select infos for setup
	 * - 
	 * 
	 * - insert scene / chase / device
	 * - 
	 * 
	 */
	

	public ArrayList<HashMap<String, String>> scene(int id) {
		//String SQL = "SELECT payload FROM " + "scenes" + " where scenenID =" + id + "";
		String SQL = "select ds.device_status_id, ds.input , d.device_description, es.effect_status_id from device_status ds "+
				"inner join effect_status es "+
				"on ds.device_status_id = es.Device_status_id "+
				"inner join device d "+
				"on ds.Device_id = d.Device_id "+
				"inner join scene_has_device_status shds "+
				"on ds.device_status_id = shds.Device_status_id "+
				"where shds.Scene_id ="+id+";";
		ArrayList<HashMap<String, String>> answer = getDbData(SQL, Constants.FIELDS_SCENE); // return an JSON array
		return answer;
	}

	public String effects(int id) {
		String SQL = "SELECT effects FROM " + "scenes" + " where scenenID =" + id + "";
		String answer = getOneData(SQL, "effects"); // return an JSON array
		return answer;
	}

	/**
	 * 
	 * @param SQL
	 * @param felde
	 * @return
	 */
	private String getOneData(String SQL, String feld) {
		try {
			// conn = dbCreate.getInstance();

			if (conn != null) {
				Statement query = conn.createStatement();
				// dLogger.log(Level.FINE, SQL);
				ResultSet result = query.executeQuery(SQL);
				result.next();
				return result.getString(feld);

				// dLogger.log(Level.FINER, allAnswers.toString());

			} else {
				// dLogger.log(Level.SEVERE, "conn wurde nicht richitg initzalisiert");
			}
		} catch (SQLException e) {
			// dLogger.log(Level.SEVERE, "Fehler 1 in getDbData " + e.toString());

		}
		// dLogger.log(Level.SEVERE, "Fehler 2 in getDbData");
		return null;

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

}
