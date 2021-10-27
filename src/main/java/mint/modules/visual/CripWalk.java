package mint.modules.visual;

import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mint.events.RenderWorldEvent;

public class CripWalk extends Module {

    public CripWalk(){
        super("Crip Walk",Category.Visual,"Cancels movement animations.");
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if(NullUtil.fullNullCheck())
            return;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getName() == mc.player.getName())
                continue;
            player.limbSwing = 0;
            player.limbSwingAmount = 0;
            player.prevLimbSwingAmount = 0;
            player.rotationYawHead = 0;
            player.rotationPitch = 0;
            player.rotationYaw = 0;
        }
    }
}
