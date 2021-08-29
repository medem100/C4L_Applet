package c4l.applet.db;

import c4l.applet.device.Device;
import c4l.applet.device.Effect;
import c4l.applet.device.Effect_ID;
import c4l.applet.scenes.Device_Setup;

import java.util.List;

public class DeviceStateSaveMoodle {
    Device_Setup setup;
    List<Effect> effects;
    int[] input;

    Effect_ID mainEffectId = null; // no main Effect
    int mainEffectSize;
    int mainEffectSpeed;
    int mainEffectState;
    boolean mainEffectAcceptInput;
    int[] mainEffectChannels;

    public DeviceStateSaveMoodle(Device device){
        this.setup = device.getSetup();
        this.effects = device.effects;
        this.input = device.getInputs();
        if(device.main_effect.size() == 1 ){
            this.mainEffectId = Effect_ID.getEffectID(device.main_effect.get(0));
            this.mainEffectSize = device.main_effect.get(0).getSize();
            this.mainEffectSpeed = device.main_effect.get(0).getSpeed();
            this.mainEffectState = device.main_effect.get(0).getState();
            this.mainEffectAcceptInput = device.main_effect.get(0).isAcceptInput();
            this.mainEffectChannels = device.main_effect.get(0).getChannels();

        }else if(device.main_effect.size() > 1){
            throw new UnsupportedOperationException("It ist only support to save Devices with 0 or"
                    + " 1 main Effect");
        }

    }

    public Device toDevice(){
        Device res = new Device(setup);
        res.setInputs(input);
        if(mainEffectId != null){
            Effect effect = Effect_ID.generateEffectFromID(mainEffectId,mainEffectSize,
                    mainEffectSpeed, mainEffectState, mainEffectAcceptInput, mainEffectChannels);
            res.main_effect.add(0, effect);
        }

        return  res;
    }

}
