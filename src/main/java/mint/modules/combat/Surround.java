package mint.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.clickgui.setting.Setting;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module {

    public Surround() {
        super("Surround", Module.Category.COMBAT, "Surrounds you with Blocks.");
    }

    public Setting<Integer> delay = register(new Setting("TickDelay", 0, 0, 20));
    private int ticks = 0;
    public Setting<Boolean> packet = register(new Setting("Packet", true));
    public Setting<Boolean> noGhostBlocks = register(new Setting("NoGhost", true));
    public Setting<Center> center = register(new Setting("Center", Center.None));
    public Setting<Integer> NCPFactor = register(new Setting("NCPFactor", 1, 1, 2, v -> center.getValue() == Center.NCP));
    public enum Center {Instant, Teleport, NCP, None}
    Vec3d CPos = Vec3d.ZERO;
    //public Setting<Boolean> rotate = register(new Setting("Rotate", false));
    //public Setting<Boolean> raytrace = register(new Setting("Raytrace", false));
    public Setting<Boolean> allowEchests = register(new Setting("AllowEChest", true));
    public Setting<SwitchMode> switchMode = register(new Setting("Switch", SwitchMode.Silent));
    public enum SwitchMode {Silent, Normal, None}
    public BlockPos startPos;
    int originalSlot;
    int obbySlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
    int ecSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
    private int safety;

    public Setting<Boolean> parentDisable = register(new Setting("Disable", true, false));
    public Setting<Boolean> disableOnCompletion = register(new Setting("Completion", false, v -> parentDisable.getValue()));
    public Setting<Boolean> disableOnMove = register(new Setting("OnMove", false, v -> parentDisable.getValue()));
    public Setting<Boolean> disableOnJump = register(new Setting("OnJump", true, v -> parentDisable.getValue()));

    @Override
    public void onEnable() {
        if (NullUtil.fullNullCheck())
            return;

        ticks = 0;
        if (obbySlot == -1 && ecSlot == -1) {
            MessageManager.sendMessage("Out of blocks, disabling");
            disable();
        } else {
            startPos = EntityUtil.getRoundedBlockPos(mc.player);
            CPos = EntityUtil.getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
            switch (center.getValue()) {
                case Instant:
                    EntityUtil.setMotion(0.0);
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(CPos.x, CPos.y, CPos.z, true));
                    mc.player.setPosition(CPos.x, CPos.y, CPos.z);
                    break;

                case Teleport:
                    EntityUtil.setMotion(0.0);
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(CPos.x - 0.2, CPos.y - 0.2, CPos.z - 0.2, true));
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(CPos.x, CPos.y, CPos.z, true));
                    mc.player.setPosition(CPos.x, CPos.y, CPos.z);
                    break;

                case NCP:
                    if (NCPFactor.getValue() == 1) {
                        mc.player.motionX = (CPos.x - mc.player.posX) / 1;
                        mc.player.motionZ = (CPos.z - mc.player.posZ) / 1;
                        break;
                    } else {
                        mc.player.motionX = (CPos.x - mc.player.posX) / 2;
                        mc.player.motionZ = (CPos.z - mc.player.posZ) / 2;
                        break;
                    }
            }
        }
    }

    @Override
    public void onUpdate() {
        ticks++;

        if (ticks >= delay.getValue()) {
            //doPlace();
        }
        if (startPos != EntityUtil.getRoundedBlockPos(mc.player) && disableOnMove.getValue()) {
            disable();
        }
        if (mc.gameSettings.keyBindJump.pressed && disableOnJump.getValue()) {
            disable();
        }
        if (disableOnCompletion.getValue()) {
            disable();
        }
    }

    public String hudInfoString() {
        switch (safety) {
            case 0: {
                return ChatFormatting.RED + "Unsafe";
            }
            case 1: {
                return  ChatFormatting.YELLOW + "Safe";
            }
            default: {
                return ChatFormatting.GREEN + "Safe";
            }
        }
    }
}