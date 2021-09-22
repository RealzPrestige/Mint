package mint.mixins;


import mint.events.RenderEntityModelEvent;
import mint.modules.visual.CrystalChanger;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {RenderEnderCrystal.class})
public class MixinRenderModifiedCrystal {
    @SubscribeEvent
    @Redirect(method={"doRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModel(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CrystalChanger.getInstance().isEnabled()) {
            GlStateManager.scale(CrystalChanger.getInstance().scale.getValue().floatValue(), CrystalChanger.getInstance().scale.getValue().floatValue(), CrystalChanger.getInstance().scale.getValue().floatValue());
        }
        if (CrystalChanger.getInstance().isEnabled() && CrystalChanger.getInstance().wireframe.getValue()) {
            RenderEntityModelEvent event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            CrystalChanger.getInstance().onRenderModel(event);
        }
        if (CrystalChanger.getInstance().isEnabled() && CrystalChanger.getInstance().chams.getValue()) {
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            if (CrystalChanger.getInstance().throughwalls.getValue()) {
                if (CrystalChanger.getInstance().throughwalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(((float) CrystalChanger.getInstance().wallsRed.getValue() / 255.0f), ((float) CrystalChanger.getInstance().wallsGreen.getValue() / 255.0f), (float) CrystalChanger.getInstance().wallsBlue.getValue() / 255.0f, (float) CrystalChanger.getInstance().wallsAlpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalChanger.getInstance().throughwalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
                GL11.glColor4f((float) CrystalChanger.getInstance().red.getValue() / 255.0f, (float) CrystalChanger.getInstance().green.getValue() / 255.0f, (float) CrystalChanger.getInstance().blue.getValue() / 255.0f, (float) CrystalChanger.getInstance().alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            } else {
                if (CrystalChanger.getInstance().throughwalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }
                GL11.glEnable(10754);
                GL11.glColor4f(((float) CrystalChanger.getInstance().red.getValue() / 255.0f), ((float) CrystalChanger.getInstance().green.getValue() / 255.0f), ((float) CrystalChanger.getInstance().blue.getValue() / 255.0f), (float) CrystalChanger.getInstance().alpha.getValue() / 255.0f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (CrystalChanger.getInstance().throughwalls.getValue()) {
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                }
            }
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (CrystalChanger.getInstance().isEnabled()) {
            GlStateManager.scale((1.0f / CrystalChanger.getInstance().scale.getValue().floatValue()), (1.0f / CrystalChanger.getInstance().scale.getValue().floatValue()), 1.0f / CrystalChanger.getInstance().scale.getValue().floatValue());
        }
        if (CrystalChanger.getInstance().glow.getValue()) {
            entity.glowing = true;
        }
    }
}