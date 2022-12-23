/*
 * Alle n�tigen Funktion um eine DB Komunikatzion aufzubauen
 * 
 * AS: kopieren der bereits erstellte funcktzionen / methoden aus Database klase kopiert 
 *  - dbCrate()
 *  - getInstance()
 *  - initproperties()
 *  - initlogging()
 */
package c4l.applet.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Create {
	
	private static Logger logger = Logger.getLogger(Create.class);
	
	private  Connection conn = null;
	//private static File dLoggerFile = new File(fullPath);
//	private static File dLoggerFile = new File("./Properties/OpenSasLogging.properties");
//    private static Logger dLogger = initLogger();
    //private static File propertiesFile = new File(fullPath);
//    private  String propetiesPath = "DB.properties";
//    private  String loggingPath = "OpenSasLogging.properties";
//    private  Logger dLogger = initLogger();
//    private  Properties props = initProperties();
    private static final Create dbCreate = new Create();
   // private static final Singleton OBJ = new Singleton(); 
    
//    private String dbHost = "127.0.0.1";
//    private String dbPort = "3306";
//    private String database ="c4l";
//    private String dbUser ="root";
//    private String dbPassword ="A125";
//    
  
	
	public Create()  {
	    try {
	    	logger.log(Level.TRACE, "crate database... ");
	      Class.forName("com.mysql.jdbc.Driver");
	       conn = DriverManager.getConnection("jdbc:mysql://" + DBConstants.dbHost + ":"
	          + DBConstants.dbPort + "/" + DBConstants.database + "?" + "user=" + DBConstants.dbUser + "&"
	          + "password=" + DBConstants.dbPassword);
	       logger.log(Level.INFO, "conn was created");
	    } catch (Exception e) {
	      logger.log(Level.FATAL, "Treiber nicht gefunden");
	    } 
	    
	  }
	
	/**
	 * �berpr�ft ob eine verbindung vorhanden ist .
	 * giebt endweder die oder eine neue zur�ck
	 * @return conection
	 * @throws SQLException
	 */
	 public Connection getInstance() throws SQLException
	  {

	    if(conn == null)
			try {
				new Create();
			} catch (Exception e) {
				logger.log(Level.FATAL, "connection was not found", e);
			}
	    return conn;
	  }
	 
	 
	


}
