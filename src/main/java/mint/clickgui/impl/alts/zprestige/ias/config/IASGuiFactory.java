package mint.clickgui.impl.alts.zprestige.ias.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class IASGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {}

  @Override
  public boolean hasConfigGui() {
    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
    return new IASConfigGui(parentScreen);
  }

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}
}
