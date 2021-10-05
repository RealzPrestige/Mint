package mint.modules.core;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.clickgui.setting.Setting;
import mint.events.Render2DEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Notifications extends Module {

    private static Notifications INSTANCE = new Notifications();
    public HashMap<String, Integer> notification = new HashMap<>();
    public static HashMap<String, Integer> TotemPopCounter = new HashMap<>();
    public boolean hasReachedEndState;
    public int waitTime;
    public int width;
    public Setting<Mode> mode = register(new Setting("Mode", Mode.CHAT));
    public enum Mode {CHAT, HUD, BOTH}
    public Setting<Boolean> targetsParent = register(new Setting<>("Targets", true, false));
    public Setting<Boolean> pops = register(new Setting("Pops", false, v -> targetsParent.getValue()));
    public Setting<Boolean> modules = register(new Setting("Modules", false, v -> targetsParent.getValue()));
    public Setting<Boolean> othersParent = register(new Setting<>("Others", false, true, v -> (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Integer> y = register(new Setting<>("y", 255, 0, 1000, v -> othersParent.getValue()));
    public Setting<Boolean> newMode = register(new Setting("New Mode", false, v -> othersParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Integer> staticTime = register(new Setting<>("Static Time", 30, 0, 100, v -> othersParent.getValue()));
    public Setting<Boolean> startColorParent = register(new Setting<>("Start Color", false, true, v -> (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> red = register(new Setting<>("Start Red", 255, 0, 255, v -> startColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> green = register(new Setting<>("Start Green", 110, 0, 255, v -> startColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> blue = register(new Setting<>("Start Blue", 255, 0, 255, v -> startColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> alpha = register(new Setting<>("Start Alpha", 255, 0, 255, v -> startColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Boolean> endColorParent = register(new Setting<>("End Color", false, true, v -> (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> endRed = register(new Setting<>("End Red", 255, 0, 255, v -> endColorParent.getValue() && ((mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))) && !newMode.getValue()));
    public Setting<Integer> endGreen = register(new Setting<>("End Green", 255, 0, 255, v -> endColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> endBlue = register(new Setting<>("End Blue", 0, 0, 255, v -> endColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> endAlpha = register(new Setting<>("End Alpha", 255, 0, 255, v -> endColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Boolean> outlineColorParent = register(new Setting<>("Outline Color", false, true, v -> (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> outlineRed = register(new Setting<>("Outline Red", 255, 0, 255, v -> outlineColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> outlineGreen = register(new Setting<>("Outline Green", 255, 0, 255, v -> outlineColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH) && !newMode.getValue())));
    public Setting<Integer> outlineBlue = register(new Setting<>("Outline Blue", 255, 0, 255, v -> outlineColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Integer> outlineAlpha = register(new Setting<>("Outline Alpha", 100, 0, 255, v -> outlineColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !newMode.getValue()));
    public Setting<Boolean> backgroundColorParent = register(new Setting<>("Background Color", false, true, v -> (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Integer> backgroundRed = register(new Setting<>("Background Red", 50, 0, 255, v -> backgroundColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Integer> backgroundGreen = register(new Setting<>("Background Green", 50, 0, 255, v -> backgroundColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Integer> backgroundBlue = register(new Setting<>("Background Blue", 50, 0, 255, v -> backgroundColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Integer> backgroundAlpha = register(new Setting<>("Background Alpha", 200, 0, 255, v -> backgroundColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Boolean> newColorParent = register(new Setting<>("New Color", false, true, v -> (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Boolean> rainbow = register(new Setting("Rainbow", false, v -> newColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH))));
    public Setting<Integer> newColorRed = register(new Setting<>("New Color Red", 50, 0, 255, v -> newColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !rainbow.getValue()));
    public Setting<Integer> newColorGreen = register(new Setting<>("New Color Green", 50, 0, 255, v -> newColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !rainbow.getValue()));
    public Setting<Integer> newColorBlue = register(new Setting<>("New Color Blue", 50, 0, 255, v -> newColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !rainbow.getValue()));
    public Setting<Integer> newColorAlpha = register(new Setting<>("New Color Alpha", 200, 0, 255, v -> newColorParent.getValue() && (mode.getValue() == Mode.HUD || mode.getValue().equals(Mode.BOTH)) && !rainbow.getValue()));
    public boolean lefinalewidth;

    public Notifications() {
        super("Notifications", Category.CORE, "Notifies you when stuff happens."); //ongh
        this.setInstance();
    }

    public void onRender2D(Render2DEvent event) {
        for (Map.Entry<String, Integer> entry : notification.entrySet()) {
            if (modules.getValue()) {
                String moduleString = entry.getKey();
                if(width > -renderer.getStringWidth(moduleString) - 7 && !lefinalewidth) {
                    width = width - 2;
                }
                if(width == -renderer.getStringWidth(moduleString) - 7 || width == -renderer.getStringWidth(moduleString) - 8){
                    lefinalewidth = true;
                }
                if (entry.getValue() > 950 && !hasReachedEndState) {
                    notification.put(entry.getKey(), entry.getValue() - 1);
                }
                if (entry.getValue() == 951) {
                    hasReachedEndState = true;
                }
                if (hasReachedEndState && waitTime == staticTime.getValue()) {
                    if(lefinalewidth){
                        width = width + 2;
                    }
                    notification.put(entry.getKey(), entry.getValue() + 1);
                }
                if (entry.getValue() > 1100) {
                    notification.remove(entry.getKey());
                }
                if (newMode.getValue()) {
                    RenderUtil.drawRect(entry.getValue() - renderer.getStringWidth(moduleString) - 5, y.getValue() - 5, entry.getValue() - renderer.getStringWidth(moduleString) + renderer.getStringWidth(moduleString) + 5, y.getValue() + renderer.getFontHeight() + 10, ColorUtil.toRGBA(backgroundRed.getValue(), backgroundGreen.getValue(), backgroundBlue.getValue(), backgroundAlpha.getValue()));
                    renderer.drawString(moduleString, entry.getValue() - renderer.getStringWidth(moduleString), y.getValue() + 2, -1, false);
                    RenderUtil.drawRect(entry.getValue() - renderer.getStringWidth(moduleString) - 5, y.getValue() - 5, entry.getValue() - renderer.getStringWidth(moduleString) + renderer.getStringWidth(moduleString) + 5 + width, y.getValue() + renderer.getFontHeight() + 10, rainbow.getValue() ? ColorUtil.rainbow(300).getRGB() : ColorUtil.toRGBA(newColorRed.getValue(), newColorGreen.getValue(), newColorBlue.getValue(), newColorAlpha.getValue()));
                    RenderUtil.drawRect(entry.getValue() - renderer.getStringWidth(moduleString) - 5, y.getValue() + renderer.getFontHeight() + 10, entry.getValue() - renderer.getStringWidth(moduleString) + renderer.getStringWidth(moduleString) + 5 - (waitTime * renderer.getStringWidth(moduleString) / 70), y.getValue() + renderer.getFontHeight() + 11, rainbow.getValue() ? ColorUtil.rainbow(300).getRGB() : ColorUtil.toRGBA(newColorRed.getValue(), newColorGreen.getValue(), newColorBlue.getValue(), newColorAlpha.getValue()));
                } else {
                    RenderUtil.drawRect(entry.getValue() - renderer.getStringWidth(moduleString) - 5, y.getValue() - 5, entry.getValue() - renderer.getStringWidth(moduleString) + renderer.getStringWidth(moduleString) + 5, y.getValue() + renderer.getFontHeight() + 10, ColorUtil.toRGBA(backgroundRed.getValue(), backgroundGreen.getValue(), backgroundBlue.getValue(), backgroundAlpha.getValue()));
                    RenderUtil.drawBorder(entry.getValue() - renderer.getStringWidth(moduleString) - 4, y.getValue() - 4, renderer.getStringWidth(moduleString) + 8, renderer.getFontHeight() + 13, new Color(outlineRed.getValue(), outlineGreen.getValue(), outlineBlue.getValue(), outlineAlpha.getValue()));
                    RenderUtil.drawGradientRect(entry.getValue() - renderer.getStringWidth(moduleString) - 4, y.getValue() - 5, entry.getValue() - renderer.getStringWidth(moduleString) + renderer.getStringWidth(moduleString) + 4, y.getValue() - 4, ColorUtil.toRGBA(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()), ColorUtil.toRGBA(endRed.getValue(), endGreen.getValue(), endBlue.getValue(), endAlpha.getValue()));
                    renderer.drawString(moduleString, entry.getValue() - renderer.getStringWidth(moduleString), y.getValue() + 2, -1, false);
                }
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
                    width = 0;
                    lefinalewidth = false;
                    MessageManager.sendRemovableMessage(ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", id);
                }
                if (pops.getValue() && (mode.getValue() == Mode.HUD || mode.getValue() == Mode.BOTH)) {
                    width = 0;
                    lefinalewidth = false;
                    Notifications.getInstance().notification.clear();
                    Notifications.getInstance().hasReachedEndState = false;
                    Notifications.getInstance().waitTime = 0;
                    notification.put(ChatFormatting.WHITE + "" + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", 1000);
                }
            } else {
                int id = 0;
                for (char character : player.getName().toCharArray()) {
                    id += character;
                    id *= 10;
                }
                if (pops.getValue() && (mode.getValue() == Mode.CHAT || mode.getValue() == Mode.BOTH)) {
                    width = 0;
                    lefinalewidth = false;
                    MessageManager.sendRemovableMessage(ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " died after popping " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", id);
                }
                if (pops.getValue() && (mode.getValue() == Mode.HUD || mode.getValue() == Mode.BOTH)) {
                    width = 0;
                    lefinalewidth = false;
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
                width = 0;
                lefinalewidth = false;
                MessageManager.sendRemovableMessage(ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totem.", id);
            }
            if (pops.getValue() && (mode.getValue() == Mode.HUD || mode.getValue() == Mode.BOTH)) {
                width = 0;
                lefinalewidth = false;
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
                width = 0;
                lefinalewidth = false;
                MessageManager.sendRemovableMessage(ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + ChatFormatting.BOLD + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", id);
            }
            if (pops.getValue() && (mode.getValue() == Mode.HUD || mode.getValue() == Mode.BOTH)) {
                width = 0;
                lefinalewidth = false;
                Notifications.getInstance().notification.clear();
                Notifications.getInstance().hasReachedEndState = false;
                Notifications.getInstance().waitTime = 0;
                notification.put(ChatFormatting.WHITE + player.getName() + ChatFormatting.RESET + ChatFormatting.RED + " has popped " + ChatFormatting.WHITE + totemCount + ChatFormatting.RESET + ChatFormatting.RED + " totems.", 1000);
            }
        }
    }

    //HAHAHAAA FUCK U KAMBING ONTICK TIMERS ON TOP EZZ
    public void onTick() {
        if (waitTime < staticTime.getValue()) {
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
