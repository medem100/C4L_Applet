/*
 * Alle nötigen Funktion um eine DB Komunikatzion aufzubauen
 * 
 * AS: kopieren der bereits erstellte funcktzionen / methoden aus Database klase kopiert 
 *  - dbCrate()
 *  - getInstance()
 *  - initproperties()
 *  - initlogging()
 */
package c4l.applet.db;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Create {
	
	
	
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
    
    private String dbHost = "127.0.0.1";
    private String dbPort = "3306";
    private String database ="c4l";
    private String dbUser ="root";
    private String dbPassword ="A125";
    
  
	
	public Create()  {
	    try {
//	      dLogger.log(Level.FINER, "crate database... ");
	      Class.forName("com.mysql.jdbc.Driver");
	       conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":"
	          + dbPort + "/" + database + "?" + "user=" + dbUser + "&"
	          + "password=" + dbPassword);
//	       dLogger.log(Level.INFO, "conn wurde erstellt");
	    } catch (Exception e) {
//	      dLogger.log(Level.SEVERE, "Treiber nicht gefunden");
	    } 
	    
	  }
	
	/**
	 * überprüft ob eine verbindung vorhanden ist .
	 * giebt endweder die oder eine neue zurück
	 * @return conection
	 * @throws SQLException
	 */
	 public Connection getInstance() throws SQLException
	  {
//		dLogger.log(Level.FINER, "holt conn instans");
	    if(conn == null)
			try {
				new Create();
			} catch (Exception e) {
//				dLogger.log(Level.SEVERE, "fehler beim Conn holen", e);
			}
	    return conn;
	  }
	 
	 
	 /**
	  * initalsiert den Logger 
	  * @return DATALOGGER
	  */
	public Logger initLogger() throws IOException{
		 LogManager logManager = LogManager.getLogManager();
	     Logger DATALOGGER = Logger.getLogger("DataLog");
	
	         DATALOGGER.log(Level.INFO, "Logging Objeckt wurde erstellt");
	         return DATALOGGER;
	     }
	/**
	 * Initialisiert das Properties Objekt mit der entsprechenden Zeile
	 * @return properties
	 * @throws IOException 
	 */
//	public Properties initProperties() {
////		dLogger.log(Level.FINER, "Initzalisiere props");
//		Properties Props = new Properties();
//		try{
//
//		  BufferedInputStream bis = new BufferedInputStream(Create.class.getClassLoader().getResourceAsStream(propetiesPath));
//		  
//		  Props.load(bis);
////		  dLogger.log(Level.FINER, "props sind geladen");
//		  bis.close();  
//
//		}catch (IOException e){
////			dLogger.log(Level.SEVERE, "props sind nicht geladen ", e);
//		}
//		 
//		return Props;
//	}

}
