package mint.managers;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.events.*;
import mint.modules.Feature;
import mint.commands.Command;
import mint.modules.core.Notifications;
import mint.modules.visual.PopChams;
import mint.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.management.NotificationFilter;
import java.util.Objects;
import java.util.UUID;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class EventManager extends Feature {
    private final Timer logoutTimer = new Timer();
    private final Timer timer = new Timer();
    public int clickAlpha;
    EntityPlayer player = null;
    ModelPlayer playerModel = null;
    long startTime;
    public final static Minecraft mc = Minecraft.getMinecraft();

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onUnload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!fullNullCheck() && (event.getEntity().getEntityWorld()).isRemote && event.getEntityLiving().equals(Mint.INSTANCE.mc.player)) {
            Mint.moduleManager.onUpdate();
            Mint.moduleManager.sortModules(true);
        }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        this.logoutTimer.reset();
        Mint.moduleManager.onLogin();
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Mint.moduleManager.onLogout();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (fullNullCheck())
            return;
        Mint.moduleManager.onTick();
        for (EntityPlayer player : Mint.INSTANCE.mc.world.playerEntities) {
            if (player == null || player.getHealth() > 0.0F)
                continue;
        }
        if (clickAlpha > 0) {
            clickAlpha = clickAlpha - 3;
        }
        for (EntityPlayer player : Mint.INSTANCE.mc.world.playerEntities) {
            if (player == null || player.getHealth() > 0.0F) {
                continue;
            }
            if (Notifications.getInstance().isEnabled() && Notifications.getInstance().pops.getValue()) {
                Notifications.getInstance().onDeath(player);
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0)
            return;
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(Mint.INSTANCE.mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(Mint.INSTANCE.mc.world);
                if (PopChams.INSTANCE.isEnabled()) {
                    if (PopChams.getInstance().self.getValue() || player != mc.player) {
                        GameProfile profile = new GameProfile(mc.player.getUniqueID(), "");
                        player = new EntityOtherPlayerMP(mc.world, profile);
                        player.copyLocationAndAnglesFrom(player);
                        startTime = System.currentTimeMillis();
                    }

                }
                if (Notifications.getInstance().isEnabled() && Notifications.getInstance().pops.getValue()) {
                    Notifications.getInstance().onTotemPop(player);
                }
            }
        }
        if (event.getPacket() instanceof SPacketPlayerListItem && !fullNullCheck() && this.logoutTimer.passedS(1.0D)) {
            SPacketPlayerListItem packet = event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction()))
                return;
            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> (!Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null))
                    .forEach(data -> {
                        String name;
                        EntityPlayer entity;
                        UUID id = data.getProfile().getId();
                        switch (packet.getAction()) {
                            case ADD_PLAYER:
                                name = data.getProfile().getName();
                                MinecraftForge.EVENT_BUS.post(new ConnectionEvent(0, id, name));
                                break;
                            case REMOVE_PLAYER:
                                entity = Mint.INSTANCE.mc.world.getPlayerEntityByUUID(id);
                                if (entity != null) {
                                    String logoutName = entity.getName();
                                    MinecraftForge.EVENT_BUS.post(new ConnectionEvent(1, entity, id, logoutName));
                                    break;
                                }
                                MinecraftForge.EVENT_BUS.post(new ConnectionEvent(2, id, null));
                                break;
                        }
                    });
            //this is so chink
        } else if (event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT) {
            if (!timer.passedMs(100)) {
                MinecraftForge.EVENT_BUS.post(new ChorusEvent(((SPacketSoundEffect) event.getPacket()).getX(), ((SPacketSoundEffect) event.getPacket()).getY(), ((SPacketSoundEffect) event.getPacket()).getZ()));
                timer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (event.isCanceled())
            return;
        playerModel = new ModelPlayer(0, false);

        GL11.glLineWidth(1);

        int fillA = PopChams.getInstance().alpha.getValue();
        int lineA = 255;

        if (System.currentTimeMillis() - startTime > PopChams.getInstance().fadeStart.getValue()) {
            long time = System.currentTimeMillis() - startTime - PopChams.getInstance().fadeStart.getValue();
            double normal = normalize(((double) time), 0, PopChams.getInstance().fadeStart.getValue());
            normal = MathHelper.clamp(normal, 0, 1);
            normal = (-normal) + 1;
            lineA = (int) (normal * lineA);
            fillA = (int) (normal * fillA);
        }
        if (player != null && lineA > 0) {
            //TODO:add solid
            PopChams.RenderTesselator.prepareGL();
            if (PopChams.getInstance().outline.getValue()) {
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                glDisable(3553);
                glDisable(2896);
                glDisable(2929);
                glEnable(2848);
                glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(PopChams.getInstance().red.getValue() / 255.0f, PopChams.getInstance().green.getValue() / 255.0f, PopChams.getInstance().blue.getValue() / 255.0f, lineA / 255.0f);
                mc.getRenderManager().renderEntityStatic(player, event.getPartialTicks(), false);
                glEnable(2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
            PopChams.RenderTesselator.releaseGL();
        }

        Mint.INSTANCE.mc.profiler.startSection("mint");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1.0F);
        Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
        Mint.moduleManager.onRender3D(render3dEvent);
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        Mint.INSTANCE.mc.profiler.endSection();
    }

    @SubscribeEvent
    public void renderHUD(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
            Mint.textManager.updateResolution();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            ScaledResolution resolution = new ScaledResolution(Mint.INSTANCE.mc);
            Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
            Mint.moduleManager.onRender2D(render2DEvent);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState())
            Mint.moduleManager.onKeyPressed(Keyboard.getEventKey());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                Mint.INSTANCE.mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                if (event.getMessage().length() > 1) {
                    Mint.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                } else {
                    Command.sendMessage("Please enter a command.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Command.sendMessage(ChatFormatting.RED + "An error occurred while running this command. Check the log!");
            }
        }
    }

    double normalize(double value, double min, double max) {
        return ((value - min) / (max - min));
    }
}
