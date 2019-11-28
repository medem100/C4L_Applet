package c4l.applet.db;

public class Constants {
	// TODO in props
	
	// Conection -> in props
    public static final String dbHost = "127.0.0.1";
    public static final String dbPort = "3306";
    public static final String database ="c4lv2";
    public static final String dbUser ="root";
    public static final String dbPassword ="A125";
    
    
    // Select 
    public static final String[] FIELDS_SCENE = {"device_status_id","device_description","effect_status_id"};
    /** default setup */
    public static final int DEFAULT_SETUP = 1;
    /** default name for new scene*/
    public static final String DEFAULT_NEW_SCENE_NAME ="New Scene";
    public static final String DEFAULT_NEW_DESCRIPTIOM ="New Scene -- descritpion";
    
    public static final String DELIMITER = ";";

}
