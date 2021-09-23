package mint.modules.visual;

import mint.clickgui.setting.Setting;
import mint.events.Render3DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.Entity;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PlayerTrails extends Module {

    public Setting<Double> lineWidth = register(new Setting<>("Line Width", 2.0, 0.1, 5.0));
    public Setting<Boolean> fade = register(new Setting<>("Fade", false));

    public Setting<Integer> selfTime = register(new Setting<>("Remove Delay", 1000, 0, 2000));

    public Setting<Integer> startRed = register(new Setting<>("Start Red", 0, 0, 255));
    public Setting<Integer> startGreen = register(new Setting<>("Start Green", 0, 0, 255));
    public Setting<Integer> startBlue = register(new Setting<>("Start Blue", 0, 0, 255));
    public Setting<Integer> startAlpha = register(new Setting<>("Start Alpha", 100, 0, 255));

    public Setting<Integer> endRed = register(new Setting<>("End Red", 0, 0, 255));
    public Setting<Integer> endGreen = register(new Setting<>("End Green", 0, 0, 255));
    public Setting<Integer> endBlue = register(new Setting<>("End Blue", 0, 0, 255));
    public Setting<Integer> endAlpha = register(new Setting<>("End Alpha", 100, 0, 255));

    Map<UUID, ItemTrail> trails = new HashMap<>();

    public PlayerTrails() {
        super("PlayerTrails", Category.VISUAL, "");
    }

    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        if (trails.containsKey(mc.player.getUniqueID())) {
                final ItemTrail playerTrail = trails.get(mc.player.getUniqueID());
                playerTrail.timer.reset();
                final List<Position> toRemove = new ArrayList<>();
                for (final Position position : playerTrail.positions) {
                    if (System.currentTimeMillis() - position.time > selfTime.getValue().longValue()) {
                        toRemove.add(position);
                    }
                }
                playerTrail.positions.removeAll(toRemove);
                playerTrail.positions.add(new Position(mc.player.getPositionVector()));
        }
            else {
                trails.put(mc.player.getUniqueID(), new ItemTrail(mc.player));
            }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (fullNullCheck()) {
            return;
        }
        for (final Map.Entry<UUID, ItemTrail> entry : trails.entrySet()) {
            if (entry.getValue().entity.isDead || mc.world.getEntityByID(entry.getValue().entity.getEntityId()) == null) {
                if (entry.getValue().timer.isPaused()) {
                    entry.getValue().timer.reset();
                }
                entry.getValue().timer.setPaused(false);
            }
            if (!entry.getValue().timer.isPassed()) {
                drawTrail(entry.getValue());
            }
        }
    }

    void drawTrail(final ItemTrail trail) {
        final Color fadeColor = new Color(endRed.getValue(), endGreen.getValue(), endBlue.getValue(), endAlpha.getValue());
        RenderUtil.prepare();
        GL11.glLineWidth(lineWidth.getValue().floatValue());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        (RenderUtil.builder = RenderUtil.tessellator.getBuffer()).begin(3, DefaultVertexFormats.POSITION_COLOR);
        buildBuffer(RenderUtil.builder, trail, new Color(startRed.getValue(), startGreen.getValue(), startBlue.getValue(), startAlpha.getValue()), fade.getValue() ? fadeColor : new Color(startRed.getValue(), startGreen.getValue(), startBlue.getValue(), startAlpha.getValue()));
        RenderUtil.tessellator.draw();
        RenderUtil.release();
    }

    void buildBuffer(final BufferBuilder builder, final ItemTrail trail, final Color start, final Color end) {
        for (final Position p : trail.positions) {
            final Vec3d pos = RenderUtil.updateToCamera(p.pos);
            final double value = normalize(trail.positions.indexOf(p), trail.positions.size());
            RenderUtil.addBuilderVertex(builder, pos.x, pos.y, pos.z, ColorUtil.interpolate((float)value, start, end));
        }
    }

    double normalize(final double value, final double max) {
        return (value - 0.0) / (max - 0.0);
    }

    static class ItemTrail {
        public Entity entity;
        public List<Position> positions;
        public Timer timer;

        ItemTrail(Entity entity) {
            this.entity = entity;
            positions = new ArrayList<>();
            (timer = new Timer()).setDelay(1000);
            timer.setPaused(true);
        }
    }

    static class Position {
        public Vec3d pos;
        public long time;

        public Position(Vec3d pos) {
            this.pos = pos;
            time = System.currentTimeMillis();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Position position = (Position)o;
            return time == position.time && Objects.equals(pos, position.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, time);
        }
    }
}
