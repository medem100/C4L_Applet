package c4l.applet.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class to manage Properties for the C4L_Applet
 * 
 * There only shall be one instance of this class. It is generated with the static function init() and then can be accessed via getInstance().
 * Check exist(), if you're unsure if it already has been initialized.
 * 
 * @author Timon
 *
 */
public class PropertyManager {
	//Structure similar to Singleton
	private static PropertyManager INSTANCE = null;
	private PropertyManager(Properties main) throws Exception {
		this.mainProp = main;
		this.propertiesFolder = mainProp.getProperty("PROPERTIESFOLDER", "properties/");
		this.propertiesPath = resourcePath + propertiesFolder;

		this.log4jPropPath = mainProp.getProperty("LOG4J");
		if (log4jPropPath != null) {
			this.log4jPropPath = propertiesPath + log4jPropPath;
		} else {
			throw new Exception("Missing filename for Log4j-Properties inside main-properties.");
		}
		
		this.arduinoPropPath = mainProp.getProperty("ARDUINO");
		if (arduinoPropPath != null) this.arduinoPropPath = propertiesPath + arduinoPropPath;
		
		this.serverProp = openPropertiesFile(propertiesPath + mainProp.getProperty("SERVER", "server.properties"));
		this.SERVER = new Server(serverProp);
		
		this.directButtonsProp = openPropertiesFile(propertiesPath + mainProp.getProperty("DIRECT_BUTTONS","directButtons.properties"));
	}
	public static PropertyManager getInstance() throws Exception {
		if (INSTANCE == null) throw new Exception("PropertyManger wasn't initialized.");
		return INSTANCE;
	}
	public static void init(String resourcePath, String mainPropetiesSubpath) throws Exception {
		if (INSTANCE != null) throw new Exception("PropertyManager was already initialized.");
		
		PropertyManager.resourcePath = resourcePath;
		INSTANCE = new PropertyManager(openPropertiesFile(resourcePath + mainPropetiesSubpath));
	}
	public static boolean exist() {
		return (INSTANCE != null);
	}
	
	//Meta-Property-Managing
	private static String resourcePath;
	private String propertiesFolder;
	private String propertiesPath;
	
	private String log4jPropPath;
	private String arduinoPropPath;
	
	private Properties mainProp;
	private Properties serverProp;
	private Properties directButtonsProp;
	
	/**
	 * Static function to get Properties-Object from path.
	 * Esp. needed for constructors, where you need to call super() in first line and therefore can't open the file without this function.
	 * 
	 * @param filePath	Full path of property-file to be loaded
	 * @return			Properties-Object
	 */
	private static Properties openPropertiesFile(String filePath) {
		Properties prop = new Properties();
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filePath));
			prop.load(stream);
			stream.close();
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
		return prop;
	}
	
	public String getLog4jPropPath() {
		return log4jPropPath;
	}
	public String getArduinoPropPath() {
		return arduinoPropPath;
	}
	public Properties getDirectButtonsProp() {
		return directButtonsProp;
	}

	//Server-Properties
	public class Server {
		/** Server-IP **/							public final String IP;//Protokoll muss immer Mitgegeben werde !!!!!
		/** Server Port for API*/					public final String PORT;
	    /** Complete server address */				public final String ADDRESS;
	    /** Name of the server web-app */			public final String WEB_APP;
	    
	    /** Path for API-Request */					public final String INFORMATIONPATH;
	    /** Path for read Effect jsp */				public final String EFFECTPATH;
	    /** Path for read Effect jsp */				public final String SAVEPATH;
	    /** Path for check is server Available **/	public final String INDEXPATH;	    
	    
	    private Server(Properties serverProperties) {
	    	IP						= serverProperties.getProperty("IP", "localhost");
	    	PORT					= serverProperties.getProperty("PORT", "8080");
	    	ADDRESS					= IP + ":" + PORT;
	    	WEB_APP					= serverProperties.getProperty("WEB_APP", "c4l_server");
	    	
	    	INFORMATIONPATH			= "/" + WEB_APP + serverProperties.getProperty("INFORMATIONPATH");
	    	EFFECTPATH				= "/" + WEB_APP + serverProperties.getProperty("EFFECTPATH");
	    	SAVEPATH			= "/" + WEB_APP + serverProperties.getProperty("SAVEPATH");
	    	INDEXPATH			= "/" + WEB_APP + serverProperties.getProperty("INDEXPATH");
	    }
	}
	public final Server SERVER;
	
	
	//main for test-Purposes
	public static void main(String[] args) throws Exception {
		String path; PropertyManager propM;
		path = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
		path = path.substring(0, path.lastIndexOf("/"));
		path = path.substring(0, path.lastIndexOf("/")) + "/resources/";
		System.out.println("Resource path:"); System.out.println(path);
		

		PropertyManager.init(path, "properties/main.properties");
		propM = PropertyManager.getInstance();
		
		System.out.println(propM.SERVER.ADDRESS);
	}
}