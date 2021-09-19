package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.commands.Command;
import mint.modules.Module;
import mint.utils.BlockUtil;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SelfFill extends Module {

    public SelfFill () {
        super("Self Fill ", Category.COMBAT, "Rubberbands you in a block.");
    }

    public Setting<Block> prefer = register(new Setting("Prefer", Block.EChest));
    public Setting<LagMode> lagBack= register(new Setting("LagBack", LagMode.Teleport));
    public BlockPos startPos = null;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            disable();
            return;
        }
        startPos = new BlockPos(mc.player.getPositionVector());
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            disable();
            return;
        }

        int originalSlot = mc.player.inventory.currentItem;
        int ecSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
        int obbySlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (ecSlot == -1 && obbySlot == -1) {
            Command.sendMessage("Out of blocks, disabling");
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

        if (mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        EntityUtil.packetJump(true);
        BlockUtil.placeBlock(startPos, EnumHand.MAIN_HAND, false, true, false, true, EnumHand.MAIN_HAND);
        switch (lagBack.getValue()) {
            case Packet: {
                //todo nigga what the fuck is this - kambing | orble,m? - zenov
                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1, mc.player.posZ, true));
            }
            case YMotion: {
                mc.player.motionY = 1.75;
            }
            case Teleport: {
                mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY + 1, mc.player.posZ);
            }
            case LagFall: {
                mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + 2.35, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        if (originalSlot != -1) {
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
        }
        disable();
    }

    public enum Block {
        EChest,
        Obsidian
    }
    public enum LagMode {
        Packet,
        YMotion,
        Teleport,
        LagFall
    }
}