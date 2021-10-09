package mint.mixins;

import mint.modules.core.NoPotionHud;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public class MixinGuiIngame {

    @Inject(method = {"renderPotionEffects"}, at = {@At("HEAD")}, cancellable = true)
    protected void renderPotionEffectsHook(final ScaledResolution scaledResolution, final CallbackInfo callbackInfo) {
        if (NoPotionHud.getInstance().isEnabled()) {
            callbackInfo.cancel();
        }
    }
}
