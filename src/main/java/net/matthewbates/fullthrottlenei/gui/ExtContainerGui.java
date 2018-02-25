package net.matthewbates.fullthrottlenei.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

/**
 * Created by Matthew on 04/05/2016.
 */
public abstract class ExtContainerGui extends GuiContainer
{
    public ExtContainerGui(Container container)
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
