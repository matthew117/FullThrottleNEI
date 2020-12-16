package net.matthewbates.fullthrottlenei;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Comparator;

/**
 * Created by Matthew on 21/04/2016.
 */
public class Utils
{
    public static float sum(Collection<Float> xs)
    {
        float sum = 0.0f;
        for (float x : xs) sum += x;
        return sum;
    }

    public static float min(Collection<Float> xs)
    {
        float min = Float.POSITIVE_INFINITY;
        for (float x : xs) if (x < min) min = x;
        return min;
    }

    public static String getElementAmountFormatted(float amount)
    {
        NumberFormat format = NumberFormat.getInstance();
        if (amount >= 100.0F)
        {
            format.setMaximumFractionDigits(1);
        } else
        {
            format.setMaximumFractionDigits(2);
        }
        return format.format(amount) + "g";
    }

    public static class ItemIDComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(ItemStack a, ItemStack b)
        {
            if (a == b) return 0;
            int aID = Item.getIdFromItem(a.getItem());
            int bID = Item.getIdFromItem(b.getItem());
            if (aID == bID) return a.getItemDamage() < b.getItemDamage() ? -1 : 1;
            return aID < bID ? -1 : 1;
        }
    }
}
