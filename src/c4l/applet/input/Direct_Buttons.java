package c4l.applet.input;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Properties;
import java.util.stream.Collectors;

import c4l.applet.device.Device;
import c4l.applet.main.Constants;
import c4l.applet.scenes.State;

/**
 * Class to handle direct buttons, that trigger specific actions from the hardware-controller.
 * 
 * @author Timon
 *
 */
public class Direct_Buttons {
	public enum Direct_Button_Action {
		/** Clear all Effects on active devices */ 			FX_CLEAR,
		/** Tap Effect speed for active devices */			TAP_BPM,
		/** Deactivate  all devices */						CLEAR_ACTIVE,
		/** Freeze current output while pushed */ 			FREEZE,
		/** Trigger next scene of a chase */				STEP_CHASE,
		/** Output 255 on certain channels while pushed */	FLASH
	}
	
	/** Reference to state to manipulate it */
	State state;
	/** Reference to input to manipulate it */
	Input input;
	
	//Real member variables
	/** Number of direct buttons */
	private int size;
	/** Array of functionalities for the direct buttons */
	private Direct_Button_Action types[];
	/** May contain some additional info needed for a specific functionality of the direct button
	 * 
	 * Each button gets one list, for functionalities without further specifications this list is empty*/
	private LinkedList<Integer>[] info;
	/** holding last pushed-values */
	private boolean[] old_pushed;
	
	//Constructor
	/**
	 * Main constructor for Direct-Buttons-Class
	 * @param state		State, which shall be manipulated by this buttons
	 * @param input		Input, which shall be manipulated by this buttons
	 * @param count		Number of direct buttons
	 * @param types		What these buttons do (array of length count)
	 * @param info		possibly further specifications (array of length count, empty lists for easy functionalities)
	 */
	public Direct_Buttons(State state, Input input, int count, Direct_Button_Action types[], LinkedList<Integer>[] info) {
		this.state = state;
		this.input = input;
		this.size = count;
		this.types = types;
		this.info = info;
		
		old_pushed = new boolean[size];
	}
	/**
	 * Constructs object from a property-file instead of arrays
	 * @param state		State, which shall be manipulated by this buttons
	 * @param input		Input, which shall be manipulated by this buttons
	 * @param prop		Property-object holding all the other information
	 * @return			new Direct_Button-object (the constructor is called internally after properties are parsed
	 */
	public static Direct_Buttons Direct_Buttons_fromProp(State state, Input input, Properties prop) {
		int count = Integer.parseInt(prop.getProperty("NUM", "8"));
		Direct_Button_Action types[] = new Direct_Button_Action[count];
		@SuppressWarnings("unchecked")
		LinkedList<Integer>[] info = new LinkedList[count]; 
		for (int i = 0; i < count; i++) {
			types[i] = Direct_Button_Action.valueOf(prop.getProperty(("BUTTON"+i)));
			String s = prop.getProperty("INFO"+i);
			if ((s != "") && (s != null)) {
				//Convert String (like "1,23,4") to LinkedList<Integer>
				info[i] = (LinkedList<Integer>) Arrays.asList(s.split(",")).stream().map(String::trim).mapToInt(Integer::parseInt).mapToObj(Integer::valueOf).collect(Collectors.toCollection(LinkedList::new));
			} else {
				info[i] = new LinkedList<Integer>();
			}
		}
		return new Direct_Buttons(state, input, count, types, info);
	}
	
	//Other functions
	public void update(boolean[] pushed) {
		boolean[] flash = new boolean[512]; //flashing is accumulated for (possibly multiple) flash-buttons and then send to state after the for-loop
		
		for (int i = 0; i < Math.max(size, pushed.length); i++) {
			if (pushed[i] != old_pushed[i]) {
				switch (types[i]) {
				case FX_CLEAR:
					if (pushed[i]) {
						boolean active[] = input.getActive();
						Device d;
						for (int j = 0; j < Constants.DYNAMIC_DEVICES; j++)
							if (active[j]) {
								d = state.getDevice(j);
								d.clearEffect();
								d.clearMainEffect();
							}
					}
					break;
				case TAP_BPM:
					//TODO: Implement
					break;
				case CLEAR_ACTIVE:
					if (pushed[i]) input.clearActive();
					break;
				case FREEZE:
					state.setFreezed(pushed[i]);
					break;
				case STEP_CHASE:
					if (pushed[i]) {
						//TODO: Implement
					}
					break;
				case FLASH:
					if (pushed[i])
						for (ListIterator<Integer> it = info[i].listIterator(); it.hasNext(); flash[it.next()] = true);
					break;
				} /* switch */
			} /* if pushed changed */
		} /* for */
		try {
			state.flash(flash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		old_pushed = pushed.clone();
	} /* update() */
}
