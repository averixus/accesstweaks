/*******************************************************************************
 * Copyright (C) 2016 Jay Avery
 * 
 * This file is part of AccessTweaks. AccessTweaks is free software: distributed
 * under the GNU Affero General Public License (<http://www.gnu.org/licenses/>).
 ******************************************************************************/
package jayavery.accesstweaks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
        
        super(parentScreen, getConfigElements(), Accesstweaks.MODID,
                false, false, GuiConfig.getAbridgedConfigPath(
                Accesstweaks.config.toString()));
    }
    
    /** Constructs a list of all config elements needed for the gui. */
    private static List<IConfigElement> getConfigElements() {
        
        List<IConfigElement> result = new ArrayList<IConfigElement>();
  
        result.add(getChildren(Portals.CONFIG_PORTALS, "Nether portals"));
        result.add(getChildren(Particles.CONFIG_PARTICLES, "Particles"));
        result.add(getChildren(Sounds.CONFIG_SOUNDS, "Sounds and Subtitles"));
        result.add(getChildren(Maxbright.CONFIG_MAXBRIGHT, "Max Brightness"));
        
        return result;
    }
    
    /** Gets the sub elements for a config category. */
    private static IConfigElement getChildren(String category, String name) {
        
        return new DummyConfigElement.DummyCategoryElement(name, name,
                new ConfigElement(Accesstweaks.config.getCategory(category))
                .getChildElements());
    }
    
    /** Factory for config gui. */
    public static class Factory implements IModGuiFactory {

        @Override
        public void initialize(Minecraft minecraftInstance) {}
        
        @Override
        public boolean hasConfigGui() {
            
            return true;
        }

        @Override
        public GuiScreen createConfigGui(GuiScreen parent) {
            
            return new ConfigGui(parent);
        }

        @Override
        public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

            return Collections.emptySet();
        }
    }
}
