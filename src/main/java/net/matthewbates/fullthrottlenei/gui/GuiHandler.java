package net.matthewbates.fullthrottlenei.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by Matthew Bates on 30/04/2016.
 */
public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case GuiTypes.EDIT_ELEMENTS:
                return new EditElementsContainer(player.inventory);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case GuiTypes.EDIT_ELEMENTS:
                return new EditElementsGui(new EditElementsContainer(player.inventory));
        }

        return null;
    }
}
