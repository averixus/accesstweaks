package com.jayavery.accesstweaks.utilities;

import java.util.Collections;
import java.util.Set;
import com.jayavery.accesstweaks.main.ConfigGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

/** Factory for config gui. */
public class ConfigGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {

        return ConfigGui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

        return Collections.emptySet();
    }

    @Override
    public RuntimeOptionGuiHandler
            getHandlerFor(RuntimeOptionCategoryElement element) {

        return null;
    }
}
