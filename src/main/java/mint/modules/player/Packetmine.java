package mint.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.setting.Bind;
import mint.setting.Setting;
import mint.events.BlockEvent;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
import java.util.Objects;

/**
 * @author kambing, zPrestige
 * phobos speedmine heavily modified
 * <p>
 * also this shit is p messy so lol
 */

public class Packetmine extends Module {
    private static Packetmine INSTANCE = new Packetmine();
    int delay;
    public Timer timer = new Timer();
    private final Timer readyTimer = new Timer();

    public Setting<Boolean> silentSwitch = register(new Setting("SilentSwitch", false));
    public Setting<SilentSwitchMode> silentSwitchMode = register(new Setting<>("SilentSwitchMode", SilentSwitchMode.AUTO));

    public enum SilentSwitchMode {AUTO, KEYBIND}

    public Setting<Bind> switchBind = register(new Setting<>("SwitchBind", new Bind(-1), z -> silentSwitch.getValue() && silentSwitchMode.getValue() == SilentSwitchMode.KEYBIND));
    public Setting<Boolean> render = register(new Setting<>("Render", true, false));

    public Setting<RenderMode> renderMode = register(new Setting("RenderMode", RenderMode.EXPAND, z -> render.getValue()));
    public Setting<BoxMode> boxMode = register(new Setting("BoxMode", BoxMode.BOTH, z -> render.getValue()));
    public Setting<ColorMode> colorMode = register(new Setting("ColorMode", ColorMode.READYFADE, z -> render.getValue()));

    public enum ColorMode {READYFADE, STATUS, STATIC}

    public enum RenderMode {FADE, EXPAND, EXPAND2, STATIC}

    public enum BoxMode {FILL, OUTLINE, BOTH}

    public Setting<Integer> red = register(new Setting<>("Red", 254, 0, 254, z -> render.getValue()));
    public Setting<Integer> green = register(new Setting<>("Green", 0, 0, 254, z -> render.getValue()));
    public Setting<Integer> blue = register(new Setting<>("Blue", 0, 0, 254, z -> render.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 120, 0, 254, z -> render.getValue()));

    public Setting<Integer> readyRed = register(new Setting<>("ReadyRed", 0, 0, 254, z -> render.getValue() && (colorMode.getValue().equals(ColorMode.STATUS) || colorMode.getValue().equals(ColorMode.READYFADE))));
    public Setting<Integer> readyGreen = register(new Setting<>("ReadyGreen", 254, 0, 254, z -> render.getValue() && (colorMode.getValue().equals(ColorMode.STATUS) || colorMode.getValue().equals(ColorMode.READYFADE))));
    public Setting<Integer> readyBlue = register(new Setting<>("ReadyBlue", 0, 0, 254, z -> render.getValue() && (colorMode.getValue().equals(ColorMode.STATUS) || colorMode.getValue().equals(ColorMode.READYFADE))));
    public Setting<Integer> speed = register(new Setting<>("ReadySpeed", 2, 1, 5, z -> render.getValue() && (colorMode.getValue().equals(ColorMode.STATUS) || colorMode.getValue().equals(ColorMode.READYFADE))));

    int currentAlpha;
    int count;
    ItemStack item;
    int subVal = 40;
    AxisAlignedBB bb;
    public BlockPos currentPos;
    public Block currentBlock;
    public IBlockState currentBlockState;
    int pickSlot;
    int oldSlot;
    int red0 = red.getValue();
    int green0 = green.getValue();
    int blue0 = blue.getValue();

    public Packetmine() {
        super("Packetmine", Category.Player, "Mines with packet.");
        setInstance();
    }

    public static Packetmine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Packetmine();
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
        pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
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
        if (colorMode.getValue().equals(ColorMode.READYFADE)) {
            if (red0 != readyRed.getValue()) {
                if (red0 > readyRed.getValue()) {
                    red0 = red0 - speed.getValue();
                } else {
                    red0 = red0 + speed.getValue();
                }
            }
            if (green0 != readyGreen.getValue()) {
                if (green0 > readyGreen.getValue()) {
                    green0 = green0 - speed.getValue();
                } else {
                    green0 = green0 + speed.getValue();
                }
            }
            if (blue0 != readyBlue.getValue()) {
                if (blue0 > readyBlue.getValue()) {
                    blue0 = blue0 - speed.getValue();
                } else {
                    blue0 = blue0 + speed.getValue();
                }
            }
        }
    }


    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            return;
        }
        mc.playerController.blockHitDelay = 0;
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        try {
            if (currentPos != null) {
                if (getMineTime(currentBlock, item, false) == -1)
                    return;
                bb = mc.world.getBlockState(currentPos).getSelectedBoundingBox(mc.world, currentPos);

                // i had a headache making this i hope this works - kambing
                Color color = new Color(colorMode.getValue().equals(ColorMode.STATIC) ? red.getValue() : colorMode.getValue().equals(ColorMode.READYFADE) ? red0 : colorMode.getValue().equals(ColorMode.STATUS) && this.timer.passedMs((int) (2000.0f * Mint.serverManager.getTpsFactor())) ? readyRed.getValue() : red.getValue(),
                        colorMode.getValue().equals(ColorMode.STATIC) ? green.getValue() : colorMode.getValue().equals(ColorMode.READYFADE) ? green0 : colorMode.getValue().equals(ColorMode.STATUS) && this.timer.passedMs((int) (2000.0f * Mint.serverManager.getTpsFactor())) ? readyGreen.getValue() : green.getValue(),
                        colorMode.getValue().equals(ColorMode.STATIC) ? blue.getValue() : colorMode.getValue().equals(ColorMode.READYFADE) ? blue0 : colorMode.getValue().equals(ColorMode.STATUS) && this.timer.passedMs((int) (2000.0f * Mint.serverManager.getTpsFactor())) ? readyBlue.getValue() : blue.getValue(),
                        renderMode.getValue().equals(RenderMode.FADE) ? currentAlpha : alpha.getValue());

                switch (renderMode.getValue()) {
                    case EXPAND:
                        bb = bb.shrink(Math.max(Math.min(normalize(count, getMineTime(currentBlock, item, false) - subVal, 0), 1.0), 0.0));
                        break;
                    case EXPAND2:
                        bb = bb.setMaxY(bb.minY - 0.5 + (Math.max(Math.min(normalize(count * 2, getMineTime(currentBlock, item, false) - subVal, 0), 1.5), 0.0)));
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
        } catch (NullPointerException ignored) {
        }
    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (NullUtil.fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && mc.playerController.curBlockDamageMP > 0.1f) {
            mc.playerController.isHittingBlock = true;
        }

        if (event.pos != currentPos && currentPos != null) {
            red0 = red.getValue();
            green0 = green.getValue();
            blue0 = blue.getValue();
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
                if (currentPos == null || event.pos != currentPos) {
                    currentPos = event.pos;
                    currentBlock = mc.world.getBlockState(currentPos).getBlock();
                    currentBlockState = mc.world.getBlockState(currentPos);
                    timer.reset();
                    if (getBestItem(currentBlock) == null) {
                        item = mc.player.getHeldItem(EnumHand.MAIN_HAND);
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

    public static double getMineTime(Block block, ItemStack stack, boolean raw) {
        if (Objects.requireNonNull(stack.item.equals(Items.AIR)) || stack.item.equals(null))
            return -1.0;

        float speedMultiplier = stack.getDestroySpeed(block.getDefaultState());
        float damage;

        if (stack.canHarvestBlock(block.getDefaultState())) {
            damage = speedMultiplier / block.blockHardness / 30.0f;
        } else {
            damage = speedMultiplier / block.blockHardness / 100.0f;
        }
        if (raw)
            return damage;
        return (float) Math.ceil(1.0 / damage);
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

