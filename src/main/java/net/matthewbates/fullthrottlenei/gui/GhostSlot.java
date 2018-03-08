package net.matthewbates.fullthrottlenei.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Matthew on 03/05/2016.
 */
public class GhostSlot extends Slot
{
    private int limit = -1;

    public GhostSlot(IInventory inventory, int slotIndex, int x, int y, int limit)
    {
        super(inventory, slotIndex, x, y);
        this.limit = limit;
    }

    public GhostSlot(IInventory inventory, int slotIndex, int x, int y)
    {
        super(inventory, slotIndex, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getSlotStackLimit()
    {
        return limit > 0 ? limit : Integer.MAX_VALUE;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        return false;
    }
}
