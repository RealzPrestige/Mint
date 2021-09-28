package mint.modules.visual;

import mint.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CripWalk extends Module {

    public CripWalk(){
        super("CripWalk",Category.VISUAL,"cancels movement animations");
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
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
