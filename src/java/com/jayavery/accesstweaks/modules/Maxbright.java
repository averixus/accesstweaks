package com.jayavery.accesstweaks.modules;

import com.jayavery.accesstweaks.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class Maxbright {
    
    @SubscribeEvent
    public void render(RenderTickEvent event) {
        
        if (maxbright && Minecraft.getMinecraft().gameSettings
                .gammaSetting !=100F) {
        
            Minecraft.getMinecraft().gameSettings.gammaSetting = 100F;
        }
    }
    
    public static boolean maxbright = false;
    
    public static final String CONFIG_MAXBRIGHT = "maxbright";
    
    public static void syncConfig() {
        
        maxbright = Main.config.get(CONFIG_MAXBRIGHT, "maxbright", false)
                .getBoolean();
    }
}
