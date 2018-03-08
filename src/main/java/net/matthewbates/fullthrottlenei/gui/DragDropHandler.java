package net.matthewbates.fullthrottlenei.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import net.matthewbates.fullthrottlenei.FullThrottleNEI;
import net.matthewbates.fullthrottlenei.network.DragDropPacket;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;

/**
 * Created by Matthew on 04/05/2016.
 */
public class DragDropHandler implements INEIGuiHandler
{

    @Override
    public VisiblityData modifyVisiblity(GuiContainer paramGuiContainer, VisiblityData paramVisiblityData)
    {
        return null;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer arg0, ItemStack arg1)
    {
        return Collections.emptyList();
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer paramGuiContainer)
    {
        return null;
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int x, int y, ItemStack item, int button)
    {
        if (!(gui instanceof ExtContainerGui))
            return false;

        Slot slot = null;

        x = x - ((ExtContainerGui) gui).getLeft();
        y = y - ((ExtContainerGui) gui).getTop();

        int index = 0;
        //noinspection unchecked
        for (Slot s : (List<Slot>) gui.inventorySlots.inventorySlots)
        {
            if (x >= s.xDisplayPosition - 1 && x <= s.xDisplayPosition + 16 && y >= s.yDisplayPosition - 1 && y <= s.yDisplayPosition + 16)
            {
                slot = s;
                break;
            }
            ++index;
        }

        if (slot instanceof GhostSlot)
        {
            int modifiers = 0;
            if (GuiScreen.isShiftKeyDown())
                modifiers = 1;
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
                modifiers |= 2;

            FullThrottleNEI.packetHandler.sendToServer(new DragDropPacket(gui.inventorySlots.windowId, index, button, modifiers, item.copy()));
            return true;
        }

        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h)
    {
        return false;
    }
}