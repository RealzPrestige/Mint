package mint.modules.combat;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.utils.EntityUtil;
import mint.utils.Timer;
import net.minecraft.entity.player.EntityPlayer;
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
    public Setting<Boolean> predictBreak = register(new Setting("Predict", true));

    public Setting<Boolean> parentPlace = register(new Setting("Place", true, false));
    public Setting<Float> placeRange = register(new Setting("PlaceRange", 6.0f, 0.1f, 6.0f, v -> parentPlace.getValue()));


    public Setting<Boolean> parentVisual = register(new Setting("Visual", true, false));
    public Setting<Boolean> damageRender = register(new Setting("DamageText", false));


    public Setting<Boolean> parentMisc = register(new Setting("Misc", true, false));
    public Setting<Float> scanRange = register(new Setting("ScanRange", 12.0f, 0.1f, 15.0f, v -> parentMisc.getValue()));
    public Setting<Integer> resetDelay = register(new Setting("ResetDelay", 100, 1, 250, v -> parentMisc.getValue()));


    private final Timer resetTimer = new Timer();
    public EntityPlayer Target;
    private BlockPos Position;

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
        Target = EntityUtil.getTarget(scanRange.getValue());
        if (Target == null) {
            return;
        }
    }

    public void doPlace() {
        Target = EntityUtil.getTarget(scanRange.getValue());
        if (Target == null) {
            return;
        }
        //Position == ;
    }

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
}