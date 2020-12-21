package net.matthewbates.fullthrottlenei.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

/**
 * Created by Matthew Bates on 04/05/2016.
 */
abstract class ExtContainerGui extends GuiContainer
{
    ExtContainerGui(Container container)
    {
        super(container);
    }

    int getLeft()
    {
        return guiLeft;
    }

    int getTop()
    {
        return guiTop;
    }

}
