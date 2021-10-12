package mint.modules.miscellaneous;

import mint.clickgui.MintGui;
import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Backpack extends Module {

    public Backpack() {
        super("Backpack", Module.Category.MISCELLANEOUS, "Manipulates container packets.");
    }

    public Setting<Boolean> containerParent = register(new Setting("Containers", true, false));
    //todo add other containers like echest, anvil etc
    public Setting<Boolean> chest = register(new Setting("Chest", false, v -> containerParent.getValue()));
    public Setting<Boolean> furnace = register(new Setting("Furnace", false, v -> containerParent.getValue()));
    public Setting<Boolean> inventory = register(new Setting("Inventory", false, v -> containerParent.getValue()));

    public Setting<Boolean> miscParent = register(new Setting("Misc", true, false));
    public Setting<Boolean> open = register(new Setting("Open", false, v -> miscParent.getValue()));
    public Setting<Boolean> close = register(new Setting("Close", false, v -> miscParent.getValue()));
    private GuiScreen cancelledGui = null;

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if(!isEnabled())
            return;
        if (e.getPacket() instanceof CPacketCloseWindow && close.getValue()) {
            e.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        if(!isEnabled())
            return;
        if (mc.currentScreen instanceof GuiContainer && open.getValue()) {
            if (mc.currentScreen instanceof MintGui ||
                    //chest
                    mc.currentScreen instanceof GuiChest && !chest.getValue() ||
                    //furnace
                    mc.currentScreen instanceof GuiFurnace && !furnace.getValue() ||
                    //inventory
                    mc.currentScreen instanceof GuiInventory && !inventory.getValue()) {
                return;
            }
            cancelledGui = mc.currentScreen;
            mc.currentScreen = null;
        }
    }

    @Override
    public void onDisable() {
        if (!fullNullCheck() && cancelledGui != null && open.getValue()) {
            mc.displayGuiScreen(cancelledGui);
        }
        cancelledGui = null;
    }
}
