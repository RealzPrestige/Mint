package mint.mixins;

import mint.modules.visual.ViewTweaks;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author kambing
 * 4/10/2021
 */

@Mixin(value = {RenderItem.class}, priority=0x7FFFFFFE)
public abstract class MixinItemRenderer {
    @Inject(method = { "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V" }, at = { @At("INVOKE") })
    public void renderItem(final ItemStack stack, final EntityLivingBase entitylivingbaseIn, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        if (ViewTweaks.getInstance().glow.getValue()) {
            entitylivingbaseIn.glowing = true;
        }else{
            entitylivingbaseIn.glowing = false;
        }
    }
}
