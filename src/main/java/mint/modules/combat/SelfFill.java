package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.commands.Command;
import mint.modules.Module;
import mint.utils.BlockUtil;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import mint.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class SelfFill extends Module {

    public SelfFill() {
        super("Self Fill ", Category.COMBAT, "Rubberbands you in a block.");
    }

    public Setting<Block> prefer = register(new Setting("Prefer", Block.EChest));
    public Setting<LagMode> lagBack = register(new Setting("LagBack", LagMode.Teleport));
    public Setting<Boolean> offground = register(new Setting("OffGround", true, v -> lagBack.getValue() == LagMode.Strict));
    public BlockPos startPos = null;
    Timer timer = new Timer();

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

        //a player jumps(otherwise u wont be able to place the block and lag back into it
        EntityUtil.packetJump(true);

        //places the block while the player is in air
        BlockUtil.placeBlock(startPos, EnumHand.MAIN_HAND, false, true, false, true, EnumHand.MAIN_HAND);

        //after placing u have to cause a lag back
        switch (lagBack.getValue()) {
            case Packet: {
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
            case Strict: {
                //its the same as before but using an existing method
                EntityUtil.packetJump(offground.getValue());
            }
            case Jump: {
                mc.player.jump();
                if (timer.passedMs(200)) {
                    mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1, mc.player.posZ, true));
                }
            }
            case Kambing: {
                mc.player.posY = 100;
                fakePop(mc.player);
                fakePop(mc.player);
                fakePop(mc.player);
                fakePop(mc.player);
                Minecraft.getMinecraft().getConnection().handleDisconnect(new SPacketDisconnect(new TextComponentString("Left the server with 1.0 hp")));
                this.disable();
            }
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        if (originalSlot != -1) {
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
        }
        timer.reset();
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
        LagFall,
        Strict,
        Jump,
        Kambing
    }

    public void fakePop(EntityPlayer player) {
        try {
            mc.player.connection.handleEntityStatus(new SPacketEntityStatus(player, (byte) 35));
        } catch (Exception e) {
        }
    }
}
