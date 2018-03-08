package net.matthewbates.fullthrottlenei.gui;

import codechicken.nei.NEIServerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Matthew on 01/05/2016.
 */
public abstract class ExtContainer extends Container
{
    void bindPlayerInventory(InventoryPlayer inventoryPlayer, int x, int y)
    {
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 9; i++)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, i + j * 9 + 9, x + i * 18, y + j * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, x + i * 18, y + 58));
        }

    }

    private void handleFakeSlot(GhostSlot slot, int mouseButton, int modifier, ItemStack held)
    {
        if (held == null && !slot.getHasStack()) return;
        if (mouseButton == 2) // middle click
        {
            slot.putStack(null);
        } else
        {
            boolean shift = (modifier & 0x1) != 0;
            boolean ctrl = (modifier & 0x10) != 0;

            if (held != null)
            {
                if (slot.isItemValid(held))
                {
                    if (mouseButton == 0) // left click
                    {
                        ItemStack stack = new ItemStack(held.getItem(), held.stackSize, held.getItemDamage());
                        stack.stackSize = stack.stackSize > slot.getSlotStackLimit() ? slot.getSlotStackLimit() : stack.stackSize;
                        slot.putStack(stack);
                    } else if (mouseButton == 1) // right click
                    {
                        ItemStack stackInSlot = slot.getStack();
                        if (stackInSlot != null && NEIServerUtils.areStacksSameTypeCrafting(stackInSlot, held))
                        {
                            if (stackInSlot.stackSize + 1 <= slot.getSlotStackLimit())
                                stackInSlot.stackSize++;
                        } else
                        {
                            ItemStack stack = new ItemStack(held.getItem(), held.stackSize, held.getItemDamage());
                            stack.stackSize = 1;
                            slot.putStack(stack);
                        }
                    }
                }
            } else
            {
                ItemStack stackInSlot = slot.getStack();
                if (mouseButton == 0) // left click
                {
                    stackInSlot.stackSize += shift ? 16 : 1;
                    stackInSlot.stackSize = stackInSlot.stackSize > slot.getSlotStackLimit() ? slot.getSlotStackLimit() : stackInSlot.stackSize;
                } else if (mouseButton == 1) // right click
                {
                    stackInSlot.stackSize -= shift ? 16 : 1;
                    if (stackInSlot.stackSize <= 0)
                        slot.putStack(null);
                }
            }
        }
    }

    @Override
    public ItemStack slotClick(int slotIndex, int mouseButton, int modifier, EntityPlayer player)
    {
        if (slotIndex >= 0 && slotIndex < inventorySlots.size())
        {
            if (inventorySlots.get(slotIndex) instanceof GhostSlot)
            {
                ItemStack existingItem = ((Slot) inventorySlots.get(slotIndex)).getStack();
                if (existingItem != null)
                    existingItem = existingItem.copy();

                handleFakeSlot((GhostSlot) inventorySlots.get(slotIndex), mouseButton, modifier, player.inventory.getItemStack());

                return existingItem;
            }
        }

        return super.slotClick(slotIndex, mouseButton, modifier, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean canDragIntoSlot(Slot slot)
    {
        return !(slot instanceof GhostSlot) && super.canDragIntoSlot(slot);
    }

    public void dropItem(int slotIndex, int mouseButton, int modifier, ItemStack item)
    {
        if (slotIndex >= 0 && slotIndex < inventorySlots.size())
        {
            if (inventorySlots.get(slotIndex) instanceof GhostSlot)
                handleFakeSlot((GhostSlot) inventorySlots.get(slotIndex), mouseButton, modifier, item);
        }
    }
}
