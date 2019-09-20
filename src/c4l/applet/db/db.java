/*
 * Singeltan Pattern für die DB um ei einziges object zur komunikatzion mit der DB zu erzeugen
 * 
 */
package c4l.applet.db;

import c4l.applet.db.*;

public class db {
	
	private static db OBJ = getInstance(); 
	public Select Select = new Select();
	public Insert Insert = new Insert();
	//public Update Update = new Update();
	public Create Create = new Create();
	
     private db() { 
        
     } 
     
     public static synchronized db getInstance() {
    	 if (db.OBJ == null) {
    		 db.OBJ = new db();
    	    }
    	    return db.OBJ;
     };
     
     /*public static db getInstance() { 
       return OBJ; 
     } */

}
