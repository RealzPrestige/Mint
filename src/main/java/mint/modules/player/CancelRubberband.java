package mint.modules.player;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class CancelRubberband extends Module {
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.Cancel));
    public Setting<Boolean> onGroundOnly = register(new Setting("On Ground Only", false));
    public Setting<Division> posYDivision = register(new Setting<>("Pos Y Division", Division.Ten, v -> mode.getValue().equals(Mode.PacketTeleport) || mode.getValue().equals(Mode.VanillaTeleport)));
    public Setting<FirstY> firstY = register(new Setting<>("First Y", FirstY.Positive, v -> mode.getValue().equals(Mode.PacketTeleport) || mode.getValue().equals(Mode.VanillaTeleport)));
    public Setting<Boolean> resetY = register(new Setting("Reset Y", false, v -> mode.getValue().equals(Mode.PacketTeleport) || mode.getValue().equals(Mode.VanillaTeleport)));
    public Setting<Boolean> onGroundIn = register(new Setting("On Ground In", false, v -> mode.getValue().equals(Mode.PacketTeleport)));

    public enum Division {Ten, Hundred, Thousand, TenThousand, HundredThousand, Million, TenMillion, HundredMillion, Billion}

    public enum Mode {Cancel, Reset, VanillaTeleport, PacketTeleport, ClearFlag, ReverseTeleportId, Debug}

    public enum FirstY {Positive, Negative}

    public CancelRubberband() {
        super("Cancel Rubberband", Category.PLAYER, "Cancels Rubberbamds (Hopefully)");
    }


    @SubscribeEvent
    public void onPacketEvent(PacketEvent event) {
        if(NullUtil.fullNullCheck() || !isEnabled())
            return;
        if (onGroundOnly.getValue() && !mc.player.onGround)
            return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = new SPacketPlayerPosLook();
            int division = 0;
            switch (posYDivision.getValue()) {
                case Ten:
                    division = 10;
                    break;
                case Hundred:
                    division = 100;
                    break;
                case Thousand:
                    division = 1000;
                    break;
                case TenThousand:
                    division = 10000;
                    break;
                case HundredThousand:
                    division = 100000;
                    break;
                case Million:
                    division = 1000000;
                    break;
                case TenMillion:
                    division = 10000000;
                    break;
                case HundredMillion:
                    division = 100000000;
                    break;
                case Billion:
                    division = 1000000000;
                    break;
            }

            switch (mode.getValue()) {
                case Cancel:
                    event.setCanceled(true);
                    break;
                case Reset:
                    packet.x = mc.player.posX;
                    packet.y = mc.player.posY;
                    packet.z = mc.player.posZ;
                    packet.pitch = mc.player.rotationPitch;
                    packet.yaw = mc.player.rotationYaw;
                    break;
                case VanillaTeleport:
                    if(firstY.getValue().equals(FirstY.Positive)){
                        mc.player.setPosition(mc.player.posX, mc.player.posY + (1f / division), mc.player.posZ);
                        if (resetY.getValue())
                            mc.player.setPosition(mc.player.posX, mc.player.posY - (1f / division), mc.player.posZ);
                    } else if(firstY.getValue().equals(FirstY.Negative)){
                        mc.player.setPosition(mc.player.posX, mc.player.posY - (1f / division), mc.player.posZ);
                        if (resetY.getValue())
                            mc.player.setPosition(mc.player.posX, mc.player.posY + (1f / division), mc.player.posZ);
                    }

                    break;
                case PacketTeleport:
                    if(firstY.getValue().equals(FirstY.Positive)){
                        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (1f / division), mc.player.posZ, onGroundIn.getValue()));
                        if (resetY.getValue())
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - (1f / division), mc.player.posZ, onGroundIn.getValue()));
                    } else if(firstY.getValue().equals(FirstY.Negative)){
                        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - (1f / division), mc.player.posZ, onGroundIn.getValue()));
                        if (resetY.getValue())
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + (1f / division), mc.player.posZ, onGroundIn.getValue()));
                    }
                    break;
                case ClearFlag:
                    packet.flags.clear();
                    break;
                case ReverseTeleportId:
                    packet.teleportId = packet.getTeleportId() - 1;
                    break;
                case Debug:
                    MessageManager.sendMessage("Cancel Rubberband: Sending Logger info Successfully.");
                    Mint.INSTANCE.getLOGGER().info("Packet Flags: " + packet.getFlags());
                    break;
            }
        }
    }
}
