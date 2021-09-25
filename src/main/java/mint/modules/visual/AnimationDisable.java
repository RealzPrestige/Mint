package mint.modules.visual;

import mint.modules.Module;
import net.minecraft.entity.player.EntityPlayer;

public class AnimationDisable extends Module {

    public AnimationDisable(){
        super("CripWalk",Category.VISUAL,"cancels movement animations");
    }
    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getName().equals(mc.player.getName())) return;
            player.limbSwing = 0;
            player.limbSwingAmount = 0;
            player.prevLimbSwingAmount = 0;
            player.rotationYawHead = 0;
            player.rotationPitch = 0;
            player.rotationYaw = 0;
        }
    }
}
