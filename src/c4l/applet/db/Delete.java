package c4l.applet.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import org.apache.log4j.Logger;

public class Delete {

	private Create dbCreate = new Create();
	private Connection conn = null;
	// public static String[] felderSDschueler = { "ID", "Name", "Vorname",
	// "Klasse", "Telephone" };
	// private Logger dLogger = dbCreate.initLogger();
	static Logger logger = Logger.getLogger(Delete.class);

	public Delete() {
		try {
			this.conn = dbCreate.getInstance();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * delete an scen from the setup
	 * @param id
	 * @throws SQLException
	 */
	public void deleteScene(int id) throws SQLException {
	//	String SQLDeleteSceneSelf = "delete from scene where scene_id=" + id + ";";
		String SQLDeleteSetupHasScene = "delete from setup_has_scene where scene_id=" + id + ";";
		String SQLDeleteChaseHasScene = "delete from chase_has_scene where scene_id=" + id + ";";
		
		// TODO delete effect and device statis

		try {
			conn = dbCreate.getInstance();
			Statement query = conn.createStatement();
		//	query.executeUpdate(SQLDeleteSceneSelf);
			query.executeUpdate(SQLDeleteSetupHasScene);
			query.executeUpdate(SQLDeleteChaseHasScene);

		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e);
		}

	}
	
	
	/**
	 * delete a chase from setup
	 * @param id id of the chase
	 * @param setupid setup id
	 * @throws SQLException
	 */
	public void deleteChase(int id , int setupid) throws SQLException {
			String SQLDeleteSetupHasChase = "delete from setup_has_chase where setUp_id=" + setupid + "and case_id="+id +";";
			
			// TODO delete effect and device statis

			try {
				conn = dbCreate.getInstance();
				Statement query = conn.createStatement();
				query.executeUpdate(SQLDeleteSetupHasChase);
				

			} catch (SQLException e) {
				logger.error(e);
				throw new SQLException(e);
			}

		}
	
	/**
	 * delete all scenes from an chase
	 * @param chaseId
	 */
	protected void chaseHasScene(int chaseId) {

		String SQLDeleteChaseHaseScene = "delete from chase_has_scene where case_id="+ chaseId +";";
		
		try {
			conn = dbCreate.getInstance();
			Statement query = conn.createStatement();
			query.executeUpdate(SQLDeleteChaseHaseScene);
			

		} catch (SQLException e) {
			logger.error(e);
		}

	}


}
