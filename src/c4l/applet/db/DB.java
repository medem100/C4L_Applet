/*
 * Singeltan Pattern für die DB um ei einziges object zur komunikatzion mit der DB zu erzeugen
 * 
 */
package c4l.applet.db;

import java.net.http.HttpClient;

import com.google.gson.Gson;

public class DB {

	private static final HttpClient CLIENT = HttpClient.newHttpClient(); ;
	private static final Gson GSON = new Gson();
	private static DB OBJ = getInstance();
	public Select Select = new Select(CLIENT, GSON);
	public Insert Insert = new Insert(CLIENT, GSON);
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

     protected HttpClient getCLIENT(){
     	return CLIENT;
	 }

	 protected Gson getGSON(){
     	return GSON;
	 }
     
     /*public static db getInstance() { 
       return OBJ; 
     } */

}
