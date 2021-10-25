package mint.modules.combat;

import mint.setting.Setting;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.utils.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SelfFill extends Module {


    public SelfFill() {
        super("Self Fill", Category.COMBAT, "Rubberbands you in a block.");
    }

    public Setting<Block> prefer = register(new Setting("Prefer", Block.EChest));
    public enum Block {EChest, Obsidian}
    public Setting<Float> height = register(new Setting("Height", 1.0f, 1.0f, 5.0f));
    public Setting<LagMode> lagBack = register(new Setting("LagBack", LagMode.Teleport));
    public enum LagMode {Packet, YMotion, Teleport, LagFall, DoubleJump}
    public Setting<Boolean> packetJump = register(new Setting("PacketJump", true, v -> lagBack.getValue() == LagMode.DoubleJump));
    public BlockPos startPos = null;
    Timer timer = new Timer();

    @Override
    public void onEnable() {
        if (NullUtil.fullNullCheck()) {
            disable();
            return;
        }
        startPos = new BlockPos(mc.player.getPositionVector());
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            disable();
            return;
        }

        int originalSlot = mc.player.inventory.currentItem;
        int ecSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
        int obbySlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (ecSlot == -1 && obbySlot == -1) {
            MessageManager.sendMessage("Out of blocks, disabling");
            disable(); //how did no1 notice this oml
        }

        if (prefer.getValue() == Block.EChest && ecSlot != -1) {
            InventoryUtil.SilentSwitchToSlot(ecSlot);
            if (ecSlot == -1 && obbySlot != -1) {
                InventoryUtil.SilentSwitchToSlot(obbySlot);
            }
        }
        if (prefer.getValue() == Block.Obsidian) {
            InventoryUtil.SilentSwitchToSlot(obbySlot);
            if (obbySlot == -1 && ecSlot != -1) {
                InventoryUtil.SilentSwitchToSlot(ecSlot);
            }
        }

        EntityUtil.startSneaking();
        EntityUtil.packetJump(true);
        BlockUtil.placeBlock(startPos, EnumHand.MAIN_HAND, false, true, false, true, EnumHand.MAIN_HAND);

        switch (lagBack.getValue()) {
            case Packet: {
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ, true));
                break;
            }
            case YMotion: {
                mc.player.motionY = height.getValue();
                break;
            }
            case Teleport: {
                mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ);
                break;
            }
            case LagFall: {
                mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                break;
            }
            case DoubleJump: {
                if (packetJump.getValue()) {
                    EntityUtil.packetJump(true);
                } else {
                    mc.player.jump();
                }
                break;
            }
        }
        EntityUtil.stopSneaking(false);

        if (originalSlot != -1) {
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
        }
        timer.reset();
        disable();
    }
}