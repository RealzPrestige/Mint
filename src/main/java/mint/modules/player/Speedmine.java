package mint.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Bind;
import mint.clickgui.setting.Setting;
import mint.events.BlockEvent;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.InventoryUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

import static mint.modules.player.Speedmine.Mode.PACKET;

/**
 * @author kambing, zPrestige
 * phobos speedmine heavily modified
 */
public class Speedmine extends Module {
    private static Speedmine INSTANCE = new Speedmine();
    int delay;
    public Timer timer = new Timer();
    public Setting<Mode> mode = register(new Setting<>("Mode", PACKET));

    public enum Mode {PACKET, INSTANT}

    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch", false, v -> mode.getValue() == PACKET));
    public Setting<SilentSwitchMode> silentSwitchMode = register(new Setting<>("SilentSwitchMode", SilentSwitchMode.AUTO, v -> mode.getValue() == PACKET && silentSwitch.getValue()));

    public enum SilentSwitchMode {AUTO, KEYBIND}

    public Setting<Bind> switchBind = register(new Setting<>("SwitchBind", new Bind(-1), v -> silentSwitch.getValue() && silentSwitchMode.getValue() == SilentSwitchMode.KEYBIND));
    public Setting<Boolean> render = register(new Setting<>("Render", true, false));

    public Setting<RenderMode> renderMode = register(new Setting("RenderMode", RenderMode.EXPAND, v -> render.getValue()));
    public Setting<BoxMode> boxMode = register(new Setting("BoxMode", BoxMode.BOTH, v -> render.getValue()));

    public enum RenderMode {FADE, EXPAND, EXPAND2}

    public enum BoxMode {FILL, OUTLINE, BOTH}

    public Setting<Integer> red = register(new Setting<>("Red", 120, 0, 255, v -> render.getValue()));
    public Setting<Integer> green = register(new Setting<>("Green", 120, 0, 255, v -> render.getValue()));
    public Setting<Integer> blue = register(new Setting<>("Blue", 120, 0, 255, v -> render.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 120, 0, 255, v -> render.getValue()));

    int currentAlpha;
    int count;
    ItemStack item;
    int subVal = 40;
    AxisAlignedBB bb;
    public BlockPos currentPos;
    public Block currentBlock;
    public IBlockState currentBlockState;
    int pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
    int oldSlot;

    public Speedmine() {
        super("Speedmine", Category.PLAYER, "Tweaks your mining.");
        setInstance();
    }

    public static Speedmine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Speedmine();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void onLogin() {
        if (isEnabled()) {
            disable();
            enable();
        }
    }

    @Override
    public void onTick() {
        if (delay > 5) {
            delay = 0;
        } else {
            ++delay;
        }
        if (currentPos != null) {
            if (!mc.world.getBlockState(currentPos).equals(currentBlockState) || mc.world.getBlockState(currentPos).getBlock() == Blocks.AIR) {
                currentPos = null;
                currentBlockState = null;
            }
        }
        if (currentAlpha < (alpha.getValue() - 2)) {
            currentAlpha = currentAlpha + 3;
        }

        if (mc.player != null && silentSwitch.getValue() && silentSwitchMode.getValue() == SilentSwitchMode.AUTO && timer.passedMs((int) (2000.0f * Mint.serverManager.getTpsFactor())) && getPickSlot() != -1) {
            if (pickSlot == -1) {
                TextComponentString text = new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.WHITE + ChatFormatting.BOLD + " Speedmine: " + ChatFormatting.RESET + ChatFormatting.GRAY + "No pickaxe found, stopped" + ChatFormatting.WHITE + ChatFormatting.BOLD + " SilentSwitch");
                Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
            } else {
                /* i doubt that packet switch works as silentswitch but whatever
                omg i got a headache from reading the code
                nigger.NIGGA _- 2124 530i 23r=== 32rt49 m _= 555 what the fuck nigga china code */
                mc.player.connection.sendPacket(new CPacketHeldItemChange(getPickSlot()));
            }
        }
        if (mc.player != null && silentSwitch.getValue() && silentSwitchMode.getValue() == SilentSwitchMode.AUTO && timer.passedMs((int) (2200.0f * Mint.serverManager.getTpsFactor()))) {
            oldSlot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        }
        if (mc.player != null && silentSwitch.getValue() && silentSwitchMode.getValue() == SilentSwitchMode.KEYBIND) {
            if (switchBind.getValue().getKey() != -1) {
                if (Keyboard.isKeyDown(switchBind.getValue().getKey())) {
                    if (pickSlot == -1) {
                        TextComponentString text = new TextComponentString(Mint.commandManager.getClientMessage() + ChatFormatting.WHITE + ChatFormatting.BOLD + " Speedmine: " + ChatFormatting.RESET + ChatFormatting.GRAY + "No pickaxe found, stopped" + ChatFormatting.WHITE + ChatFormatting.BOLD + " SilentSwitch");
                        Module.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(text, 1);
                    } else {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(getPickSlot()));

                        if (delay == 5) {
                            oldSlot = mc.player.inventory.currentItem;
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                        }

                    }
                }
            }
        }
        if (currentPos != null) {
            if (currentBlock == Blocks.OBSIDIAN && getBestItem(currentBlock) != null) {
                subVal = 146;
            } else if (currentBlock == Blocks.ENDER_CHEST && getBestItem(currentBlock) != null) {
                subVal = 66;
            }
        }
        count++;
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) {
            return;
        }
        mc.playerController.blockHitDelay = 0;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (currentPos != null) {
            bb = mc.world.getBlockState(currentPos).getSelectedBoundingBox(mc.world, currentPos);

            Color color = new Color(red.getValue(), green.getValue(), blue.getValue(), renderMode.getValue().equals(RenderMode.FADE) ? currentAlpha : alpha.getValue());
            switch (renderMode.getValue()) {
                case EXPAND:
                    bb = bb.shrink(Math.max(Math.min(normalize(count, getMineTime(currentBlock, item) - subVal, 0), 1.0), 0.0));
                    break;
                case EXPAND2:
                    bb = bb.setMaxY(bb.minY - 0.5 + (Math.max(Math.min(normalize(count * 2, getMineTime(currentBlock, item) - subVal, 0), 1.5), 0.0)));
                    break;
                default:
                    break;
            }
            if (render.getValue() && currentPos != null) {
                switch (boxMode.getValue()) {
                    case OUTLINE:
                        RenderUtil.drawBlockOutlineBB(bb, color, 1f);
                        break;
                    case FILL:
                        RenderUtil.drawBBBox(bb, color, color.getAlpha());
                        break;
                    case BOTH:
                        RenderUtil.drawBBBox(bb, color, color.getAlpha());
                        RenderUtil.drawBlockOutlineBB(bb, color, 1f);
                        break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && mc.playerController.curBlockDamageMP > 0.1f) {
            mc.playerController.isHittingBlock = true;
        }

        if (event.pos != currentPos && currentPos != null) {
            currentAlpha = 0;
            count = 0;
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos, event.facing));
            mc.playerController.isHittingBlock = false;
            mc.playerController.curBlockDamageMP = 0;
            currentPos = event.pos;
        }

        if (event.getStage() == 4) {
            if (canBreak(event.pos)) {
                mc.playerController.isHittingBlock = false;
                switch (mode.getValue()) {
                    case PACKET: {
                        if (currentPos == null || event.pos != currentPos) {
                            currentPos = event.pos;
                            currentBlock = mc.world.getBlockState(currentPos).getBlock();
                            currentBlockState = mc.world.getBlockState(currentPos);
                            timer.reset();
                            if (getBestItem(currentBlock) == null) {
                                if (mc.player.getHeldItem(EnumHand.MAIN_HAND) != getItemStackFromItem(Items.AIR) || mc.player.getHeldItem(EnumHand.MAIN_HAND) != null) {
                                    item = mc.player.getHeldItem(EnumHand.MAIN_HAND);
                                }else{
                                    item = getItemStackFromItem(Items.GOLDEN_APPLE);
                                }
                            } else {
                                item = getItemStackFromItem(getBestItem(currentBlock));
                            }
                        }
                        currentAlpha = 0;
                        count = 0;
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        event.setCanceled(true);
                        break;
                    }
                    case INSTANT: {
                        currentAlpha = 0;
                        count = 0;
                        currentBlock = mc.world.getBlockState(currentPos).getBlock();
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        mc.playerController.onPlayerDestroyBlock(event.pos);
                        mc.world.setBlockToAir(event.pos);
                    }
                }
            }
        }
    }

    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != Items.DIAMOND_PICKAXE) continue;
            return i;
        }
        return -1;
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    public static double getMineTime(Block block, ItemStack stack) {
        float speedMultiplier = stack.getDestroySpeed(block.getDefaultState());
        float damage;

        if (stack.canHarvestBlock(block.getDefaultState())) {
            damage = speedMultiplier / block.blockHardness / 30;
        } else {
            damage = speedMultiplier / block.blockHardness / 100;
        }

        return (float) Math.ceil(1 / damage);
    }

    private double normalize(final double value, final double max, final double min) {
        return (1 - 0.5) * ((value - min) / (max - min)) + 0.5;
    }

    public static Item getBestItem(Block block) {
        String tool = block.getHarvestTool(block.getDefaultState());
        if (tool != null) {
            switch (tool) {
                case "axe":
                    return Items.DIAMOND_AXE;
                case "shovel":
                    return Items.DIAMOND_SHOVEL;
                default:
                    return Items.DIAMOND_PICKAXE;
            }
        } else {
            return Items.DIAMOND_PICKAXE;
        }

    }

    public static ItemStack getItemStackFromItem(Item item) {
        if (mc.player == null) return null;
        for (int slot = 0; slot <= 9; slot++) {
            if (mc.player.inventory.getStackInSlot(slot).getItem() == item)
                return mc.player.inventory.getStackInSlot(slot);
        }
        return null;
    }
}

