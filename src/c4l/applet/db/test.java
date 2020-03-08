package c4l.applet.db;
import java.sql.SQLException;

import org.apache.log4j.PropertyConfigurator;

import c4l.applet.db.*;
import c4l.applet.main.Constants;
import c4l.applet.main.PropertyManager;

public class test {
	private static  DB db = c4l.applet.db.DB.getInstance();
	public static PropertyManager propM;
	 public static void main(String[] args){ 
		 //db.Update.setAnwesend("1", 0);
		// db.Insert.scene(0, "s0", "Scene 0", "{ t:88,abc:'tt'}");
		String resourcePath = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
			resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("/"));
			resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("/")) + "/resources/";

			try {
				PropertyManager.init(resourcePath, Constants.MAIN_PROPERTIES);
				propM = PropertyManager.getInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Initialize Logging
			PropertyConfigurator.configure(propM.getLog4jPropPath());
		//Select select = new Select();
		int[] sc = {8,46,45}; 
			
		 db.Update.chaseScens(6, sc , new int[3], new int[3]);
	 }
}
