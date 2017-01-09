package com.jayavery.accesstweaks.main;

import java.util.ArrayList;
import java.util.List;
import com.jayavery.accesstweaks.modules.Myself;
import com.jayavery.accesstweaks.modules.Particles;
import com.jayavery.accesstweaks.modules.Portals;
import com.jayavery.accesstweaks.modules.Sounds;
import com.jayavery.accesstweaks.modules.Subtitles;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
        
        super(parentScreen, getConfigElements(), Main.MODID, false, false,
                GuiConfig.getAbridgedConfigPath(Main.config.toString()));
    }
    
    private static List<IConfigElement> getConfigElements() {
        
        List<IConfigElement> result = new ArrayList<IConfigElement>();
  
        result.add(getChildren(Subtitles.CONFIG_SUBTITLES, "Subtitles"));
        result.add(getChildren(Portals.CONFIG_PORTALS, "Nether portals"));
        result.add(getChildren(Particles.CONFIG_PARTICLES, "Particles"));
        result.add(getChildren(Sounds.CONFIG_SOUNDS, "Sounds"));
        result.add(getChildren(Myself.CONFIG_MYSELF,
                "My player sounds and subtitles"));
        
        return result;
    }
    
    private static IConfigElement getChildren(String category, String name) {
        
        return new DummyConfigElement.DummyCategoryElement(name, name,
                new ConfigElement(Main.config.getCategory(category))
                .getChildElements());
    }
}
