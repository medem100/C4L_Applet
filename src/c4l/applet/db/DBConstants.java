package c4l.applet.db;

public class DBConstants {
	// TODO in props
	
	// Conection -> in props
    public static final String dbHost = "127.0.0.1";
    public static final String dbPort = "3306";
    public static final String database ="c4lv2";
    public static final String dbUser ="root";
    public static final String dbPassword ="A125";

    public static final String DB_SERVER_BASE =  "http://localhost:4000/db";
    public static final String SCENES = "scenes";
    public static final String DEVICES = "devices";
    
    
    // Select 
    public static final String[] FIELDS_SCENE = {"device_status_id","device_description","effect_status_id"};
    /** default setup */
    public static final int DEFAULT_SETUP = 1;
    /** default name for new scene*/
    public static final String DEFAULT_NEW_SCENE_NAME ="New Scene";
    public static final String DEFAULT_NEW_DESCRIPTIOM ="New Scene -- descritpion";
    
    public static final String DELIMITER = ";";

}
