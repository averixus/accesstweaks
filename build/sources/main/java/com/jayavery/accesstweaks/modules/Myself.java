package com.jayavery.accesstweaks.modules;

import com.jayavery.accesstweaks.main.Main;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Myself {
    
    public static final String CONFIG_MYSELF = "myself";
    
    public static boolean myself;

    @SubscribeEvent
    public void playEntitySound(PlaySoundAtEntityEvent event) {
        
        if (Minecraft.getMinecraft().thePlayer == null ||
                event.getEntity() != Minecraft.getMinecraft().thePlayer) {
            
            return;
        }
                
        if (!myself) {
            
            event.setCanceled(true);
        }
    }
    
    public static void syncConfig() {
        
        myself = Main.config.get(CONFIG_MYSELF, "myself", true).getBoolean();
    }
}
