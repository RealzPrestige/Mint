package mint.clickgui;

import mint.Mint;
import mint.modules.Feature;
import mint.clickgui.impl.Component;
import mint.clickgui.impl.Frame;
import mint.clickgui.impl.buttons.ModuleFrame;
import mint.modules.Module;
import mint.modules.client.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class MintGui
        extends GuiScreen {
    private static MintGui INSTANCE;

    static {
        INSTANCE = new MintGui();
    }

    private final ArrayList<Component> components = new ArrayList();

    public MintGui() {
        this.setInstance();
        this.load();
    }

    public static MintGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MintGui();
        }
        return INSTANCE;
    }
    @Override
    public void initGui() {
        if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer && Gui.getInstance().blur.getValue()) {
            if (mc.entityRenderer.getShaderGroup() != null) {
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
    }
            public static MintGui getClickGui() {
        return MintGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -119;
        for (final Module.Category category : Mint.moduleManager.getCategories()) {
            this.components.add(new Component(category.getName(), x += 121, 2, true) {

                @Override
                public void setupItems() {
                    counter1 = new int[]{1};
                    Mint.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton(new ModuleFrame(module));
                        }
                    });
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    public void updateModule(Module module) {
        for (Component component : this.components) {
            for (Frame item : component.getItems()) {
                if (!(item instanceof ModuleFrame)) continue;
                ModuleFrame button = (ModuleFrame) item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.checkMouseWheel();
        //this.drawDefaultBackground();
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) continue;
            return component;
        }
        return null;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }
            public void drawGradient(double left, double top, double right, double bottom, int startColor, int endColor) {
        drawGradientRect((int)left, (int)top, (int)right, (int)bottom, startColor, endColor);
    }
}

