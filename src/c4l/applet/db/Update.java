package c4l.applet.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import sun.util.logging.resources.logging;

import java.sql.*;

public class Update {
	private Create dbCreate = new Create();
	private Connection conn = null;
	// private Logger dLogger = dbCreate.initLogger();

//	/**
//	 * setzt die anwesenheit mit der id
//	 * 
//	 * @param id
//	 * @param anwesend
//	 */
//	public void setAnwesend(String id, String anwesend) {
//		int a = 3;
//		try {
//			a = Integer.parseInt(anwesend);
//		} catch (Exception e) {
//
//		}
//		dLogger.log(Level.FINER, id);
//		setAnwesend(id, null, null, a);
//	}
//
//	/**
//	 * endert die anwesenheit 1 = anwesend 0 = abwesend
//	 * 
//	 * @param name
//	 * @param vorname
//	 * @param anwesend
//	 */
//	public void setAnwesend(String name, String vorname, int anwesend) {
//		// dLogger.log(Level.INFO, "setAnwesend 1 oder 0");
//		setAnwesend(null, name, vorname, anwesend);
//	}
//
//	/**
//	 * ändert die anwesenheit es mus die susID oder der vor und zuname übergeben
//	 * werden 1 = anwesend 0 = abwesend
//	 * 
//	 * @param susID
//	 * @param name
//	 * @param vorname
//	 * @param anwesend
//	 */
//	public void setAnwesend(String susID, String name, String vorname, int anwesend) {
//
//		try {
//			dLogger.log(Level.INFO, "setAnwesend 1");
//			String SQL = null;
//			if (susID.equals(null)) {
//				SQL = "UPDATE anwesend" + " INNER JOIN sdSchueler ON anwesend.SusID = sdSchueler.ID "
//						+ " SET anwesend.Anwesend = '" + anwesend + "'" + " WHERE sdSchueler.Name = '" + name + "'"
//						+ " AND sdSchueler.Vorname ='" + vorname + "'";
//			} else if (name == null && vorname == null) { // Ungetestet
//				SQL = "UPDATE anwesend" + " set Anwesend ='" + anwesend + "'" + " WHERE SusID='" + susID + "'";
//			} else {
//				dLogger.log(Level.SEVERE, susID + name + vorname + " Fassches sql bei der anwesend meldung");
//			}
//			dLogger.log(Level.INFO, SQL);
//			updatDbData(SQL);
//		} catch (Exception e) {
//			dLogger.log(Level.FINER, e.toString());
//		}
//	}

	public void scen(int scenenID,String payload) {
		String SQL = "UPDATE scenes" +
				" SET payload = '" + payload + "'" + " WHERE scenenID = " + scenenID;
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
			if (conn != null) {
				Statement query = conn.createStatement();
				query.execute(SQL);

			} else {

			}
		} catch (SQLException e) {

		}
	}

}
