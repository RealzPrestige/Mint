package mint.modules.core;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.clickgui.setting.Setting;
import mint.events.Render2DEvent;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Notifications extends Module {

    private static Notifications INSTANCE = new Notifications();
    public HashMap<String, Integer> notification = new HashMap();
    public static HashMap<String, Integer> TotemPopCounter = new HashMap();
    public boolean hasReachedEndState;
    public int waitTime;

    public Setting<Mode> mode = register(new Setting("Mode", Mode.CHAT));
    public enum Mode{CHAT, HUD, BOTH}
    public Setting<Boolean> targetsParent = register(new Setting("Targets", true, false));
    public Setting<Boolean> pops = register(new Setting("Pops", false, v-> targetsParent.getValue()));
    public Setting<Boolean> modules = register(new Setting("Modules", false, v-> targetsParent.getValue()));
    public Setting<Boolean> othersParent = register(new Setting("Others", false, true, v-> mode.getValue() == Mode.HUD));
    public Setting<Integer> y = register(new Setting("y", 255, 0, 1000, v-> othersParent.getValue()));
    public Setting<Integer> staticTime = register(new Setting("StaticTime", 30, 0, 100, v-> othersParent.getValue()));
    public Setting<Boolean> startColorParent = register(new Setting("StartColor", false, true, v-> mode.getValue() == Mode.HUD));
    public Setting<Integer> red = register(new Setting("StartRed", 255, 0, 255, v-> startColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> green = register(new Setting("StartGreen", 110, 0, 255, v-> startColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> blue = register(new Setting("StartBlue", 255, 0, 255, v-> startColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> alpha = register(new Setting("StartAlpha", 255, 0, 255, v-> startColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Boolean> endColorParent = register(new Setting("EndColor", false, true, v-> mode.getValue() == Mode.HUD));
    public Setting<Integer> endRed = register(new Setting("EndRed", 255, 0, 255, v-> endColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> endGreen = register(new Setting("EndGreen", 255, 0, 255, v-> endColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> endBlue = register(new Setting("EndBlue", 0, 0, 255, v-> endColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> endAlpha = register(new Setting("EndAlpha", 255, 0, 255, v-> endColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Boolean> outlineColorParent = register(new Setting("OutlineColor", false, true, v-> mode.getValue() == Mode.HUD));
    public Setting<Integer> outlineRed = register(new Setting("OutlineRed", 255, 0, 255, v-> outlineColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> outlineGreen = register(new Setting("OutlineGreen", 255, 0, 255, v-> outlineColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> outlineBlue = register(new Setting("OutlineBlue", 255, 0, 255, v-> outlineColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> outlineAlpha = register(new Setting("OutlineAlpha", 100, 0, 255, v-> outlineColorParent.getValue() && mode.getValue() == Mode.HUD));

    public Setting<Boolean> backgroundColorParent = register(new Setting("BackgroundColor", false, true, v-> mode.getValue() == Mode.HUD));
    public Setting<Integer> backgroundRed = register(new Setting("BackgroundRed", 50, 0, 255, v-> backgroundColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> backgroundGreen = register(new Setting("BackgroundGreen", 50, 0, 255, v-> backgroundColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> backgroundBlue = register(new Setting("BackgroundBlue", 50, 0, 255, v-> backgroundColorParent.getValue() && mode.getValue() == Mode.HUD));
    public Setting<Integer> backgroundAlpha = register(new Setting("BackgroundAlpha", 200, 0, 255, v-> backgroundColorParent.getValue() && mode.getValue() == Mode.HUD));

    public Notifications(){
        super("Notifications", Category.CORE, "Notifies you when you toggle a Module.");
        this.setInstance();
    }

    public void onRender2D(Render2DEvent event){
        for(Map.Entry<String, Integer> entry : notification.entrySet()) {
            if (modules.getValue()) {
                String moduleString = entry.getKey();
                if (entry.getValue() > 950 && !hasReachedEndState) {
                    notification.put(entry.getKey(), entry.getValue() - 1);
                }
                if (entry.getValue() == 951) {
                    hasReachedEndState = true;
                }
                if (hasReachedEndState && waitTime == staticTime.getValue()) {
                    notification.put(entry.getKey(), entry.getValue() + 1);
                }
                if (entry.getValue() > 1100) {
                    notification.remove(entry.getKey());
                }
                RenderUtil.drawRect(entry.getValue() - renderer.getStringWidth(moduleString) - 5, y.getValue() - 5, entry.getValue() - renderer.getStringWidth(moduleString) + renderer.getStringWidth(moduleString) + 5, y.getValue() + renderer.getFontHeight() + 10, ColorUtil.toRGBA(backgroundRed.getValue(), backgroundGreen.getValue(), backgroundBlue.getValue(), backgroundAlpha.getValue()));
                RenderUtil.drawBorder(entry.getValue() - renderer.getStringWidth(moduleString) - 4, y.getValue() - 4, renderer.getStringWidth(moduleString) + 8, renderer.getFontHeight() + 13, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()));
                RenderUtil.drawGradientRect(entry.getValue() - renderer.getStringWidth(moduleString) - 4, y.getValue() - 5, entry.getValue() - renderer.getStringWidth(moduleString) + renderer.getStringWidth(moduleString) + 4, y.getValue() - 4, ColorUtil.toRGBA(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()), ColorUtil.toRGBA(endRed.getValue(), endGreen.getValue(), endBlue.getValue(), endAlpha.getValue()));
                renderer.drawString(moduleString, entry.getValue() - renderer.getStringWidth(moduleString), y.getValue() + 2, -1, false);
            }
        }
    }

    public void onDeath(EntityPlayer player) {
        if (TotemPopCounter.containsKey(player.getName())) {
            int totemCount = TotemPopCounter.get(player.getName());
            TotemPopCounter.remove(player.getName());
            if (totemCount == 1) {
                int id = 0;
                for (char character : player.getName().toCharArray()) {
                    id += character;
                    id *= 10;
                }
                if (pops.getValue() && (mode.getValue() == Mode.CHAT || mode.getValue() == Mode.BOTH)) {
                    mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem."), id);
                }
                if(pops.getValue() && (mode.getValue() == Mode.HUD || mode.getValue() == Mode.BOTH)) {
                    Notifications.getInstance().notification.clear();
                    Notifications.getInstance().hasReachedEndState = false;
                    Notifications.getInstance().waitTime = 0;
                    notification.put(ChatFormatting.WHITE + "" + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", 1000);
                }
            }else {
                int id = 0;
                for (char character : player.getName().toCharArray()) {
                    id += character;
                    id *= 10;
                }
                if (pops.getValue() && (mode.getValue() == Mode.CHAT || mode.getValue() == Mode.BOTH)) {
                    mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems."), id);
                }
                if(pops.getValue() && (mode.getValue() == Mode.HUD || mode.getValue() == Mode.BOTH)) {
                    Notifications.getInstance().notification.clear();
                    Notifications.getInstance().hasReachedEndState = false;
                    Notifications.getInstance().waitTime = 0;
                    notification.put(ChatFormatting.WHITE + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", 1000);
                }
            }
        }
    }

    public void onTotemPop(EntityPlayer player) {
        if (fullNullCheck()) {
            return;
        }
        if (mc.player.equals(player)) {
            return;
        }
        int totemCount = 1;
        if (TotemPopCounter.containsKey(player.getName())) {
            totemCount = TotemPopCounter.get(player.getName());
            TotemPopCounter.put(player.getName(), ++totemCount);
        } else {
            TotemPopCounter.put(player.getName(), totemCount);
        }
        if (totemCount == 1) {
            int id = 0;
            for (char character : player.getName().toCharArray()) {
                id += character;
                id *= 10;
            }
            if (pops.getValue() && (mode.getValue() == Mode.CHAT || mode.getValue() == Mode.BOTH)) {
                mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Mint.commandManager.getClientMessage() + " " + ChatFormatting.WHITE + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem."), id);
            }
            if(pops.getValue() && (mode.getValue() == Mode.HUD || mode.getValue() == Mode.BOTH)){
                Notifications.getInstance().notification.clear();
                Notifications.getInstance().hasReachedEndState = false;
                Notifications.getInstance().waitTime = 0;
                notification.put(ChatFormatting.WHITE + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", 1000);
            }
        } else {
            int id = 0;
            for (char character : player.getName().toCharArray()) {
                id += character;
                id *= 10;
            }
            if (pops.getValue() && (mode.getValue() == Mode.CHAT || mode.getValue() == Mode.BOTH)) {
                mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems."), id);
            }
            if(pops.getValue() && (mode.getValue() == Mode.HUD || mode.getValue() == Mode.BOTH)){
                Notifications.getInstance().notification.clear();
                Notifications.getInstance().hasReachedEndState = false;
                Notifications.getInstance().waitTime = 0;
                notification.put(ChatFormatting.WHITE + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", 1000);
            }
        }
    }
    //HAHAHAAA FUCK U KAMBING ONTICK TIMERS ON TOP EZZ
    public void onTick(){
        if(waitTime < staticTime.getValue()){
            ++waitTime;
        }
    }

    public static Notifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifications();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

}
