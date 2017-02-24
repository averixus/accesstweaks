package com.jayavery.accesstweaks.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

/** Module to override brightness of each dimension. */
public class Maxbright {
    
    /** Whether to display the Nether at max brightness. */
    private static boolean netherBright = false;
    /** Whether to display the End at max brightness. */
    private static boolean endBright = false;
    /** Whether to display the Overworld at max brightness. */
    private static boolean overworldBright = false;
    
    /** Config key for this module. */
    public static final String CONFIG_MAXBRIGHT = "maxbright";
    
    /** Updates the settings of this module from the config. */
    public static void syncConfig(Configuration config) {
        
        netherBright = config.get(CONFIG_MAXBRIGHT, "netherBright", false)
                .getBoolean();
        endBright = config.get(CONFIG_MAXBRIGHT, "endBright", false)
                .getBoolean();
        overworldBright = config.get(CONFIG_MAXBRIGHT, "overworldBright",
                false).getBoolean();
        
        if (Minecraft.getMinecraft().player != null) {
        
            updateBrightness(Minecraft.getMinecraft().player.dimension);
        }
    }
    
    /** Updates the brightness when the player first joins. */
    @SubscribeEvent
    public void joinWorld(EntityJoinWorldEvent event){

        if (event.getEntity() == Minecraft.getMinecraft().player) {

            updateBrightness(event.getWorld().provider.getDimension());
        }
        
    }
    
    /** Updates the brightness when the player changes dimension. */
    @SubscribeEvent
    public void changeDimension(PlayerChangedDimensionEvent event) {

        updateBrightness(event.toDim);
    }
    
    /** Sets the brightness according to the settings and dimension. */
    private static void updateBrightness(int id) {
        System.out.println("updating brightness for id " + id);
        System.out.println("netherbright? " + netherBright);
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
}
