package mint.modules.miscellaneous;

import mint.clickgui.setting.Setting;
import mint.managers.MessageManager;
import mint.modules.Module;

public class PacketManipulator extends Module {

    public PacketManipulator() {
        super("PacketManipulator", Module.Category.MISCELLANEOUS, "Manipulates packets.");
    }

    /*
    a test thingy dont mind it for now
     */

    public Setting<Boolean> cancel = register(new Setting("Cancel", true, false));
    public Setting<Boolean> s = register(new Setting("Server", true, false, v -> cancel.getValue()));
    public Setting<Boolean> c = register(new Setting("Client", true, false, v -> cancel.getValue()));

    public Setting<Boolean> send = register(new Setting("Send", true, false));
    public Setting<Integer> packet1 = register(new Setting("Packet1", 1, 1, 10, v -> send.getValue()));

    @Override
    public void onUpdate() {
        MessageManager.sendMessage("1=");
        MessageManager.sendMessage("2=");
        MessageManager.sendMessage("3=");
        MessageManager.sendMessage("4=");
        MessageManager.sendMessage("5=");
        MessageManager.sendMessage("6=");
    }
}