package com.jayavery.accesstweaks.modules;

import com.jayavery.accesstweaks.main.Main;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class Portals {
    
    public static final String CONFIG_PORTALS = "portals";
    
    public static boolean portalEffects = true;

    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        
        if (event.player instanceof EntityPlayerSP && !portalEffects) {
            
            EntityPlayerSP player = (EntityPlayerSP) event.player;
            
            if (player.prevTimeInPortal != 0) {
                
                player.prevTimeInPortal = 0;
            }
            
            if (player.timeInPortal != 0) {
                
                player.timeInPortal = 0;
            }
        }
    }
    
    public static void syncConfig() {
        
        portalEffects = Main.config.get(CONFIG_PORTALS, "portalEffects", true)
                .getBoolean();
    }
}
