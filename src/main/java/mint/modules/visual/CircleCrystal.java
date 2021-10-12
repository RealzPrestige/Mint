package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CircleCrystal extends Module {
    public Setting<Integer> range = register(new Setting("Range", 200, 0, 250));
    public Setting<Integer> travelSpeed = register(new Setting("Travel Speed", 10, -100, 100));
    public Setting<Float> radius = register(new Setting("Radius", 0.6f, 0.0f, 1.0f));
    public Setting<Boolean> crystalPlace = register(new Setting("Place", false));
    public Setting<Boolean> crystalBreak = register(new Setting("Break", false));

    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));

    public Setting<Boolean> fade = register(new Setting<>("Fade", false, false));
    public Setting<Integer> startAlpha = register(new Setting<>("Start Alpha", 255, 0, 255, v -> fade.getValue()));
    public Setting<Integer> endAlpha = register(new Setting<>("End Alpha", 0, 0, 255, v -> fade.getValue()));
    public Setting<Integer> fadeSpeed = register(new Setting<>("Fade Speed", 20, 0, 100, v -> fade.getValue()));


    HashMap<BlockPos, Integer> crystals = new HashMap<>();

    public CircleCrystal(){
        super("Circle Crystal", Category.VISUAL, "renders circles on crystals roflkeksaucecopter.");
    }

    public void onRender3D(Render3DEvent event){
        //TODO: fix fade, make it travel
        for (Map.Entry<BlockPos, Integer> entry : crystals.entrySet()) {
            crystals.put(entry.getKey(), entry.getValue() - (fadeSpeed.getValue() / 10));

            if (entry.getValue() <= endAlpha.getValue()) {
                crystals.remove(entry.getKey());
                return;
            }

            RenderUtil.drawCircle(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ(), radius.getValue(), new Color(red.getValue(), green.getValue(), blue.getValue(),entry.getValue()));
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if(!isEnabled())
            return;

        if (event.getPacket() instanceof SPacketSoundEffect && crystalBreak.getValue()) {
            SPacketSoundEffect packet = event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal) {
                        BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
                        if(mc.player.getDistance(entity) > range.getValue())
                            continue;

                        crystals.put(pos, startAlpha.getValue());
                    }
                }
            }
        }
        if (event.getPacket() instanceof SPacketSpawnObject && crystalPlace.getValue()) {
            SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51){
                BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getY());

                if(mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < range.getValue())
                    crystals.put(pos, startAlpha.getValue());
            }
        }
    }
}

