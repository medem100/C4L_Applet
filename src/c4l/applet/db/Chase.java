package c4l.applet.db;

import java.util.Arrays;

/**
 * Represant an chase
 * @author andre
 *
 */
public class Chase {
	
	private final Scene[] SCENES;
	private final int[] FADETIMES, SHOWTIMES;
	private final String NAME , DESCRIPTION ;
	private final int ID,SETUPID;
	
	/**
	 * 
	 * @param Scenes
	 * @param FadeTimes
	 * @param ShowTimes
	 * @param Name
	 * @param Description
	 * @param iD
	 * @param Setupid
	 */
	public Chase(Scene[] Scenes, int[] FadeTimes, int[] ShowTimes, String Name, String Description, int iD,
			int Setupid) {
		super();
		SCENES = Scenes;
		FADETIMES = FadeTimes;
		SHOWTIMES = ShowTimes;
		NAME = Name;
		DESCRIPTION = Description;
		ID = iD;
		SETUPID = Setupid;
	}


	public Scene[] getSCENES() {
		return SCENES;
	}


	public int[] getFADETIMES() {
		return FADETIMES;
	}


	public int[] getSHOWTIMES() {
		return SHOWTIMES;
	}


	public String getNAME() {
		return NAME;
	}


	public String getDESCRIPTION() {
		return DESCRIPTION;
	}


	public int getID() {
		return ID;
	}


	public int getSETUPID() {
		return SETUPID;
	}


	@Override
	public String toString() {
		return "Chase [SCENES=" + Arrays.toString(SCENES) + ", FADETIMES=" + Arrays.toString(FADETIMES) + ", SHOWTIMES="
				+ Arrays.toString(SHOWTIMES) + ", NAME=" + NAME + ", DESCRIPTION=" + DESCRIPTION + ", ID=" + ID
				+ ", SETUPID=" + SETUPID + "]";
	}
	
	
	
	

}
