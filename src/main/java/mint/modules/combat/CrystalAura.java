package mint.modules.combat;

import mint.clickgui.setting.BindSetting;
import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.InventoryUtil;
import mint.utils.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalAura extends Module {

    public CrystalAura(){
        super("Crystal Aura", Module.Category.COMBAT, "Automatically places and breaks crystals.");
    }

    public Setting<Boolean> parentBreak = register(new Setting("Break", true, false));
    public Setting<Float> breakRange = register(new Setting("BreakRange", 6.0f, 0.1f, 6.0f, v -> parentBreak.getValue()));
    public Setting<Float> breakMinDmg = register(new Setting("BreakMinDamage", 6.5f, 0.1f, 36.0f, v -> parentBreak.getValue()));
    public Setting<Float> breakMaxSelf = register(new Setting("BreakMaxSelfDamage", 13.5f, 0.1f, 36.0f, v -> parentBreak.getValue()));
    public Setting<Boolean> predictBreak = register(new Setting("Predict", true, v-> parentBreak.getValue()));

    public Setting<Boolean> parentPlace = register(new Setting("Place", true, false));
    public Setting<Float> placeRange = register(new Setting("PlaceRange", 5.0f, 0.1f, 6.0f, v -> parentPlace.getValue()));
    public Setting<Float> placeMinDmg = register(new Setting("PlaceMinDamage", 6.0f, 0.1f, 36.0f, v -> parentPlace.getValue()));
    public Setting<Float> placeMaxSelf = register(new Setting("PlaceMaxSelfDamage", 8.0f, 0.1f, 36.0f, v -> parentPlace.getValue()));

    public Setting<Boolean> targetParent = register(new Setting("Target", true, false));
    public Setting<Float> targetRange = register(new Setting("TargetRange", 12.0f, 0.1f, 15.0f, v -> targetParent.getValue()));

    public Setting<Boolean> parentVisual = register(new Setting("Visual", true, false));
    public Setting<Boolean> damageRender = register(new Setting("DamageText", false, v-> parentVisual.getValue()));
    public Setting<Boolean> boxParent = register(new Setting("Box", false, true, v-> parentVisual.getValue()));
    public Setting<Boolean> boxSetting = register(new Setting("BoxSetting", false, v-> boxParent.getValue()));
    public Setting<Integer> boxRed = register(new Setting<>( "BoxRed", 255, 0, 255, v-> boxParent.getValue()));
    public Setting<Integer> boxGreen = register(new Setting<>("BoxGreen", 255, 0, 255, v-> boxParent.getValue()));
    public Setting<Integer> boxBlue = register(new Setting<>("BoxBlue", 255, 0, 255, v-> boxParent.getValue()));
    public Setting<Integer> boxAlpha = register(new Setting<>("BoxAlpha", 120, 0, 255, v-> boxParent.getValue()));
    public Setting<Boolean> outlineParent = register(new Setting("Outline", false, true, v-> parentVisual.getValue()));
    public Setting<Boolean> outlineSetting = register(new Setting("OutlineSetting", false, v-> boxParent.getValue()));
    public Setting<Integer> outlineRed = register(new Setting<>( "OutlineRed", 255, 0, 255, v-> boxParent.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("OutlineGreen", 255, 0, 255, v-> boxParent.getValue()));
    public Setting<Integer> outlineBlue = register(new Setting<>("OutlineBlue", 255, 0, 255, v-> boxParent.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("OutlineAlpha", 120, 0, 255, v-> boxParent.getValue()));

    public Setting<Boolean> parentFacePlace = register(new Setting("FacePlace", true, false));
    public Setting<Boolean> health = register(new Setting("Health", false, false, v-> parentFacePlace.getValue()));
    public Setting<Float> healthAmount = register(new Setting("HealthAmount", 7.4f, 0.1f, 36.0f, v -> parentFacePlace.getValue() && health.getValue()));
    public Setting<Boolean> armor = register(new Setting("Armor", false, false, v-> parentFacePlace.getValue()));
    public Setting<Integer> armorPercent = register(new Setting("ArmorPercent", 20, 0, 100, v -> parentFacePlace.getValue() && armor.getValue()));
    public Setting<Boolean> bind = register(new Setting("Bind", false, false, v-> parentFacePlace.getValue()));
    public Setting<BindSetting> facePlaceBind = register(new Setting<>("FaceplaceBind:", new BindSetting(1), v-> !parentFacePlace.getValue() && bind.getValue()));

    public Setting<Boolean> parentMisc = register(new Setting("Misc", true, false));
    public Setting<AutoSwitch> autoSwitch = register(new Setting("AutoSwitch", AutoSwitch.None));
    public Setting<Integer> resetDelay = register(new Setting("ResetDelay", 100, 1, 250, v -> parentMisc.getValue()));

    private final Timer resetTimer = new Timer();
    public EntityPlayer Target;
    private BlockPos Position;
    int originalSlot;
    int crystalSlot;

    @Override
    public void onToggle() {
        Target = null;
        Position = null;
    }

    @Override
    public void onUpdate() {
        if (resetTimer.passedMs(resetDelay.getValue())) {
            Target = null;
            Position = null;
        }
        doBreak();
        doPlace();
    }

    public void doBreak() {
        Target = EntityUtil.getTarget(targetRange.getValue());
        if (Target == null) {
            return;
        }

        //do break
    }

    public void doPlace() {
        Target = EntityUtil.getTarget(targetRange.getValue());
        crystalSlot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        originalSlot = mc.player.inventory.currentItem;
        if (Target == null) {
            return;
        }

        switch (autoSwitch.getValue()) {
            case Silent:
                InventoryUtil.SilentSwitchToSlot(crystalSlot);
                break;

            case Normal:
        }

        //do placing here

        switch (autoSwitch.getValue()) {
            case Silent:
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
                break;

            case Normal:
        }


    }
        //Position == ;

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof SPacketSpawnObject && predictBreak.getValue()) {
            final SPacketSpawnObject packet = e.getPacket();

            if (packet.getType() == 51 && Position != null && Target != null) {
                final CPacketUseEntity predict = new CPacketUseEntity();
                predict.entityId = packet.getEntityID();
                predict.action = CPacketUseEntity.Action.ATTACK;
                mc.getConnection().sendPacket(predict);
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    public enum AutoSwitch {
        None,
        Silent,
        Normal
    }
}