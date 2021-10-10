package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import mint.utils.InventoryUtil;
import net.minecraft.init.Items;

public class FastPlace extends Module {

    public Setting <Boolean> exp = register(new Setting<>("Exp", false));
    public Setting <Boolean> crystal = register(new Setting<>("Crystals", false));

    public FastPlace(){
        super("Fast Place", Category.PLAYER, "Allows you to do things faster.");
    }

    @Override
    public void onUpdate(){
        if (fullNullCheck()) {
            return;
        }
        if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both) && exp.getValue()) {
            mc.rightClickDelayTimer = 0;
        }
        if (InventoryUtil.heldItem(Items.END_CRYSTAL, InventoryUtil.Hand.Both) && crystal.getValue()){
            mc.rightClickDelayTimer = 0;
        }
    }
}
