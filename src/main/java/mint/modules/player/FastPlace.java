package mint.modules.player;

import mint.clickgui.setting.Setting;
import mint.modules.Module;
import net.minecraft.init.Items;

public class FastPlace extends Module {

    public Setting <Boolean> exp = register(new Setting<>("Exp", false));
    public Setting <Boolean> crystal = register(new Setting<>("Crystals", false));

    public FastPlace(){
        super("Fast Place", Category.PLAYER, "Allows you to do things faster.");
    }

    @Override
    public void onUpdate(){
        if(fullNullCheck()){
            return;
        }
        if ((mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE) && exp.getValue()) {
            FastPlace.mc.rightClickDelayTimer = 0;
        }
        if ((mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL || mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) && crystal.getValue()){
            FastPlace.mc.rightClickDelayTimer = 0;
        }
    }
}
