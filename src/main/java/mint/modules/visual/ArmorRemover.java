package mint.modules.visual;

import mint.modules.Module;

public class ArmorRemover extends Module {
    static ArmorRemover INSTANCE = new ArmorRemover();

    public ArmorRemover() {
        super("Armor Remover", Category.VISUAL, "Removes armor from other lads or sumn");
        this.setInstance();
    }

    public static ArmorRemover getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ArmorRemover();
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }
}
