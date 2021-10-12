package mint.mixins;


import mint.modules.visual.SwingAnimations;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@TODO: refactor this to MixinEntityLivingBase
@Mixin({EntityLivingBase.class})
public class MixinToSlowUrHand {
    SwingAnimations swingAnimations = new SwingAnimations();
    @Inject(method = {"getArmSwingAnimationEnd"}, at = {@At("HEAD")}, cancellable = true)
    private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> paramCallbackInfoReturnable) {
        if (swingAnimations.isEnabled() && swingAnimations.speed.getValue())
            paramCallbackInfoReturnable.setReturnValue(swingAnimations.amplifier.getValue());
    }
}
