package mint.modules.visual;

import mint.setting.Setting;
import mint.modules.Module;
import mint.utils.NullUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumHand;


public class SwingAnimations extends Module {

    boolean urMomLikestosuckCOCKYEASHYEAH = true;
    private final Setting<Switch> switchSetting = register(new Setting<>("Switch", Switch.ONEDOTEIGHT));
    private enum Switch {ONEDOTNINE, ONEDOTEIGHT}
    private final Setting<Swing> swing = register(new Setting<>("Swing", Swing.MAINHAND));
    private enum Swing {MAINHAND, OFFHAND, CANCEL}
    public final Setting<Boolean> speed = register(new Setting<>("Speed", Boolean.valueOf(urMomLikestosuckCOCKYEASHYEAH)));
    public final Setting<Integer> amplifier = register(new Setting<>("SpeedVal", 1, 1, 1000));

    public SwingAnimations() {
        super("Swing", Category.VISUAL, "Tweaks the way your swing looks.");
    }
    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            return;
        }

        if (switchSetting.getValue() == Switch.ONEDOTEIGHT && (double) mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
    }

    public void onTick(){
        if(swing.getValue() == Swing.OFFHAND) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        } else if(swing.getValue() == Swing.MAINHAND){
            mc.player.swingingHand = EnumHand.MAIN_HAND;
        } else if(swing.getValue() == Swing.CANCEL){
            mc.player.isSwingInProgress = false;
            mc.player.swingProgressInt = 0;
            mc.player.swingProgress = 0.0f;
            mc.player.prevSwingProgress = 0.0f;
        }
    }

    @Override
    public void onDisable() {
        mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
        mc.player.removePotionEffect(MobEffects.HASTE);
    }
}