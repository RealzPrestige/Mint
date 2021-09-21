package mint.modules.visual;

import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;


public class SwingAnimations extends Module {

    private final Setting<Switch> switchSetting = this.register(new Setting<>("Switch", Switch.ONEDOTEIGHT));
    private enum Switch {ONEDOTNINE, ONEDOTEIGHT}
    private final Setting<Swing> swing = this.register(new Setting<>("Swing", Swing.MAINHAND));
    private enum Swing {MAINHAND, OFFHAND, CANCEL}
    private final Setting<Speed> speed = this.register(new Setting<>("Speed", Speed.NORMAL));
    private enum Speed {SLOW, NORMAL, FAST}
    private final Setting<Integer> amplifier = this.register(new Setting<>("Amplifier", 14,1,255));

    public SwingAnimations() {
        super("Swing Animations", Category.VISUAL, "Tweaks the way your swing looks.");
    }
    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }

        if (switchSetting.getValue() == Switch.ONEDOTEIGHT && (double) mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
        }
        if(speed.getValue() == Speed.SLOW) {
            mc.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 10,amplifier.getValue()));
            mc.player.removePotionEffect(MobEffects.HASTE);
        } else if(speed.getValue() == Speed.NORMAL){
            mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
            mc.player.removePotionEffect(MobEffects.HASTE);
        } else if(speed.getValue() == Speed.FAST){
            mc.player.removePotionEffect(MobEffects.MINING_FATIGUE);
            mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 10,amplifier.getValue()));
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