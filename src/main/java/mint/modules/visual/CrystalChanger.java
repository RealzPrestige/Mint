package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.PacketEvent;
import mint.events.RenderEntityModelEvent;
import mint.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CrystalChanger extends Module {
    private static CrystalChanger INSTANCE;
    public int limbswinga;

    public CrystalChanger() {
        super("CrystalChanger", Category.VISUAL,"Tweaks looks of End crystals.");
        this.setInstance();
    }

    private void setInstance() {
        CrystalChanger.INSTANCE = this;
    }

    public static CrystalChanger getInstance() {
        if (CrystalChanger.INSTANCE == null) {
            CrystalChanger.INSTANCE = new CrystalChanger();
        }
        return CrystalChanger.INSTANCE;
    }

    public Setting<Boolean> chams = register(new Setting<>("Chams", true));
    public Setting<Boolean> wireframe = register(new Setting<>("Wireframe", true));
    public Setting<Boolean> throughwalls = register(new Setting<>("Walls", true));
    public Setting<Boolean> glow = register(new Setting<>("Glow", false));
    public Setting<Boolean> sync = register(new Setting("Sync", false));
    public Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255, v -> this.chams.getValue() && !sync.getValue()));
    public Setting<Integer> green = register(new Setting<>("Green", 255, 0, 255, v -> this.chams.getValue() && !sync.getValue()));
    public Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255, v -> this.chams.getValue() && !sync.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("Alpha", 150, 0, 255, v -> this.chams.getValue()));
    public Setting<Boolean> wallsSync = register(new Setting("WallsSync", false));
    public Setting<Integer> wallsRed = register(new Setting<>("WallsRed", 255, 0, 255, v -> this.throughwalls.getValue() && !wallsSync.getValue()));
    public Setting<Integer> wallsGreen = register(new Setting<>("WallsGreen", 255, 0, 255, v -> this.throughwalls.getValue() && !wallsSync.getValue()));
    public Setting<Integer> wallsBlue = register(new Setting<>("WallsBlue", 255, 0, 255, v -> this.throughwalls.getValue() && !wallsSync.getValue()));
    public Setting<Integer> wallsAlpha = register(new Setting<>("WallsAlpha", 150, 0, 255, v -> this.throughwalls.getValue()));

    public Setting<Double> width = register(new Setting<>("LineWidth", 3.0, 0.1, 5.0));
    public Setting<Double> scale = register(new Setting<>("Scale", 1.0, 0.1, 3.0));

    public Map<EntityEnderCrystal, Float> scaleMap = new ConcurrentHashMap<>();


    @Override
    public void onUpdate() {
        for (Entity crystal : mc.world.loadedEntityList) {
            if (crystal instanceof EntityEnderCrystal) {
                if (!this.scaleMap.containsKey(crystal)) {
                    this.scaleMap.put((EntityEnderCrystal) crystal, 3.125E-4f);
                } else {
                    try {
                        this.scaleMap.put((EntityEnderCrystal) crystal, this.scaleMap.get(crystal) + 3.125E-4f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!(this.scaleMap.get(crystal) >= 0.0625f * this.scale.getValue()))
                    continue;

                this.scaleMap.remove(crystal);
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = event.getPacket();
            for (int id : packet.getEntityIDs()) {
                try {
                    Entity entity = mc.world.getEntityByID(id);
                    if (entity instanceof EntityEnderCrystal) {
                        this.scaleMap.remove(entity);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void onRenderModel(RenderEntityModelEvent event) {
        if (event.getStage() != 0 || !(event.entity instanceof EntityEnderCrystal) || !this.wireframe.getValue()) {
            return;
        }
        mc.gameSettings.fancyGraphics = false;
        mc.gameSettings.gammaSetting = 10000.0f;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glPolygonMode(1032, 6913);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        if (this.throughwalls.getValue()) {
            GL11.glDisable(2929);
        }
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((float) red.getValue() / 255.0f, (float) green.getValue() / 255.0f, (float) blue.getValue() / 255.0f, (float) alpha.getValue() / 255.0f);
        GlStateManager.glLineWidth(this.width.getValue().floatValue());
        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

}
