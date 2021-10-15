package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.RenderWorldEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.utils.shader.Aqua;
import mint.utils.shader.FrameBuffer;
import mint.utils.shader.Glow;
import mint.utils.shader.Red;
import net.minecraft.entity.Entity;

import java.awt.*;

/**
 * @author kambing
 * inspired by europa
 */
public class ShaderChams extends Module {
    public ShaderChams() {
        super("Shader Chams" , Category.VISUAL , "shiny..");
    }
    public Setting<Mode> mode = register(new Setting("Mode", Mode.AQUA));
    public enum Mode {AQUA, RED, GLOW}

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        final FrameBuffer shader = mode.getValue().equals(Mode.AQUA)
                ? Aqua.AQUA_SHADER : mode.getValue().equals(Mode.RED)
                ? Red.RED_SHADER : mode.getValue().equals(Mode.GLOW) ? Glow.Glow_SHADER :
                null;

        if (shader == null)
            return;

        shader.startDraw(event.getPartialTicks());

        try {
            for (Entity entity : mc.world.loadedEntityList) {
                mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
            }
        } catch (Exception l) {
            MessageManager.sendMessage("cannot render entity");
        }

        shader.stopDraw(new Color(255, 255, 255), 1F, 1F);
    }
}
