/*
 * klasse zur abfrage der DB alle select funcktzionen
 * 
 * AS: erstellt die klasse und baut eine Verknüpfung mit dbCreate und kopiert die select 
 * funcktzionen aus database.class
 *  
 *  14.05.18 11:50 Anpassend der KLasse für die Holder Klasse
 *  
 */

package c4l.applet.db;


//import constant.constant;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;

public class Select {
	
	private Create dbCreate = new Create();
	private  Connection conn = null;
	public static String[] felderSDschueler = {"ID","Name","Vorname","Klasse","Telephone"};
	//private  Logger dLogger = dbCreate.initLogger();
	

	/**
	 * giebt den anwesenheits status zurück
	 * @param Name
	 * @param Vorname
	 * @return anwesend oder nicht vorallem ob er anwesend ist 
	 */
//	public  String getSchuelerAnwesend(String Name ,String Vorname){
//		dLogger.log(Level.INFO, "getSchuelerAnwesend");
//		String[] felder = {"Vorname","Name","Anwesend"}; // mus warscheinlich noch klasse rein
//		String SQL = " SELECT sdSchueler.Vorname,sdSchueler.Name, anwesend.Anwesend"+
//				" FROM sdSchueler"+
//				" INNER JOIN anwesend ON sdSchueler.ID = anwesend.SusID AND sdSchueler.Vorname = '" + Vorname + "' AND sdSchueler.Name = '"+ Name +"'"+
//				" ORDER BY sdSchueler.Vorname";
//		dLogger.log(Level.FINER, SQL);
//		return getDbData(SQL,felder);
//		
//	}
//	
//	/**
//	 * giebt eine klassen liste aus der DB zurück
//	 * @param Klasse
//	 * @return name vorname satus der anwesenheit in einem json string
//	 */
//	public String getKlasseAnwesend(String Klasse){
//		
//		dLogger.log(Level.INFO, "getKlasseAnwesend");
//		String[] felder = {"Vorname","Name","Anwesend","ID"};
//		String SQL = " SELECT sdSchueler.Vorname,sdSchueler.ID,sdSchueler.Name, anwesend.Anwesend"+
//				" FROM sdSchueler"+
//				" INNER JOIN anwesend ON sdSchueler.ID = anwesend.SusID AND sdSchueler.Klasse = '"+Klasse+"'"+ 
//				" ORDER BY sdSchueler.Vorname";
//		dLogger.log(Level.FINER, SQL);
//		return getDbData(SQL,felder);
//	}
//	
//	/**
//	 *  abfrage von Schüler Stammdaten mit beiden oder nur einem parameter
//	 *  
//	 * @param name
//	 * @param vorname
//	 * @return String mit den werten aus der DB in Json String
//	 */
//	public String getSchueler(String name, String vorname){
//		dLogger.log(Level.INFO, "getSchueler");
//		String SQL = null;
//		if(name.equals("") && (!vorname.equals(""))){
//			SQL =  "SELECT * FROM "+ constant.TSDSchueler + " where Vorname ='"+vorname+"'";  // Ungetestet
//		}else if((!name.equals("")) && vorname.equals("")){
//			SQL =  "SELECT * FROM "+ constant.TSDSchueler + " where Name ='"+name+"'";
//		}else{
//			SQL =  "SELECT * FROM "+ constant.TSDSchueler + " where Name ='"+name+"' AND Vorname ='"+vorname+"'";
//		}
//		dLogger.log(Level.FINER, SQL);
//		return getDbData(SQL, constant.felderSDschueler);
//	}
//	
//	
	
	public String scene(int id) {
		String SQL =  "SELECT payload FROM "+ "scenes" + " where scenenID ='"+id+"'";
		String[] a = {"payload"};
		String answer = getDbData(SQL, a ); // return an JSON array
		return answer.substring(1, answer.length()-1); 
	}
	
	
	
	
	/**
	 * zum ausführen von SQL auf die Datenbank übergeben würde aus das benötigte SQL 
	 * und aus den Konstanten werden die Felder übergeben
	 * @param SQL
	 * @param felder
	 * @return
	 */
	private String getDbData(String SQL,String[] felder )
	{
		try{
		conn = dbCreate.getInstance();
		
						
		if( conn != null)
		{
			Statement query = conn.createStatement();
		//	dLogger.log(Level.FINE, SQL);
		    ResultSet result = query.executeQuery(SQL);
		    
		    JSONArray allAnswers = new JSONArray();
		    while (result.next()) { 
		    	 HashMap<String, String> answer = new HashMap<>();
				  for( String feld : felder)
				   {
					  answer.put(feld, result.getString(feld));
				   }
				  allAnswers.put(answer);
		    }
	//	    dLogger.log(Level.FINER, allAnswers.toString());
	        return allAnswers.toString();
		}else{
		//	dLogger.log(Level.SEVERE, "conn wurde nicht richitg initzalisiert");
		}
		} catch( SQLException e){
		//	dLogger.log(Level.SEVERE, "Fehler 1 in getDbData " + e.toString());
			
		}
//		dLogger.log(Level.SEVERE, "Fehler 2 in getDbData");
		return null;

	}
	

}
