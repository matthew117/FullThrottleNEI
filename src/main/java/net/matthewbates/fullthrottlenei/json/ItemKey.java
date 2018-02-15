package net.matthewbates.fullthrottlenei.json;

import net.minecraft.item.ItemStack;

/**
 * Created by Matthew on 15/04/2016.
 */
public class ItemKey
{
    private int hashcode;
    private ItemStack stack;

    public ItemKey(ItemStack stack)
    {
        this.stack = stack;
        if (stack != null)
            hashcode = (stack.getItem().hashCode() ^ stack.getItemDamage());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemKey itemKey = (ItemKey) o;

        return hashcode == itemKey.hashcode;

    }

    @Override
    public int hashCode()
    {
        return hashcode;
    }

    public ItemStack getItemStack()
    {
        return stack.copy();
    }
}
