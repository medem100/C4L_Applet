package c4l.applet.db;

import c4l.applet.device.Device;
import c4l.applet.device.Effect;
import c4l.applet.device.Effect_ID;
import c4l.applet.scenes.Device_Setup;

import java.util.ArrayList;
import java.util.List;

public class DeviceStateSaveModel {
    Device_Setup setup;
    List<EffectStateSaveModel> effectStateSaveModels;
    int[] input;

    Effect_ID mainEffectId = null; // no main Effect
    int mainEffectSize;
    int mainEffectSpeed;
    int mainEffectState;
    boolean mainEffectAcceptInput;
    int[] mainEffectChannels;

    public DeviceStateSaveModel(Device device) {
        this.setup = device.getSetup();
        effectStateSaveModels = new ArrayList<>();
        for (Effect effect : device.effects) {
            effectStateSaveModels.add(mapEffect(effect));
        }
        this.input = device.getInputs();
        if (device.main_effect.size() == 1) {
            this.mainEffectId = Effect_ID.getEffectID(device.main_effect.get(0));
            this.mainEffectSize = device.main_effect.get(0).getSize();
            this.mainEffectSpeed = device.main_effect.get(0).getSpeed();
            this.mainEffectState = device.main_effect.get(0).getState();
            this.mainEffectAcceptInput = device.main_effect.get(0).isAcceptInput();
            this.mainEffectChannels = device.main_effect.get(0).getChannels();

        } else if (device.main_effect.size() > 1) {
            throw new UnsupportedOperationException(
                    "It ist only support to save Devices with 0 or" + " 1 main Effect");
        }

    }

    public Device toDevice() {
        Device res = new Device(setup);
        res.setInputs(input);
        // I don't know why, but the effects don't "start" unless you assemble them by hand.
        if (mainEffectId != null) {
            Effect effect =
                    Effect_ID.generateEffectFromID(mainEffectId, mainEffectSize, mainEffectSpeed,
                            mainEffectState, mainEffectAcceptInput, mainEffectChannels);
            res.main_effect.add(0, effect);
        }

        if (effectStateSaveModels != null) {
            for (EffectStateSaveModel effectStateSaveModel : effectStateSaveModels) {
                res.addEffect(Effect_ID.generateEffectFromID(effectStateSaveModel.getEffectId(),
                        effectStateSaveModel.getSize(), effectStateSaveModel.getSpeed(),
                        effectStateSaveModel.getOffset(), effectStateSaveModel.isAcceptInput(),
                        effectStateSaveModel.getChannels()));
            }
        }

        return res;
    }

    public EffectStateSaveModel mapEffect(Effect effect) {
        return new EffectStateSaveModel(Effect_ID.getEffectID(effect), effect.getSize(),
                effect.getSpeed(), effect.getState(), effect.isAcceptInput(), effect.getChannels());
    }

}
