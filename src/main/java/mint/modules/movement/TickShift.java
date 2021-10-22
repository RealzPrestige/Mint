package mint.modules.movement;

import com.mojang.authlib.GameProfile;
import mint.clickgui.setting.Setting;
import mint.events.MoveEvent;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TickShift extends Module {

    public Setting<Boolean> step = register(new Setting<>("Step", Boolean.valueOf(Boolean.FALSE)));
    public Setting<Integer> timerFactor = register(new Setting<>("Factor", 1, 0, 9));

    public Setting<DisableMode> disableMode = register(new Setting<>("Disable", DisableMode.Distance));

    public enum DisableMode {Ticks, Distance, None}

    public Setting<Integer> ticksVal = register(new Setting<>("Ticks", 12, 1, 100, v -> disableMode.getValue() == DisableMode.Ticks));
    public Setting<Double> distanceVal = register(new Setting<>("Distance", 3.2d, 0.1d, 15.0d, v -> disableMode.getValue() == DisableMode.Distance));

    public Setting<Boolean> blink = register(new Setting<>("Blink", false));
    public Setting<D> mode = register(new Setting<>("Mode", D.Client, v -> blink.getValue()));

    public enum D {Client, Server, Both}

    public Setting<Boolean> renderPlayer = register(new Setting("Visualize", false, v -> blink.getValue()));
    public Setting<Boolean> test = register(new Setting("Phobos Test", false, v -> mode.getValue() != D.Server && blink.getValue()));

    Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    EntityOtherPlayerMP fakePlayer;
    BlockPos startPos = null;
    int packetsCanceled = 0;
    int ticks;

    public TickShift() {
        super("Tick Shift", Category.MOVEMENT, "Does smth");
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            disable();

        ticks++;
        if (disableMode.getValue() == DisableMode.Ticks && ticks >= ticksVal.getValue())
            disable();

        if (disableMode.getValue() == DisableMode.Distance && startPos != null && mc.player.getDistanceSq(startPos) >= MathUtil.square(distanceVal.getValue()))
            disable();

    }

    @Override
    public void onEnable() {
        if (NullUtil.fullNullCheck())
            disable();

        if (timerFactor.getValue() != 0)
            Timer.resetTimer();

        ticks = 0;
        startPos = mc.player.getPosition();
        if (step.getValue())
            mc.player.stepHeight = 0.6f;

        packetsCanceled = 0;
        if (renderPlayer.getValue() && blink.getValue()) {
            fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), mc.session.getUsername()));
            fakePlayer.copyLocationAndAnglesFrom(mc.player);
            fakePlayer.rotationYawHead = mc.player.rotationYawHead;
            fakePlayer.inventory = mc.player.inventory;
            mc.world.addEntityToWorld(-555555, fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        if (timerFactor.getValue() != 0)
            Timer.resetTimer();

        ticks = 0;
        startPos = null;
        if (step.getValue())
            mc.player.stepHeight = 0.6f;

        if (renderPlayer.getValue() && blink.getValue())
            try {
                mc.world.removeEntity(fakePlayer);
            } catch (Exception ignored) {
            }

    }

    @SubscribeEvent
    public void onMode(MoveEvent event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        if (event.getStage() == 0 && !NullUtil.fullNullCheck() && !mc.player.isSneaking() && !EntityUtil.isInLiquid() && (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f) || mc.player.isOnLadder()) {
            if (timerFactor.getValue() != 0) {
                if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both) && mc.player.isHandActive()) {
                    return;
                } else {
                    switch (timerFactor.getValue()) {
                        case 1: {
                            Timer.setTimer(1.15f);
                            break;
                        }
                        case 2: {
                            Timer.setTimer(1.3f);
                            break;
                        }
                        case 3: {
                            Timer.setTimer(1.45f);
                            break;
                        }
                        case 4: {
                            Timer.setTimer(1.6f);
                            break;
                        }
                        case 5: {
                            Timer.setTimer(1.75f);
                            break;
                        }
                        case 6: {
                            Timer.setTimer(1.9f);
                            break;
                        }
                        case 7: {
                            Timer.setTimer(2.05f);
                            break;
                        }
                        case 8: {
                            Timer.setTimer(2.2f);
                            break;
                        }
                        case 9: {
                            Timer.setTimer(2.35f);
                            break;
                        }
                    }
                }
            }
            if (step.getValue())
                mc.player.stepHeight = 2.0f;

            final MovementInput movementInput = mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = mc.player.rotationYaw;
            if (moveForward == 0.0 && moveStrafe == 0.0) {
                event.x = (0.0);
                event.z = (0.0);
            } else {
                if (moveForward != 0.0) {
                    if (moveStrafe > 0.0)
                        rotationYaw += ((moveForward > 0.0) ? -45 : 45);
                    else if (moveStrafe < 0.0)
                        rotationYaw += ((moveForward > 0.0) ? 45 : -45);

                    moveStrafe = 0.0f;
                    moveForward = ((moveForward == 0.0f) ? moveForward : ((moveForward > 0.0) ? 1.0f : -1.0f));
                }
                moveStrafe = ((moveStrafe == 0.0f) ? moveStrafe : ((moveStrafe > 0.0) ? 1.0f : -1.0f));
                event.x = (moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)) + moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)));
                event.z = (moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians(rotationYaw + 90.0f)) - moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians(rotationYaw + 90.0f)));
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;

        if (event.getStage() == 0 && mc.world != null && !mc.isSingleplayer() && (mode.getValue() != D.Server) && blink.getValue() && isEnabled()) {
            Packet<?> packet = event.getPacket();
            if (test.getValue() && packet instanceof CPacketPlayer) {
                event.setCanceled(true);
                packets.add(packet);
                ++packetsCanceled;
            }
            if (!test.getValue()) {
                if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
                    return;
                }
                packets.add(packet);
                event.setCanceled(true);
                ++packetsCanceled;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        if (mc.world != null && !mc.isSingleplayer() && (mode.getValue() != D.Client) && blink.getValue() && isEnabled()) {
            event.setCanceled(true);
        }
    }
}
