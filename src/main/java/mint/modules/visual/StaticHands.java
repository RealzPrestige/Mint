package mint.modules.visual;

import mint.Mint;
import mint.modules.Module;
import net.minecraft.util.EnumHand;


public class StaticHands extends Module {


    public StaticHands() {
        super("Static Hands", Category.VISUAL, "removes the bob animation when swinging");
    }
//MINT.INSTANCE.MC IS SO CHINESE
    public void onUpdate() {
        if (Mint.INSTANCE.mc.world == null)
            return;
        Mint.INSTANCE.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand = 1.0f;
        Mint.INSTANCE.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
        Mint.INSTANCE.mc.entityRenderer.itemRenderer.equippedProgressOffHand = 1.0f;
        Mint.INSTANCE.mc.entityRenderer.itemRenderer.itemStackMainHand = Mint.INSTANCE.mc.player.getHeldItem(EnumHand.MAIN_HAND);
        Mint.INSTANCE.mc.entityRenderer.itemRenderer.itemStackOffHand = Mint.INSTANCE.mc.player.getHeldItem(EnumHand.OFF_HAND);

    }
}