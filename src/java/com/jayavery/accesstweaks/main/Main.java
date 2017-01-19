package com.jayavery.accesstweaks.main;

import com.jayavery.accesstweaks.modules.Myself;
import com.jayavery.accesstweaks.modules.Particles;
import com.jayavery.accesstweaks.modules.Portals;
import com.jayavery.accesstweaks.modules.Sounds;
import com.jayavery.accesstweaks.modules.Subtitles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

    @Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION,
         guiFactory = "com.jayavery.accesstweaks.utilities.ConfigGuiFactory")
    public class Main {

    public static final String MODID = "accesstweaks";
    public static final String NAME = "AccessTweaks";
    public static final String VERSION = "1.0-mc1.11";

    @Instance
    public static Main instance = new Main();

    public static Configuration config;

    public static Subtitles SUBTITLES;
    public static final Portals PORTALS = new Portals();
    public static Particles PARTICLES;
    public static final Sounds SOUNDS = new Sounds();
    public static final Myself MYSELF = new Myself();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        SUBTITLES = new Subtitles(Minecraft.getMinecraft());
        
        MinecraftForge.EVENT_BUS.register(PORTALS);
        MinecraftForge.EVENT_BUS.register(SOUNDS);
        MinecraftForge.EVENT_BUS.register(MYSELF);
        MinecraftForge.EVENT_BUS.register(SUBTITLES);

        FMLCommonHandler.instance().bus().register(instance);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        
        PARTICLES = new Particles();
        Minecraft.getMinecraft().renderGlobal = PARTICLES;
        ((IReloadableResourceManager) Minecraft.getMinecraft()
                .getResourceManager()).registerReloadListener(PARTICLES);
        PARTICLES.onResourceManagerReload(null);
        
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

        if (event.getModID().equals(MODID)) {

            syncConfig();
        }
    }

    public static void syncConfig() {
        
        Subtitles.syncConfig();
        Myself.syncConfig();
        Particles.syncConfig();
        Portals.syncConfig();
        Sounds.syncConfig();
        
        if (config.hasChanged()) {

            config.save();
        }
    }
}
