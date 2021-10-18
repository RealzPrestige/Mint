package mint.modules.movement;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.NullUtil;
import net.minecraft.network.play.client.CPacketPlayer;

public class Clip extends Module {

    public Clip() {
        super("Clip", Module.Category.MOVEMENT, "VClip bypass for crystalpvp cc.");
    }

    public Setting<Float> offset = register(new Setting("Y Offset", 1.4f, 1.1f, 2.0f));
    public Setting<Boolean> offground = register(new Setting("Offground", false));

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            disable();
            return;
        }
        if (mc.player.posY < 6 && !mc.player.isElytraFlying() && mc.player.onGround) {
            EntityUtil.startSneaking();
            EntityUtil.packetJump(offground.getValue());
            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, ((mc.player.posY + offset.getValue()) * -1), mc.player.posZ, offground.getValue()));
            EntityUtil.stopSneaking(false);
            disable();
        }
    }
}