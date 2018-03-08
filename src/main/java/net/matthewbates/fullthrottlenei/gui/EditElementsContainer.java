package net.matthewbates.fullthrottlenei.gui;

import net.matthewbates.fullthrottlenei.integration.AlchemyUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Matthew on 29/04/2016.
 */
class EditElementsContainer extends ExtContainer
{
    final IInventory editedItem = new InventoryCrafting(this, 1, 1);
    private final IInventory inventory;

    EditElementsContainer(InventoryPlayer playerInv)
    {
        inventory = new InventoryBasic("", false, 16);
        bindPlayerInventory(playerInv, 48, 137);

        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                addSlotToContainer(new ElementFlaskGhostSlot(inventory, j + i * 4, 8 + j * 55, 8 + i * 40));
            }
        }

        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                addSlotToContainer(new GhostSlot(inventory, 8 + j + i * 4, 8 + j * 18, 93 + i * 18));
            }
        }


        addSlotToContainer(new GhostSlot(editedItem, 0, 95, 102, 1));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotIndex)
    {
        return null;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        ItemStack stack = editedItem.getStackInSlot(0);
        if (stack != null)
        {
            try
            {
                Class<?> clazz = Class.forName("pa.data.PAItems");
                Field flaskf = clazz.getDeclaredField("forceFlask");
                Item flask = (Item) flaskf.get(null);

                if (flask != null)
                {
                    int i = 0;
                    for (Map.Entry<String, Float> entry : AlchemyUtil.getElementsSortedByAmount(stack).entrySet())
                    {
                        ItemStack fstack = new ItemStack(flask);
                        fstack.setTagCompound(new NBTTagCompound());
                        fstack.setItemDamage(32 + AlchemyUtil.getElementByName(entry.getKey()).getNumber());
                        fstack.getTagCompound().setFloat("amount", entry.getValue());
                        this.inventory.setInventorySlotContents(i, fstack);
                        i++;
                    }
                    for (int j = i; j < 8; j++)
                    {
                        this.inventory.setInventorySlotContents(j, null);
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        } else
        {
            for (int i = 0; i < 8; i++)
            {
                this.inventory.setInventorySlotContents(i, null);
            }
        }
    }

}
