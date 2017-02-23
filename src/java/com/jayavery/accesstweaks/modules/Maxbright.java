package com.jayavery.accesstweaks.modules;

import com.jayavery.accesstweaks.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class Maxbright {
    
    @SubscribeEvent
    public void changeDimension(PlayerChangedDimensionEvent event) {
        
        int id = event.toDim;
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        
        if (id == -1) {
            
            if (netherBright) {
                
                settings.gammaSetting = 100F;
                
            } else {
                
                settings.loadOptions();
            }
            
        } else if (id == 1) {
            
            if (endBright) {
                
                settings.gammaSetting = 100F;
                
            } else {
                
                settings.loadOptions();
            }
            
        } else if (id == 0) {
            
            if (overworldBright) {
            
                settings.gammaSetting = 100F;
                
            } else {
                
                settings.loadOptions();
            }
        }
    }
    
    public static boolean netherBright = false;
    public static boolean endBright = false;
    public static boolean overworldBright = false;
    
    public static final String CONFIG_MAXBRIGHT = "maxbright";
    
    public static void syncConfig() {
        
        netherBright = Main.config.get(CONFIG_MAXBRIGHT, "netherBright", false)
                .getBoolean();
        endBright = Main.config.get(CONFIG_MAXBRIGHT, "endBright", false)
                .getBoolean();
        overworldBright = Main.config.get(CONFIG_MAXBRIGHT, "overworldBright",
                false).getBoolean();
    }
}
