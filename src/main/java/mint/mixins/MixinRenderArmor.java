package mint.mixins;

import mint.modules.visual.ArmorRemover;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {LayerBipedArmor.class})
public abstract class MixinRenderArmor {

    @Shadow
    protected abstract void setModelVisible(ModelBiped var1);

    /**
     * @author zPrestige_ (idk for some reason @Overwrite needs an @Author)
     **/

    @Overwrite
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn) {
        setModelVisible(p_188359_1_);
        switch (slotIn) {
            case HEAD:
                p_188359_1_.bipedHead.showModel = !ArmorRemover.getInstance().isEnabled();
                p_188359_1_.bipedHeadwear.showModel = !ArmorRemover.getInstance().isEnabled();
                break;
            case CHEST:
                p_188359_1_.bipedBody.showModel = !ArmorRemover.getInstance().isEnabled();
                p_188359_1_.bipedRightArm.showModel = !ArmorRemover.getInstance().isEnabled();
                p_188359_1_.bipedLeftArm.showModel = !ArmorRemover.getInstance().isEnabled();
                break;
            case LEGS:
                p_188359_1_.bipedBody.showModel = !ArmorRemover.getInstance().isEnabled();
                p_188359_1_.bipedRightLeg.showModel = !ArmorRemover.getInstance().isEnabled();
                p_188359_1_.bipedLeftLeg.showModel = !ArmorRemover.getInstance().isEnabled();
                break;
            case FEET:
                p_188359_1_.bipedRightLeg.showModel = !ArmorRemover.getInstance().isEnabled();
                p_188359_1_.bipedLeftLeg.showModel = !ArmorRemover.getInstance().isEnabled();
        }
    }
}

