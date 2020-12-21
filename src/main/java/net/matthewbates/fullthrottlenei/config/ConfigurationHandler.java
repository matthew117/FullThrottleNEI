package net.matthewbates.fullthrottlenei.config;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.matthewbates.fullthrottlenei.FullThrottleNEI;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Matthew Bates on 16/04/2016.
 */
public class ConfigurationHandler
{
    public static Configuration configuration;
    public static boolean allowJson;
    public static boolean showNEIAtelierResearch;

    public static void init(File configFile)
    {
        if (configuration == null)
        {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        allowJson = configuration
                .getBoolean("Allow JSON",
                            "Features",
                            true,
                            "Allows adding elements, atelier recipes, grinding recipes and freezing recipes via the json file in the config folder.");
        showNEIAtelierResearch = configuration
                .getBoolean("Show Atelier Research in NEI",
                            "Features",
                            true,
                            "Shows items that can be used to unlock atelier recipes in NEI.");

        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equals(FullThrottleNEI.MODID))
        {
            loadConfiguration();
        }
    }
}
