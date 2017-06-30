/*******************************************************************************
 * Copyright (C) 2016 Jay Avery
 * 
 * This file is part of AccessTweaks. AccessTweaks is free software: distributed
 * under the GNU Affero General Public License (<http://www.gnu.org/licenses/>).
 ******************************************************************************/
package jayavery.accesstweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

    @Mod(modid = Accesstweaks.MODID, name = Accesstweaks.NAME, version = Accesstweaks.VERSION, guiFactory = Accesstweaks.GUI, clientSideOnly = true)
    @EventBusSubscriber
    public class Accesstweaks {

    public static final String MODID = "accesstweaks";
    public static final String NAME = "AccessTweaks";
    public static final String VERSION = "3.0.0";
    public static final String GUI = "jayavery.accesstweaks.ConfigGui$Factory";

    @Instance
    public static Accesstweaks instance = new Accesstweaks();

    public static Configuration config;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        
        Minecraft.getMinecraft().effectRenderer =
                new Particles(Minecraft.getMinecraft().world,
                Minecraft.getMinecraft().renderEngine);
    }

    @SubscribeEvent
    public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

        if (event.getModID().equals(MODID)) {

            syncConfig();
        }
    }

    /** Syncs the config for all modules. */
    private static void syncConfig() {

        Particles.syncConfig(config);
        Portals.syncConfig(config);
        Sounds.syncConfig(config);
        Maxbright.syncConfig(config);
                
        if (config.hasChanged()) {

            config.save();
        }
    }
}
