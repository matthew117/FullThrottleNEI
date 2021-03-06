package net.matthewbates.fullthrottlenei.gui;

import cpw.mods.fml.client.config.GuiConfig;
import net.matthewbates.fullthrottlenei.FullThrottleNEI;
import net.matthewbates.fullthrottlenei.config.ConfigurationHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

/**
 * Created by Matthew Bates on 16/04/2016.
 */
public class ModGuiConfig extends GuiConfig
{
    public ModGuiConfig(GuiScreen guiScreen)
    {
        //noinspection unchecked
        super(guiScreen,
              new ConfigElement(ConfigurationHandler.configuration.getCategory("features")).getChildElements(),
              FullThrottleNEI.MOD_ID,
              false,
              false,
              GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
        //TODO: Check whether world needs reload or not
    }
}
