package mint.modules.miscellaneous;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.entity.item.EntityBoat;

public class BoatPhase extends Module {

    public BoatPhase() {
        super("BoatPhase", Module.Category.MISCELLANEOUS, "Phases your boat into blocks.");
    }

    public Setting<Boolean> noClip = register(new Setting("NoClip", true));
    public Setting<Boolean> onGround = register(new Setting("OnGround", false));
    public Setting<Boolean> cancelGravity = register(new Setting("CancelGravity", true));
    public Setting<Boolean> cancelMotion = register(new Setting("CancelMotion", true));


    @Override
    public void onUpdate() {
        if (!(mc.player.ridingEntity instanceof EntityBoat)) {
            return;
        }
        mc.player.noClip = noClip.getValue();
        mc.player.ridingEntity.noClip = noClip.getValue();
        mc.player.onGround = onGround.getValue();
        mc.player.ridingEntity.setNoGravity(cancelGravity.getValue());
        if (cancelMotion.getValue()) {
            mc.player.motionY = 0.0f;
            mc.player.ridingEntity.motionY = 0.0f;
        }
    }
}