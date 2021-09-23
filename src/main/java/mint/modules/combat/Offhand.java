package mint.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.clickgui.setting.Setting;
import mint.events.Render2DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import mint.utils.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import java.util.HashMap;
import java.util.Map;

public class Offhand extends Module {

    public Setting<Boolean> itemParent = register(new Setting("Item", true, false));
    public Setting<Boolean> crystal = register(new Setting("Crystal", false, v-> itemParent.getValue()));
    public Setting<Boolean> crystalOnSword = register(new Setting("Sword Crystal", false, v-> !crystal.getValue() && itemParent.getValue()));
    public Setting<Boolean> crystalOnPickaxe = register(new Setting("Pickaxe Crystal", false, v-> !crystal.getValue() && itemParent.getValue()));

    public Setting<Boolean> miscParent = register(new Setting("Misc", true, false));
    public Setting<Integer> switchDelay = register(new Setting("Switch Delay", 50, 0, 200, v-> miscParent.getValue()));
    public Setting<Boolean> fallBack = register(new Setting("FallBack", false, v-> miscParent.getValue()));

    public Setting<Boolean> healthParent = register(new Setting("Health", true, false));
    public Setting<Float> totemHealth = register(new Setting("Totem Health", 10f, 0f, 20f, v-> healthParent.getValue()));
    public Setting<Boolean> hole = register(new Setting("Hole Check", false, v-> healthParent.getValue()));
    public Setting<Float> holeHealth = register(new Setting("Totem Hole Health", 10f, 0f, 20f, v-> healthParent.getValue() && hole.getValue()));

    public Setting<Boolean> fallDistanceParent = register(new Setting("Fall Distance", true, false));
    public Setting<Boolean> fallDistance = register(new Setting("Fall Distance Check", false, v-> fallDistanceParent.getValue()));
    public Setting<Float> minDistance = register(new Setting("Min Distance", 10f, 1f, 100f, v-> fallDistance.getValue() && fallDistanceParent.getValue()));

    public Setting<Boolean> gappleParent = register(new Setting("Gapple", true, false));
    public Setting<Boolean> gapple = register(new Setting("Gapple Switch", false, v-> gappleParent.getValue()));
    public Setting<Boolean> rightClick = register(new Setting("Right Click Only", false, v-> gapple.getValue() && gappleParent.getValue()));

    public Setting<Boolean> visualParent = register(new Setting("Visual", true, false));
    public Setting<RenderMode> render = register(new Setting("RenderMode", RenderMode.ALWAYS, v-> visualParent.getValue()));
    public enum RenderMode{ALWAYS, ONSWITCH, NEVER}


    Timer switchTimer = new Timer();
    HashMap<String, Integer> renderString = new HashMap();

    public Offhand(){
        super("Offhand", Category.COMBAT, "Changes your offhand item.");
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        int offhandSlot = InventoryUtil.getItemSlot(getOffhandItem());
        if (mc.player.getHeldItemOffhand().getItem() != getOffhandItem() && offhandSlot != -1 && switchTimer.passedMs(switchDelay.getValue())) {
            switchItem(offhandSlot < 9 ? offhandSlot + 36 : offhandSlot);
            mc.playerController.updateController();
            switchTimer.reset();
            if(render.getValue() != RenderMode.NEVER) {
                renderString.clear();
                renderString.put(getRendererString(), 255);
            }
        }
    }
    void switchItem(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
    }

    public void onRender2D(Render2DEvent event){
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        for (Map.Entry<String, Integer> entry : renderString.entrySet()) {
        if(render.getValue() == RenderMode.ALWAYS) {
            renderer.drawStringWithShadow("Current Item: " + ChatFormatting.BOLD + entry.getKey(), (screenWidth / 2f) - (renderer.getStringWidth("Current Item: " + entry.getKey()) / 2f), 0, ColorUtil.toRGBA(255, 255, 255, entry.getValue()));
           } else if(render.getValue() == RenderMode.ONSWITCH){
            renderString.put(entry.getKey(), entry.getValue() - 1);
            if (entry.getValue() <= 0) {
                renderString.remove(entry.getKey());
                return;
            }
            renderer.drawStringWithShadow("Offhand switched to Item: " + ChatFormatting.BOLD + entry.getKey(), (screenWidth / 2f) - (renderer.getStringWidth("Offhand switched to Item: " + entry.getKey()) / 2f), 0, ColorUtil.toRGBA(255, 255, 255, entry.getValue()));
          }
        }
     }

    Item getOffhandItem() {
        if(hole.getValue()) {

            if (!EntityUtil.isPlayerSafe(mc.player) && EntityUtil.getHealth(mc.player) < totemHealth.getValue()) return Items.TOTEM_OF_UNDYING;

            if (EntityUtil.isPlayerSafe(mc.player) && EntityUtil.getHealth(mc.player) < holeHealth.getValue()) return Items.TOTEM_OF_UNDYING;

        } else {

            if (EntityUtil.getHealth(mc.player) < totemHealth.getValue()) return Items.TOTEM_OF_UNDYING;

        }
        if (gapple.getValue() && ((rightClick.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && mc.gameSettings.keyBindUseItem.isKeyDown()) || (!rightClick.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD))) return Items.GOLDEN_APPLE;

        if(fallDistance.getValue() && mc.player.fallDistance > minDistance.getValue()) return Items.TOTEM_OF_UNDYING;

        if (!crystal.getValue() || (fallBack.getValue() && InventoryUtil.getStackCount(Items.END_CRYSTAL) == 0)) return Items.TOTEM_OF_UNDYING;

        if(!crystal.getValue() && crystalOnSword.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) return Items.END_CRYSTAL;

        if(!crystal.getValue() && crystalOnPickaxe.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE) return Items.END_CRYSTAL;

        if(crystal.getValue()) return Items.END_CRYSTAL;

        return Items.TOTEM_OF_UNDYING;
    }

    String getRendererString(){
        if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL){
            return "End Crystal";
        }
        if(mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING){
            return "Totem";
        }
        if(mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE){
            return "Golden Apple";
        }
        return "";
    }
}
