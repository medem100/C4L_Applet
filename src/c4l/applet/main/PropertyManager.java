package c4l.applet.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
	
	//Property-Managing
	private static String resourcePath;
	private String propertiesFolder;
	private String propertiesPath;
	
	private String log4jPropPath;
	private String arduinoPropPath;
	
	private Properties mainProp;
	private Properties serverProp;
	
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

	//Interesting part
	public class Server {
		/** Server-IP **/							public final String IP;//Protokoll muss immer Mitgegeben werde !!!!!
		/** Server Port for API*/					public final String PORT;
	    /** Complete server address */				public final String ADDRESS;
	    /** Path for API-Request */					public final String INFORMATIONPATH;
	    
	    
	    private Server(Properties serverProperties) {
	    	IP						= serverProperties.getProperty("IP", "http://127.0.0.1");
	    	PORT					= serverProperties.getProperty("PORT", "8080");
	    	ADDRESS					= IP + ":" + PORT;
	    	INFORMATIONPATH			= serverProperties.getProperty("INFORMATIONPATH", "C4L_Server/helpJSP/info.jsp?info=true");
	    }
	}
	public final Server SERVER;
	
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