package net.matthewbates.fullthrottlenei.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by Matthew Bates on 05/05/2016.
 */
public class ElementFlaskGhostSlot extends GhostSlot
{
    public ElementFlaskGhostSlot(IInventory inventory, int slotIndex, int x, int y)
    {
        super(inventory, slotIndex, x, y, 1);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        try
        {
            return (stack.getItem().getClass() == Class.forName("pa.item.ItemFlask") && stack.getItemDamage() >= 32 && stack.hasTagCompound());
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
