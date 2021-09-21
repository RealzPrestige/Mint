package mint.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.clickgui.setting.Setting;
import mint.commands.Command;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import mint.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module {

    public Surround() {
        super("Surround", Module.Category.COMBAT, "Surrounds you with Obsidian/EChest.");
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

    public Setting<Boolean> parentDisable = register(new Setting("Disable", true, false));
    public Setting<Boolean> disableOnCompletion = register(new Setting("Completion", false, v -> parentDisable.getValue()));
    public Setting<Boolean> disableOnMove = register(new Setting("OnMove", false, v -> parentDisable.getValue()));
    public Setting<Boolean> disableOnJump = register(new Setting("OnJump", true, v -> parentDisable.getValue()));

    @Override
    public void onEnable() {
        ticks = 0;
        if (obbySlot == -1 && ecSlot == -1) {
            Command.sendMessage("Out of blocks, disabling");
            disable();
        }
        startPos = EntityUtil.getRoundedBlockPos(mc.player);
        CPos = EntityUtil.getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
        switch (center.getValue()) {
            case Instant:
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
                mc.getConnection().sendPacket(new CPacketPlayer.Position(CPos.x, CPos.y, CPos.z, true));
                mc.player.setPosition(CPos.x, CPos.y, CPos.z);
                break;

            case Teleport:
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
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

    @Override
    public void onUpdate() {
        ticks++;

        if (ticks >= delay.getValue()) {
            doPlace();
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
        //todo someone make a safety check
        return ChatFormatting.GREEN + "Safe";
    }

    public void doPlace() {

    }

    public boolean passedTicks(int tick) {
        return ticks >= tick;
    }
}