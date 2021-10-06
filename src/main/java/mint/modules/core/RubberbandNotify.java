package mint.modules.core;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kambing
 * 30.9.2021
 */
public class RubberbandNotify extends Module {
    public RubberbandNotify() {
        super("Rubber Band Notify", Category.CORE, "Notify rubberbands.");
    }
    //@TODO : make a hud module? hud editor or sumn?
    HashMap<String, Integer> rubberbandString = new HashMap<>();
    private final Timer timer = new Timer();
    float seconds;

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            timer.reset();
            rubberbandString.put("Rubberband detected! ", 255);
            seconds = ((System.currentTimeMillis() - this.timer.getTime()) / 1000.0f) % 60.0f;
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        for (Map.Entry<String, Integer> entry : rubberbandString.entrySet()) {
            rubberbandString.put(entry.getKey(), (int) (entry.getValue() - 0.25));
            if (entry.getValue() <= 0) {
                rubberbandString.remove(entry.getKey());
                return;
            }
            renderer.drawStringWithShadow(ChatFormatting.RED + entry.getKey() + ChatFormatting.WHITE + "(" + seconds + ")", (screenWidth / 2f) - (renderer.getStringWidth(entry.getKey() + seconds + ")") / 2f), 0, ColorUtil.toRGBA(255, 255, 255, entry.getValue()));
        }
    }
}
