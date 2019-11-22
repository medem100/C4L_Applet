package c4l.applet.db;

/*
 * AS: 21.05.18 17:00 erstellend er ersten notwendigenm insert funcktzionen
 */
import org.apache.log4j.Logger;

import c4l.applet.device.Device;

import java.sql.*;
import java.util.LinkedList;
import c4l.applet.db.Select;

public class Insert {
	private Create dbCreate = new Create();
	private Connection conn = null;

	private Select select = new Select();
	static Logger logger = Logger.getLogger(Insert.class);
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

	public ResultSet scene(Device[] Devices, int setupID) {
		logger.debug("insert new scene");

		try {
			conn = dbCreate.getInstance();
			String findSQL = "select d.device_id from device d " + "inner join setup_has_device shd "
					+ "on shd.device_id = d.device_id " + "where shd.setup_id =" + setupID + " and d.start_address =";

			String INSERT_RECORD = "insert into device_status(input, device_id) values(?,?)";

			PreparedStatement pstmt = conn.prepareStatement(INSERT_RECORD,Statement.RETURN_GENERATED_KEYS);
			
			// insert the device statis
			for (Device device : Devices) {

				int deviceID = Integer.valueOf(select.getOneData(findSQL + device.getStartAddres() + ";", "device_id"));
				logger.debug("deviceId: " + deviceID);
				pstmt.setString(1, toSaveString(device.getInputs()));
				pstmt.setInt(2, deviceID);
				pstmt.addBatch();

			}
			
			// Execut the batchs
			  int[] updateCounts = pstmt.executeBatch();
			  return pstmt.getGeneratedKeys();
			  
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;

		}
	}

	public ResultSet device(int[] permutation, String description, String name, int[] rotaryChannels, int startAddress,
			int virtualDimmerChannel, int[] virtualDimming, int deviceCategory, int[] mainEffectChanels) {
		logger.debug("insert device");
		try {
			conn = dbCreate.getInstance();
			String SQL = "insert into device (permutation,device_description,device_name,start_address,virtual_dimmer_channel,virtual_dimming,device_category_id,rotary_channels,main_effect_channels)"
					+ "values(?,?,?,?,?,?,?,?,?)";
			PreparedStatement preparedStmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

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

			return preparedStmt.getGeneratedKeys();

		} catch (SQLException e) {
			logger.error("can´t insert device", e);
			return null;

		}

	}

	public boolean scene(int scenenID, String scenenName, String info, String payload) {

		try {
			conn = dbCreate.getInstance();
			String SQL = "INSERT INTO scenes (scenenID, scenenName, info, payload)" + " VALUES (?, ?, ?, ?)";

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
			result += e + ";";
		}
		return result.substring(0, result.length() - 1);
	}

	private String toSaveString(LinkedList<Integer> list) {
		String result = "";
		for (int e : list) {
			result += e + ";";
		}
		return result.substring(0, result.length() - 1);
	}

}
