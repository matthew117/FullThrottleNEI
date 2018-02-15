package net.matthewbates.fullthrottlenei.json;

import net.minecraft.item.ItemStack;
import pa.api.alchemy.IElementProvider;

import java.util.HashMap;

/**
 * Created by Matthew on 15/04/2016.
 */
public class JsonElementProvider implements IElementProvider
{
    private final HashMap<ItemKey, HashMap<String, Float>> cachedElements;

    public JsonElementProvider()
    {
        cachedElements = new HashMap<ItemKey, HashMap<String, Float>>();
    }

    public JsonElementProvider(HashMap<ItemKey, HashMap<String, Float>> elements)
    {
        if (elements != null)
            cachedElements = elements;
        else
            cachedElements = new HashMap<ItemKey, HashMap<String, Float>>();
    }

    public void addElements(ItemStack stack, HashMap<String, Float> elements)
    {
        cachedElements.put(new ItemKey(stack), elements);
    }

    @Override
    public HashMap<String, Float> getElements(ItemStack stack)
    {
        if (stack.getItem().isItemTool(stack) && stack.getItemDamage() > 0)
        {
            float percent = 1.0f - (float)stack.getItemDamage() / (float)stack.getMaxDamage();
            HashMap<String, Float> proportionalElements = new HashMap<String, Float>();
            ItemStack baseItemStack = new ItemStack(stack.getItem());
            for (HashMap.Entry<String, Float> p : cachedElements.get(new ItemKey(baseItemStack)).entrySet())
            {
                proportionalElements.put(p.getKey(), p.getValue() * percent);
            }
            return proportionalElements;
        }
        return cachedElements.get(new ItemKey(stack));
    }
}
