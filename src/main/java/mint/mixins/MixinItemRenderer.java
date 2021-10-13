package mint.mixins;

import mint.events.RenderItemEvent;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 * @author zPrestige_
 * 13/10/2021
 *
 */

@Mixin(value = {RenderItem.class})
public abstract class MixinItemRenderer {

    @Inject(method = {"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"}, at = {@At("INVOKE")})
    public void renderItem(final ItemStack stack, final EntityLivingBase entityLivingBaseIn, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            if (transform.equals(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND))
                MinecraftForge.EVENT_BUS.post(new RenderItemEvent.Offhand(stack));
            else
                MinecraftForge.EVENT_BUS.post(new RenderItemEvent.MainHand(stack));
        }
    }
}
