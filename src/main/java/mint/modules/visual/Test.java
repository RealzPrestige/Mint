package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.RenderUtil;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kambing
 * inspired by fbware circle render
 */
public class Test extends Module {
    public Test() {
        super("Test", Category.VISUAL, "Test");
    }
    public HashMap<Circle, Integer> circlesToFade = new HashMap<>();
    Setting<Integer> extension = register(new Setting("Extension", 1, 0, 255));
    Setting<Integer> fadeSpeed = register(new Setting<>("Fade Speed", 5, 0, 10));
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                if (mc.player.getDistance(packet.getX(),packet.getY(),packet.getZ()) > 10)return;
                circlesToFade.put(new Circle(new BlockPos(packet.getX(),packet.getY(),packet.getZ()), 0) , 255);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (circlesToFade.size() > 8) {
            circlesToFade.clear();
        }
    }

    /** note for @zPrestige
     *
     * everything works smoothly but the circle render moves
     * awkwardly for some reason (wp3 renders never fails to amaze me)
     * old one works just fine i think its because it uses int idk lol
     * - kambing :D
     */
    @Override
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null)
            return;
        for (Map.Entry<Circle, Integer> entry : circlesToFade.entrySet()) {
            entry.getKey().height = entry.getKey().height + 1;
            circlesToFade.put(entry.getKey(), entry.getValue() - (fadeSpeed.getValue()));
            if (entry.getValue() <= 1) {
                circlesToFade.remove(entry.getKey());
                return;
            }

            RenderUtil.drawCircle(entry.getKey().getPos().getX(), entry.getKey().getPos().getY() + entry.getKey().getHeight(), entry.getKey().getPos().getZ(), 0.6f, new Color(255,255,255,entry.getValue()));

        }
    }
    static class Circle {
        BlockPos pos;
        public int height;

        public Circle(BlockPos pos, int height) {
            this.pos = pos;
            this.height = height;
        }

        public BlockPos getPos() {
            return pos;
        }

        public int getHeight() {
            return height;
        }
    }
}
