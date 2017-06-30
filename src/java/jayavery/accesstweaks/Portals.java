/*******************************************************************************
 * Copyright (C) 2016 Jay Avery
 * 
 * This file is part of AccessTweaks. AccessTweaks is free software: distributed
 * under the GNU Affero General Public License (<http://www.gnu.org/licenses/>).
 ******************************************************************************/
package jayavery.accesstweaks;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/** Module to control visual effects of Nether portals. */
@EventBusSubscriber
public class Portals {
    
    /** Whether to show the visual effects of Nether portals. */
    private static boolean portalEffects = true;
    
    /** Config key for this module. */
    public static final String CONFIG_PORTALS = "portals";
    
    /** Updates settings of this module from the config. */
    public static void syncConfig(Configuration config) {
        
        portalEffects = config.get(CONFIG_PORTALS, "portalEffects", true)
                .getBoolean();
    }

    /** Cancels Nether portal visual effects according to setting. */
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent event) {
        
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
}
