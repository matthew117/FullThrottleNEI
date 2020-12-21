package net.matthewbates.fullthrottlenei.gui;

import codechicken.lib.inventory.SlotDummy;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;

/**
 * Created by Matthew Bates on 01/05/2016.
 */
class SlotElementFlask extends SlotDummy
{
    private final ExtContainer container;

    SlotElementFlask(ExtContainer container, IInventory inv, int slot, int x, int y)
    {
        super(inv, slot, x, y, 1);
        this.container = container;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        try
        {
            Class<?> clazz = Class.forName("pa.data.PAItems");
            Field flaskf = clazz.getDeclaredField("forceFlask");
            Item flask = (Item) flaskf.get(null);
            if (stack != null && stack.getItem() == flask && stack.getItemDamage() >= 32)
                return true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onSlotChanged()
    {
        super.onSlotChanged();
//        container.update();
    }
}
