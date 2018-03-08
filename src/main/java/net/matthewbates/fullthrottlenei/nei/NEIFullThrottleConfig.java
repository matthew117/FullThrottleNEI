package net.matthewbates.fullthrottlenei.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.matthewbates.fullthrottlenei.FullThrottleNEI;
import net.matthewbates.fullthrottlenei.config.ConfigurationHandler;
import net.matthewbates.fullthrottlenei.gui.DragDropHandler;

/**
 * Created by Matthew on 30/03/2016.
 */
@SideOnly(Side.CLIENT)
public class NEIFullThrottleConfig implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        AtelierCraftingRecipeHandler atelierCrafting = new AtelierCraftingRecipeHandler();
        API.registerRecipeHandler(atelierCrafting);
        API.registerUsageHandler(atelierCrafting);

        AtelierDecompositionRecipeHandler atelierDecomposition = new AtelierDecompositionRecipeHandler();
        API.registerRecipeHandler(atelierDecomposition);
        if (ConfigurationHandler.showNEIAtelierResearch)
        {
            AtelierDiscoveryRecipeHandler atelierDiscovery = new AtelierDiscoveryRecipeHandler();
            API.registerRecipeHandler(atelierDiscovery);
            API.registerUsageHandler(atelierDiscovery);
        }
        API.registerUsageHandler(atelierDecomposition);
        API.registerNEIGuiHandler(new DragDropHandler());

        //TODO: element consumer efficiency when right-click on elements "Element Consumers"
        //TODO: fancy background graphics
    }

    @Override
    public String getName()
    {
        return FullThrottleNEI.NAME;
    }

    @Override
    public String getVersion()
    {
        return FullThrottleNEI.VERSION;
    }
}
