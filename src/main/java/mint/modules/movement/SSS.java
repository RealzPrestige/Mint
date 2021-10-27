package mint.modules.movement;

import com.mojang.authlib.GameProfile;
import mint.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.MathUtil;
import mint.utils.NullUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * tpc rewrite(sixet owns me and all) / tickshift type beat shit
 * some shit is pasted
 */

public class SSS extends Module {

    public SSS() {
        super("SSS", Module.Category.Movement, "Just doing funny shit");
    }

    public Setting<Boolean> movementParent = register(new Setting("Movement", true, false));

    public Setting<MoveType> moveType = register(new Setting("MoveType", MoveType.YPort, z -> movementParent.getValue()));

    public Setting<Boolean> step = register(new Setting("Step", true, z -> movementParent.getValue()));
    public Setting<Double> yPortSpeed = register(new Setting("YPortSpeed", 0.1d, 0.0d, 1.0d, z -> movementParent.getValue() && moveType.getValue() == MoveType.YPort));
    public Setting<Float> fallSpeed = register(new Setting("FallSpeed", 0.8f, 0.1f, 9.0f, z -> movementParent.getValue() && moveType.getValue() == MoveType.YPort));
    public Setting<Integer> yMotion = register(new Setting("YMotion", 390, 350, 420, z -> movementParent.getValue() && moveType.getValue() == MoveType.YPort));


    public Setting<Boolean> playerParent = register(new Setting("Player", true, false));

    public Setting<PlayerType> playerType = register(new Setting("Type", PlayerType.Blink, z -> playerParent.getValue()));

    //blink
    public Setting<Mode> mode = register(new Setting("Mode", Mode.Both, z -> playerParent.getValue() && playerType.getValue() == PlayerType.Blink));
    public Setting<Boolean> renderPlayer = register(new Setting("Visualize", false, z -> playerParent.getValue() && playerType.getValue() == PlayerType.Blink));
    public Setting<DisableMode> disableMode = register(new Setting("Disable", DisableMode.Distance, z -> playerParent.getValue() && playerType.getValue() == PlayerType.Blink));
    public Setting<Integer> ticksVal = register(new Setting("Ticks", 20, 1, 100, z -> playerParent.getValue() && playerType.getValue() == PlayerType.Blink && disableMode.getValue() == DisableMode.Ticks));
    public Setting<Double> distanceVal = register(new Setting("Distance", 3.2d, 0.1d, 15.0d, z -> playerParent.getValue() && playerType.getValue() == PlayerType.Blink && disableMode.getValue() == DisableMode.Distance));


    //something else
    EntityOtherPlayerMP fakePlayer;
    BlockPos startPos = null;
    int ticks;

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            disable();
        ticks++;
        if (disableMode.getValue() == DisableMode.Ticks && ticks >= ticksVal.getValue()) {
            disable();
        }
        if (disableMode.getValue() == DisableMode.Distance && startPos != null && mc.player.getDistanceSq(startPos) >= MathUtil.square(distanceVal.getValue())) {
            disable();
        }
        if (moveType.getValue() == MoveType.YPort) {
            if (mc.player.isSneaking() || EntityUtil.isInLiquid() || mc.player.isOnLadder()) {
                return;
            }
            if (step.getValue()) {
                mc.player.stepHeight = 2.0f;
            }
            if (mc.player.onGround) {
                mc.player.motionY = yMotion.getValue() / 1000.0f;
                EntityUtil.setSpeed(mc.player, EntityUtil.getDefaultMoveSpeed() + yPortSpeed.getValue());
            } else {
                for (double y = 0.0; y < 2.5 + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY = -fallSpeed.getValue();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (NullUtil.fullNullCheck())
            return;

        ticks = 0;
        startPos = mc.player.getPosition();
        mc.player.stepHeight = 0.6f;
        if (renderPlayer.getValue() && playerType.getValue() == PlayerType.Blink) {
            fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(mc.player.getUniqueID(), mc.session.getUsername()));
            fakePlayer.copyLocationAndAnglesFrom(mc.player);
            fakePlayer.rotationYawHead = mc.player.rotationYawHead;
            fakePlayer.inventory = mc.player.inventory;
            fakePlayer.setHealth(EntityUtil.getHealth(mc.player));
            mc.world.addEntityToWorld(-555555, fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        ticks = 0;
        startPos = null;
        mc.player.stepHeight = 0.6f;
        if (renderPlayer.getValue() && playerType.getValue() == PlayerType.Blink) {
            try {
                mc.world.removeEntity(fakePlayer);
            } catch (Exception ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if(!isEnabled())
            return;
        if (isEnabled() && playerType.getValue() == PlayerType.Blink && mode.getValue() != Mode.Server) {
            event.setCanceled(true); // or add == client || == both
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if(!isEnabled())
            return;
        if (isEnabled() && playerType.getValue() == PlayerType.Blink && mode.getValue() != Mode.Client) {
            event.setCanceled(true);
        }
    }

    //playerParent
    public enum PlayerType {
        Blink
    }

    public enum Mode {
        Both,
        Server,
        Client
    }

    public enum DisableMode {
        Ticks,
        Distance,
        None
    }

    //movementParent
    public enum MoveType {
        YPort,
        Angle
    }
}