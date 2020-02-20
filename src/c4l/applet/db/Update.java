package c4l.applet.db;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import c4l.applet.device.Device;

import java.sql.*;

public class Update {
	private Create dbCreate = new Create();
	private Connection conn = null;
	private Select select = new Select();
	private Insert insert = new Insert();
	private Logger logger = Logger.getLogger(Update.class);
	// private Logger dLogger = dbCreate.initLogger();

	// /**
	// * setzt die anwesenheit mit der id
	// *
	// * @param id
	// * @param anwesend
	// */
	// public void setAnwesend(String id, String anwesend) {
	// int a = 3;
	// try {
	// a = Integer.parseInt(anwesend);
	// } catch (Exception e) {
	//
	// }
	// dLogger.log(Level.FINER, id);
	// setAnwesend(id, null, null, a);
	// }
	//
	// /**
	// * endert die anwesenheit 1 = anwesend 0 = abwesend
	// *
	// * @param name
	// * @param vorname
	// * @param anwesend
	// */
	// public void setAnwesend(String name, String vorname, int anwesend) {
	// // dLogger.log(Level.INFO, "setAnwesend 1 oder 0");
	// setAnwesend(null, name, vorname, anwesend);
	// }
	//
	// /**
	// * ändert die anwesenheit es mus die susID oder der vor und zuname übergeben
	// * werden 1 = anwesend 0 = abwesend
	// *
	// * @param susID
	// * @param name
	// * @param vorname
	// * @param anwesend
	// */
	// public void setAnwesend(String susID, String name, String vorname, int
	// anwesend) {
	//
	// try {
	// dLogger.log(Level.INFO, "setAnwesend 1");
	// String SQL = null;
	// if (susID.equals(null)) {
	// SQL = "UPDATE anwesend" + " INNER JOIN sdSchueler ON anwesend.SusID =
	// sdSchueler.ID "
	// + " SET anwesend.Anwesend = '" + anwesend + "'" + " WHERE sdSchueler.Name =
	// '" + name + "'"
	// + " AND sdSchueler.Vorname ='" + vorname + "'";
	// } else if (name == null && vorname == null) { // Ungetestet
	// SQL = "UPDATE anwesend" + " set Anwesend ='" + anwesend + "'" + " WHERE
	// SusID='" + susID + "'";
	// } else {
	// dLogger.log(Level.SEVERE, susID + name + vorname + " Fassches sql bei der
	// anwesend meldung");
	// }
	// dLogger.log(Level.INFO, SQL);
	// updatDbData(SQL);
	// } catch (Exception e) {
	// dLogger.log(Level.FINER, e.toString());
	// }
	// }

	// TODO DONT WORK WITH SCENE BUNDELS !!!!!! NOT SAVE !!!!
	public void scene(Device[] devices, int id) {
		ArrayList<Integer> deviceStatusIDs = select.getDeviceStatusIdsForScene(id);
		int iterator = 0;
		for (int dsid : deviceStatusIDs) {
			deletEffects(dsid);
			insert.insertEffectStatis(devices[iterator].effects, dsid, false);
			insert.insertEffectStatis(devices[iterator].main_effect, dsid, true);
			String SQL = "update device_status set input ='" + toSaveString(devices[iterator].getInputs())
					+ "' where device_status_id= " + dsid;
			updatDbData(SQL);
			iterator++;
		}
		// TODO insert rest devices

	}
	
	public void scenenName(int id , String name) {
		logger.debug("set scenen name for: " + id +" -> " +name);
		String SQL = "update scene set scene_name = '"+name+"' where scene_id = "+id+";"; 
		updatDbData(SQL);
	}

	private void deletEffects(int dsid) {
		String SQL = "delete from effect_status where device_status_id = " + dsid;
		updatDbData(SQL);
	}

	public void scen(int scenenID, String payload, String ef) {
		String SQL = "UPDATE scenes" + " SET payload = '" + payload + "'" + ", effects='" + ef + "'"
				+ " WHERE scenenID = " + scenenID;
		updatDbData(SQL);
	}

	/**
	 * führd das übergeben sql aus und logt es ggf.
	 * 
	 * @param SQL
	 */
	private void updatDbData(String SQL) {

		try {
			conn = dbCreate.getInstance();

			Statement query = conn.createStatement();
			query.execute(SQL);

		} catch (SQLException e) {
			logger.error(e);

		}
	}
	
	public void chaseName(int chaseid , String name) {
		String SQL = "update chase set chase_name="+ name+ "where chase_id="+ chaseid +";";
		updatDbData(SQL);
	}
	
	public void chaseScens(int chaseId ,int[] sceneIds , int[] fadeTimes, int[] showTimes) {
		DB db = DB.getInstance();
		db.Delete.chaseHasScene(chaseId);
		db.Insert.chaseHasScene(chaseId, sceneIds, fadeTimes, showTimes);
	}

	private String toSaveString(int[] array) {
		String result = "";
		for (int e : array) {
			result += e + Constants.DELIMITER;
		}
		return result.substring(0, result.length() - 1);
	}

}
