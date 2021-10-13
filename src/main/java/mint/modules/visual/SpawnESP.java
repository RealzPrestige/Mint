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
 * @author FB for Y-travel
 **/

public class SpawnESP extends Module {
    public SpawnESP() {
        super("Spawn ESP", Category.VISUAL, "Renders circles around recently spawned end crystals.");
    }

    HashMap<Circle, Integer> circlesToFade = new HashMap<>();
    Setting<Boolean> otherParent = register(new Setting<>("Other", true, false));
    Setting<Float> travelSpeed = register(new Setting<>("Travel Speed", 1.0f, 0.0f, 100.0f, v-> otherParent.getValue()));
    Setting<Integer> fadeSpeed = register(new Setting<>("Fade Speed", 5, 0, 10, v-> otherParent.getValue()));
    Setting<Boolean> colorParent = register(new Setting<>("Color", true, false));
    Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255, v-> colorParent.getValue()));
    Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, v-> colorParent.getValue()));
    Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v-> colorParent.getValue()));

    float heaven;
    long startTime = 0;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51) {
                if (mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) > 10)
                    return;
                circlesToFade.put(new Circle(new BlockPos(packet.getX(), packet.getY(), packet.getZ()), 0), 255);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (circlesToFade.size() > 8) {
            circlesToFade.clear();
        }
    }

    /**
     * note for @zPrestige
     * <p>
     * everything works smoothly but the circle render moves
     * awkwardly for some reason (wp3 renders never fails to amaze me)
     * old one works just fine i think its because it uses int idk lol
     * - kambing :D
     */

    // ok

    @Override
    public void onRender3D(Render3DEvent event) {
        if(fullNullCheck())
            return;

        for (Map.Entry<Circle, Integer> entry : circlesToFade.entrySet()) {
            entry.getKey().height = entry.getKey().height + 1;
            circlesToFade.put(entry.getKey(), entry.getValue() - (fadeSpeed.getValue()));
            startTime = System.currentTimeMillis();
            heaven = ((System.currentTimeMillis() - startTime) / 100f);
            if (entry.getValue() <= 1) {
                circlesToFade.remove(entry.getKey());
                return;
            }

            RenderUtil.drawCircle(entry.getKey().getPos().getX(), entry.getKey().getHeight() + (heaven/ 10 ), entry.getKey().getPos().getZ(), 0.6f, new Color(red.getValue(), green.getValue(), blue.getValue(), entry.getValue()));

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
