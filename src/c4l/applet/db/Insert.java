package c4l.applet.db;

/*
 * AS: 21.05.18 17:00 erstellend er ersten notwendigenm insert funcktzionen
 */
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

public class Insert {
	private Create dbCreate = new Create();
	private Connection conn = null;
	// private Logger dLogger = dbCreate.initLogger();

//	public String setSchueler(String Name, String Vorname, String Klasse, String Telephone) {
//		try {
//			// dLogger.log(Level.FINER,"Name2 " + Name);
//
//			conn = dbCreate.getInstance();
//			// dLogger.log(Level.INFO, "setSchueler");
//			java.sql.Statement st = conn.createStatement();
//			// dLogger.log(Level.FINER, st.toString());
//
//			String SQL = "INSERT INTO sdSchueler (Name, Vorname, Klasse, Telephone)" + " VALUES (?, ?, ?, ?)";
//
//			PreparedStatement preparedStmt = conn.prepareStatement(SQL);
//			preparedStmt.setString(1, Name);
//			preparedStmt.setString(2, Vorname);
//			preparedStmt.setString(3, Klasse);
//			preparedStmt.setString(4, Telephone);
//			// dLogger.log(Level.FINER, SQL);
//			// dLogger.log(Level.FINER, preparedStmt.toString());
//			preparedStmt.execute();
//
//			String SQL2 = "INSERT INTO sdSchueler (Name, Vorname, Klasse, Telephone)" + " VALUES (?, ?, ?, ?)";
//
//			return "ok";
//
//		} catch (Exception e) {
//			return e.toString();
//		}
//
//	}

	public boolean scene(int scenenID, String scenenName, String info, String payload) {
		
		try {
			conn = dbCreate.getInstance();
			String SQL = "INSERT INTO scenes (scenenID, scenenName, info, payload)" + " VALUES (?, ?, ?, ?)";
			
			PreparedStatement preparedStmt = conn.prepareStatement(SQL);
			preparedStmt.setInt(1,scenenID );
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


}
