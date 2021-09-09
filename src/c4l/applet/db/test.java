package c4l.applet.db;

import java.io.IOException;
import java.util.Arrays;

import c4l.applet.device.Device;
import c4l.applet.device.Effect_ID;
import c4l.applet.scenes.Device_Setup;

import c4l.applet.main.PropertyManager;

public class test {
    private static DB db = c4l.applet.db.DB.getInstance();
    public static PropertyManager propM;

    public static void main(String[] args) throws IOException, InterruptedException {
        //db.Update.setAnwesend("1", 0);
        // db.Insert.scene(0, "s0", "Scene 0", "{ t:88,abc:'tt'}");
	/*	String resourcePath =
				 Thread.currentThread().getContextClassLoader().getResource(".").getPath();
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
			
		 db.Update.chaseScens(6, sc , new int[3], new int[3]); */

        DB db = DB.getInstance();

        //System.out.println(db.Insert.devicesToScene(getDevices(), 12));

        System.out.println(Arrays.toString(
                db.Select.devicesFromScene(1)[0].getSetup().getMain_effect_channels()));
      //  System.out.println(db.Insert.devicesToScene(db.Select.devicesFromScene(11), 1));
    }

    private static Device[] getDevices() {
        Device[] res = new Device[30];

        for (int i = 0; i < res.length; i++) {
            res[i] = new Device(new Device_Setup());
        }

        res[0].main_effect.add(Effect_ID.generateEffectFromID(new Effect_ID("03"), 20, 10, 0,
                new int[] { 1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }));

        return res;
    }
}
