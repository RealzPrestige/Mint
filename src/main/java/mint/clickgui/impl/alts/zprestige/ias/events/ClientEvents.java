package mint.clickgui.impl.alts.zprestige.ias.events;

import mint.clickgui.impl.alts.tools.Config;
import mint.clickgui.impl.alts.zprestige.ias.IAS;
import mint.clickgui.impl.alts.zprestige.ias.gui.GuiAccountSelector;
import mint.clickgui.impl.alts.zprestige.ias.gui.GuiButtonWithImage;
import mint.clickgui.impl.alts.zprestige.ias.tools.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEvents {
	@SubscribeEvent
	public void guiEvent(InitGuiEvent.Post event){
		GuiScreen gui = event.getGui();
		if(gui instanceof GuiMultiplayer){
			event.getButtonList().add(new GuiButtonWithImage(20, gui.width - 110, gui.height - 30, 100, 20, "Alt Manager"));
		}
	}
	@SubscribeEvent
	public void onClick(ActionPerformedEvent event){
		if(event.getGui() instanceof GuiMultiplayer && event.getButton().id == 20){
			if(Config.getInstance() == null){
				Config.load();
			}
			Minecraft.getMinecraft().displayGuiScreen(new GuiAccountSelector());
		}
	}
	@SubscribeEvent
	public void onTick(TickEvent.RenderTickEvent t) {
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if (screen instanceof GuiMultiplayer) {
			screen.drawCenteredString(Minecraft.getMinecraft().fontRenderer, I18n.format("Logged in as ") + Minecraft.getMinecraft().getSession().getUsername()+".", screen.width / 2, screen.height / 4 - 130, 0xFFCC8888);
		}else if(screen instanceof GuiMultiplayer){
			if (Minecraft.getMinecraft().getSession().getToken().equals("0")) {
				screen.drawCenteredString(Minecraft.getMinecraft().fontRenderer, I18n.format("Offline mode"), screen.width / 2, 10, 16737380);
			}
		}
	}
	@SubscribeEvent
	public void configChanged(ConfigChangedEvent event){
		if(event.getModID().equals(Reference.MODID)){
			IAS.syncConfig();
		}
	}
}
