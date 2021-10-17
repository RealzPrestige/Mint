package mint.managers;

import mint.Mint;
import mint.clickgui.MintGui;
import mint.events.RenderOverlayEvent;
import mint.events.RenderWorldEvent;
import mint.modules.Feature;
import mint.modules.Module;
import mint.modules.combat.*;
import mint.modules.core.*;
import mint.modules.miscellaneous.*;
import mint.modules.movement.*;
import mint.modules.player.*;
import mint.modules.visual.*;
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
    public ArrayList<Module> moduleList = new ArrayList<>();
    public List<Module> sortedModules = new ArrayList<>();
    public static Boolean doneLoad = true;

    public void init() {
        if (doneLoad) {
            SignExploit.nullCheck();
            doneLoad = false;
        }

        /** Core **/
        moduleList.add(new Gui());
        moduleList.add(new FontChanger());
        moduleList.add(new Descriptions());
        moduleList.add(new Notifications());
        moduleList.add(new RubberbandNotify());
        moduleList.add(new NoPotionHud());
        moduleList.add(new PacketManager());

        /** Combat **/
        moduleList.add(new AutoCrystal());
        moduleList.add(new AutoPiston());
        moduleList.add(new BowAmplifier());
        moduleList.add(new CityAnvil());
        moduleList.add(new Crits());
        moduleList.add(new HoleFiller());
        moduleList.add(new KillAura());
        moduleList.add(KotlinAura.INSTANCE);
        moduleList.add(new Offhand());
        moduleList.add(new SelfFill());
        //moduleList.add(new Surround());
        moduleList.add(new Waller());
        moduleList.add(new EntityPredict());

        /** Miscellaneous **/
        moduleList.add(new AutoEnderChest());
        moduleList.add(new Backpack());
        //moduleList.add(new BuildHeight());
        moduleList.add(new ChorusPredict());
        moduleList.add(new EntityCrammer());
        moduleList.add(new FakePlayer());
        //moduleList.add(new PacketManipulator());
        moduleList.add(new SelfAnvil());
        moduleList.add(new NoEntityTrace());
        moduleList.add(new TabTweaks());

        /** Movement **/
        moduleList.add(new Anchor());
        moduleList.add(new AntiWeb());
        moduleList.add(new BoatFly());
        moduleList.add(new Clip());
        moduleList.add(new LongJump());
        moduleList.add(new Phase());
        moduleList.add(new ReverseStep());
        moduleList.add(new SSS());
        moduleList.add(new Step());
        moduleList.add(new Strafe());
        moduleList.add(new YPort());

        /** Player **/
        moduleList.add(new AntiAim());
        //moduleList.add(new AutoLog());
        moduleList.add(new AutoMine());
        moduleList.add(new Blink());
        moduleList.add(new ChorusManipulator());
        moduleList.add(new FastPlace());
        moduleList.add(new Interaction());
        moduleList.add(new PacketEXP());
        moduleList.add(new Packetmine());

        /** Visual **/
        moduleList.add(new BreakESP());
        moduleList.add(new CripWalk());
        moduleList.add(new CrystalChams());
        moduleList.add(new Hand());
        moduleList.add(new HoleESP());
        moduleList.add(new NameTags());
        moduleList.add(new NoCluster());
        moduleList.add(new PlayerChams());
        moduleList.add(new PlayerTrails());
        moduleList.add(new PopESP());
        moduleList.add(new ShaderChams());
        moduleList.add(new SpawnESP());
        moduleList.add(new SwingAnimations());
        moduleList.add(new ViewTweaks());
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

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isEnabled();
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<>();
        for (Module module : this.moduleList) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<>();
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

    public void renderOverlayEvent(RenderOverlayEvent event) {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(module -> module.renderOveylayEvent(event));
    }

    public void renderWorldEvent(RenderWorldEvent event) {
        this.moduleList.stream().filter(Feature::isEnabled).forEach(module -> module.renderWorldLastEvent(event));
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

