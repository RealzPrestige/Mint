package mint.modules.movement;

import mint.setting.Setting;
import mint.events.EntityCollisionEvent;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module {
    public Setting<Boolean> entityVelocity = register(new Setting<>("Entity Velocity", false));
    public Setting<Boolean> entityCollisionPush = register(new Setting<>("Entity Push Collision", false));
    public Setting<Boolean> entityCollisionBlock = register(new Setting<>("Entity Block Collision", false));
    public Setting<Boolean> webCollision = register(new Setting<>("Entity Web Collision", false));
    public Setting<WebCollisionMode> webCollisionMode = register(new Setting<>("Entity Web Collision Mode", WebCollisionMode.Speed, z -> webCollision.getValue()));

    public enum WebCollisionMode {Speed, Cancel}

    public Setting<Float> webCollisionSpeed = register(new Setting<>("Web Speed", 1.0f, 0.1f, 50.0f, z -> webCollisionMode.getValue().equals(WebCollisionMode.Speed)));

    public Velocity() {
        super("Velocity", Category.Movement, "Changes Velocity of stuff");
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;

        if (event.getPacket() instanceof SPacketEntityVelocity)
            if (isEnabled())
                event.setCanceled(entityVelocity.getValue() && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId());
    }

    @SubscribeEvent
    public void onEntityCollision(EntityCollisionEvent.Entity event) {
        if (isEnabled())
            event.setCanceled(entityCollisionPush.getValue());
    }

    @SubscribeEvent
    public void onEntityCollision(EntityCollisionEvent.Block event) {
        if (isEnabled())
            event.setCanceled(entityCollisionBlock.getValue());
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (webCollisionMode.getValue().equals(WebCollisionMode.Speed) && mc.player != null && mc.player.isInWeb && !mc.player.onGround && mc.gameSettings.keyBindSneak.isKeyDown())
            mc.player.motionY *= webCollisionSpeed.getValue();

        if (webCollisionMode.getValue().equals(WebCollisionMode.Cancel) && mc.player.isInWeb)
            mc.player.isInWeb = false;
    }
}
