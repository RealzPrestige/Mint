package mint.clickgui.impl.alts.zprestige.ias;

import mint.clickgui.impl.alts.zprestige.MR;
import mint.clickgui.impl.alts.zprestige.ias.config.ConfigValues;
import mint.clickgui.impl.alts.zprestige.ias.events.ClientEvents;
import mint.clickgui.impl.alts.zprestige.ias.tools.Reference;
import mint.clickgui.impl.alts.zprestige.ias.tools.SkinTools;
import mint.clickgui.impl.alts.zprestige.iasencrypt.Standards;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid= Reference.MODID, name=Reference.MODNAME, clientSideOnly=true, guiFactory="client.gui.alts.zprestige.ias.config.IASGuiFactory", updateJSON = "http://thefireplace.bitnamiapp.com/jsons/ias.json", acceptedMinecraftVersions = "[1.11,)")
public class IAS {
	public static Configuration config;
	private static Property CASESENSITIVE_PROPERTY;
	private static Property ENABLERELOG_PROPERTY;

	public static void syncConfig(){
		ConfigValues.CASESENSITIVE = CASESENSITIVE_PROPERTY.getBoolean();
		ConfigValues.ENABLERELOG = ENABLERELOG_PROPERTY.getBoolean();
		if(config.hasChanged())
			config.save();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		CASESENSITIVE_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.CASESENSITIVE_NAME, ConfigValues.CASESENSITIVE_DEFAULT, I18n.format(ConfigValues.CASESENSITIVE_NAME+".tooltip"));
		ENABLERELOG_PROPERTY = config.get(Configuration.CATEGORY_GENERAL, ConfigValues.ENABLERELOG_NAME, ConfigValues.ENABLERELOG_DEFAULT, I18n.format(ConfigValues.ENABLERELOG_NAME+".tooltip"));
		syncConfig();
		if(!event.getModMetadata().version.equals("${version}"))//Dev environment needs to use a local list, to avoid issues
			Standards.updateFolder();
		else
			System.out.println("Dev environment detected!");
	}
	@EventHandler
	public void init(FMLInitializationEvent event){
		MR.init();
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		Standards.importAccounts();
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		SkinTools.cacheSkins();
	}
}
