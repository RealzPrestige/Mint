package mint.modules;

import mint.Mint;
import mint.events.Render2DEvent;
import mint.events.Render3DEvent;
import mint.clickgui.MintGui;
import mint.modules.visual.*;
import mint.modules.combat.*;
import mint.modules.core.*;
import mint.modules.movement.*;
import mint.modules.miscellaneous.*;
import mint.modules.player.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager
        extends Feature {
    public ArrayList<Module> moduleList = new ArrayList();
    public List<Module> sortedModules = new ArrayList<>();

    public void init() {
        /** Core **/
        moduleList.add(new Gui());
        moduleList.add(new FontChanger());
        moduleList.add(new Descriptions());
        moduleList.add(new Notifications());

        /** Combat **/
        moduleList.add(new HoleFiller());
        moduleList.add(new SelfFill());
        moduleList.add(new CrystalAura());
        moduleList.add(new AutoCrystal());
        moduleList.add(new Offhand());

        /** Miscellaneous **/
        moduleList.add(new FakePlayer());
        moduleList.add(new ChorusPredict());
        moduleList.add(new PacketCancel());

        /** Movement **/
        moduleList.add(new Clip());
        moduleList.add(new ReverseStep());
        moduleList.add(new Step());
        //moduleList.add(new Strafe());

        /** Player **/
        moduleList.add(new AntiAim());

        /** Visual **/
        moduleList.add(new SwingAnimations());
        moduleList.add(new HoleESP());
        moduleList.add(new NameTags());
        moduleList.add(new PopESP());
        moduleList.add(new CrystalChanger());
        moduleList.add(new PlayerChams());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.moduleList) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.moduleList) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }

    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.moduleList) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.moduleList) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList<String>();
        for (Module module : this.moduleList) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.moduleList.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.moduleList.stream().filter(Module::listening).forEach(((EventBus) MinecraftForge.EVENT_BUS)::register);
        this.moduleList.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }


    public void onLogout() {
        this.moduleList.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.moduleList.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.moduleList.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.moduleList.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.moduleList) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || Mint.INSTANCE.mc.currentScreen instanceof MintGui) {
            return;
        }
        this.moduleList.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }
}

