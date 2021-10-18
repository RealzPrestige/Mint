package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.NullUtil;

public class AntiWeb extends Module {

    public AntiWeb() {
        super("AntiWeb", Module.Category.MOVEMENT, "Fall down faster in webs.");
    }

    public Setting<Mode> mode = register(new Setting("Mode", Mode.Speed));

    public enum Mode {Speed, Cancel}

    public Setting<Float> speed = register(new Setting("Speed", 1.0f, 0.1f, 50.0f));

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (mode.getValue().equals(Mode.Speed))
            if (mc.player != null && mc.player.isInWeb && !mc.player.onGround && mc.gameSettings.keyBindSneak.isKeyDown())
                mc.player.motionY *= speed.getValue();

        if (mode.getValue().equals(Mode.Cancel))
            if (mc.player.isInWeb)
                mc.player.isInWeb = false;
    }
}