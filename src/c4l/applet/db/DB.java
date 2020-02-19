/*
 * Singeltan Pattern für die DB um ei einziges object zur komunikatzion mit der DB zu erzeugen
 * 
 */
package c4l.applet.db;

import java.sql.SQLException;

import org.apache.log4j.Logger;


public class DB {
	
	private static DB OBJ = getInstance(); 
	public Select Select = new Select();
	public Insert Insert = new Insert();
	public Update Update = new Update();
	public Create Create = new Create();
	public Delete Delete = new Delete();
	//static Logger logger = Logger.getLogger(DB.class);
     private DB() { 
    	
       
     } 
     
     public static synchronized DB getInstance() {
    	 if (DB.OBJ == null) {
    		// logger.debug("Create new DB objeckt");
    		 DB.OBJ = new DB();
    	    }
    	    return DB.OBJ;
     };
     
     /*public static db getInstance() { 
       return OBJ; 
     } */

}
